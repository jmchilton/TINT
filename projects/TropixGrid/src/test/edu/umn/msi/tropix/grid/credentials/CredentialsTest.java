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

package edu.umn.msi.tropix.grid.credentials;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import org.easymock.EasyMock;
import org.globus.gsi.GlobusCredential;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.EasyMockUtils;

public class CredentialsTest {

  @Test(groups = "unit")
  public void serialization() {
    final Credential m1 = Credentials.getMock(), m2 = Credentials.getMock();
    assert Credentials.fromString(m1.toString()).equals(m1);
    assert Credentials.fromString(m2.toString()).equals(m2);
    assert !Credentials.fromString(m1.toString()).equals(m2);
    assert !Credentials.fromString(m2.toString()).equals(m1);
  }

  @Test(groups = "unit")
  public void differWithSameIdents() {
    final Credential m1 = Credentials.getMock("moo"), m2 = Credentials.getMock("moo");
    assert !m1.equals(m2);
    assert m1.hashCode() != m2.hashCode();
    assert Credentials.fromString(m2.toString()).equals(m2);
    assert !Credentials.fromString(m2.toString()).equals(m1);
    assert m1.getIdentity().equals("moo");
  }

  @Test(groups = "unit")
  public void equalsAndHashCode() {
    final Credential m1 = Credentials.getMock(), m2 = Credentials.getMock();
    assert m1.equals(m1);
    assert !m1.equals(m2);
    assert m1.hashCode() != m2.hashCode();
  }
  
  @Test(groups = "unit")
  public void getTimeLeft() throws InterruptedException {
    final Credential c1 = Credentials.getMock(UUID.randomUUID().toString(), 3); // 3 Seconds
    Thread.sleep(5);  // Wait 5 milliseconds, time left should be 2 or 3 seconds
    assert (1 < c1.getTimeLeft()) && (c1.getTimeLeft() < 4);
  }
  
  @Test(groups = "unit")
  public void getGlobusCrednetialIsNull() throws InterruptedException {
    final Credential c1 = Credentials.getMock();
    assert c1.getGlobusCredential() == null;
  }

  @Test(groups = "unit", expectedExceptions=RuntimeException.class)
  public void testInvalidCredentialThrowsException() {
    Credentials.fromString("this is not a valid credential");
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void testGlobusSerializationException() throws IOException {
    final GlobusCredential globusCredential = EasyMock.createMock(GlobusCredential.class);
    final Credential credential = Credentials.get(globusCredential);
    globusCredential.save(EasyMock.isA(OutputStream.class));
    EasyMock.expectLastCall().andThrow(new IOException());
    EasyMock.replay(globusCredential);
    credential.toString();    
  }
  
  @Test(groups = "unit")
  public void testGlobusMethods() throws IOException {
    final GlobusCredential globusCredential = EasyMock.createMock(GlobusCredential.class);
    final GlobusCredential globusCredential2 = EasyMock.createMock(GlobusCredential.class);
    final Credential credential = Credentials.get(globusCredential);
    
    EasyMock.expect(globusCredential.getTimeLeft()).andReturn(100L);
    EasyMock.replay(globusCredential);
    assert credential.getTimeLeft() == 100L; 
    EasyMock.reset(globusCredential);
    
    EasyMock.expect(globusCredential.getIdentity()).andReturn("moo cow");
    EasyMock.replay(globusCredential);
    assert credential.getIdentity().equals("moo cow"); 
    EasyMock.reset(globusCredential);

    assert credential.hashCode() == globusCredential.hashCode(); 

    assert credential.equals(Credentials.get(globusCredential));
    assert !credential.equals(Credentials.get(globusCredential2));
    assert !credential.equals(Credentials.getMock());    
    assert !credential.equals(new Object());
    
    globusCredential.save(EasyMockUtils.copy(new ByteArrayInputStream("hello!".getBytes())));
    EasyMock.replay(globusCredential);
    assert credential.toString().equals("hello!"); 
    EasyMock.reset(globusCredential);
    
  }
  
  
}
