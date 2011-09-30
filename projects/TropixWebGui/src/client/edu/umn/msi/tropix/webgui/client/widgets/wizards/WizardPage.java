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

public interface WizardPage {
  Canvas getCanvas();

  boolean canFinishEarly();

  void registerWizardStateListener(WizardStateListener wizardStateListener);

  boolean isValid();
  
  String readAndResetError();
  
  /**
   * In most cases allowNext() should be the same as
   * isValid(), but in cases like browser upload forms there is not way
   * to register a callback, so the next/finish button should just be
   * turned on and then checked with isValid() when clicked. To enable this
   * behavior for a given page this method should return true.
   * 
   * @return true iff the "Next" or "Finish" button of the wizard should be 
   * enabled when the page is first turned to.
   */
  boolean allowNext();

  String getTitle();

  String getDescription();
  
  boolean isEnabled();
  
  void onDisplay();
  
}
