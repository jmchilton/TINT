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

import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.storage.core.authorization.AuthorizationProvider;

public class IterableAuthorizationProviderImpl implements AuthorizationProvider {
  private Iterable<String> canReadIds, canWriteIds, canDeleteIds;
  private Boolean inPermission = true, outPermission = null;

  public void setInPermission(final Boolean inPermission) {
    this.inPermission = inPermission;
  }

  public void setOutPermission(final Boolean outPermission) {
    this.outPermission = outPermission;
  }

  public void setCanReadIds(final Iterable<String> canReadIds) {
    this.canReadIds = canReadIds;
  }

  public void setCanWriteIds(final Iterable<String> canWriteIds) {
    this.canWriteIds = canWriteIds;
  }

  public void setCanDeleteIds(final Iterable<String> canDeleteIds) {
    this.canDeleteIds = canDeleteIds;
  }

  public Boolean canDelete(final String id, final String callerIdentity) {
    if(Iterables.contains(canDeleteIds, callerIdentity)) {
      return inPermission;
    } else {
      return outPermission;
    }
  }

  public Boolean canDownload(final String id, final String callerIdentity) {
    return canRead(callerIdentity);
  }

  private Boolean canRead(final String callerIdentity) {
    if(Iterables.contains(canReadIds, callerIdentity)) {
      return inPermission;
    } else {
      return outPermission;
    }
  }

  public Boolean canUpload(final String id, final String callerIdentity) {
    if(Iterables.contains(canWriteIds, callerIdentity)) {
      return inPermission;
    } else {
      return outPermission;
    }
  }

  public Boolean canDownloadAll(String[] ids, String callerIdentity) {
    return canRead(callerIdentity);
  }

}
