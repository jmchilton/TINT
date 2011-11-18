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

package edu.umn.msi.tropix.labs.requests.impl;

import java.util.UUID;

import javax.inject.Inject;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;

import edu.umn.msi.tropix.common.test.ConfigDirBuilder;
import edu.umn.msi.tropix.common.test.FreshConfigTest;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.labs.requests.RequestUpdateHandler.RequestResult;
import edu.umn.msi.tropix.models.InternalRequest;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.service.FileService;
import edu.umn.msi.tropix.persistence.service.RequestService;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;
import edu.umn.msi.tropix.persistence.service.UserService;
import edu.umn.msi.tropix.persistence.service.requestid.RequestId;
import edu.umn.msi.tropix.storage.client.impl.CredentialBoundModelStorageDataFactoryImpl;
import edu.umn.msi.tropix.transfer.http.client.HttpTransferClients;
import edu.umn.msi.tropix.transfer.types.HttpTransferResource;

@ContextConfiguration
public class HandlerRequestResultSpringTest extends FreshConfigTest {
  private String requestServiceIdentity = UUID.randomUUID().toString();

  @Override
  protected void initializeConfigDir(final ConfigDirBuilder configDirBuilder) {
    final ConfigDirBuilder storageConfig = configDirBuilder.createSubConfigDir("storage");
    storageConfig.addDeployProperty("storage.committing.caller.ids", requestServiceIdentity);
  }

  @Inject
  private RequestUpdateHandlerImpl handler;

  @Inject
  private CredentialBoundModelStorageDataFactoryImpl storageManager;

  @Inject
  private RequestService requestService;

  @Inject
  private UserService userService;

  @Inject
  private TropixObjectService tropixObjectService;

  @Inject
  private FileService fileService;

  @Test(groups = "spring")
  public void testHandleResult() throws InterruptedException {
    storageManager.setCredentialSupplier(Suppliers.ofInstance(Credentials.getMock(requestServiceIdentity)));
    final String requestorId = UUID.randomUUID().toString();
    final String requestServiceId = UUID.randomUUID().toString();
    final User user = userService.createOrGetUser(requestorId);
    final Credential credential = Credentials.getMock(requestorId);

    final InternalRequest request = new InternalRequest();

    request.setServiceId(requestServiceId);
    request.setRequestorId(credential.getIdentity());
    request.setName("name");
    request.setDescription("description");
    request.setDestination("destination");
    request.setRequestServiceUrl(requestServiceId);
    request.setStorageServiceUrl("url");
    request.setState("ACTIVE");
    final InternalRequest savedRequest = requestService.setupInternalRequest(user.getCagridId(), request, user.getHomeFolder().getId());

    final String eRequestId = savedRequest.getId();
    final String hostId = UUID.randomUUID().toString();
    final RequestId requestId = new RequestId(eRequestId, hostId);

    final RequestResult requestResult = new RequestResult(requestId, "filename", "description");
    final HttpTransferResource resource = (HttpTransferResource) handler.handleResult(requestResult);
    HttpTransferClients.getInstance().getOutputContext(resource.getUrl()).put("Moo Cow".getBytes());

    Thread.sleep(1000);

    final TropixObject[] objects = tropixObjectService.getChildren(requestorId, user.getHomeFolder().getId());
    assert objects.length == 1 : objects.length;
    final TropixFile file = (TropixFile) objects[0];
    assert fileService.loadPhysicalFile(file.getFileId()).getSize() == "Moo Cow".getBytes().length;
    assert file.getCommitted();

  }

}
