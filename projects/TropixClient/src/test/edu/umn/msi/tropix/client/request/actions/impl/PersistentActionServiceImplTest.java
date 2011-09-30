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

package edu.umn.msi.tropix.client.request.actions.impl;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

@ContextConfiguration(locations = { "classpath:edu/umn/msi/tropix/client/request/actions/impl/testDatabaseContext.xml" })
@TransactionConfiguration(transactionManager = "requestActionTransactionManager", defaultRollback = true)
public class PersistentActionServiceImplTest extends AbstractTransactionalTestNGSpringContextTests {
  @Autowired
  private PersistentActionService persistentActionService;

  @Test(groups = "persistence")
  public void allOps() {
    assert persistentActionService.loadActions().isEmpty();
    final UpdateRequestAction action = new UpdateRequestAction(123443L, "http://request.com", UUID.randomUUID().toString(), "COMPLETE");
    persistentActionService.persistRequestAction(action);
    assert Iterables.getOnlyElement(persistentActionService.loadActions()).getActionId() == 123443L;
    final UploadRequestAction uploadAction = new UploadRequestAction(123L, "http://moo", UUID.randomUUID().toString(), "123", "345", "456");
    persistentActionService.persistRequestAction(uploadAction);
    assert persistentActionService.loadActions().size() == 2;
    persistentActionService.completeAction(123443L);
    assert Iterables.getOnlyElement(persistentActionService.loadActions()).getActionId() == 123L;
    persistentActionService.completeAction(123L);
    assert Iterables.isEmpty(persistentActionService.loadActions());
  }

}
