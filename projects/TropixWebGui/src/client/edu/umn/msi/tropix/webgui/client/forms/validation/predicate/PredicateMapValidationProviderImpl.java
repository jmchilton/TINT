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

package edu.umn.msi.tropix.webgui.client.forms.validation.predicate;

import java.io.Serializable;
import java.util.Map;

import com.google.common.base.Predicate;

import edu.umn.msi.tropix.webgui.services.forms.ValidationProvider;

public class PredicateMapValidationProviderImpl implements ValidationProvider, Serializable {
  private static final long serialVersionUID = 1L;
  private Map<String, Predicate<String>> validationPredicates;

  public boolean validate(final String key, final String value) {
    final Predicate<String> validationPredicate = this.validationPredicates.get(key);
    if(validationPredicate != null) {
      return validationPredicate.apply(value);
    } else {
      return true;
    }
  }

  public void setValidationPredicates(final Map<String, Predicate<String>> validationPredicates) {
    this.validationPredicates = validationPredicates;
  }

}
