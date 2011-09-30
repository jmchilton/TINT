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

package edu.umn.msi.tropix.webgui.client.forms;

import java.util.HashSet;
import java.util.Set;

import edu.umn.msi.tropix.webgui.services.forms.ValidationProvider;

/**
 * Wraps a validation provider and tracks invalid keys. Tracking changes it determine if any invalid keys are still outstanding. It also provides validation events to a ValidationMediator for the set of keys validated against it.
 * 
 * @author John Chilton (chitlon at msi dot umn dot edu)
 * 
 */
public class ValidationTracker implements ValidationProvider {
  private ValidationProvider baseProvider;
  private ValidationMediator mediator;
  private final Set<String> invalidKeys = new HashSet<String>();
  private boolean allValid = true;

  public ValidationTracker(final ValidationProvider baseProvider, final ValidationMediator mediator) {
    this.baseProvider = baseProvider;
    this.mediator = mediator;
  }

  public void fireValidationEvent() {
    this.mediator.onValidation(this.allValid);
  }

  public boolean validate(final String key, final String value) {
    final boolean isValid = this.baseProvider.validate(key, value);
    boolean validationStatusChange = false;
    if(!isValid) {
      this.invalidKeys.add(key);
      if(this.allValid) {
        this.allValid = false;
        validationStatusChange = true;
      }
    } else {
      this.invalidKeys.remove(key);
      if(this.invalidKeys.isEmpty()) {
        this.allValid = true;
        validationStatusChange = true;
      }
    }

    if(validationStatusChange) {
      this.fireValidationEvent();
    }

    return isValid;
  }

  public ValidationProvider getBaseProvider() {
    return this.baseProvider;
  }

  public void setBaseProvider(final ValidationProvider baseProvider) {
    this.baseProvider = baseProvider;
  }

  public ValidationMediator getMediator() {
    return this.mediator;
  }

  public void setMediator(final ValidationMediator mediator) {
    this.mediator = mediator;
  }
}
