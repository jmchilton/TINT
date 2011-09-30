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

package edu.umn.msi.tropix.webgui.client.widgets.wizards;

import java.util.Arrays;

import javax.annotation.Nullable;

public final class WizardPageGroup<T extends WizardPageImpl<?>> {
  private Iterable<T> wizardPages;
  
  public static <S extends WizardPageImpl<?>> WizardPageGroup<S> getWizardPageGroupFor(final S... wizardPages) {
    return new WizardPageGroup<S>(Arrays.<S>asList(wizardPages));
  }
  
  private WizardPageGroup(final Iterable<T> wizardPages) {
    this.wizardPages = wizardPages;
  }
  
  public void disableAll() {
    for(T wizardPage : wizardPages) {
      wizardPage.setEnabled(false);
    }
  }

  public void enableOnly(final T wizardPage) {
    disableAll();
    wizardPage.setEnabled(true);
  }

  @Nullable
  public T getEnabledWizardPage() {
    T enabledWizardPage = null;
    for(T wizardPage : wizardPages) {
      if(wizardPage.isEnabled()) {
        enabledWizardPage = wizardPage;
        break;
      }
    }
    return enabledWizardPage;
  }
  
}
