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

import info.minnesotapartnership.tropix.request.models.RequestStatus;
import info.minnesotapartnership.tropix.request.models.ServiceResult;

import java.util.Arrays;
import java.util.LinkedList;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;

import edu.umn.msi.tropix.common.concurrent.GrouppedExecutor;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.EasyMockUtils.Reference;
import edu.umn.msi.tropix.common.test.MockObjectCollection;

public class PersistentActionManagerImplTest {
  private PersistentActionManagerImpl actionManager;
  private GrouppedExecutor<String> grouppedExecutor;
  private PersistentActionService persistentActionService;
  private Function<RequestAction, Runnable> requestActionToRunnableFunction;
  private Runnable runnable;
  private MockObjectCollection mockObjects;

  @BeforeMethod(groups = "unit")
  public void init() {
    this.actionManager = new PersistentActionManagerImpl();
    this.grouppedExecutor = EasyMock.createMock(GrouppedExecutor.class);
    this.persistentActionService = EasyMock.createMock(PersistentActionService.class);
    this.requestActionToRunnableFunction = EasyMockUtils.createMockFunction();
    this.runnable = EasyMock.createMock(Runnable.class);

    this.mockObjects = MockObjectCollection.fromObjects(grouppedExecutor, persistentActionService, requestActionToRunnableFunction, runnable);
    this.actionManager.setGrouppedExecutor(this.grouppedExecutor);
    this.actionManager.setPersistentActionService(this.persistentActionService);
    this.actionManager.setRequestActionToRunnableFunction(this.requestActionToRunnableFunction);
  }

  @Test(groups = "unit")
  public void uploadAndPersit() {
    upload(false);
  }

  @Test(groups = "unit")
  public void uploadAndPersitProblems() {
    upload(true);
  }

  @Test(groups = "unit")
  public void updateAndPersit() {
    update(false);
  }

  @Test(groups = "unit")
  public void updateAndPersitProblems() {
    update(true);
  }

  private static class MockRequestAction extends RequestAction {
    MockRequestAction(final long id) {
      super(id, "s" + id, "id" + id);
    }
  }

  @Test(groups = "unit")
  public void savedActions() {
    final MockRequestAction a1 = new MockRequestAction(1L), a2 = new MockRequestAction(2L), a3 = new MockRequestAction(3L);
    final Runnable r1 = EasyMock.createMock(Runnable.class), r2 = EasyMock.createMock(Runnable.class), r3 = EasyMock.createMock(Runnable.class);

    EasyMock.expect(this.persistentActionService.loadActions()).andReturn(Arrays.<RequestAction>asList(a2, a3, a1));

    EasyMock.expect(this.requestActionToRunnableFunction.apply(a1)).andReturn(r1);
    EasyMock.expect(this.requestActionToRunnableFunction.apply(a2)).andReturn(r2);
    EasyMock.expect(this.requestActionToRunnableFunction.apply(a3)).andReturn(r3);

    this.grouppedExecutor.execute("id1@s1", r1);
    this.grouppedExecutor.execute("id2@s2", r2);
    this.grouppedExecutor.execute("id3@s3", r3);

    this.mockObjects.replay();

    this.actionManager.init();

    this.mockObjects.verifyAndReset();

    this.persistentActionService.persistRequestAction(EasyMock.isA(UpdateRequestAction.class));
    final Reference<RequestAction> actionReference = EasyMockUtils.newReference();
    EasyMock.expect(this.requestActionToRunnableFunction.apply(EasyMockUtils.record(actionReference))).andReturn(runnable);
    this.grouppedExecutor.execute("r123@service", runnable);

    this.mockObjects.replay();
    this.actionManager.update("service", "r123", RequestStatus.COMPLETE);
    this.mockObjects.verifyAndReset();

    assert actionReference.get().getActionId() == 4;
  }

  private void upload(final boolean exception) {
    final ServiceResult result = new ServiceResult();
    result.setName("Moo");

    EasyMock.expect(this.persistentActionService.loadActions()).andReturn(new LinkedList<RequestAction>());
    this.persistentActionService.persistRequestAction(EasyMock.isA(UploadRequestAction.class));
    if(exception) {
      EasyMock.expectLastCall().andThrow(new RuntimeException());
    }
    EasyMock.expect(this.requestActionToRunnableFunction.apply(EasyMock.isA(UploadRequestAction.class))).andReturn(runnable);
    this.grouppedExecutor.execute("r123@service", runnable);
    this.mockObjects.replay();
    this.actionManager.init();
    this.actionManager.upload("service", "r123", "f123", "sservice", result);
    this.mockObjects.verifyAndReset();
  }

  private void update(final boolean exception) {
    EasyMock.expect(this.persistentActionService.loadActions()).andReturn(new LinkedList<RequestAction>());
    this.persistentActionService.persistRequestAction(EasyMock.isA(UpdateRequestAction.class));
    if(exception) {
      EasyMock.expectLastCall().andThrow(new RuntimeException());
    }
    EasyMock.expect(this.requestActionToRunnableFunction.apply(EasyMock.isA(UpdateRequestAction.class))).andReturn(runnable);
    this.grouppedExecutor.execute("r123@service", runnable);
    this.mockObjects.replay();
    this.actionManager.init();
    this.actionManager.update("service", "r123", RequestStatus.COMPLETE);
    this.mockObjects.verifyAndReset();
  }

}
