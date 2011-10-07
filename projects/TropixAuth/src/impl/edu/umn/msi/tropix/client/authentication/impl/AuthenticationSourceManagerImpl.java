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

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.client.authentication.AuthenticationSourceManager;
import edu.umn.msi.tropix.client.authentication.config.AuthenticationSources;
import edu.umn.msi.tropix.client.authentication.config.CaGrid;
import edu.umn.msi.tropix.client.credential.GlobusCredentialOptions;
import edu.umn.msi.tropix.common.xml.XMLUtility;

public class AuthenticationSourceManagerImpl implements AuthenticationSourceManager {
  private static final XMLUtility<AuthenticationSources> XML_UTILITY = new XMLUtility<AuthenticationSources>(AuthenticationSources.class);
  private final Map<String, AuthenticationSource> authenticationSources = Maps.newLinkedHashMap();
  
  public AuthenticationSourceManagerImpl() {
    authenticationSources.put("Local", new AuthenticationSource());
  }
  
  public Collection<String> getAuthenticationSourceKeys() {
    return ImmutableList.copyOf(authenticationSources.keySet());
  }
  
  private static class AuthenticationSource {
    private String name = "Local";
    private String dorianUrl = "local";
    private String authenticationUrl = "local";
  }

  public void setAuthenticationSourcesFile(@Nullable final File sourcesFile) {
    if(sourcesFile == null || !sourcesFile.exists()) {
      return; 
    }

    final AuthenticationSources sources = XML_UTILITY.deserialize(sourcesFile);
    Preconditions.checkState(sources.getAuthenticationSource().size() > 0, "Authentication sources file " + sourcesFile + " defines no authentication sources.");
    authenticationSources.clear();
    for(final Object sourceObject : sources.getAuthenticationSource()) {
      final AuthenticationSource source = new AuthenticationSource();
      if(sourceObject instanceof CaGrid) {
        final CaGrid caGridAuthenticationSource = (CaGrid) sourceObject;
        source.name = caGridAuthenticationSource.getTitle();
        source.dorianUrl = caGridAuthenticationSource.getDorianServiceUrl();
        source.authenticationUrl = caGridAuthenticationSource.getAuthenticationServiceUrl();
      }
      authenticationSources.put(source.name, source);
    }
  }

  public GlobusCredentialOptions getAuthenticationOptions(final String authenticationSourceKey) {
    final AuthenticationSource source = authenticationSources.get(authenticationSourceKey);
    Preconditions.checkState(source != null, "No authentication source corresponding to authentication source key " + authenticationSourceKey);
    final GlobusCredentialOptions options = new GlobusCredentialOptions();
    options.setIdpUrl(source.authenticationUrl);
    options.setIfsUrl(source.dorianUrl);
    return options;
  }
  
}
