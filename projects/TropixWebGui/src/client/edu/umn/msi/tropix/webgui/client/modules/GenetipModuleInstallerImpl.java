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

package edu.umn.msi.tropix.webgui.client.modules;

import edu.umn.msi.tropix.webgui.client.Session;
import edu.umn.msi.tropix.webgui.client.models.NewItemContext;
import edu.umn.msi.tropix.webgui.client.models.NewItemModelImpl;
import edu.umn.msi.tropix.webgui.services.session.Module;

public class GenetipModuleInstallerImpl extends BaseModuleInstallerImpl implements ModuleInstaller {

  public void installNewItemModels(final Session session, final NewItemContext newItemContext) {
    final NewItemContext analysisContext = newItemContext.getChildContext("Analysis");
    analysisContext.addModel(new NewItemModelImpl("Bowtie Analysis", "Creates a Bowtie alignment analysis."));
    final NewItemContext otherContext = newItemContext.getChildContext("Other");
    otherContext.addModel(new NewItemModelImpl("Upload Bowtie Index", "Upload a preexisting Bowtie index file."));    
  }

  public Module getModule() {
    return Module.GENETIP;
  }

}
