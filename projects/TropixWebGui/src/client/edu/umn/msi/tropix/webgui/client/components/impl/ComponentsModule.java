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

package edu.umn.msi.tropix.webgui.client.components.impl;

import java.util.List;

import com.google.common.base.Supplier;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.user.client.Command;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.tab.TabSet;

import edu.umn.msi.tropix.client.directory.GridUser;
import edu.umn.msi.tropix.client.search.models.GridData;
import edu.umn.msi.tropix.client.services.GridService;
import edu.umn.msi.tropix.client.services.IdentificationGridService;
import edu.umn.msi.tropix.client.services.QueueGridService;
import edu.umn.msi.tropix.client.services.ScaffoldGridService;
import edu.umn.msi.tropix.galaxy.tool.Tool;
import edu.umn.msi.tropix.models.GalaxyTool;
import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.Note;
import edu.umn.msi.tropix.models.Request;
import edu.umn.msi.tropix.webgui.client.catalog.CatalogServiceController;
import edu.umn.msi.tropix.webgui.client.catalog.CatalogServiceControllerSupplierImpl;
import edu.umn.msi.tropix.webgui.client.catalog.beans.Provider;
import edu.umn.msi.tropix.webgui.client.catalog.beans.ServiceBean;
import edu.umn.msi.tropix.webgui.client.components.CanvasComponent;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.DescribableLocationCommandComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.DynamicUploadComponent;
import edu.umn.msi.tropix.webgui.client.components.EditCatalogProviderFormComponent;
import edu.umn.msi.tropix.webgui.client.components.EditCatalogServiceFormComponent;
import edu.umn.msi.tropix.webgui.client.components.EditGroupFolderFormComponent;
import edu.umn.msi.tropix.webgui.client.components.EditObjectComponent;
import edu.umn.msi.tropix.webgui.client.components.FileTypeFormItemComponent;
import edu.umn.msi.tropix.webgui.client.components.GridUserItemComponent;
import edu.umn.msi.tropix.webgui.client.components.GridUserSelectionComponent;
import edu.umn.msi.tropix.webgui.client.components.GroupSelectionComponent;
import edu.umn.msi.tropix.webgui.client.components.LocationCommandComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.LoginComponent;
import edu.umn.msi.tropix.webgui.client.components.MainToolStripComponent;
import edu.umn.msi.tropix.webgui.client.components.MetadataInputComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.MultiSelectionWindowComponent;
import edu.umn.msi.tropix.webgui.client.components.PageConfiguration;
import edu.umn.msi.tropix.webgui.client.components.ResultComponent;
import edu.umn.msi.tropix.webgui.client.components.SelectionWindowComponent;
import edu.umn.msi.tropix.webgui.client.components.ServiceSelectionComponent;
import edu.umn.msi.tropix.webgui.client.components.UploadComponent;
import edu.umn.msi.tropix.webgui.client.components.UploadComponentFactory.UploadComponentOptions;
import edu.umn.msi.tropix.webgui.client.components.galaxy.AddGalaxyToolWindowComponentSupplierImpl;
import edu.umn.msi.tropix.webgui.client.components.galaxy.EditGalaxyToolComponentFactoryImpl;
import edu.umn.msi.tropix.webgui.client.components.galaxy.EditGalaxyToolWindowComponentFactoryImpl;
import edu.umn.msi.tropix.webgui.client.components.galaxy.EditGalaxyToolXmlWindowComponentFactoryImpl;
import edu.umn.msi.tropix.webgui.client.components.galaxy.GalaxyActionComponentFactoryImpl;
import edu.umn.msi.tropix.webgui.client.components.galaxy.GalaxyActionEvent;
import edu.umn.msi.tropix.webgui.client.components.galaxy.ManageGalaxyToolsWindowComponentSupplierImpl;
import edu.umn.msi.tropix.webgui.client.components.impl.GalaxyExportComponentSupplierImpl.GalaxyFileExportComponentSupplierImpl;
import edu.umn.msi.tropix.webgui.client.components.impl.GalaxyExportComponentSupplierImpl.GalaxyPeakListExportComponentSupplierImpl;
import edu.umn.msi.tropix.webgui.client.components.impl.GalaxyExportComponentSupplierImpl.GalaxyRawExportComponentSupplierImpl;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.services.object.SearchResult;

/**
 * Binds concrete classes to various interfaces defined in the package
 * edu.umn.msi.tropix.webgui.client.components.
 * 
 * @author John Chilton
 * 
 */
public class ComponentsModule extends AbstractGinModule {

  protected <T extends Command> void bindDescribableLocationCommandComponentFactory(final String name,
      final Class<? extends DescribableLocationCommandComponentFactory<T>> clazz) {
    bind(clazz).in(Singleton.class);
    bind(new TypeLiteral<LocationCommandComponentFactory<? extends Command>>() {
    }).annotatedWith(Names.named(name)).to(clazz);
    bind(new TypeLiteral<DescribableLocationCommandComponentFactory<? extends Command>>() {
    }).annotatedWith(Names.named(name)).to(clazz);
  }

  /**
   * Specifies actual bindings.
   */
  protected void configure() {

    bind(LoginComponent.class).to(LoginComponentImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<TabSet>>() {
    }).annotatedWith(Names.named("main")).to(MainTabSetSupplierImpl.class).in(Singleton.class);
    bind(MainToolStripComponent.class).to(MainToolStripComponentImpl.class).in(Singleton.class);

    // User components
    bind(new TypeLiteral<Supplier<GridUserSelectionComponent>>() {
    }).to(GridUserSelectionComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<GridUserItemComponent>>() {
    }).to(GridUserItemComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends SelectionWindowComponent<GridUser, ? extends Window>>>() {
    }).to(GridUserWindowSelectionComponentSupplierImpl.class).in(Singleton.class);

    // Group components
    bind(new TypeLiteral<Supplier<GroupSelectionComponent>>() {
    }).to(GroupSelectionComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends SelectionWindowComponent<Group, ? extends Window>>>() {
    }).to(GroupWindowSelectionComponentSupplierImpl.class).in(Singleton.class);

    // Catalog components
    bind(new TypeLiteral<Supplier<CatalogServiceController>>() {
    }).to(CatalogServiceControllerSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<ComponentFactory<ServiceBean, ? extends Command>>() {
    }).annotatedWith(Names.named("requestService")).to(RequestServiceWindowComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<ComponentFactory<ServiceBean, ? extends CanvasComponent<? extends Canvas>>>() {
    }).annotatedWith(Names.named("showCatalogService")).to(ShowCatalogServiceLayoutComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<ComponentFactory<Provider, ? extends Command>>() {
    }).annotatedWith(Names.named("showCatalogProvider")).to(ShowCatalogProviderWindowComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<ComponentFactory<Provider, ? extends Command>>() {
    }).annotatedWith(Names.named("editCatalogProvider")).to(EditCatalogProviderWindowComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<EditCatalogProviderFormComponent>>() {
    }).to(EditCatalogProviderFormComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("addCatalogProvider")).to(AddCatalogProviderWindowComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<ComponentFactory<Provider, ? extends CanvasComponent<? extends Canvas>>>() {
    }).annotatedWith(Names.named("manageCatalogServices")).to(ManageServicesComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<ComponentFactory<ServiceBean, ? extends Command>>() {
    }).annotatedWith(Names.named("showCatalogService")).to(ShowCatalogServiceWindowComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<EditCatalogServiceFormComponent>>() {
    }).to(EditCatalogServiceFormComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<ComponentFactory<ServiceBean, ? extends Command>>() {
    }).annotatedWith(Names.named("editCatalogService")).to(EditCatalogServiceWindowComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<ComponentFactory<Provider, ? extends Command>>() {
    }).annotatedWith(Names.named("addCatalogService")).to(AddCatalogServiceWindowComponentFactoryImpl.class);

    bind(new TypeLiteral<Supplier<EditGroupFolderFormComponent>>() {
    }).to(EditGroupFolderFormComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("addGroupFolder")).to(AddGroupFolderWindowComponentSupplierImpl.class).in(Singleton.class);

    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("about")).to(AboutWindowComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("manageCatalogProviders")).to(ManageProvidersWindowComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("manageFileTypes")).to(ManageFileTypesWindowComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("changePassword")).to(ChangeLocalUserPasswordWindowComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("gridSearch")).to(GridSearchWindowComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("catalogAdmin")).to(CatalogAdminWindowComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("groupAdmin")).to(GroupAdminWindowComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("userAdmin")).to(UserAdminWindowComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("createLocalUser")).to(CreateLocalUserWindowComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("search")).to(SearchWindowComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("quickSearch")).to(QuickSearchWindowComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("findSharedFolders")).to(FindSharedFoldersWindowComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("quickCatalogSearch")).to(CatalogQuickSearchWindowComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("catalogSearch")).to(CatalogSearchWindowComponentSupplierImpl.class).in(Singleton.class);

    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("manageGroupFolders")).to(ManageGroupFoldersWindowComponentSupplierImpl.class).in(Singleton.class);

    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("manageGalaxyTools")).to(ManageGalaxyToolsWindowComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<ComponentFactory<Tool, ? extends EditObjectComponent<? extends Canvas, Tool>>>() {
    }).to(EditGalaxyToolComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("addGalaxyTool")).to(AddGalaxyToolWindowComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<ComponentFactory<GalaxyTool, ? extends Command>>() {
    }).annotatedWith(Names.named("editGalaxyTool")).to(EditGalaxyToolWindowComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<ComponentFactory<GalaxyTool, ? extends Command>>() {
    }).annotatedWith(Names.named("editGalaxyToolXml")).to(EditGalaxyToolXmlWindowComponentFactoryImpl.class).in(Singleton.class);

    bind(new TypeLiteral<ComponentFactory<GalaxyActionEvent, ? extends Command>>() {
    }).to(GalaxyActionComponentFactoryImpl.class).in(Singleton.class);

    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("bulkMgfProteinPilotDownload")).to(BulkMgfProteinPilotDownloadComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("bulkMgfProteinPilotITraqDownload")).to(BulkMgfProteinPilotITraqDownloadComponentSupplierImpl.class)
        .in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("bulkMgfMascotDownload")).to(BulkMgfMascotDownloadComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("bulkDownload")).to(BulkDownloadComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("gridFtpExport")).to(GridFtpExportComponentSupplierImpl.class).in(Singleton.class);

    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("galaxyExport")).to(GalaxyFileExportComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("galaxyRawExport")).to(GalaxyRawExportComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends Command>>() {
    }).annotatedWith(Names.named("galaxyPeakListExport")).to(GalaxyPeakListExportComponentSupplierImpl.class).in(Singleton.class);

    bindDescribableLocationCommandComponentFactory("changeDescription", ChangeDescriptionCommandComponentFactoryImpl.class);
    bindDescribableLocationCommandComponentFactory("move", MoveCommandComponentFactoryImpl.class);
    bindDescribableLocationCommandComponentFactory("rename", RenameCommandComponentFactoryImpl.class);
    bindDescribableLocationCommandComponentFactory("delete", DeleteCommandComponentFactoryImpl.class);
    /*
     * bind(new TypeLiteral<LocationCommandComponentFactory<? extends Command>>() {
     * }).annotatedWith(Names.named("changeDescription")).to(ChangeDescriptionCommandComponentFactoryImpl.class).in(Singleton.class);
     * bind(new TypeLiteral<LocationCommandComponentFactory<? extends Command>>() {
     * }).annotatedWith(Names.named("move")).to(MoveCommandComponentFactoryImpl.class).in(Singleton.class);
     * bind(new TypeLiteral<LocationCommandComponentFactory<? extends Command>>() {
     * }).annotatedWith(Names.named("rename")).to(RenameCommandComponentFactoryImpl.class).in(Singleton.class);
     * bind(new TypeLiteral<LocationCommandComponentFactory<? extends Command>>() {
     * }).annotatedWith(Names.named("delete")).to(DeleteCommandComponentFactoryImpl.class).in(Singleton.class);
     */

    bind(UploadComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<ComponentFactory<UploadComponentOptions, ? extends UploadComponent>>() {
    }).to(UploadComponentFactoryImpl.class);
    bind(new TypeLiteral<ComponentFactory<UploadComponentOptions, DynamicUploadComponent>>() {
    }).to(UploadComponentFactoryImpl.class);

    bind(MetadataInputComponentFactory.class).to(MetadataInputComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<ComponentFactory<Note, ? extends CanvasComponent<? extends Canvas>>>() {
    }).to(NoteComponentFactoryImpl.class).in(Singleton.class);

    bind(new TypeLiteral<ComponentFactory<Request, Canvas>>() {
    }).to(IncomingRequestComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<ComponentFactory<PageConfiguration, ? extends CanvasComponent<? extends Canvas>>>() {
    }).to(PageComponentFactoryImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends MultiSelectionWindowComponent<TreeItem, ? extends Window>>>() {
    }).annotatedWith(Names.named("concreteObjects")).to(ObjectSelectionWindowComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends SelectionWindowComponent<TreeItem, ? extends Window>>>() {
    }).annotatedWith(Names.named("sharedFolder")).to(VirtualFolderSelectionWindowComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<ComponentFactory<PageConfiguration, ? extends CanvasComponent<? extends Canvas>>>() {
    }).annotatedWith(Names.named("sharing")).to(SharingComponentFactoryImpl.class).in(Singleton.class);

    // Search results
    bind(new TypeLiteral<Supplier<? extends ResultComponent<List<GridData>, ? extends Window>>>() {
    }).to(GridSearchResultWindowComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends ResultComponent<List<ServiceBean>, ? extends Window>>>() {
    }).to(CatalogResultWindowComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<? extends ResultComponent<List<SearchResult>, ? extends Window>>>() {
    }).to(SearchResultComponentSupplierImpl.class).in(Singleton.class);

    bind(new TypeLiteral<Supplier<FileTypeFormItemComponent>>() {
    }).to(FileTypeFormItemComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<ComponentFactory<FileTypeFormItemComponent.FileTypeFormItemOptions, FileTypeFormItemComponent>>() {
    }).to(FileTypeFormItemComponentSupplierImpl.class).in(Singleton.class);

    // Service lists
    bind(new TypeLiteral<Supplier<ServiceSelectionComponent<GridService>>>() {
    }).to(ServiceSelectionComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<ServiceSelectionComponent<QueueGridService>>>() {
    }).to(QueueServiceSelectionComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<ServiceSelectionComponent<IdentificationGridService>>>() {
    }).to(QueueServiceSelectionComponentSupplierImpl.class).in(Singleton.class);
    bind(new TypeLiteral<Supplier<ServiceSelectionComponent<ScaffoldGridService>>>() {
    }).to(ServiceSelectionComponentSupplierImpl.class).in(Singleton.class);

  }

}
