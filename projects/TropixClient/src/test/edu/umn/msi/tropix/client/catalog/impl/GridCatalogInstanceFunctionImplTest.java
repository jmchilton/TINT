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

import java.util.concurrent.Callable;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;

import edu.mayo.bmi.bic.bobcat.api.Attribute;
import edu.mayo.bmi.bic.bobcat.api.AttributeType;
import edu.mayo.bmi.bic.bobcat.api.BasicEntry;
import edu.mayo.bmi.bic.bobcat.api.Category;
import edu.mayo.bmi.bic.bobcat.api.Field;
import edu.mayo.bmi.bic.bobcat.api.FieldValue;
import edu.mayo.bmi.bic.bobcat.api.FullEntry;
import edu.mayo.bmi.bic.bobcat.api.Provider;
import edu.mayo.bmi.bic.bobcat.api.Query;
import edu.mayo.bmi.bic.bobcat.api.Reference;
import edu.mayo.bmi.bic.bobcat.api.SearchHit;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.grid.GridServiceFactory;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.labs.catalog.CatalogInstance;

public class GridCatalogInstanceFunctionImplTest {

  private static <T> void throwsUnsupportOperationException(final Callable<T> runnable) throws Exception {
    UnsupportedOperationException exception = null;
    try {
      runnable.call();
    } catch(UnsupportedOperationException e) {
      exception = e;
    }
    assert exception != null;
  }

  @Test(groups = "unit")
  public void catalogInstance() throws Exception {
    @SuppressWarnings("unchecked")
    final GridServiceFactory<TropixCatalogService> factory = EasyMock.createMock(GridServiceFactory.class);
    final TropixCatalogService service = EasyMock.createMock(TropixCatalogService.class);

    final Credential credential = Credentials.getMock();

    final GridCatalogInstanceFunctionImpl function = new GridCatalogInstanceFunctionImpl();
    function.setServiceFactory(factory);
    function.setProxySupplier(Suppliers.ofInstance(credential));

    EasyMock.expect(factory.getService("http://moo", credential)).andReturn(service);
    EasyMock.replay(factory, service);
    final CatalogInstance instance = function.apply("http://moo");
    EasyMockUtils.verifyAndResetAll(factory, service);

    assert instance.getId().equals("http://moo");

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogOntAPI().addCategory(null, null, null);
      }
    });

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogOntAPI().addField(null, null, null);
      }
    });

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogOntAPI().addFieldValue(null, null);
      }
    });

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogOntAPI().addProvider(null, null, null, null, null, null);
      }
    });

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogOntAPI().associateFieldToValue(null, null);
      }
    });

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogOntAPI().disassociateFieldFromValue(null, null);
      }
    });

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogOntAPI().removeCategory(null);
      }
    });

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogOntAPI().removeField(null);
      }
    });

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogOntAPI().removeFieldValue(null);
      }
    });

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogOntAPI().removeProvider(null);
      }
    });

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogAdminAPI().rebuildTextIndex(12);
      }
    });

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogOntAPI().updateCategory(null, null, null, null);
      }
    });

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogOntAPI().updateFieldValue(null, null, null);
      }
    });

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogOntAPI().updateProvider(null, null, null, null, null, null, null);
      }
    });

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogEntryAPI().addEntry(null, null, null, null, null, null);
      }
    });

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogEntryAPI().addFreeTextAttribute(null, null, null);
      }
    });

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogEntryAPI().addReference(null, null);
      }
    });

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogEntryAPI().addSimpleAttribute(null, null);
      }
    });

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogEntryAPI().addValueAttribute(null, null, null);
      }
    });

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogEntryAPI().removeAttribute(null);
      }
    });

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogEntryAPI().removeReference(null);
      }
    });

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogEntryAPI().updateEntry(null, null, null, null, null);
      }
    });

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogEntryAPI().updateFreeTextAttribute(null, null);
      }
    });

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogEntryAPI().updateValueAttribute(null, null);
      }
    });

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogAdminAPI().removeEntries(null);
      }
    });

    throwsUnsupportOperationException(new Callable<Object>() {
      public Object call() throws Exception {
        return instance.getCatalogSearchAPI().fieldValueAutoCompletion(null);
      }
    });

    final Field[] fields = new Field[] {null};
    EasyMock.expect(service.getAssociatedFields("id")).andReturn(fields);
    final FieldValue[] fieldValues = new FieldValue[] {null};
    EasyMock.expect(service.getAssociatedValues("id")).andReturn(fieldValues);
    final Category[] categories = new Category[] {null};
    EasyMock.expect(service.getCategories(EasyMock.aryEq(new String[] {"id"}))).andReturn(categories);
    EasyMock.expect(service.getFieldValueByStringVal("id")).andReturn("fieldVal");
    EasyMock.expect(service.getFieldValues(EasyMock.aryEq(new String[] {"id"}))).andReturn(fieldValues);
    EasyMock.expect(service.getFields(EasyMock.aryEq(new String[] {"id"}))).andReturn(fields);
    final Provider[] providers = new Provider[] {null};
    EasyMock.expect(service.getProviders(EasyMock.aryEq(new String[] {"id"}))).andReturn(providers);

    final AttributeType aType = AttributeType.FREETEXT;
    EasyMock.expect(service.getAttributeType("id")).andReturn(aType);
    final Attribute[] attributes = new Attribute[] {null};
    EasyMock.expect(service.getAttributes("id")).andReturn(attributes);
    final BasicEntry[] basicEntries = new BasicEntry[] {null};
    EasyMock.expect(service.getBasicDetails(EasyMock.aryEq(new String[] {"id"}), EasyMock.eq(3))).andReturn(basicEntries);
    final FullEntry[] fullEntries = new FullEntry[] {null};
    EasyMock.expect(service.getFullDetails(EasyMock.aryEq(new String[] {"id"}))).andReturn(fullEntries);
    final Reference[] references = new Reference[] {null};
    EasyMock.expect(service.getReferences("id")).andReturn(references);

    SearchHit[] hits = new SearchHit[] {null};
    EasyMock.expect(service.entrySearch("moo")).andReturn(hits);
    Query query = new Query();
    EasyMock.expect(service.customEntrySearch(query)).andReturn(hits);

    EasyMock.replay(service);

    assert fields == instance.getCatalogOntAPI().getAssociatedFields("id");
    assert fieldValues == instance.getCatalogOntAPI().getAssociatedValues("id");
    assert categories == instance.getCatalogOntAPI().getCategories(new String[] {"id"});
    assert "fieldVal".equals(instance.getCatalogOntAPI().getFieldValueByStringVal("id"));
    assert fieldValues == instance.getCatalogOntAPI().getFieldValues(new String[] {"id"});
    assert fields == instance.getCatalogOntAPI().getFields(new String[] {"id"});
    assert providers == instance.getCatalogOntAPI().getProviders(new String[] {"id"});

    assert aType == instance.getCatalogEntryAPI().getAttributeType("id");
    assert attributes == instance.getCatalogEntryAPI().getAttributes("id");
    assert basicEntries == instance.getCatalogEntryAPI().getBasicDetails(new String[] {"id"}, 3);
    assert fullEntries == instance.getCatalogEntryAPI().getFullDetails(new String[] {"id"});
    assert references == instance.getCatalogEntryAPI().getReferences("id");

    assert hits == instance.getCatalogSearchAPI().entrySearch("moo");
    assert hits == instance.getCatalogSearchAPI().customEntrySearch(query);

    EasyMock.verify(service);
  }

}
