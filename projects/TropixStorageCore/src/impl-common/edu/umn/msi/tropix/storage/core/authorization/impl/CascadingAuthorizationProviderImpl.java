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

package edu.umn.msi.tropix.storage.core.authorization.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.storage.core.authorization.AuthorizationProvider;

// TODO: Refactor this to be much simpler there are test cases.
public class CascadingAuthorizationProviderImpl implements AuthorizationProvider {

  private static final class CanUpload implements Function<AuthorizationProvider, Boolean> {
    private final String callerIdentity;
    private final String id;

    private CanUpload(final String callerIdentity, final String id) {
      this.callerIdentity = callerIdentity;
      this.id = id;
    }

    public Boolean apply(final AuthorizationProvider authorizationProvider) {
      return authorizationProvider.canUpload(id, callerIdentity);
    }
  }

  private static final class CanDownloadAll implements Function<AuthorizationProvider, Boolean> {
    private final String callerIdentity;
    private final String[] ids;
    
    private CanDownloadAll(final String callerIdentity, final String[] ids) {
      this.callerIdentity = callerIdentity;
      this.ids = ids;
    }
    
    public Boolean apply(final AuthorizationProvider authorizationProvider) {
      return authorizationProvider.canDownloadAll(ids, callerIdentity);
    }
    
  }
  
  private static final class CanDownload implements Function<AuthorizationProvider, Boolean> {
    private final String callerIdentity;
    private final String id;

    private CanDownload(final String callerIdentity, final String id) {
      this.callerIdentity = callerIdentity;
      this.id = id;
    }

    public Boolean apply(final AuthorizationProvider authorizationProvider) {
      return authorizationProvider.canDownload(id, callerIdentity);
    }
  }

  private static final class CanDelete implements Function<AuthorizationProvider, Boolean> {
    private final String callerIdentity;
    private final String id;

    private CanDelete(final String callerIdentity, final String id) {
      this.callerIdentity = callerIdentity;
      this.id = id;
    }

    public Boolean apply(final AuthorizationProvider authorizationProvider) {
      return authorizationProvider.canDelete(id, callerIdentity);
    }
  }

  private Iterable<AuthorizationProvider> authorizationProviders = Lists.newArrayList();
  private boolean readDefault = true, writeDefault = true, deleteDefault = false;

  public void setReadDefault(final boolean readDefault) {
    this.readDefault = readDefault;
  }

  public void setWriteDefault(final boolean writeDefault) {
    this.writeDefault = writeDefault;
  }

  public void setDeleteDefault(final boolean deleteDefault) {
    this.deleteDefault = deleteDefault;
  }

  public void setAuthorizationProviders(final Iterable<AuthorizationProvider> authorizationProviders) {
    this.authorizationProviders = authorizationProviders;
  }

  private boolean decide(final boolean defaultDecision, final Function<AuthorizationProvider, Boolean> decisionFunction) {
    Boolean decision = null;
    for(final AuthorizationProvider authorizationProvider : authorizationProviders) {
      decision = decisionFunction.apply(authorizationProvider);
      if(decision != null) {
        break;
      }
    }
    return decision == null ? defaultDecision : decision;
  }

  public Boolean canDelete(final String id, final String callerIdentity) {
    return decide(deleteDefault, new CanDelete(callerIdentity, id));
  }

  public Boolean canDownload(final String id, final String callerIdentity) {
    return decide(readDefault, new CanDownload(callerIdentity, id));
  }

  public Boolean canUpload(final String id, final String callerIdentity) {
    return decide(writeDefault, new CanUpload(callerIdentity, id));
  }

  public Boolean canDownloadAll(String[] ids, String callerIdentity) {
    return decide(readDefault, new CanDownloadAll(callerIdentity, ids));
  }

}
