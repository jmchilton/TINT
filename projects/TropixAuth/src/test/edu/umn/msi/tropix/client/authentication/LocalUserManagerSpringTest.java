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

package edu.umn.msi.tropix.client.authentication;

import java.util.UUID;

import javax.annotation.Resource;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.FreshConfigTest;

@ContextConfiguration(locations = "classpath:edu/umn/msi/tropix/client/authentication/context.xml")
public class LocalUserManagerSpringTest extends FreshConfigTest {

  @Resource
  private LocalUserManager localUserManager;
  
  @Test(groups = "spring")
  public void createTest() {
    final String newId = UUID.randomUUID().toString();
    localUserManager.createUser(newId, "newpassword");
    
    assert localUserManager.userExists(newId);
    
    assert localUserManager.isUsersPassword(newId, "newpassword");
    assert !localUserManager.isUsersPassword(newId, "newpasswordx");
    
    localUserManager.changePassword(newId, "passmoo");
    assert !localUserManager.isUsersPassword(newId, "newpassword");
    assert !localUserManager.isUsersPassword(newId, "newpasswordx");
    assert localUserManager.isUsersPassword(newId, "passmoo");    
    
  }
  
}
