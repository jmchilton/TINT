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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.mayo.bmi.bic.bobcat.api.CatalogEntryAPI;
import edu.mayo.bmi.bic.bobcat.api.CatalogOntAPI;
import edu.mayo.bmi.bic.bobcat.api.Field;
import edu.mayo.bmi.bic.bobcat.api.FieldValue;
import edu.mayo.bmi.bic.bobcat.api.Provider;
import edu.mayo.bmi.bic.bobcat.api.SearchHit;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.webgui.client.catalog.beans.Attribute;

public class CatalogUtils {

  private static final Function<SearchHit, String> ENTRY_ID_FUNCTION = new Function<SearchHit, String>() {
    public String apply(final SearchHit hit) {
      return hit.getEntryID();
    }
  };

  public static String[] getServiceIDsFromSHBeanList(final Iterable<SearchHit> shBeans) {
    return Iterables.toArray(Iterables.transform(shBeans, ENTRY_ID_FUNCTION), String.class);
  }

  public static edu.umn.msi.tropix.webgui.client.catalog.beans.Provider getUIProvider(final Provider provider) {
    return new edu.umn.msi.tropix.webgui.client.catalog.beans.Provider(provider.getId(), provider.getRevision(), new SimpleDateFormat("MM/dd/yyyy").format(provider.getCreated().getTime()), new SimpleDateFormat("MM/dd/yyyy").format(provider.getLastModified().getTime()), provider
        .getName(), provider.getContact(), provider.getAddress(), provider.getPhone(), provider.getEmail(), provider.getWebsite(), null);
  }

  public static List<Attribute> getAttributes(final CatalogOntAPI catalogOntAPI, final CatalogEntryAPI catalogEntryAPI, final String serviceId) {
    try {
      edu.mayo.bmi.bic.bobcat.api.Attribute[] attributes = catalogEntryAPI.getAttributes(serviceId);
      if(attributes == null) {
        attributes = new edu.mayo.bmi.bic.bobcat.api.Attribute[0];
      }
      final String[] fieldIds = new String[attributes.length];
      for(int i = 0; i < attributes.length; i++) {
        fieldIds[i] = attributes[i].getFieldID();
      }
      final ArrayList<Attribute> attributeBeans = new ArrayList<Attribute>();
      final Field[] fields = catalogOntAPI.getFields(fieldIds);
      int i = 0;
      for(final edu.mayo.bmi.bic.bobcat.api.Attribute attribute : attributes) {
        List<edu.umn.msi.tropix.webgui.client.catalog.beans.FieldValue> uiFieldValues = null;
        final Field field = catalogOntAPI.getFields(new String[] {attribute.getFieldID()})[0];
        final String bioportalURL = (field != null && field.getConcepts() != null) ? field.getConcepts()[0].getBioportalURL().toString() : null;
        final String type = attribute.getType().getValue();
        final String attributeId = attribute.getId();
        final int revision = attribute.getRevision();
        final String entryId = attribute.getEntryID();
        final String fieldId = attribute.getFieldID();
        final String fieldName = fields[i++].getName();
        if(type.equals("VALUE")) {
          final List<FieldValue> fieldValues = Lists.newArrayList(catalogOntAPI.getFieldValues(attribute.getValues()));
          uiFieldValues = Lists.newArrayList(); //new edu.umn.msi.tropix.webgui.client.catalog.beans.FieldValue[attribute.getValues().length];
          for(FieldValue fieldValue : fieldValues) {
            String bioURL = null;
            if(fieldValue.getConcepts() != null) {
              bioURL = fieldValue.getConcepts()[0].getBioportalURL().toString();
            }
            uiFieldValues.add(new edu.umn.msi.tropix.webgui.client.catalog.beans.FieldValue(fieldValue.getId(), fieldValue.getRevision(), null, null, null, fieldValue.getValue(), null, bioURL));
          }
        } else if(type.equals("FREETEXT")) {
          uiFieldValues = Lists.newArrayList(new edu.umn.msi.tropix.webgui.client.catalog.beans.FieldValue("-1", -1, null, null, attribute.getFieldID(), attribute.getValues()[0], null, null));
        } else {
          System.out.println("Unknown type encountered " + type);
        }
        attributeBeans.add(new Attribute(attributeId, revision, "", "", entryId, fieldId, uiFieldValues, fieldName, bioportalURL));
      }
      return attributeBeans;
    } catch(final RemoteException e) {
      throw ExceptionUtils.convertException(e);
    }
  }

}
