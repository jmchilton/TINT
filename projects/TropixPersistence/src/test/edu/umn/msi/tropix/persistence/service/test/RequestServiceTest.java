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

package edu.umn.msi.tropix.persistence.service.test;

import java.util.HashSet;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.InternalRequest;
import edu.umn.msi.tropix.models.Provider;
import edu.umn.msi.tropix.models.Request;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.dao.ProviderDao;
import edu.umn.msi.tropix.persistence.service.RequestService;
import edu.umn.msi.tropix.persistence.service.request.RequestSubmission;
import edu.umn.msi.tropix.persistence.service.requestid.RequestId;
import edu.umn.msi.tropix.persistence.service.security.SecurityProvider;

public class RequestServiceTest extends ServiceTest {
  @Autowired
  private RequestService requestService;

  @Autowired
  private ProviderDao providerDao;

  @Autowired
  private SecurityProvider securityProvider;

  private static class RequestInfo {
    private Request request;
    private RequestId requestId;
  }

  private RequestInfo getNewRequestInfo(final boolean internal) {
    final Request request = internal ? new InternalRequest() : new Request();
    final String id = newId();
    final String externalId = internal ? id : newId();
    request.setState("ACTIVE");
    request.setContents(new HashSet<TropixObject>());
    request.setId(id);
    request.setRequestorId(newId());
    request.setExternalId(externalId);
    final RequestInfo info = new RequestInfo();
    info.request = request;
    info.requestId = new RequestId();
    info.requestId.setRequestExternalId(request.getExternalId());
    info.requestId.setRequestHostId(request.getRequestorId());
    return info;
  }

  @Test
  public void permissions() {
    final Request request = new Request();
    saveNewTropixObject(request);
    final String requestId = request.getId();

    final Provider provider = new Provider();
    provider.setObjects(new HashSet<TropixObject>());
    provider.setUsers(new HashSet<User>());
    provider.setGroups(new HashSet<Group>());
    provider.setRole("write");
    providerDao.saveObject(provider);

    final User u1 = createTempUser();

    assert !this.securityProvider.canRead(requestId, u1.getCagridId());
    assert !this.securityProvider.canModify(requestId, u1.getCagridId());

    provider.getObjects().add(request);
    getTropixObjectDao().saveOrUpdateTropixObject(request);
    providerDao.saveObject(provider);

    assert !this.securityProvider.canRead(requestId, u1.getCagridId());
    assert !this.securityProvider.canModify(requestId, u1.getCagridId());

    provider.getUsers().add(u1);
    providerDao.saveObject(provider);
    getUserDao().saveOrUpdateUser(u1);

    assert this.securityProvider.canRead(requestId, u1.getCagridId());
    assert this.securityProvider.canModify(requestId, u1.getCagridId());

    final User u2 = createTempUser();
    assert !this.securityProvider.canRead(requestId, u2.getCagridId());
    assert !this.securityProvider.canModify(requestId, u2.getCagridId());

    final Group g = createTempGroup(u2);
    assert !this.securityProvider.canRead(requestId, u2.getCagridId());
    assert !this.securityProvider.canModify(requestId, u2.getCagridId());
    provider.getGroups().add(g);
    providerDao.saveObject(provider);
    getDaoFactory().getDao(Group.class).saveObject(g);

    assert this.securityProvider.canRead(requestId, u2.getCagridId());
    assert this.securityProvider.canRead(requestId, u1.getCagridId());
    assert this.securityProvider.canModify(requestId, u2.getCagridId());
    assert this.securityProvider.canModify(requestId, u1.getCagridId());

  }

  @Test
  public void getOutgoingRequests() {
    final User user = createTempUser();
    final String userId = user.getCagridId();
    InternalRequest[] requests = requestService.getOutgoingRequests(userId);
    assert requests.length == 0;

    final InternalRequest request = new InternalRequest();
    request.setState("ACTIVE");
    request.setCommitted(true);
    super.saveNewTropixObject(request, user);

    requests = requestService.getOutgoingRequests(userId);
    assert requests.length == 1;
    assert requests[0].getId().equals(request.getId());
  }

  @Test
  public void getInternalReport() {
    final User user = createTempUser();
    final InternalRequest request = new InternalRequest();
    request.setState("ACTIVE");
    request.setCommitted(true);
    request.setReport("This is a long report");
    super.saveNewTropixObject(request, user);

    assert requestService.getReport(user.getCagridId(), request.getId()).equals("This is a long report");
  }

  @Test
  public void getContact() {
    final User user = createTempUser();
    final InternalRequest request = new InternalRequest();
    request.setState("ACTIVE");
    request.setCommitted(true);
    request.setContact("Contact");
    super.saveNewTropixObject(request, user);

    assert requestService.getContact(user.getCagridId(), request.getId()).equals("Contact");

  }

  @Test
  public void getActiveRequests() {
    final User user = createTempUser();
    assert requestService.getActiveRequests(user.getCagridId()).length == 0;

    final Request request = new Request();
    request.setState("ACTIVE");
    request.setCommitted(true);
    super.saveNewTropixObject(request, user);
    assert requestService.getActiveRequests(user.getCagridId()).length == 0;

    final Provider provider = new Provider();
    provider.setUsers(new HashSet<User>());
    providerDao.saveObject(provider);

    provider.getUsers().add(user);

    getUserDao().saveOrUpdateUser(user);
    providerDao.saveObject(provider);

    assert requestService.getActiveRequests(user.getCagridId()).length == 0;

    request.setProvider(provider);
    getTropixObjectDao().saveOrUpdateTropixObject(request);
    providerDao.saveObject(provider);

    assert requestService.getActiveRequests(user.getCagridId()).length == 1;

    request.setState("COMPLETE");
    getTropixObjectDao().saveOrUpdateTropixObject(request);
    assert requestService.getActiveRequests(user.getCagridId()).length == 0;

    final Group group = super.createTempGroup();

    final Request request2 = new Request();
    request2.setState("ACTIVE");
    request2.setCommitted(true);
    super.saveNewTropixObject(request, user);

    final Provider provider2 = new Provider();
    provider2.setGroups(new HashSet<Group>());
    providerDao.saveObject(provider2);

    assert requestService.getActiveRequests(user.getCagridId()).length == 0;

    request2.setProvider(provider2);
    getTropixObjectDao().saveOrUpdateTropixObject(request2);

    assert requestService.getActiveRequests(user.getCagridId()).length == 0;

    provider2.getGroups().add(group);

    providerDao.saveObject(provider2);
    getDaoFactory().getDao(Group.class).saveObject(group);

    assert requestService.getActiveRequests(user.getCagridId()).length == 0;

    group.getUsers().add(user);
    user.getGroups().add(group);

    getDaoFactory().getDao(Group.class).saveObject(group);
    getUserDao().saveOrUpdateUser(user);

    assert requestService.getActiveRequests(user.getCagridId()).length == 1;

  }

  @Test
  public void getReport() {
    final RequestInfo newRequest = getNewRequestInfo(false);
    newRequest.request.setReport("The Report");
    getTropixObjectDao().saveOrUpdateTropixObject(newRequest.request);
    assert "The Report".equals(requestService.getReport(newRequest.requestId));
  }

  @Test
  public void loadInternalRequestId() {
    final RequestInfo newRequest = getNewRequestInfo(true);
    getTropixObjectDao().saveOrUpdateTropixObject(newRequest.request);
    final InternalRequest internalRequest = requestService.loadInternalRequest(newRequest.requestId);
    assert internalRequest != null;
    assert internalRequest.getId().equals(newRequest.request.getId());
  }

  @Test
  public void loadInternalRequest() {
    final User user = createTempUser();
    final RequestInfo newRequest = getNewRequestInfo(true);
    super.saveNewTropixObject(newRequest.request, user);
    final InternalRequest internalRequest = requestService.loadInternalRequest(user.getCagridId(), newRequest.request.getId());
    assert internalRequest.getId().equals(newRequest.request.getId());
  }

  @Test
  public void setupRequestFile() {
    final Request request = new Request();
    super.initTempRequest(request);

    final TropixFile file = new TropixFile();
    file.setName("Moo Cow");
    file.setCommitted(false);

    requestService.setupRequestFile(request.getId(), file);

    final TropixFile loadedFile = getTropixObjectDao().loadTropixObject(file.getId(), TropixFile.class);
    assert !loadedFile.getCommitted();
    assert loadedFile.getName().equals("Moo Cow");
    assert getTropixObjectDao().loadTropixObject(request.getId(), Request.class).getContents().contains(loadedFile);
    assert request.getProvider().getObjects().contains(loadedFile);
  }

  @Test
  public void setupInternalRequestFile() {
    final User user = super.createTempUser();
    final InternalRequest request = new InternalRequest();
    request.setDestinationFolder(user.getHomeFolder());
    super.initTempRequest(request);

    final TropixFile file = new TropixFile();
    file.setName("Moo Cow");
    file.setCommitted(false);

    requestService.setupRequestFile(request.getId(), file);

    final TropixFile loadedFile = getTropixObjectDao().loadTropixObject(file.getId(), TropixFile.class);
    assert !loadedFile.getCommitted();
    assert loadedFile.getName().equals("Moo Cow");
    assert getTropixObjectDao().loadTropixObject(request.getId(), Request.class).getContents().contains(loadedFile);
    assert request.getProvider().getObjects().contains(loadedFile);
    assert user.getHomeFolder().getContents().contains(loadedFile);
    assert getTropixObjectDao().getOwnerId(loadedFile.getId()).equals(user.getCagridId());
  }

  @Test
  public void updatedStateExternal() {
    final RequestInfo newRequest = getNewRequestInfo(true); // Must be for an InternalRequest...
    getTropixObjectDao().saveOrUpdateTropixObject(newRequest.request);
    requestService.updateState(newRequest.requestId, "COMPLETE");
    final Request loadedRequest = getTropixObjectDao().loadTropixObject(newRequest.request.getId(), Request.class);
    assert loadedRequest.getState().equals("COMPLETE");
  }

  @Test
  public void updatedState() {
    final User tempUser = createTempUser();
    final Request request = new Request();
    super.saveNewTropixObject(request, tempUser);
    requestService.updateState(tempUser.getCagridId(), request.getId(), "COMPLETE");
    final Request loadedRequest = getTropixObjectDao().loadTropixObject(request.getId(), Request.class);
    assert loadedRequest.getState().equals("COMPLETE");
  }

  @Test
  public void updateRequest() {
    final String providerId = newId();
    final RequestInfo requestInfo = getNewRequestInfo(false);
    requestInfo.request.setState("COMPLETE");
    final Provider provider = new Provider();
    provider.setUsers(new HashSet<User>());
    provider.setGroups(new HashSet<Group>());
    provider.setObjects(new HashSet<TropixObject>());
    provider.setCatalogId(providerId);
    this.getDaoFactory().getDao(Provider.class).saveObject(provider);

    getTropixObjectDao().saveOrUpdateTropixObject(requestInfo.request);

    final RequestSubmission submission = new RequestSubmission();
    submission.setContact("123");
    submission.setDescription("descript");
    submission.setDestination("http://yahoo.net");
    submission.setName("name");
    submission.setProviderId(providerId);
    final String serviceId = newId();
    submission.setServiceId(serviceId);
    submission.setServiceInfo("info");

    final Request request = requestService.createOrUpdateRequest(requestInfo.requestId, submission);
    assert request.getName().equals("name");
    assert request.getContact().equals("123");
    assert request.getDescription().equals("descript");
    assert request.getProvider().getCatalogId().equals(providerId);
    assert request.getServiceId().equals(serviceId);
    assert request.getServiceInfo().equals("info");
    assert request.getState().equals("COMPLETE");
  }

  @Test
  public void createRequest() {
    final String providerId = newId();
    final RequestInfo requestInfo = getNewRequestInfo(false);
    final Provider provider = new Provider();
    provider.setObjects(new HashSet<TropixObject>());
    provider.setUsers(new HashSet<User>());
    provider.setCatalogId(providerId);
    this.getDaoFactory().getDao(Provider.class).saveObject(provider);

    final RequestSubmission submission = new RequestSubmission();
    submission.setContact("123");
    submission.setDescription("descript");
    submission.setDestination("http://yahoo.net");
    submission.setName("name");
    submission.setProviderId(providerId);
    final String serviceId = newId();
    submission.setServiceId(serviceId);
    submission.setServiceInfo("info");

    final Request request = requestService.createOrUpdateRequest(requestInfo.requestId, submission);
    assert request.getName().equals("name");
    assert request.getContact().equals("123");
    assert request.getDescription().equals("descript");
    assert request.getProvider().getCatalogId().equals(providerId);
    assert request.getServiceId().equals(serviceId);
    assert request.getServiceInfo().equals("info");
    assert request.getState().equals("ACTIVE");

  }

  @Test
  public void loadRequest() {
    final Request request = new Request();
    request.setName("The name");
    getTropixObjectDao().saveOrUpdateTropixObject(request);
    assert requestService.loadRequest(request.getId()).getName().equals("The name");
  }

  @Test
  public void setReport() {
    final User user = createTempUser();

    final Request request = new Request();
    super.saveNewTropixObject(request, user);

    this.requestService.setReport(user.getCagridId(), request.getId(), "The report");
    assert getTropixObjectDao().loadTropixObject(request.getId(), Request.class).getReport().equals("The report");
    this.requestService.setReport(user.getCagridId(), request.getId(), "A new report");
    assert getTropixObjectDao().loadTropixObject(request.getId(), Request.class).getReport().equals("A new report");
  }

  @Test
  public void isInternalRequest() {
    final Request request = new Request();
    getTropixObjectDao().saveOrUpdateTropixObject(request);
    assert !requestService.isInternalRequest(request.getId());
    final InternalRequest iRequest = new InternalRequest();
    getTropixObjectDao().saveOrUpdateTropixObject(iRequest);
    assert requestService.isInternalRequest(iRequest.getId());
  }

  @Test
  public void setupInternalRequest() {
    final User tempUser = super.createTempUser();
    final InternalRequest iRequest = new InternalRequest();
    final String name = "name";
    final String description = "description";
    final String destination = "dest";
    final String requestServiceUrl = "requestServiceUrl";
    final String serviceId = UUID.randomUUID().toString();
    final String serviceInfo = "info";
    final String storageServiceUrl = UUID.randomUUID().toString();

    iRequest.setName(name);
    iRequest.setDescription(description);
    iRequest.setDestination(destination);
    iRequest.setRequestServiceUrl(requestServiceUrl);
    iRequest.setServiceId(serviceId);
    iRequest.setServiceInfo(serviceInfo);
    iRequest.setStorageServiceUrl(storageServiceUrl);

    final InternalRequest returnedRequest = this.requestService.setupInternalRequest(tempUser.getCagridId(), iRequest, tempUser.getHomeFolder().getId());
    final InternalRequest loadedRequest = super.getTropixObjectDao().loadTropixObject(returnedRequest.getId(), InternalRequest.class);

    assert loadedRequest.getName().equals(name);
    assert loadedRequest.getDescription().equals(description);
    assert loadedRequest.getDestination().equals(destination);
    assert loadedRequest.getServiceId().equals(serviceId);
    assert loadedRequest.getServiceInfo().equals(serviceInfo);
    assert loadedRequest.getStorageServiceUrl().equals(storageServiceUrl);
    assert loadedRequest.getExternalId().equals(returnedRequest.getId());
    assert loadedRequest.getCommitted();

    assert returnedRequest.getName().equals(name);
    assert returnedRequest.getDescription().equals(description);
    assert returnedRequest.getDestination().equals(destination);
    assert returnedRequest.getServiceId().equals(serviceId);
    assert returnedRequest.getServiceInfo().equals(serviceInfo);
    assert returnedRequest.getStorageServiceUrl().equals(storageServiceUrl);

  }

}
