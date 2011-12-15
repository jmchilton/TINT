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

import java.util.EnumSet;
import java.util.List;

import com.google.inject.Inject;

import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.models.GalaxyTool;
import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.webgui.client.modules.ModuleManager;
import edu.umn.msi.tropix.webgui.services.session.Module;
import edu.umn.msi.tropix.webgui.services.session.SessionInfo;

public final class Session {
  private SessionInfo sessionInfo;
  private User user;
  private String username;
  private boolean admin;
  private boolean guest;
  private EnumSet<Module> module;
  private String sessionId;
  private List<GalaxyTool> galaxyTools;
  private List<FileType> fileTypes;
  private ModuleManager moduleManager;

  @Inject
  public void setModuleManager(final ModuleManager moduleManager) {
    this.moduleManager = moduleManager;
  }

  public List<FileType> getFileTypes() {
    return fileTypes;
  }

  public boolean isGuest() {
    return guest;
  }

  public EnumSet<Module> getModules() {
    return module;
  }

  public boolean isAdmin() {
    return this.admin;
  }

  public User getUser() {
    return this.user;
  }

  public String getUserName() {
    return this.username;
  }

  public List<GalaxyTool> getGalaxyTools() {
    return galaxyTools;
  }

  public Group getPrimaryGroup() {
    return this.sessionInfo.getPrimaryGroup();
  }

  public List<Group> getGroups() {
    return this.sessionInfo.getGroups();
  }

  public void init(final SessionInfo sessionInfo) {
    this.user = sessionInfo.getUser();
    this.admin = sessionInfo.isAdmin();
    this.username = this.user.getCagridId();
    this.guest = sessionInfo.isGuest();

    this.galaxyTools = sessionInfo.getGalaxyTools();
    this.fileTypes = sessionInfo.getFileTypes();
    this.sessionId = sessionInfo.getSessionId();
    this.sessionInfo = sessionInfo;
    // Install specified modules...
    moduleManager.init(this, EnumSet.copyOf(sessionInfo.getModules()));
  }

  public String getConfigurationOption(final String configurationOptionKey) {
    return sessionInfo.getConfigurationOptions().get(configurationOptionKey);
  }

  public boolean getConfigurationOptionAsBoolean(final String configurationOptionKey) {
    final String optionValue = getConfigurationOption(configurationOptionKey);
    return Boolean.parseBoolean(optionValue);
  }

  public String getSessionId() {
    return sessionId;
  }
}
