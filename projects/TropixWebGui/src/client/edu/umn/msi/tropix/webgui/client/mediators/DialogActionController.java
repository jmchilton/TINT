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

package edu.umn.msi.tropix.webgui.client.mediators;

import com.google.common.base.Supplier;
import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.google.inject.name.Named;

public class DialogActionController {
  private final ActionMediator actionMediator;

  @Inject
  public DialogActionController(final ActionMediator actionMediator) {
    this.actionMediator = actionMediator;
  }

  private void registerListener(final String actionType, final Supplier<? extends Command> windowComponentSupplier) {
    actionMediator.registerActionListener(actionType, new CommandActionEventListener(windowComponentSupplier));
  }

  @Inject
  public void setBulkDownloadWindowComponentSupplier(@Named("bulkDownload") final Supplier<? extends Command> windowComponentSupplier) {
    registerListener("bulkDownload", windowComponentSupplier);
  }

  @Inject
  public void setBulkMgfProteinPilotDownloadWindowComponentSupplier(
      @Named("bulkMgfProteinPilotDownload") final Supplier<? extends Command> windowComponentSupplier) {
    registerListener("bulkMgfProteinPilotDownload", windowComponentSupplier);
  }

  @Inject
  public void setBulkMgfProteinPilotITraqDownloadWindowComponentSupplier(
      @Named("bulkMgfProteinPilotITraqDownload") final Supplier<? extends Command> windowComponentSupplier) {
    registerListener("bulkMgfProteinPilotITraqDownload", windowComponentSupplier);
  }

  @Inject
  public void setBulkMgfMascotDownloadWindowComponentSupplier(
      @Named("bulkMgfMascotDownload") final Supplier<? extends Command> windowComponentSupplier) {
    registerListener("bulkMgfMascotDownload", windowComponentSupplier);
  }

  @Inject
  public void setManageGroupFoldersWindowComponentSupplier(@Named("manageGroupFolders") final Supplier<? extends Command> windowComponentSupplier) {
    registerListener("manageGroupFolders", windowComponentSupplier);
  }

  @Inject
  public void setGridFtpExportWindowComponentSupplier(@Named("gridFtpExport") final Supplier<? extends Command> windowComponentSupplier) {
    registerListener("gridFtpExport", windowComponentSupplier);
  }

  @Inject
  public void setGalaxyExportWindowComponentSupplier(@Named("galaxyExport") final Supplier<? extends Command> windowComponentSupplier) {
    registerListener("galaxyExport", windowComponentSupplier);
  }

  @Inject
  public void setManageFileTypesWindowComponentSupplier(@Named("manageFileTypes") final Supplier<? extends Command> windowComponentSupplier) {
    registerListener("manageFileTypes", windowComponentSupplier);
  }

  @Inject
  public void setManagedGalaxyToolsWindowComponentSupplier(@Named("manageGalaxyTools") final Supplier<? extends Command> windowComponentSupplier) {
    registerListener("manageGalaxyTools", windowComponentSupplier);
  }

  @Inject
  public void setChangePasswordWindowComponentSupplier(@Named("changePassword") final Supplier<? extends Command> windowComponentSupplier) {
    registerListener("changePassword", windowComponentSupplier);
  }

  @Inject
  public void setCreateLocalUserWindowComponentSupplier(@Named("createLocalUser") final Supplier<? extends Command> windowComponentSupplier) {
    registerListener("createLocalUser", windowComponentSupplier);
  }

  @Inject
  public void setAboutWindowComponentSupplier(@Named("about") final Supplier<? extends Command> windowComponentSupplier) {
    registerListener("about", windowComponentSupplier);
  }

  @Inject
  public void setManagerCatalogProviderComponentSupplier(@Named("manageCatalogProviders") final Supplier<? extends Command> windowComponentSupplier) {
    registerListener("manageCatalogProviders", windowComponentSupplier);
  }

  @Inject
  public void setGridSearchComponentSupplier(@Named("gridSearch") final Supplier<? extends Command> windowComponentSupplier) {
    registerListener("gridSearch", windowComponentSupplier);
  }

  @Inject
  public void setCatalogAdminComponentSupplier(@Named("catalogAdmin") final Supplier<? extends Command> windowComponentSupplier) {
    registerListener("catalogAdmin", windowComponentSupplier);
  }

  @Inject
  public void setGroupAdminComponentSupplier(@Named("groupAdmin") final Supplier<? extends Command> windowComponentSupplier) {
    registerListener("groupAdmin", windowComponentSupplier);
  }

  @Inject
  public void setUserAdminComponentSupplier(@Named("userAdmin") final Supplier<? extends Command> windowComponentSupplier) {
    registerListener("userAdmin", windowComponentSupplier);
  }

  @Inject
  public void setSearchComponentSupplier(@Named("search") final Supplier<? extends Command> windowComponentSupplier) {
    registerListener("search", windowComponentSupplier);
  }

  @Inject
  public void setQuickSearchComponentSupplier(@Named("quickSearch") final Supplier<? extends Command> windowComponentSupplier) {
    registerListener("quickSearch", windowComponentSupplier);
  }

  @Inject
  public void setQuickCatalogSearchComponentSupplier(@Named("quickCatalogSearch") final Supplier<? extends Command> windowComponentSupplier) {
    registerListener("quickCatalogSearch", windowComponentSupplier);
  }

  @Inject
  public void setCatalogSearchComponentSupplier(@Named("catalogSearch") final Supplier<? extends Command> windowComponentSupplier) {
    registerListener("catalogSearch", windowComponentSupplier);
  }

  @Inject
  public void setFindSharedFoldersComponentSupplier(@Named("findSharedFolders") final Supplier<? extends Command> windowComponentSupplier) {
    registerListener("findSharedFolders", windowComponentSupplier);
  }

}
