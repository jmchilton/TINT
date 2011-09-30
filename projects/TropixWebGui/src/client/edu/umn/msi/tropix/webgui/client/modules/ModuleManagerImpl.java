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

import java.util.EnumSet;
import java.util.List;

import javax.annotation.Nullable;

import com.google.inject.Inject;

import edu.umn.msi.tropix.webgui.client.Session;
import edu.umn.msi.tropix.webgui.client.catalog.CatalogGlobals;
import edu.umn.msi.tropix.webgui.client.models.NewItemContext;
import edu.umn.msi.tropix.webgui.client.models.RootNewItemFolder;
import edu.umn.msi.tropix.webgui.client.utils.Lists;
import edu.umn.msi.tropix.webgui.services.session.Module;

public class ModuleManagerImpl implements ModuleManager {
  private List<ModuleInstaller> moduleInstallers = Lists.newArrayList();
  private NewItemContext newItemContext;
  private EnumSet<Module> modules;

  @Inject
  public void setNewItemContext(final RootNewItemFolder newItemContext) {
    this.newItemContext = newItemContext;
  }

  public void registerModuleInstaller(final ModuleInstaller moduleInstaller) {
    this.moduleInstallers.add(moduleInstaller);
  }
  
  public void init(final Session session, final EnumSet<Module> modules) {
    this.modules = modules;
    
    for(final ModuleInstaller moduleInstaller : moduleInstallers) {
      if(modules.contains(moduleInstaller.getModule())) {
        moduleInstaller.installNewItemModels(session, newItemContext);
      }
    }

    // This is very hacky, come up with another way to initialize this...
    if(modules.contains(Module.CATALOG)) {
      CatalogGlobals.init();
    }
  }
  
  private boolean containModule(@Nullable final Module module) {
    return module == null ? true : modules.contains(module);
  }

  public boolean apply(final Object object) {
    boolean enabled = true;
    if(object instanceof RequiresModule) {
      enabled = containModule(((RequiresModule) object).requiresModule());
    }
    if(object instanceof RequiresModules) {
      for(final Module module : ((RequiresModules) object).requiresModules()) {
        enabled = enabled && containModule(module);
      }
    }
    if(object instanceof RequiresAnyModule) {
      boolean found = false;
      for(final Module module : ((RequiresAnyModule) object).requiresAnyModule()) {
        found = found || containModule(module);
      }
      enabled = enabled && found;
    }
    return enabled;
  }

  public boolean containsModules(final Module... modules) {
    boolean allFound = true;
    for(final Module module : modules) {
      if(!containModule(module)) {
        allFound = false;
        break;
      }
    }
    return allFound;
  }

}