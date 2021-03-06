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

package edu.umn.msi.tropix.client.authentication.impl;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.client.authentication.AuthenticationSourceManager;
import edu.umn.msi.tropix.client.authentication.config.AuthenticationSource;
import edu.umn.msi.tropix.client.authentication.config.AuthenticationSources;
import edu.umn.msi.tropix.client.authentication.config.Local;
import edu.umn.msi.tropix.client.credential.CredentialCreationOptions;
import edu.umn.msi.tropix.common.xml.XMLUtility;

public class AuthenticationSourceManagerImpl implements AuthenticationSourceManager {
  private static final Log LOG = LogFactory.getLog(AuthenticationSourceManagerImpl.class);
  private static final XMLUtility<AuthenticationSources> XML_UTILITY = new XMLUtility<AuthenticationSources>(AuthenticationSources.class);
  private final Map<String, AuthenticationSource> authenticationSources = Maps.newLinkedHashMap();

  public AuthenticationSourceManagerImpl() {
    authenticationSources.put("Local", new Local());
  }

  public Collection<String> getAuthenticationSourceKeys() {
    return ImmutableList.copyOf(authenticationSources.keySet());
  }

  /*
  private static class AuthenticationSource {
    private String name = "Local";
    private String dorianUrl = "local";
    private String authenticationUrl = "local";
  }
  */

  public void setAuthenticationSourcesFile(@Nullable final File sourcesFile) {
    boolean sourcesFileExists = sourcesFile == null || !sourcesFile.exists();
    LOG.info(String.format("Authentication sources file exists - %b", sourcesFileExists));
    if(sourcesFileExists) {
      return;
    }

    final AuthenticationSources sources = XML_UTILITY.deserialize(sourcesFile);
    Preconditions.checkState(sources.getAuthenticationSource().size() > 0, "Authentication sources file " + sourcesFile
        + " defines no authentication sources.");
    authenticationSources.clear();
    for(final AuthenticationSource source : sources.getAuthenticationSource()) {
      final String authenticationSourceTitle = Optional.fromNullable(source.getTitle()).or("Local");
      LOG.info(String.format("Adding authentication source with name %s - %s", authenticationSourceTitle, source));
      authenticationSources.put(authenticationSourceTitle, source);
    }
  }

  public CredentialCreationOptions getAuthenticationOptions(final String authenticationSourceKey) {
    final AuthenticationSource source = authenticationSources.get(authenticationSourceKey);
    Preconditions.checkState(source != null, "No authentication source corresponding to authentication source key " + authenticationSourceKey);
    final CredentialCreationOptions options = new CredentialCreationOptions();
    options.setAuthenticationSource(source);
    //options.setIdpUrl(source.authenticationUrl);
    //options.setIfsUrl(source.dorianUrl);
    return options;
  }

}
