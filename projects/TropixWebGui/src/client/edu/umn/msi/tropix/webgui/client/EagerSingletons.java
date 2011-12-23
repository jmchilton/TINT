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

package edu.umn.msi.tropix.webgui.client;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import edu.umn.msi.tropix.webgui.client.mediators.DialogActionController;
import edu.umn.msi.tropix.webgui.client.mediators.LocationActionController;
import edu.umn.msi.tropix.webgui.client.mediators.NewItemActionController;
import edu.umn.msi.tropix.webgui.client.models.NewItemFolderActionListenerImpl;
import edu.umn.msi.tropix.webgui.client.modules.ModuleInstaller;

public class EagerSingletons {

  @Inject
  public EagerSingletons(final NewItemActionController c1, final DialogActionController c2, final LocationActionController c3, final NewItemFolderActionListenerImpl listener) {

  }
    
  @Inject
  public void setProtipInstaller(@Named("protipInstaller") final  ModuleInstaller protipInstaller) {
  }

  @Inject
  public void setGenetipInstaller(@Named("genetipInstaller") final ModuleInstaller genetipInstaller) {
  }

  @Inject
  public void setGalaxyInstaller(@Named("galaxyInstaller") final  ModuleInstaller galaxyInstaller) {
  }
}
