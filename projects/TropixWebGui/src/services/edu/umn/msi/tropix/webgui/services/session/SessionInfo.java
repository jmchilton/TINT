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

package edu.umn.msi.tropix.webgui.services.session;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.models.GalaxyTool;
import edu.umn.msi.tropix.models.User;

public class SessionInfo implements Serializable {
  private static final long serialVersionUID = 1L;
  private User user;
  private boolean admin;
  private boolean guest;
  private String sessionId;
  private Set<Module> module = new HashSet<Module>();
  private List<GalaxyTool> galaxyTools;
  private List<FileType> fileTypes;
  private HashMap<String, String> configurationOptions = new HashMap<String, String>();

  public HashMap<String, String> getConfigurationOptions() {
    return configurationOptions;
  }

  public void setConfigurationOptions(final HashMap<String, String> configurationOptions) {
    this.configurationOptions = configurationOptions;
  }

  public List<FileType> getFileTypes() {
    return fileTypes;
  }

  public void setFileTypes(final List<FileType> fileTypes) {
    this.fileTypes = fileTypes;
  }

  public Set<Module> getModules() {
    return module;
  }

  public void setModules(final Set<Module> module) {
    this.module = module;
  }

  public User getUser() {
    return this.user;
  }

  public void setUser(final User user) {
    this.user = user;
  }

  public boolean isAdmin() {
    return this.admin;
  }

  public void setAdmin(final boolean admin) {
    this.admin = admin;
  }

  public boolean isGuest() {
    return guest;
  }

  public void setGuest(final boolean guest) {
    this.guest = guest;
  }

  public void setSessionId(final String sessionId) {
    this.sessionId = sessionId;
  }

  public String getSessionId() {
    return sessionId;
  }

  public List<GalaxyTool> getGalaxyTools() {
    return galaxyTools;
  }

  public void setGalaxyTools(final List<GalaxyTool> galaxyTools) {
    this.galaxyTools = galaxyTools;
  }

}
