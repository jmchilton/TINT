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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;
import edu.umn.msi.tropix.persistence.service.permission.PermissionReport;
import edu.umn.msi.tropix.persistence.service.permission.PermissionType;
import edu.umn.msi.tropix.webgui.server.aop.ServiceMethod;
import edu.umn.msi.tropix.webgui.server.models.BeanSanitizer;
import edu.umn.msi.tropix.webgui.server.models.BeanSanitizerUtils;
import edu.umn.msi.tropix.webgui.server.security.UserSession;
import edu.umn.msi.tropix.webgui.services.object.ObjectService;
import edu.umn.msi.tropix.webgui.services.object.Permission;
import edu.umn.msi.tropix.webgui.services.object.TropixObjectContext;

@ManagedBean
public class ObjectServiceImpl implements ObjectService {
  private static Log log = LogFactory.getLog(ObjectServiceImpl.class);
  private Function<PermissionReport, Permission> permissionFunction;
  private TropixObjectService tropixObjectService;
  private UserSession userSession;
  private BeanSanitizer beanSanitizer;

  ObjectServiceImpl() {
  }

  @ServiceMethod(readOnly = true)
  public TropixObjectContext getObjectContext(final String objectId) {
    final boolean modifiable = tropixObjectService.canModify(userSession.getGridId(), objectId);
    final boolean canModifySharing = tropixObjectService.canModifySharing(userSession.getGridId(), objectId);
    return new TropixObjectContext(modifiable, canModifySharing);
  }

  private List<TropixObject> sanitizeObjects(final TropixObject[] objects) {
    final ArrayList<TropixObject> sanitizedObjects = new ArrayList<TropixObject>(objects.length);
    for(final TropixObject object : objects) {
      sanitizedObjects.add(this.beanSanitizer.sanitize(object));
    }
    return sanitizedObjects;
  }

  @ServiceMethod(readOnly = true)
  public List<TropixObject> loadRecent(final int num, final boolean includeFolders, final TropixObjectType[] filterTypes) {
    final TropixObject[] objects = this.tropixObjectService.loadRecent(this.userSession.getGridId(), num, includeFolders, filterTypes, true);
    return this.sanitizeObjects(objects);
  }

  @ServiceMethod(readOnly = true)
  public TropixObject load(final String objectId) {
    TropixObject object = null;
    try {
      object = this.tropixObjectService.load(this.userSession.getGridId(), objectId);
    } catch(final RuntimeException e) {
      log.trace("Refresh failed, this could be perfectly normal", e);
    }
    return this.beanSanitizer.sanitize(object);
  }

  @ServiceMethod(readOnly = true)
  public boolean canModifySharing(final String objectId) {
    return this.tropixObjectService.canModifySharing(this.userSession.getGridId(), objectId);
  }

  @ServiceMethod(readOnly = true)
  public TropixObject getAssociation(final String objectId, final String associationName) {
    final TropixObject object = this.tropixObjectService.getAssociation(this.userSession.getGridId(), objectId, associationName);
    return this.beanSanitizer.sanitize(object);
  }

  public List<TropixObject> getAssociations(final String objectId, final String associationName) {
    final TropixObject[] objects = this.tropixObjectService.getAssociations(this.userSession.getGridId(), objectId, associationName);
    return this.sanitizeObjects(objects);
  }

  @ServiceMethod
  public void move(final String objectId, final String folderId) {
    this.tropixObjectService.move(this.userSession.getGridId(), objectId, folderId);
  }

  @ServiceMethod
  public boolean move(final Collection<String> objectIds, final String folderId) {
    boolean allMoved = true;
    for(final String objectId : objectIds) {
      try {
        this.tropixObjectService.move(this.userSession.getGridId(), objectId, folderId);
      } catch(final Exception e) {
        allMoved = false;
      }
    }
    return allMoved;
  }

  @ServiceMethod
  public void rename(final String objectId, final String newName) {
    final TropixObject tropixObject = this.tropixObjectService.load(this.userSession.getGridId(), objectId);
    tropixObject.setName(newName);
    this.tropixObjectService.update(this.userSession.getGridId(), tropixObject);
  }

  @ServiceMethod
  public void changeDescription(final String objectId, final String newDescription) {
    final TropixObject tropixObject = this.tropixObjectService.load(userSession.getGridId(), objectId);
    tropixObject.setDescription(newDescription);
    this.tropixObjectService.update(this.userSession.getGridId(), tropixObject);
  }

  @ServiceMethod
  public List<Permission> getPermissions(final String objectId) {
    final PermissionReport[] reports = this.tropixObjectService.getPermissionReports(userSession.getGridId(), objectId);
    return Lists.newLinkedList(Iterables.transform(Arrays.asList(reports), permissionFunction));
  }

  @ServiceMethod
  public void addSharedFolder(final String folderId) {
    this.tropixObjectService.addSharedFolder(this.userSession.getGridId(), folderId);
  }

  @ServiceMethod
  public void addGroupSharedFolder(final String groupId, final String folderId) {
    this.tropixObjectService.addGroupSharedFolder(this.userSession.getGridId(), groupId, folderId);
  }
  
  @ServiceMethod
  public void removeSharedFolder(final String rootSharedFolderId, final boolean removeOwnedItems) {
    tropixObjectService.removeSharedFolder(userSession.getGridId(), rootSharedFolderId, removeOwnedItems);
  }

  private PermissionType getPermissionType(final String roleName) {
    if(roleName.equals("read")) {
      return PermissionType.Read;
    } else {
      return PermissionType.Write;
    }
  }

  @ServiceMethod
  public void addPermissionForUser(final String objectId, final String userId, final String roleName) {
    this.tropixObjectService.addPermissionForUser(this.userSession.getGridId(), objectId, userId, this.getPermissionType(roleName));
  }

  @ServiceMethod
  public void removePermissionForUser(final String objectId, final String userId, final String roleName) {
    this.tropixObjectService.removePermissionForUser(this.userSession.getGridId(), objectId, userId, this.getPermissionType(roleName));
  }

  @ServiceMethod
  public void removePermissionForGroup(final String objectId, final String groupId, final String roleName) {
    this.tropixObjectService.removePermissionForGroup(this.userSession.getGridId(), objectId, groupId, this.getPermissionType(roleName));
  }

  @ServiceMethod
  public void addPermissionForGroup(final String objectId, final String groupId, final String roleName) {
    this.tropixObjectService.addPermissionForGroup(this.userSession.getGridId(), objectId, groupId, this.getPermissionType(roleName));
  }

  @ServiceMethod
  public long ownedItemsInVirtualHierarchiesCount(final Iterable<String> rootSharedFolderIds) {
    final String userId = userSession.getGridId();
    long count = 0L;
    for(final String objectId : rootSharedFolderIds) {
      count += tropixObjectService.ownedObjectsVirtualHierarchyCount(userId, objectId);
    }
    return count;
  }

  

  
  @ServiceMethod
  public void addToSharedFolder(final Collection<String> objectIds, final String folderId, final boolean recursive) {
    RuntimeException lastException = null;
    try {
      for(final String objectId : objectIds) {
        this.tropixObjectService.addToSharedFolder(userSession.getGridId(), objectId, folderId, recursive);
      }
    } catch(final RuntimeException e) {
      ExceptionUtils.logQuietly(ObjectServiceImpl.log, e);
      lastException = e;
    }
    if(lastException != null) {
      throw lastException;
    }
  }

  @ServiceMethod
  public void removeFromSharedFolder(final String objectId, final String folderId) {
    this.tropixObjectService.removeFromSharedFolder(this.userSession.getGridId(), folderId, objectId);
  }

  @ServiceMethod
  public void addVirtualPermissionForGroup(final String objectId, final String groupId, final String roleName) {
    this.tropixObjectService.addVirtualPermissionForGroup(this.userSession.getGridId(), objectId, groupId, this.getPermissionType(roleName));
  }

  @ServiceMethod
  public void addVirtualPermissionForUser(final String objectId, final String userId, final String roleName) {
    this.tropixObjectService.addVirtualPermissionForUser(this.userSession.getGridId(), objectId, userId, this.getPermissionType(roleName));
  }

  @ServiceMethod
  public void removeVirtualPermissionForGroup(final String objectId, final String groupId, final String roleName) {
    this.tropixObjectService.removeVirtualPermissionForGroup(this.userSession.getGridId(), objectId, groupId, this.getPermissionType(roleName));
  }

  @ServiceMethod
  public void removeVirtualPermissionForUser(final String objectId, final String userId, final String roleName) {
    this.tropixObjectService.removeVirtualPermissionForUser(this.userSession.getGridId(), objectId, userId, this.getPermissionType(roleName));
  }

  @ServiceMethod
  public void delete(final String objectId, final String rootId) {
    this.tropixObjectService.delete(this.userSession.getGridId(), objectId);
  }

  @ServiceMethod(readOnly = true)
  public VirtualFolder getRoot(final String virtualFolderId) {
    return this.beanSanitizer.sanitize(this.tropixObjectService.getRoot(this.userSession.getGridId(), virtualFolderId));
  }

  @ServiceMethod
  public void moveVirtually(final String objectId, final String parentId, final String newParentId) {
    this.tropixObjectService.moveVirtually(this.userSession.getGridId(), parentId, objectId, newParentId);
  }

  @ServiceMethod(readOnly = true)
  public List<TropixObject> getChildren(final String objectId, final TropixObjectType[] filterTypes) {
    edu.umn.msi.tropix.models.TropixObject[] objects;
    if(filterTypes != null) {
      objects = tropixObjectService.getChildren(this.userSession.getGridId(), objectId, filterTypes);
    } else {
      objects = tropixObjectService.getChildren(this.userSession.getGridId(), objectId);
    }
    return BeanSanitizerUtils.sanitizeArray(beanSanitizer, objects);
  }

  @ServiceMethod
  public void hideSharedFolder(final String rootSharedFolderId) {
    tropixObjectService.hideSharedFolder(userSession.getGridId(), rootSharedFolderId);
  }

  @ServiceMethod
  public void hideGroupSharedFolder(final String groupId, final String rootSharedFolderId) {
    tropixObjectService.hideGroupSharedFolder(userSession.getGridId(), groupId, rootSharedFolderId);
  }

  @Inject
  public void setPermissionFunction(@Named("permissionFunction") final Function<PermissionReport, Permission> permissionFunction) {
    this.permissionFunction = permissionFunction;
  }

  @Inject
  public void setBeanSanitizer(final BeanSanitizer beanSanitizer) {
    this.beanSanitizer = beanSanitizer;
  }

  @Inject
  public void setTropixObjectService(final TropixObjectService tropixObjectService) {
    this.tropixObjectService = tropixObjectService;
  }

  @Inject
  public void setUserSession(final UserSession userSession) {
    this.userSession = userSession;
  }


}
