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

package edu.umn.msi.tropix.grid.credentials.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.globus.gsi.GlobusCredential;

import edu.umn.msi.tropix.grid.credentials.Credential;

public class CredentialImpl implements Credential, Serializable {
  @Nonnull private final GlobusCredential proxy;

  public CredentialImpl(@Nonnull final GlobusCredential proxy) {
    this.proxy = proxy;
  }

  @Nonnull
  public GlobusCredential getGlobusCredential() {
    return proxy;
  }

  public String getIdentity() {
    return proxy.getIdentity();
  }

  @Override
  public int hashCode() {    
    return proxy.hashCode();
  }

  @Override
  public boolean equals(@Nullable final Object obj) {
    boolean equal = false;
    if(obj instanceof CredentialImpl) {
      final CredentialImpl other = (CredentialImpl) obj;
      equal = other.getGlobusCredential().equals(proxy);
    }
    return equal;
  }

  public byte[] toBytes() {
    try {
      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      proxy.save(outputStream);
      return outputStream.toByteArray();
    } catch(final IOException e) {
      throw new IllegalArgumentException("Failed to serialize GlobusCredential", e);
    }
  }
  
  @Override
  public String toString() {
    return new String(toBytes());
  }

  /**
   * @see edu.umn.msi.tropix.grid.credentials.Credential#getTimeLeft()
   * @return Time left in seconds.
   */  
  public long getTimeLeft() {
    return proxy.getTimeLeft();
  }

}
