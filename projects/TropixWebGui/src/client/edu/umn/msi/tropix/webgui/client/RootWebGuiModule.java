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

import com.google.common.base.Supplier;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.tab.Tab;

import edu.umn.msi.tropix.webgui.client.components.impl.ComponentsModule;
import edu.umn.msi.tropix.webgui.client.components.newwizards.NewWizardsModule;
import edu.umn.msi.tropix.webgui.client.components.tree.impl.LocationsModule;
import edu.umn.msi.tropix.webgui.client.identification.ParametersPanelFactory;
import edu.umn.msi.tropix.webgui.client.mediators.ActionMediator;
import edu.umn.msi.tropix.webgui.client.mediators.DialogActionController;
import edu.umn.msi.tropix.webgui.client.mediators.LocationActionController;
import edu.umn.msi.tropix.webgui.client.mediators.LocationActionDescriptionManager;
import edu.umn.msi.tropix.webgui.client.mediators.LocationActionDescriptionManagerImpl;
import edu.umn.msi.tropix.webgui.client.mediators.LoginMediator;
import edu.umn.msi.tropix.webgui.client.mediators.NavigationSelectionMediator;
import edu.umn.msi.tropix.webgui.client.mediators.NewItemActionController;
import edu.umn.msi.tropix.webgui.client.models.NewItemFolderActionListenerImpl;
import edu.umn.msi.tropix.webgui.client.models.RootNewItemFolder;
import edu.umn.msi.tropix.webgui.client.modules.GalaxyModuleInstallerImpl;
import edu.umn.msi.tropix.webgui.client.modules.GenetipModuleInstallerImpl;
import edu.umn.msi.tropix.webgui.client.modules.ModuleInstaller;
import edu.umn.msi.tropix.webgui.client.modules.ModuleManager;
import edu.umn.msi.tropix.webgui.client.modules.ModuleManagerImpl;
import edu.umn.msi.tropix.webgui.client.modules.ProtipModuleInstallerImpl;
import edu.umn.msi.tropix.webgui.client.progress.ProgressControllerImpl;
import edu.umn.msi.tropix.webgui.client.search.SearchController;

public class RootWebGuiModule extends AbstractGinModule {

  protected void configure() {    
    install(new ComponentsModule());
    install(new NewWizardsModule());
    install(new LocationsModule());
    
    
    
    bind(new TypeLiteral<Supplier<? extends Layout>>() {
    }).annotatedWith(Names.named("page")).to(PageManager.class).in(Singleton.class);
    bind(NavigationSelectionMediator.class).in(Singleton.class);

    // Bindings Tab Set
    bind(new TypeLiteral<Supplier<Tab>>() {
    }).annotatedWith(Names.named("jobs")).to(ProgressControllerImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<Tab>>() {
    }).annotatedWith(Names.named("searchResults")).to(SearchController.class).in(Singleton.class);

    bind(Session.class).in(Singleton.class);
    bind(LoginMediator.class).in(Singleton.class);
    
    // Search dependencies
    bind(SearchController.class).in(Singleton.class);

    bind(ModuleInstaller.class).annotatedWith(Names.named("protipInstaller")).to(ProtipModuleInstallerImpl.class).in(Singleton.class);
    bind(ModuleInstaller.class).annotatedWith(Names.named("genetipInstaller")).to(GenetipModuleInstallerImpl.class).in(Singleton.class);
    bind(ModuleInstaller.class).annotatedWith(Names.named("galaxyInstaller")).to(GalaxyModuleInstallerImpl.class).in(Singleton.class);
    
    bind(ParametersPanelFactory.class).in(Singleton.class);
    bind(RootNewItemFolder.class).in(Singleton.class);
    bind(ActionMediator.class).in(Singleton.class);
    bind(LocationActionDescriptionManager.class).to(LocationActionDescriptionManagerImpl.class).in(Singleton.class);
    
    bind(ModuleManager.class).to(ModuleManagerImpl.class).in(Singleton.class);
    bind(LocationActionController.class).in(Singleton.class);
    bind(DialogActionController.class).in(Singleton.class);
    bind(NewItemActionController.class).in(Singleton.class);
    bind(NewItemFolderActionListenerImpl.class).in(Singleton.class);
    bind(EagerSingletons.class).in(Singleton.class);
  }
  
}
