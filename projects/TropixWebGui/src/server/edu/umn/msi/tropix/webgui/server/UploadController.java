/*******************************************************************************
 * Copyright 2009 Regents of the University of Minnesota. All rights
 * reserved.
 * Copyright 2009 Mayo Foundation for Medical Education and Research.
 * All rights reserved.
 *
 * This program is made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED INCLUDING, WITHOUT LIMITATION, ANY WARRANTIES OR CONDITIONS
 * OF TITLE, NON-INFRINGEMENT, MERCHANTABILITY OR FITNESS FOR A
 * PARTICULAR PURPOSE.  See the License for the specific language
 * governing permissions and limitations under the License.
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 ******************************************************************************/

package edu.umn.msi.tropix.webgui.server;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.UUID;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONWriter;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.google.common.base.Function;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.io.ZipUtilsFactory;
import edu.umn.msi.tropix.webgui.server.aop.ServiceMethod;
import edu.umn.msi.tropix.webgui.server.messages.MessagePusher;
import edu.umn.msi.tropix.webgui.server.progress.ProgressMessageSupplier;
import edu.umn.msi.tropix.webgui.server.progress.ProgressTrackerImpl;
import edu.umn.msi.tropix.webgui.server.progress.ProgressTrackingIOUtils;
import edu.umn.msi.tropix.webgui.server.security.UserSession;
import edu.umn.msi.tropix.webgui.server.storage.TempFileStore;
import edu.umn.msi.tropix.webgui.server.storage.TempFileStore.TempFileInfo;
import edu.umn.msi.tropix.webgui.services.message.Message;

public class UploadController implements Controller {
  private static final Log LOG = LogFactory.getLog(UploadController.class);
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();

  private TempFileStore tempFileStore;
  private MessagePusher<Message> cometPusher;
  private ProgressTrackingIOUtils progressTrackingIoUtils;
  private UserSession userSession;
  
  @Inject
  public void setProgressTrackingIoUtils(final ProgressTrackingIOUtils progressTrackingIoUtils) {
    this.progressTrackingIoUtils = progressTrackingIoUtils;
  }

  @Inject
  public void setCometPusher(final MessagePusher<Message> cometPusher) {
    this.cometPusher = cometPusher;
  }

  @Inject
  public void setTempFileStore(final TempFileStore tempFileStore) {
    this.tempFileStore = tempFileStore;
  }
  
  @Inject
  public void setUserSession(final UserSession userSession) {
    this.userSession = userSession;
  }
  

  private void recordJsonInfo(final JSONWriter jsonWriter, final TempFileInfo tempFileInfo) {
    try {
      jsonWriter.object();
      jsonWriter.key("fileName");
      jsonWriter.value(tempFileInfo.getFileName());
      jsonWriter.key("id");
      jsonWriter.value(tempFileInfo.getId());
      jsonWriter.endObject();
    } catch(Exception e) {
      throw new RuntimeException(e);
    }
  }

  @ServiceMethod(secure = true)
  public ModelAndView handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
    LOG.debug("In UploadController.handleRequest");
    //final String userId = securityProvider.getUserIdForSessionId(request.getParameter("sessionId"));
    //Preconditions.checkState(userId != null);
    final String userId = userSession.getGridId();
    LOG.debug("Upload by user with id " + userId);
    String clientId = request.getParameter("clientId");
    if(!StringUtils.hasText(clientId)) {
      clientId = UUID.randomUUID().toString();
    }
    final String endStr = request.getParameter("end");
    final String startStr = request.getParameter("start");
    final String zipStr = request.getParameter("zip");
    
    final String lastUploadStr = request.getParameter("lastUpload");
    final boolean isZip = StringUtils.hasText("zip") ? Boolean.parseBoolean(zipStr) : false;
    final boolean lastUploads = StringUtils.hasText(lastUploadStr) ? Boolean.parseBoolean(lastUploadStr) : true;

    LOG.trace("Upload request with startStr " + startStr);
    final StringWriter rawJsonWriter = new StringWriter();
    final JSONWriter jsonWriter = new JSONWriter(rawJsonWriter);
    final FileItemFactory factory = new DiskFileItemFactory();

    final long requestLength = StringUtils.hasText(endStr) ? Long.parseLong(endStr) : request.getContentLength();
    long bytesWritten = StringUtils.hasText(startStr) ? Long.parseLong(startStr) : 0L;
    
    final ServletFileUpload upload = new ServletFileUpload(factory);
    upload.setHeaderEncoding("UTF-8"); // Deal with international file names
    final FileItemIterator iter = upload.getItemIterator(request);

    // Setup message conditionalSampleComponent to track upload progress...
    final ProgressMessageSupplier supplier = new ProgressMessageSupplier();
    supplier.setId(userId + "/" + clientId);
    supplier.setName("Upload File(s) to Web Application");

    final ProgressTrackerImpl progressTracker = new ProgressTrackerImpl();
    progressTracker.setUserGridId(userId);
    progressTracker.setCometPusher(cometPusher);
    progressTracker.setProgressMessageSupplier(supplier);
       
    jsonWriter.object();
    jsonWriter.key("result");
    jsonWriter.array();
        
    while(iter.hasNext()) {
      final FileItemStream item = iter.next();
      if(item.isFormField()) {
        continue;
      }
      File destination;
      InputStream inputStream = null;
      OutputStream outputStream = null;
      
      if(!isZip) {
        final String fileName = FilenameUtils.getName(item.getName()); // new File(item.getName()).getName();
        LOG.debug("Handling upload of file with name " + fileName);
        
        final TempFileInfo info = tempFileStore.getTempFileInfo(fileName);
        recordJsonInfo(jsonWriter, info);        
        destination = info.getTempLocation();
      } else {
        destination = FILE_UTILS.createTempFile();
      }
      
      try {
        inputStream = item.openStream();
        outputStream = FILE_UTILS.getFileOutputStream(destination);
        bytesWritten += progressTrackingIoUtils.copy(inputStream, outputStream, bytesWritten, requestLength, progressTracker);

        if(isZip) {
          ZipUtilsFactory.getInstance().unzip(destination, new Function<String, File>() {
            public File apply(final String fileName) {
              final String cleanedUpFileName = FilenameUtils.getName(fileName);
              final TempFileInfo info = tempFileStore.getTempFileInfo(cleanedUpFileName);
              recordJsonInfo(jsonWriter, info);        
              return info.getTempLocation();
            }            
          });
        } 
      } finally {
        IO_UTILS.closeQuietly(inputStream);
        IO_UTILS.closeQuietly(outputStream);
        if(isZip) {
          FILE_UTILS.deleteQuietly(destination);
        }
      }
    }
    if(lastUploads) {
      progressTracker.complete();
    }
    jsonWriter.endArray();
    jsonWriter.endObject();
    // response.setStatus(200);
    final String json = rawJsonWriter.getBuffer().toString();
    LOG.debug("Upload json response " + json);
    response.setContentType("text/html"); // GWT was attaching <pre> tag to result without this
    response.getOutputStream().println(json);
    return null;
  }
}
