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

package edu.umn.msi.tropix.webgui.server.forms.validation;

import java.util.Map;

import javax.inject.Inject;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

import edu.umn.msi.tropix.webgui.client.forms.validation.predicate.PredicateMapValidationProviderImpl;
import edu.umn.msi.tropix.webgui.server.resource.ResourceAccessor;
import edu.umn.msi.tropix.webgui.server.resource.ResourceAccessorFactory;
import edu.umn.msi.tropix.webgui.services.forms.ValidationProvider;

class SchemaValidationProviderFactory implements Supplier<ValidationProvider> {
  private final ResourceAccessorFactory resourceAccessorFactory;
  private final SchemaParser schemaParser;
  private String resourceId;
  
  @Inject
  SchemaValidationProviderFactory(final ResourceAccessorFactory resourceAccessorFactory, final SchemaParser schemaParser) {
    this.resourceAccessorFactory = resourceAccessorFactory;
    this.schemaParser = schemaParser;
  }
  

  public ValidationProvider get() {
    final ResourceAccessor accessor = this.resourceAccessorFactory.get();
    accessor.setResourceId(this.resourceId);
    try {
      final Map<String, Predicate<String>> predicateMap = this.schemaParser.parse(accessor.get());
      final PredicateMapValidationProviderImpl validationProvider = new PredicateMapValidationProviderImpl();
      validationProvider.setValidationPredicates(predicateMap);
      return validationProvider;
    } catch(final Exception e) {
      throw new IllegalStateException("Failed to create validation provider", e);
    }
  }

  public void setResourceId(final String resourceId) {
    this.resourceId = resourceId;
  }

}
