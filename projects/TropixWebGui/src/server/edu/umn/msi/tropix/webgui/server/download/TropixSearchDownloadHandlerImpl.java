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

import info.minnesotapartnership.tropix.search.TropixSearchService;

import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.springframework.util.StringUtils;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.ZipUtils;
import edu.umn.msi.tropix.common.io.ZipUtilsFactory;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.grid.GridServiceFactory;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.io.transfer.DownloadContextFactory;
import edu.umn.msi.tropix.webgui.client.utils.Iterables;
import edu.umn.msi.tropix.webgui.server.security.UserSession;

@ManagedBean
@FileDownloadHandlerType("search")
class TropixSearchDownloadHandlerImpl implements FileDownloadHandler {
  private static final ZipUtils ZIP_UTILS = ZipUtilsFactory.getInstance();
  private static final Log LOG = LogFactory.getLog(TropixStorageDownloadHandlerImpl.class);
  private final DownloadContextFactory<Credential> downloadContextFactory;
  private final GridServiceFactory<TropixSearchService> tropixSearchServiceFactory;
  private final UserSession userSession;

  @Inject
  TropixSearchDownloadHandlerImpl(final DownloadContextFactory<Credential> downloadContextFactory, final GridServiceFactory<TropixSearchService> tropixSearchServiceFactory, final UserSession userSession) {
    this.downloadContextFactory = downloadContextFactory;
    this.tropixSearchServiceFactory = tropixSearchServiceFactory;
    this.userSession = userSession;
  }
  
  private InputContext getDownloadContext(final String serviceUrl, final String id) {
    LOG.debug("Attempting download from search service " + serviceUrl + " and id" + id);
    final Credential proxy = userSession.getProxy();
    final TropixSearchService tropixSearchService = tropixSearchServiceFactory.getService(serviceUrl, proxy);
    TransferServiceContextReference tscRef;
    try {
      LOG.debug("Preparing download for search file.");
      tscRef = tropixSearchService.prepareDownload(id, null);
      LOG.debug("Prepare download returned successfully.");
    } catch(final RemoteException e) {
      throw ExceptionUtils.convertException(e, "Failed to prepare download");
    }
    final InputContext inputContext = downloadContextFactory.getDownloadContext(tscRef, proxy);
    return inputContext;
  }

  public void processDownloadRequest(final OutputStream outputStream, final Function<String, String> accessor) {
    final String id = accessor.apply("id");
    final String serviceUrl = accessor.apply("serviceUrl");
    final String isBatchStr = accessor.apply("isBatch");
    Preconditions.checkNotNull(id);
    Preconditions.checkNotNull(serviceUrl);
    final boolean isBatch = StringUtils.hasText(isBatchStr) ? Boolean.parseBoolean(isBatchStr) : false;
    if(isBatch) {
      final String namesStr = accessor.apply("names");
      final String[] names = namesStr.split("\\s*,\\s*");
      final String[] serviceUrls = serviceUrl.split("\\s*,\\s*");
      final String[] ids = id.split("\\s*,\\s*");
      Preconditions.checkState(names.length == serviceUrls.length && names.length == ids.length);
      final List<Integer> zeroToN = Lists.newArrayList();
      for(int i = 0; i < ids.length; i++) {
        zeroToN.add(i, i);
      }
      Iterable<InputContext> inputContextIterable = Iterables.transform(zeroToN, new Function<Integer, InputContext>() {
        public InputContext apply(final Integer i) {
          return getDownloadContext(serviceUrls[i], ids[i]);
        }        
      });
      ZIP_UTILS.zipContextsToStream(inputContextIterable, Arrays.asList(names), outputStream);
    } else {
      final InputContext inputContext = getDownloadContext(serviceUrl, id);
      inputContext.get(outputStream);
    }
  }

}
