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

import com.smartgwt.client.widgets.Canvas;

import edu.umn.msi.tropix.webgui.client.utils.Listener;

public class WizardPageImpl<C extends Canvas> implements WizardPage {
  private static final WizardStateListener NULL_LISTENER = new WizardStateListener() {
    public void onStateChange(final WizardPage wizardPage) {
    }
  };
  private WizardStateListener wizardStateListener = WizardPageImpl.NULL_LISTENER;
  private boolean isValid = false;
  private boolean canFinishEarly = false;
  private boolean enabled = true;
  private String description;
  private String title;
  private String error = null;
  private C canvas;

  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
    this.wizardStateListener.onStateChange(this);
  }

  public C getCanvas() {
    return this.canvas;
  }

  protected Listener<Boolean> getValidationListener() {
    return new Listener<Boolean>() {
      public void onEvent(final Boolean isValid) {
        setValid(isValid);
      }
    };
  }

  protected void setValid(final boolean isValid) {
    this.isValid = isValid;
    this.wizardStateListener.onStateChange(this);
  }

  protected void setCanFinishEarly(final boolean canFinishEarly) {
    this.canFinishEarly = canFinishEarly;
    this.wizardStateListener.onStateChange(this);
  }

  public String getTitle() {
    return this.title;
  }

  public String getDescription() {
    return this.description;
  }

  public boolean isValid() {
    return this.isValid;
  }

  public boolean allowNext() {
    return isValid();
  }

  public boolean canFinishEarly() {
    return this.canFinishEarly;
  }

  public void registerWizardStateListener(final WizardStateListener wizardStateListener) {
    this.wizardStateListener = wizardStateListener;
  }

  protected void setTitle(final String title) {
    this.title = title;
  }

  protected void setDescription(final String description) {
    this.description = description;
  }

  protected void setCanvas(final C canvas) {
    this.canvas = canvas;
  }

  protected void setError(final String error) {
    this.error = error;
  }

  public boolean isEnabled() {
    return enabled;
  }

  public String readAndResetError() {
    final String error = this.error;
    this.error = null;
    return error;
  }

  // Called when page is brought to foreground of wizard.
  public void onDisplay() {
  }

  // Called when page leaves foreground of wizard.
  public void onBackground() {    
  }
  
}
