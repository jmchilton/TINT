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

package edu.umn.msi.tropix.grid.impl;

import java.io.File;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.grid.credentials.GlobusCredentialFactories;
import edu.umn.msi.tropix.grid.credentials.GlobusCredentialFactory;

@ManagedResource
public class FileProxySupplierImpl implements Supplier<Credential> {
  private GlobusCredentialFactory globusCredentialFactory = GlobusCredentialFactories.getInstance();
  private Credential proxy = null;
  private String keyPath;
  private String certificatePath;
  private boolean verify = false;

  @VisibleForTesting
  void setGlobusCredentialFactory(final GlobusCredentialFactory globusCredentialFactory) {
    this.globusCredentialFactory = globusCredentialFactory;
  }
  
  @ManagedAttribute
  public synchronized String getKeyPath() {
    return keyPath;
  }

  @ManagedAttribute
  public synchronized String getCertificatePath() {
    return certificatePath;
  }

  @ManagedAttribute
  public boolean isVerify() {
    return verify;
  }

  @ManagedAttribute
  public void setCertificatePath(final String certificatePath) {
    this.certificatePath = certificatePath;
    proxy = null; // Be sure proxy is reset
  }

  @ManagedAttribute
  public void setKeyPath(final String keyPath) {
    this.keyPath = keyPath;
    proxy = null;
  }

  // If you wish to update both from JMX, this method should be used so get doesn't get called
  // why they are in an inconsistent state.
  @ManagedOperation
  public void setCertificateAndKeyPath(final String certificatePath, final String keyPath) {
    this.certificatePath = certificatePath;
    this.keyPath = keyPath;
    proxy = null;
  }

  @ManagedAttribute
  public void setVerify(final boolean verify) {
    this.verify = verify;
  }

  public Credential get() {
    if(proxy == null && new File(certificatePath).exists() && new File(keyPath).exists()) {
      try {
        proxy = Credentials.get(globusCredentialFactory.get(certificatePath, keyPath));
      } catch(final Exception e) { 
        throw ExceptionUtils.convertException(e, "Failed to create globus proxy");
      }

      if(verify) {
        try {
          proxy.getGlobusCredential().verify();
        } catch(final Exception e) {
          proxy = null;
          throw ExceptionUtils.convertException(e, "Credential verification failed");
        }
      }
    }
    return proxy;
  }

}
