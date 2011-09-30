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

package edu.umn.msi.tropix.webgui.server.download;

import java.io.OutputStream;
import java.util.Map;

import javax.annotation.ManagedBean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.spring.AnnotatedBeanProcessor;

@ManagedBean
public class FileDownloadManager extends AnnotatedBeanProcessor<FileDownloadHandlerType> {
  private static final Log LOG = LogFactory.getLog(FileDownloadManager.class);

  public FileDownloadManager() {
    super(FileDownloadHandlerType.class);
  }

  private final Map<String, FileDownloadHandler> handlers = Maps.newHashMap();

  public void handleDownloadRequest(final String downloadType, final OutputStream stream, final Function<String, String> accessor) {
    final FileDownloadHandler handler = handlers.get(downloadType);
    handler.processDownloadRequest(stream, accessor);
  }

  protected void processBeans(final Iterable<Object> annotatedBeans) {
    for(final Object annotatedBean : annotatedBeans) {
      final String type = getAnnotation(annotatedBean).value();
      final FileDownloadHandler fileDownloadHandler = (FileDownloadHandler) annotatedBean;
      LOG.info("Registering FileDownloadHandler of type " + type + " to bean " + fileDownloadHandler);
      handlers.put(type, fileDownloadHandler);
    }
  }
}
