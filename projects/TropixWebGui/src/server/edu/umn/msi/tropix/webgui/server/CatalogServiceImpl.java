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

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.mayo.bmi.bic.bobcat.api.CategoryFieldAssociation;
import edu.mayo.bmi.bic.bobcat.api.FieldType;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.labs.catalog.CatalogInstance;
import edu.umn.msi.tropix.persistence.service.LabsService;
import edu.umn.msi.tropix.persistence.service.permission.PermissionReport;
import edu.umn.msi.tropix.webgui.client.catalog.beans.Provider;
import edu.umn.msi.tropix.webgui.server.aop.ServiceMethod;
import edu.umn.msi.tropix.webgui.server.security.UserSession;
import edu.umn.msi.tropix.webgui.services.object.Permission;
import edu.umn.msi.tropix.webgui.services.tropix.CatalogService;

@ManagedBean
public class CatalogServiceImpl implements CatalogService {
  private static final Log LOG = LogFactory.getLog(CatalogServiceImpl.class);
  private final Function<PermissionReport, Permission> permissionFunction;
  private final UserSession userSession;
  private final LabsService labsService;
  private final CatalogInstance localInstance;
  
  @Inject
  CatalogServiceImpl(@Named("permissionFunction") final Function<PermissionReport, Permission> permissionFunction, final UserSession userSession, final LabsService labsService, final CatalogInstance localInstance) {
    this.permissionFunction = permissionFunction;
    this.userSession = userSession;
    this.labsService = labsService;
    this.localInstance = localInstance;
  }

  @ServiceMethod
  public List<Provider> getMyProviders() {
    final String[] providerIds = this.labsService.getProviderIds(this.userSession.getGridId());
    if(providerIds.length == 0) {
      return Lists.newArrayList(); // Catalog API treats this as null => returns
      // all providers
    }
    final List<Provider> providers = Lists.newArrayListWithCapacity(providerIds.length);
    try {
      for(final edu.mayo.bmi.bic.bobcat.api.Provider provider : this.localInstance.getCatalogOntAPI().getProviders(providerIds)) {
        providers.add(CatalogUtils.getUIProvider(provider));
      }
    } catch(final RemoteException e) {
      ExceptionUtils.logQuietly(CatalogServiceImpl.LOG, e);
      throw new RuntimeException("Failed to obtain providers");
    }
    return providers;
  }

  @ServiceMethod
  public void createProvider(final Provider provider) {
    try {
      final String providerId = this.localInstance.getCatalogOntAPI().addProvider(provider.getName(), provider.getContact(), provider.getAddress(), provider.getPhone(), provider.getEmail(), provider.getWebsite());
      this.labsService.createProvider(this.userSession.getGridId(), providerId);
    } catch(final RemoteException e) {
      ExceptionUtils.logQuietly(CatalogServiceImpl.LOG, e);
      throw new RuntimeException("Failed to create provider");
    }
  }

  @ServiceMethod
  public void addProviderPermissionForGroup(final String providerCatalogId, final String groupId) {
    this.labsService.addGroupProviderPermission(this.userSession.getGridId(), providerCatalogId, groupId);
  }

  @ServiceMethod
  public void addProviderPermissionForUser(final String providerCatalogId, final String userId) {
    this.labsService.addUserProviderPermission(this.userSession.getGridId(), providerCatalogId, userId);
  }

  @ServiceMethod(readOnly = true)
  public boolean canModify(final String providerCatalogId) {
    return this.labsService.canModifyProvider(this.userSession.getGridId(), providerCatalogId);
  }

  @ServiceMethod(readOnly = true)
  public List<Permission> getPermissions(final String providerCatalogId) {
    final PermissionReport[] reports = this.labsService.getProviderPermissionReport(providerCatalogId);
    return Lists.newLinkedList(Iterables.transform(Arrays.asList(reports), permissionFunction));
  }

  @ServiceMethod
  public void removeProviderPermissionForGroup(final String providerCatalogId, final String groupId) {
    this.labsService.removeGroupProviderPermission(this.userSession.getGridId(), providerCatalogId, groupId);
  }

  @ServiceMethod
  public void removeProviderPermissionForUser(final String providerCatalogId, final String userId) {
    this.labsService.removeUserProviderPermission(this.userSession.getGridId(), providerCatalogId, userId);
  }

  @ServiceMethod(adminOnly = true)
  public void addFreeTextField(final String name) {
    try {
      LOG.info("Adding field " + name);
      this.localInstance.getCatalogOntAPI().addField(name, null, FieldType.UNSTRUCTURED);
    } catch(final RemoteException e) {
      throw new RuntimeException(e);
    }
  }

  @ServiceMethod(adminOnly = true)
  public void addCategory(final String name, final String description) {
    try {
      this.localInstance.getCatalogOntAPI().addCategory(name, description, new CategoryFieldAssociation[0]);
    } catch(final RemoteException e) {
      throw new RuntimeException(e);
    }
  }

  @ServiceMethod(adminOnly = true)
  public void addEnumField(final String name, final List<String> values) {
    try {
      LOG.info("Adding enumerated field " + name);
      final String fieldId = this.localInstance.getCatalogOntAPI().addField(name, null, FieldType.ENUMERATED);
      for(final String value : values) {
        final String valueId = this.localInstance.getCatalogOntAPI().addFieldValue(value, null);
        this.localInstance.getCatalogOntAPI().associateFieldToValue(fieldId, valueId);
      }
    } catch(final RemoteException e) {
      throw new RuntimeException(e);
    }
  }

}
