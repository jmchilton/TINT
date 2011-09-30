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

package edu.umn.msi.tropix.client.credential;

import javax.annotation.Nullable;

public class GlobusCredentialOptions {
  private Integer delegationPathLength = null;
  private Long lifetime = null; // Lifetime measured in seconds
  private String idpUrl = null;
  private String ifsUrl = null;

  public Integer getDelegationPathLength() {
    return delegationPathLength;
  }

  public void setDelegationPathLength(@Nullable final Integer delegationPathLength) {
    this.delegationPathLength = delegationPathLength;
  }

  public Long getLifetime() {
    return lifetime;
  }

  public void setLifetime(@Nullable final Long lifetime) {
    this.lifetime = lifetime;
  }

  public String getIdpUrl() {
    return idpUrl;
  }

  public void setIdpUrl(@Nullable final String idpUrl) {
    this.idpUrl = idpUrl;
  }

  public String getIfsUrl() {
    return ifsUrl;
  }

  public void setIfsUrl(@Nullable final String ifsUrl) {
    this.ifsUrl = ifsUrl;
  }
}
