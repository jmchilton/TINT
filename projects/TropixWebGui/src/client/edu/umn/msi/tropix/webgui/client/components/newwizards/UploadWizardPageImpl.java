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

package edu.umn.msi.tropix.webgui.client.components.newwizards;

import com.smartgwt.client.widgets.Canvas;

import edu.umn.msi.tropix.webgui.client.components.UploadComponent;
import edu.umn.msi.tropix.webgui.client.components.UploadComponent.CanUpload;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPageImpl;

class UploadWizardPageImpl extends WizardPageImpl<Canvas> {
  @Override
  public boolean allowNext() {
    return true;
  }

  @Override
  public boolean isValid() {
    final CanUpload canUpload = uploadComponent.canUpload();
    setError(canUpload.getReason());
    return canUpload.getCanUpload();
  }
    
  private final UploadComponent uploadComponent;
  
  UploadWizardPageImpl(final UploadComponent uploadComponent, final String title, final String description) {
    this.uploadComponent = uploadComponent;
    setTitle(title);
    setDescription(description);
    setValid(true);
    setCanvas(uploadComponent.get());
  }

  public void startUpload() {
    uploadComponent.startUpload();    
  }

}
