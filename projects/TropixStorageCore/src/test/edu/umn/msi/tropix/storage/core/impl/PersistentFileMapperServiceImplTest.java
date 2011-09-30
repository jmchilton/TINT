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

package edu.umn.msi.tropix.storage.core.impl;

import java.util.UUID;

import javax.inject.Inject;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.storage.core.PersistentFileMapperService;


@ContextConfiguration(locations = "testDatabaseContext.xml")
@TransactionConfiguration(transactionManager = "storageTransactionManager", defaultRollback = true)
public class PersistentFileMapperServiceImplTest  extends AbstractTransactionalTestNGSpringContextTests {
  @Inject
  private PersistentFileMapperService persistentFileMapperService;

  /**
   * Tests that getPath returns null if the path isn't registered
   */
  @Test(groups = "unit")
  public void testGetNullPath() {
    assert null == persistentFileMapperService.getPath(UUID.randomUUID().toString());
  }

  @Test(groups = "unit")
  public void testRegister() {
    final String id = UUID.randomUUID().toString();
    final String path = UUID.randomUUID().toString();
    assert !persistentFileMapperService.pathHasMapping(path);
    persistentFileMapperService.registerMapping(id, path);
    assert persistentFileMapperService.getPath(id).equals(path);
    assert persistentFileMapperService.pathHasMapping(path);
  }
  
}
