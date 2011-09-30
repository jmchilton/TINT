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

package edu.umn.msi.tropix.client.catalog.impl;

import info.minnesotapartnership.tropix.catalog.TropixCatalogService;

import java.rmi.RemoteException;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

import edu.mayo.bmi.bic.bobcat.api.Attribute;
import edu.mayo.bmi.bic.bobcat.api.AttributeType;
import edu.mayo.bmi.bic.bobcat.api.AutoCompleteHit;
import edu.mayo.bmi.bic.bobcat.api.BasicEntry;
import edu.mayo.bmi.bic.bobcat.api.CatalogAdminAPI;
import edu.mayo.bmi.bic.bobcat.api.CatalogEntryAPI;
import edu.mayo.bmi.bic.bobcat.api.CatalogOntAPI;
import edu.mayo.bmi.bic.bobcat.api.CatalogSearchAPI;
import edu.mayo.bmi.bic.bobcat.api.Category;
import edu.mayo.bmi.bic.bobcat.api.CategoryFieldAssociation;
import edu.mayo.bmi.bic.bobcat.api.Field;
import edu.mayo.bmi.bic.bobcat.api.FieldType;
import edu.mayo.bmi.bic.bobcat.api.FieldValue;
import edu.mayo.bmi.bic.bobcat.api.FullEntry;
import edu.mayo.bmi.bic.bobcat.api.OntologyConcept;
import edu.mayo.bmi.bic.bobcat.api.Provider;
import edu.mayo.bmi.bic.bobcat.api.Query;
import edu.mayo.bmi.bic.bobcat.api.Reference;
import edu.mayo.bmi.bic.bobcat.api.SearchHit;
import edu.mayo.bmi.bic.bobcat.api.StatusType;
import edu.umn.msi.tropix.grid.GridServiceFactory;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.labs.catalog.CatalogInstance;

public class GridCatalogInstanceFunctionImpl implements Function<String, CatalogInstance> {
  private GridServiceFactory<TropixCatalogService> serviceFactory;
  private Supplier<Credential> proxySupplier;

  public void setProxySupplier(final Supplier<Credential> proxySupplier) {
    this.proxySupplier = proxySupplier;
  }

  public void setServiceFactory(final GridServiceFactory<TropixCatalogService> serviceFactory) {
    this.serviceFactory = serviceFactory;
  }

  public CatalogInstance apply(final String host) {
    return new GridCatalogInstanceImpl(host, serviceFactory.getService(host, proxySupplier.get()));
  }

  private static final class GridCatalogInstanceImpl implements CatalogInstance, CatalogOntAPI, CatalogEntryAPI, CatalogSearchAPI, CatalogAdminAPI {
    private final String host;
    private final TropixCatalogService client;

    private GridCatalogInstanceImpl(final String host, final TropixCatalogService client) {
      this.host = host;
      this.client = client;
    }

    public CatalogEntryAPI getCatalogEntryAPI() {
      return this;
    }

    public CatalogOntAPI getCatalogOntAPI() {
      return this;
    }

    public CatalogSearchAPI getCatalogSearchAPI() {
      return this;
    }

    public String getId() {
      return host;
    }

    public String addCategory(final String arg0, final String arg1, final CategoryFieldAssociation[] arg2) throws RemoteException {
      throw new UnsupportedOperationException();
    }

    public String addField(final String arg0, final OntologyConcept[] arg1, final FieldType arg2) throws RemoteException {
      throw new UnsupportedOperationException();
    }

    public String addFieldValue(final String arg0, final OntologyConcept[] arg1) throws RemoteException {
      throw new UnsupportedOperationException();
    }

    public String addProvider(final String arg0, final String arg1, final String arg2, final String arg3, final String arg4, final String arg5) throws RemoteException {
      throw new UnsupportedOperationException();
    }

    public int associateFieldToValue(final String arg0, final String arg1) throws RemoteException {
      throw new UnsupportedOperationException();
    }

    public int disassociateFieldFromValue(final String arg0, final String arg1) throws RemoteException {
      throw new UnsupportedOperationException();
    }

    public Field[] getAssociatedFields(final String arg0) throws RemoteException {
      return client.getAssociatedFields(arg0);
    }

    public FieldValue[] getAssociatedValues(final String arg0) throws RemoteException {
      return client.getAssociatedValues(arg0);
    }

    public Category[] getCategories(final String[] arg0) throws RemoteException {
      return client.getCategories(arg0);
    }

    public String getFieldValueByStringVal(final String arg0) throws RemoteException {
      return client.getFieldValueByStringVal(arg0);
    }

    public FieldValue[] getFieldValues(final String[] arg0) throws RemoteException {
      return client.getFieldValues(arg0);
    }

    public Field[] getFields(final String[] arg0) throws RemoteException {
      return client.getFields(arg0);
    }

    public Provider[] getProviders(final String[] arg0) throws RemoteException {
      return client.getProviders(arg0);
    }

    public int removeCategory(final String arg0) throws RemoteException {
      throw new UnsupportedOperationException();
    }

    public int removeField(final String arg0) throws RemoteException {
      throw new UnsupportedOperationException();
    }

    public int removeFieldValue(final String arg0) throws RemoteException {
      throw new UnsupportedOperationException();
    }

    public int removeProvider(final String arg0) throws RemoteException {
      throw new UnsupportedOperationException();
    }

    public int updateCategory(final String arg0, final String arg1, final String arg2, final CategoryFieldAssociation[] arg3) throws RemoteException {
      throw new UnsupportedOperationException();
    }

    public int updateFieldValue(final String arg0, final String arg1, final OntologyConcept[] arg2) throws RemoteException {
      throw new UnsupportedOperationException();
    }

    public int updateProvider(final String arg0, final String arg1, final String arg2, final String arg3, final String arg4, final String arg5, final String arg6) throws RemoteException {
      throw new UnsupportedOperationException();
    }

    public String addEntry(final String arg0, final String arg1, final String arg2, final String arg3, final StatusType arg4, final String arg5) throws RemoteException {
      throw new UnsupportedOperationException();
    }

    public String addFreeTextAttribute(final String arg0, final String arg1, final String arg2) throws RemoteException {
      throw new UnsupportedOperationException();
    }

    public String addReference(final String arg0, final String arg1) throws RemoteException {
      throw new UnsupportedOperationException();
    }

    public String addSimpleAttribute(final String arg0, final String arg1) throws RemoteException {
      throw new UnsupportedOperationException();
    }

    public String addValueAttribute(final String arg0, final String arg1, final String[] arg2) throws RemoteException {
      throw new UnsupportedOperationException();
    }

    public AttributeType getAttributeType(final String arg0) throws RemoteException {
      return client.getAttributeType(arg0);
    }

    public Attribute[] getAttributes(final String arg0) throws RemoteException {
      return client.getAttributes(arg0);
    }

    public BasicEntry[] getBasicDetails(final String[] arg0, final int arg1) throws RemoteException {
      return client.getBasicDetails(arg0, arg1);
    }

    public FullEntry[] getFullDetails(final String[] arg0) throws RemoteException {
      return client.getFullDetails(arg0);
    }

    public Reference[] getReferences(final String arg0) throws RemoteException {
      return client.getReferences(arg0);
    }

    public int removeAttribute(final String arg0) throws RemoteException {
      throw new UnsupportedOperationException();
    }

    public int removeReference(final String arg0) throws RemoteException {
      throw new UnsupportedOperationException();
    }

    public int updateEntry(final String arg0, final String arg1, final String arg2, final StatusType arg3, final String arg4) throws RemoteException {
      throw new UnsupportedOperationException();
    }

    public int updateFreeTextAttribute(final String arg0, final String arg1) throws RemoteException {
      throw new UnsupportedOperationException();
    }

    public int updateValueAttribute(final String arg0, final String[] arg1) throws RemoteException {
      throw new UnsupportedOperationException();
    }

    public SearchHit[] customEntrySearch(final Query arg0) throws RemoteException {
      return client.customEntrySearch(arg0);
    }

    public SearchHit[] entrySearch(final String arg0) throws RemoteException {
      return client.entrySearch(arg0);
    }

    public AutoCompleteHit[] fieldValueAutoCompletion(final String arg0) throws RemoteException {
      throw new UnsupportedOperationException();
    }

    public CatalogAdminAPI getCatalogAdminAPI() {
      return this;
    }

    public int rebuildTextIndex(final int arg0) throws RemoteException {
      throw new UnsupportedOperationException();
    }

    public int removeEntries(final String[] arg0) throws RemoteException {
      throw new UnsupportedOperationException();
    }
  }

}
