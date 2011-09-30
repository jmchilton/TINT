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

import java.io.InputStream;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.grid.credentials.GlobusCredentialFactory;

public class FileProxySupplierImplTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance(); 
  
  /*
   * Tracks its inputs and outputs and generates a new GlobusCredential 
   * each time its called.
   */
  class GlobusCredentialFactoryImpl implements GlobusCredentialFactory {
    private List<String> certificatePaths = Lists.newArrayList();
    private List<String> keyPaths = Lists.newArrayList();
    private List<GlobusCredential> credentials = Lists.newArrayList();
    
    public GlobusCredential get(final InputStream inputStream) throws Exception {
      throw new UnsupportedOperationException();
    }
    
    private boolean exception = false;
    private boolean verify = false;
    private boolean verifyException = false;
    
    public GlobusCredential get(final String certificatePath, final String keyPath) throws Exception {
      certificatePaths.add(certificatePath);
      keyPaths.add(keyPath);
      final GlobusCredential credential = EasyMock.createMock(GlobusCredential.class);
      credentials.add(credential);
      if(!exception && verify) {
        credential.verify();
        if(verifyException) {
          EasyMock.expectLastCall().andThrow(EasyMock.createMock(GlobusCredentialException.class));
        }
      }
      EasyMock.replay(credential);
      if(exception) {
        System.out.println("Throwing exception");
        throw new Exception();
      }
      return credential;
    }
    
  }
  
  @Test(groups = "unit")
  public void testPaths() {
    final FileProxySupplierImpl supplier = new FileProxySupplierImpl();
    assert !supplier.isVerify();
    final GlobusCredentialFactoryImpl globusCredentialFactory = new GlobusCredentialFactoryImpl();
    supplier.setGlobusCredentialFactory(globusCredentialFactory);
    try {
      String keyPath = FILE_UTILS.createTempFile().getAbsolutePath();
      String certPath = FILE_UTILS.createTempFile().getAbsolutePath();
      supplier.setCertificateAndKeyPath(certPath, keyPath);
      assert supplier.getKeyPath().equals(keyPath);
      assert supplier.getCertificatePath().equals(certPath);

      assert supplier.get().getGlobusCredential() == globusCredentialFactory.credentials.get(0);
      assert globusCredentialFactory.keyPaths.contains(keyPath);
      assert globusCredentialFactory.certificatePaths.contains(certPath);

      
      // Set individually
      keyPath = FILE_UTILS.createTempFile().getAbsolutePath();
      certPath = FILE_UTILS.createTempFile().getAbsolutePath();
      supplier.setKeyPath(keyPath);
      supplier.setCertificatePath(certPath);

      assert supplier.getKeyPath().equals(keyPath);
      assert supplier.getCertificatePath().equals(certPath);
      
      // Assert updated
      assert supplier.get().getGlobusCredential() == globusCredentialFactory.credentials.get(1);
      assert globusCredentialFactory.keyPaths.contains(keyPath);
      assert globusCredentialFactory.certificatePaths.contains(certPath);

      // Assert cached
      assert supplier.get().getGlobusCredential() == globusCredentialFactory.credentials.get(1);
      
      globusCredentialFactory.verify = true;
      supplier.setVerify(true);
      
      keyPath = FILE_UTILS.createTempFile().getAbsolutePath();
      certPath = FILE_UTILS.createTempFile().getAbsolutePath();
      supplier.setKeyPath(keyPath);
      supplier.setCertificatePath(certPath);

      assert supplier.get().getGlobusCredential() == globusCredentialFactory.credentials.get(2);
      assert globusCredentialFactory.keyPaths.contains(keyPath);
      assert globusCredentialFactory.certificatePaths.contains(certPath);
      EasyMock.verify(globusCredentialFactory.credentials.get(2));
      
      globusCredentialFactory.exception = true;
      keyPath = FILE_UTILS.createTempFile().getAbsolutePath();
      certPath = FILE_UTILS.createTempFile().getAbsolutePath();
      supplier.setKeyPath(keyPath);
      supplier.setCertificatePath(certPath);
      try {
        supplier.get().getGlobusCredential();
        assert false; // Ensure exception is thrown
      } catch(RuntimeException e) {
        assert true;
      }      
      
      globusCredentialFactory.exception = false;
      globusCredentialFactory.verifyException = true;      
      keyPath = FILE_UTILS.createTempFile().getAbsolutePath();
      certPath = FILE_UTILS.createTempFile().getAbsolutePath();
      supplier.setKeyPath(keyPath);
      supplier.setCertificatePath(certPath);
      try {
        supplier.get().getGlobusCredential();
        assert false; // Ensure exception is thrown
      } catch(RuntimeException e) {
        assert true;
      }
      EasyMock.verify(globusCredentialFactory.credentials.get(globusCredentialFactory.credentials.size()-1));      
    } finally {
      for(final String filePath : Iterables.concat(globusCredentialFactory.certificatePaths, globusCredentialFactory.keyPaths)) {
        FILE_UTILS.deleteQuietly(filePath);
      }
    }
  }
  
}
