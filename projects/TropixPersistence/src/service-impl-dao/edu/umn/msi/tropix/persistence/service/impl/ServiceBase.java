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

package edu.umn.msi.tropix.persistence.service.impl;

import java.util.HashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.common.message.MessageSource;
import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.models.Folder;
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
import edu.umn.msi.tropix.persistence.service.impl.utils.Predicates;
import edu.umn.msi.tropix.persistence.service.security.SecurityProvider;

public abstract class ServiceBase {

  private TropixObjectDao tropixObjectDao;

  private UserDao userDao;

  private ProviderDao providerDao;

  private DaoFactory daoFactory;

  private SecurityProvider securityProvider;

  private MessageSource messageSource;

  private FileTypeResolver fileTypeResolver;

  @Inject
  public void setFileTypeResolver(final FileTypeResolver fileTypeResolver) {
    this.fileTypeResolver = fileTypeResolver;
  }

  protected FileType getFileType(final StockFileExtensionI stockFileExtension) {
    return fileTypeResolver.resolveType(stockFileExtension);
  }

  @Inject
  public void setProviderDao(final ProviderDao providerDao) {
    this.providerDao = providerDao;
  }

  @Inject
  @Named("serviceLayerMessageSource")
  public void setMessageSource(final MessageSource messageSource) {
    this.messageSource = messageSource;
  }

  @Inject
  public void setTropixObjectDao(final TropixObjectDao tropixObjectDao) {
    this.tropixObjectDao = tropixObjectDao;
  }

  @Inject
  public void setUserDao(final UserDao userDao) {
    this.userDao = userDao;
  }

  @Inject
  public void setSecurityProvider(final SecurityProvider securityProvider) {
    this.securityProvider = securityProvider;
  }

  @Inject
  public void setDaoFactory(final DaoFactory daoFactory) {
    this.daoFactory = daoFactory;
  }

  protected void updateObject(final TropixObject object) {
    tropixObjectDao.saveOrUpdateTropixObject(object);
  }

  private class UpdateParentClosure implements Closure<TropixObject> {
    private final User owner;
    private final Provider provider;

    public UpdateParentClosure(final User owner, final Provider provider) {
      this.owner = owner;
      this.provider = provider;
    }

    public void apply(final TropixObject object) {
      if(owner != null) {
        tropixObjectDao.setOwner(object.getId(), owner);
      }
      if(provider != null) {
        provider.getObjects().add(object);
      }
    }

  }

  protected void updateObjectWithParent(final TropixObject object, final String userId, final String parentId) {
    tropixObjectDao.addPermissionParent(object.getId(), parentId);
    final User destinationOwner = tropixObjectDao.getOwner(parentId);
    final Provider provider = providerDao.getObjectsProvider(parentId);
    TreeUtils.applyPermissionChange(object, new UpdateParentClosure(destinationOwner, provider));
    if(provider != null) {
      providerDao.saveObject(provider);
    }
  }

  protected void saveNewObject(final TropixObject object) {
    saveNewObject(object, null);
  }

  protected void saveNewObject(final TropixObject object, @Nullable final String userId) {
    initAndSaveObject(object);
    if(userId != null) {
      tropixObjectDao.setOwner(object.getId(), userDao.loadUser(userId));
    }
  }

  /*
   * protected void updateObject(final TropixObject object, final String userId, @Nonnull final String parentId) {
   * // initAndSaveObject(object);
   * final String objectId = object.getId();
   * tropixObjectDao.addPermissionParent(object.getId(), parentId);
   * final User destinationOwner = tropixObjectDao.getOwner(parentId);
   * if(destinationOwner != null) {
   * tropixObjectDao.setOwner(objectId, destinationOwner);
   * }
   * final Provider provider = providerDao.getObjectsProvider(parentId);
   * if(provider != null) {
   * provider.getObjects().add(object);
   * providerDao.saveObject(provider);
   * }
   * }
   */

  protected void saveNewObjectWithParent(final TropixObject object, final String userId, @Nonnull final String parentId) {
    initAndSaveObject(object);
    setupParent(parentId, object);
  }

  protected void setupParent(final String parentId, final TropixObject object) {
    tropixObjectDao.addPermissionParent(object.getId(), parentId);
    copyParentPermissions(parentId, object);
  }

  protected void copyParentPermissions(final String parentId, final TropixObject object) {
    final String objectId = object.getId();
    final User destinationOwner = tropixObjectDao.getOwner(parentId);
    if(destinationOwner != null) {
      tropixObjectDao.setOwner(objectId, destinationOwner);
    }
    final Provider provider = providerDao.getObjectsProvider(parentId);
    saveToProvider(provider, object);
  }

  protected void saveToProvider(final Provider provider, final TropixObject object) {
    if(provider != null) {
      provider.getObjects().add(object);
      providerDao.saveObject(provider);
    }
  }

  /**
   * If destinationId is null object is just saved with userId as owner.
   * 
   */
  protected void saveNewObjectToDestination(final TropixObject object, @Nullable final String userId, @Nullable final String destinationId) {
    if(destinationId == null) {
      saveNewObject(object, userId);
      return;
    }
    initAndSaveObject(object);
    final TropixObject destination = this.tropixObjectDao.loadTropixObject(destinationId);
    String folderId = null;
    if(destination instanceof Folder) {
      folderId = destination.getId();
    } else if(destination instanceof InternalRequest) {
      folderId = ((InternalRequest) destination).getDestinationFolder().getId();
    }
    if(folderId != null) {
      final String objectId = object.getId();
      tropixObjectDao.addToFolder(folderId, objectId);
      setupParent(folderId, object);
    }
    if(destination instanceof Request) {
      final Request request = (Request) destination;
      request.getContents().add(object);
      tropixObjectDao.saveOrUpdateTropixObject(destination);
      final Provider provider = request.getProvider();
      saveToProvider(provider, object);
    }
  }

  private void initAndSaveObject(final TropixObject object) {
    Utils.initObject(object);
    tropixObjectDao.saveOrUpdateTropixObject(object);
  }

  protected <T extends TropixObject> T[] filter(final Iterable<T> objects, final Class<T> clazz, final String userId) {
    final Iterable<T> iter = Iterables.filter(objects, Predicates.getValidAndCanReadPredicate(securityProvider, userId));
    return Iterables.toArray(iter, clazz);
  }

  /*
   * protected <T extends TropixObject> String[] filterToIds(final Iterable<T> objects, final Class<T> clazz, final String userId) {
   * final Iterable<T> iter = Iterables.filter(objects, Predicates.getValidAndCanReadPredicate(securityProvider, userId));
   * return Iterables.toArray(Iterables.transform(iter, ModelFunctions.getIdFunction()), String.class);
   * }
   */

  protected TropixObject[] filter(final Iterable<TropixObject> objects, final String userId) {
    return filter(objects, TropixObject.class, userId);
  }

  protected <T extends TropixObject> T filterObject(final T object, final String userId) {
    T filteredObject = object;
    if(!Predicates.getValidAndCanReadPredicate(securityProvider, userId).apply(object)) {
      filteredObject = null;
    }
    return filteredObject;
  }

  protected TropixObjectDao getTropixObjectDao() {
    return tropixObjectDao;
  }

  protected UserDao getUserDao() {
    return userDao;
  }

  protected ProviderDao getProviderDao() {
    return providerDao;
  }

  protected DaoFactory getDaoFactory() {
    return daoFactory;
  }

  protected SecurityProvider getSecurityProvider() {
    return securityProvider;
  }

  protected MessageSource getMessageSource() {
    return messageSource;
  }

  protected VirtualFolder newVirtualFolder(final VirtualFolder inputFolder) {
    final VirtualFolder folder = new VirtualFolder();
    folder.setName(inputFolder.getName());
    folder.setDescription(inputFolder.getDescription());
    folder.setContents(new HashSet<TropixObject>());
    folder.setCommitted(true);
    folder.setId(null); // make sure a new id is assigned
    return folder;
  }

  protected VirtualFolder createNewChildVirtualFolder(final String parentFolderId, final VirtualFolder inputVirtualFolder) {
    final VirtualFolder folder = newVirtualFolder(inputVirtualFolder);
    folder.setRoot(false);
    saveNewObject(folder, null); // No owner...
    getTropixObjectDao().addToVirtualFolder(parentFolderId, folder.getId());
    getTropixObjectDao().copyVirtualPermissions(parentFolderId, folder.getId());
    return folder;
  }

}
