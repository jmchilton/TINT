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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.inject.Inject;

import org.hibernate.SessionFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.google.common.collect.Sets;

import edu.umn.msi.tropix.common.message.MessageSource;
import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.InternalRequest;
import edu.umn.msi.tropix.models.Provider;
import edu.umn.msi.tropix.models.Request;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.models.utils.StockFileExtensionI;
import edu.umn.msi.tropix.persistence.dao.DaoFactory;
import edu.umn.msi.tropix.persistence.dao.ProviderDao;
import edu.umn.msi.tropix.persistence.dao.TropixObjectDao;
import edu.umn.msi.tropix.persistence.dao.UserDao;
import edu.umn.msi.tropix.persistence.service.impl.FileTypeResolver;
import edu.umn.msi.tropix.persistence.service.security.SecurityProvider;

@ContextConfiguration(locations = {"classpath:edu/umn/msi/tropix/persistence/test/applicationContext.xml"})
@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = true)
public class ServiceTest extends AbstractTransactionalTestNGSpringContextTests {
  @Inject
  private TropixObjectDao tropixObjectDao;

  @Inject
  private UserDao userDao;

  @Inject
  private DaoFactory daoFactory;

  @Inject
  private SessionFactory sessionFactory;

  @Inject
  private MessageSource messageSource;

  @Inject
  private FileTypeResolver fileTypeResolver;

  @Inject
  private ProviderDao providerDao;

  @Inject
  private SecurityProvider securityProvider;

  protected SecurityProvider getSecurityProvider() {
    return securityProvider;
  }

  protected FileType getFileType(final StockFileExtensionI stockFileExtension) {
    return fileTypeResolver.resolveType(stockFileExtension);
  }

  @SuppressWarnings("unchecked")
  protected <T> List<T> find(final String query) {
    return sessionFactory.getCurrentSession().createQuery(query).list();
  }

  @SuppressWarnings("unchecked")
  protected <T> T findUniqueResult(final String query) {
    return (T) sessionFactory.getCurrentSession().createQuery(query).uniqueResult();
  }

  private <T extends TropixObject> T saveNew(final boolean committed, final T object, @Nullable final User user) {
    object.setCommitted(committed);
    saveNewTropixObject(object, user);
    return object;
  }

  protected <T extends TropixObject> T saveNewCommitted(final T object, @Nullable final User user) {
    return saveNew(true, object, user);
  }

  protected <T extends TropixObject> T saveNewUncommitted(final T object, @Nullable final User user) {
    return saveNew(false, object, user);
  }

  protected Folder newFolder() {
    final Folder folder = new Folder();
    folder.setContents(new HashSet<TropixObject>());
    return folder;
  }

  protected Folder newFolderWithName(final String name) {
    final Folder folder = newFolder();
    folder.setName(name);
    return folder;
  }

  protected User createTempUser(final Group group) {
    final User user = new User();
    if(group == null) {
      user.setGroups(new HashSet<Group>());
    } else {
      user.setGroups(Sets.newHashSet(group));
    }
    user.setSharedFolders(new HashSet<VirtualFolder>());
    user.setCagridId(newId());
    final Folder folder = newFolder();
    folder.setCommitted(true);
    saveNewTropixObject(folder);
    user.setHomeFolder(folder);
    userDao.saveOrUpdateUser(user);
    tropixObjectDao.setOwner(folder.getId(), user);
    return user;
  }

  protected User createTempUser() {
    return createTempUser(null);
  }

  protected Group createTempGroup() {
    final Group group = new Group();
    group.setName(newId());
    group.setUsers(new HashSet<User>());
    daoFactory.getDao(Group.class).saveObject(group);
    return group;
  }

  protected Group createTempGroup(final boolean primary, final User... users) {
    final Group group = createTempGroup();
    for(final User user : users) {
      group.getUsers().add(user);
      user.getGroups().add(group);
    }
    group.setSharedFolders(Sets.<VirtualFolder>newHashSet());
    daoFactory.getDao(Group.class).saveObject(group);
    for(final User user : users) {
      if(primary) {
        user.setPrimaryGroup(group);
      }
      userDao.saveOrUpdateUser(user);
    }
    return group;
  }

  protected Group createTempGroup(final User... users) {
    return createTempGroup(false, users);
  }

  protected TropixObject saveNewTropixObject(final TropixObject tropixObject) {
    tropixObject.setPermissionChildren(new HashSet<TropixObject>());
    tropixObject.setPermissionParents(new HashSet<TropixObject>());
    tropixObject.setParentVirtualFolders(new HashSet<VirtualFolder>());
    tropixObject.setCreationTime("" + System.currentTimeMillis());
    tropixObjectDao.saveOrUpdateTropixObject(tropixObject);
    return tropixObject;
  }

  protected TropixObject saveNewTropixObject(final TropixObject tropixObject, @Nullable final User user) {
    saveNewTropixObject(tropixObject);
    if(user != null) {
      tropixObjectDao.setOwner(tropixObject.getId(), user);
    }
    return tropixObject;
  }

  protected String newId() {
    return UUID.randomUUID().toString();
  }

  protected Provider createTempProvider() {
    final Provider provider = new Provider();
    provider.setGroups(new HashSet<Group>());
    provider.setUsers(new HashSet<User>());
    provider.setObjects(new HashSet<TropixObject>());
    provider.setRequests(new HashSet<Request>());
    provider.setRole("write");
    daoFactory.getDao(Provider.class).saveObject(provider);
    return provider;
  }

  protected Folder createTempGroupFolder(final User user, final User owner, final String name) {
    final Group group = createTempGroup(owner, user);

    final Folder folder = newFolder();
    saveWithName(folder, name, owner);

    final Provider provider = createTempProvider();
    provider.setObjects(Sets.<TropixObject>newHashSet(folder));
    provider.setGroups(Sets.<Group>newHashSet(group));
    provider.setUsers(Sets.<User>newHashSet(owner));
    saveProvider(provider);

    return folder;

  }

  protected Folder createTempGroupFolder(final User user, final String name) {
    final User owner = createTempUser();
    return createTempGroupFolder(user, owner, name);
  }

  protected void initTempRequest(final Request request) {
    final Provider provider = createTempProvider();
    request.setProvider(provider);
    request.setContents(new HashSet<TropixObject>());
    tropixObjectDao.saveOrUpdateTropixObject(request);
    provider.getObjects().add(request);
    saveProvider(provider);
  }

  private void saveProvider(final Provider provider) {
    daoFactory.getDao(Provider.class).saveObject(provider);
  }

  protected VirtualFolder createTempRootVirtualFolder() {
    final VirtualFolder root = new VirtualFolder();
    root.setRoot(true);
    root.setContents(new HashSet<TropixObject>());
    saveNewTropixObject(root);
    tropixObjectDao.createVirtualPermission(root.getId(), "read");
    tropixObjectDao.createVirtualPermission(root.getId(), "write");
    return root;
  }

  protected VirtualFolder createTempChildVirtualFolder(final VirtualFolder parentFolder) {
    final VirtualFolder folder = new VirtualFolder();
    folder.setRoot(false);
    folder.setContents(new HashSet<TropixObject>());
    saveNewTropixObject(folder);
    tropixObjectDao.copyVirtualPermissions(parentFolder.getId(), folder.getId());
    tropixObjectDao.addToVirtualFolder(parentFolder.getId(), folder.getId());
    return folder;
  }

  interface Destination {
    Collection<TropixObject> getContents();

    void validate(TropixObject... objects);

    String getId();

    void verifyContains(TropixObject tropixObject);

  }

  class FolderWithProvider extends FolderDestination {
    private final User providerUser;
    private final Provider provider;

    FolderWithProvider(final User user) {
      super(createTempUser());
      this.providerUser = user;
      provider = createTempProvider();
      provider.getUsers().add(getOwner());
      provider.getUsers().add(user);
      provider.getObjects().add(getFolder());
      getDaoFactory().getDao(Provider.class).saveObject(provider);
      assert providerDao.getObjectsProvider(getFolder().getId()) != null;
    }

    public void validate(final TropixObject... objects) {
      super.validate(objects);
      for(final TropixObject object : objects) {
        final Provider objectsProvider = providerDao.getObjectsProvider(object.getId());
        assert objectsProvider != null;
        assert objectsProvider.getId().equals(provider.getId());
        assert securityProvider.canModify(object.getId(), providerUser.getCagridId());
      }
    }

    // public void verifyContains(final TropixObject tropixObject) {
    // super.verifyContains(tropixObject);
    // }

  }

  class FolderDestination implements Destination {
    private final User user;
    private final Folder folder;

    protected Folder getFolder() {
      return folder;
    }

    public User getOwner() {
      return user;
    }

    FolderDestination(final User user) {
      this.user = user;
      folder = new Folder();
      folder.setContents(new HashSet<TropixObject>());
      saveNewTropixObject(folder, user);
    }

    public Collection<TropixObject> getContents() {
      final Collection<TropixObject> contents = folder.getContents();
      for(final TropixObject object : contents) {
        assert object.getPermissionParents().contains(folder);
      }
      return contents;
    }

    public void validate(final TropixObject... objects) {
      for(final TropixObject object : objects) {
        assert getTropixObjectDao().getOwnerId(object.getId()).equals(user.getCagridId());
      }
    }

    public String getId() {
      return folder.getId();
    }

    public void verifyContains(final TropixObject tropixObject) {
      assert getContents().contains(tropixObject) : getContents().size();
    }
  }

  static class RequestDestination implements Destination {
    private final Request request = new Request();

    RequestDestination(final User user, final ServiceTest serviceTest) {
      serviceTest.initTempRequest(request);
      // initTempRequest(request);
      request.getProvider().getUsers().add(user);
      serviceTest.getDaoFactory().getDao(Provider.class).saveObject(request.getProvider());
    }

    public Collection<TropixObject> getContents() {
      return request.getContents();
    }

    public User getOwner() {
      return null;
    }

    public void validate(final TropixObject... objects) {
      for(final TropixObject object : objects) {
        assert request.getProvider().getObjects().contains(object);
      }
    }

    public String getId() {
      return request.getId();
    }

    public void verifyContains(final TropixObject tropixObject) {
      assert getContents().contains(tropixObject);
    }

  }

  class InternalRequestDestination implements Destination {
    private final InternalRequest iRequest = new InternalRequest();
    private final User requestor;
    private final Folder requestorFolder;

    InternalRequestDestination(final User user) {
      initTempRequest(iRequest);
      requestor = createTempUser();

      requestorFolder = new Folder();
      requestorFolder.setContents(new HashSet<TropixObject>());
      saveNewTropixObject(requestorFolder, requestor);

      iRequest.setDestinationFolder(requestorFolder);
      getTropixObjectDao().saveOrUpdateTropixObject(iRequest);

      iRequest.getProvider().getUsers().add(user);
      getDaoFactory().getDao(Provider.class).saveObject(iRequest.getProvider());

    }

    public User getOwner() {
      return requestor;
    }

    public Collection<TropixObject> getContents() {
      final Collection<TropixObject> contents = iRequest.getContents();
      for(final TropixObject object : contents) {
        assert requestorFolder.getContents().contains(object);
      }
      return contents;
    }

    public void validate(final TropixObject... objects) {
      // TODO Auto-generated method stub
      for(final TropixObject object : objects) {
        assert iRequest.getProvider().getObjects().contains(object);
        assert getTropixObjectDao().getOwnerId(object.getId()).equals(requestor.getCagridId());
      }
    }

    public String getId() {
      return iRequest.getId();
    }

    public void verifyContains(final TropixObject tropixObject) {
      assert getContents().contains(tropixObject);
    }
  }

  protected final class NullDestination implements Destination {
    private final User owner;

    private NullDestination(final User owner) {
      this.owner = owner;
    }

    public Collection<TropixObject> getContents() {
      return null;
    }

    public String getId() {
      return null;
    }

    public User getOwner() {
      return null;
    }

    public void validate(final TropixObject... objects) {
      for(TropixObject object : objects) {
        assert getTropixObjectDao().getOwnerId(object.getId()).equals(owner.getCagridId());
      }
    }

    public void verifyContains(final TropixObject tropixObject) {
      // No destination to verify
    }

  }

  protected Iterable<Destination> getTestDestinations(final User user) {
    return Arrays.<Destination>asList(new FolderDestination(user), new RequestDestination(user, this), new InternalRequestDestination(user));
  }

  protected Iterable<Destination> getTestDestinationsWithNull(final User user) {
    return Arrays.<Destination>asList(new FolderDestination(user), new RequestDestination(user, this), new InternalRequestDestination(user),
        new FolderWithProvider(user),
        new NullDestination(user));
  }

  protected TropixObjectDao getTropixObjectDao() {
    return tropixObjectDao;
  }

  protected UserDao getUserDao() {
    return userDao;
  }

  protected DaoFactory getDaoFactory() {
    return daoFactory;
  }

  protected SessionFactory getSessionFactory() {
    return sessionFactory;
  }

  protected MessageSource getMessageSource() {
    return messageSource;
  }

  protected <T extends TropixObject> T saveWithName(final T object, final String name, final User owner) {
    object.setName(name);
    return saveNewCommitted(object, owner);
  }

  protected <T extends TropixObject> T saveWithNameToParent(final T object, final String name, final Folder parent, final User owner) {
    object.setName(name);
    return saveToParent(object, parent, owner);
  }

  protected <T extends TropixObject> T saveToParent(final T object, final Folder parent, final User owner) {
    final T savedObject = saveToParentObject(object, parent, owner);
    getTropixObjectDao().addToFolder(parent.getId(), object.getId());
    return savedObject;
  }

  protected <T extends TropixObject> T saveToParentObject(final T object, final TropixObject parent, final User owner) {
    saveNewCommitted(object, owner);
    getTropixObjectDao().addPermissionParent(object.getId(), parent.getId());
    return object;
  }

}
