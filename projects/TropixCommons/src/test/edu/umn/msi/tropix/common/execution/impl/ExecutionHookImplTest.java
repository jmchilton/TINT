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

package edu.umn.msi.tropix.common.execution.impl;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.concurrent.Interruptable;
import edu.umn.msi.tropix.common.concurrent.InterruptableExecutor;
import edu.umn.msi.tropix.common.execution.ExecutionConfiguration;
import edu.umn.msi.tropix.common.execution.process.Process;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.EasyMockUtils.Reference;

// TODO: Move these into ExecutionFactoryImplTest and make ExecutionHookImpl package visible again
public class ExecutionHookImplTest {

  @Test(groups = "unit", timeOut=1000)
  public void waitFor() throws InterruptedException {
    final ExecutionHookImpl hook = new ExecutionHookImpl();
    final InterruptableExecutor interruptableExecutor = EasyMock.createMock(InterruptableExecutor.class);
    final Process process = EasyMock.createMock(Process.class);
    final Reference<Interruptable> interruptableReference = EasyMockUtils.newReference();
    interruptableExecutor.execute(EasyMockUtils.record(interruptableReference));
    hook.setInterruptableExecutor(interruptableExecutor);
    hook.setProcess(process);
    EasyMock.replay(interruptableExecutor, process);
    hook.waitFor();
    EasyMock.verify(interruptableExecutor, process);
    EasyMock.reset(process);
    EasyMock.expect(process.waitFor()).andReturn(5);
    EasyMock.replay(process);
    interruptableReference.get().run();
    EasyMock.verify(process);
    assert hook.getException() == null;
  }

  @Test(groups = "unit", timeOut=1000)
  public void executionConfiguration() {
    final ExecutionConfiguration configuration = new ExecutionConfiguration();
    final ExecutionHookImpl hook = new ExecutionHookImpl();
    hook.setExecutionConfiguration(configuration);
    assert hook.getExecutionConfiguration() == configuration;
  }

  @Test(groups = "unit", timeOut=1000)
  public void waitForException() {
    final ExecutionHookImpl hook = new ExecutionHookImpl();
    final InterruptableExecutor interruptableExecutor = EasyMock.createMock(InterruptableExecutor.class);
    interruptableExecutor.execute(EasyMock.isA(Interruptable.class));
    EasyMock.expectLastCall().andThrow(new IllegalStateException());
    EasyMock.replay(interruptableExecutor);
    hook.setInterruptableExecutor(interruptableExecutor);
    Exception exception = null;
    try {
      hook.waitFor();
    } catch(final Exception iException) {
      exception = iException;
    }
    assert exception != null;
    assert hook.getException().equals(exception);
    EasyMock.verify(interruptableExecutor);
  }

  @Test(groups = "unit", timeOut=1000)
  public void timeout() {
    final ExecutionHookImpl hook = new ExecutionHookImpl();
    final Process process = EasyMock.createMock(Process.class);
    hook.setProcess(process);
    process.destroy();
    EasyMock.replay(process);
    hook.timeout();
    assert hook.getKilled();
    assert hook.getTimedOut();
    EasyMock.verify(process);
  }

  @Test(groups = "unit", timeOut=1000)
  public void isComplete() {
    final ExecutionHookImpl hook = new ExecutionHookImpl();
    final Process process = EasyMock.createMock(Process.class);
    hook.setProcess(process);
    EasyMock.expect(process.isComplete()).andReturn(false);
    EasyMock.replay(process);
    assert !hook.getComplete();
    EasyMock.verify(process);
    EasyMock.reset(process);
    EasyMock.expect(process.isComplete()).andReturn(true);
    EasyMock.replay(process);
    assert hook.getComplete();
    EasyMock.verify(process);
  }

  @Test(groups = "unit", timeOut=1000)
  public void kill() {
    final ExecutionHookImpl hook = new ExecutionHookImpl();
    final Process process = EasyMock.createMock(Process.class);
    hook.setProcess(process);
    process.destroy();
    EasyMock.replay(process);
    hook.kill();
    assert hook.getKilled();
    assert !hook.getTimedOut();
    EasyMock.verify(process);
  }

  @Test(groups = "unit", timeOut=1000)
  public void returnValue() {
    final ExecutionHookImpl hook = new ExecutionHookImpl();
    final Process process = EasyMock.createMock(Process.class);
    hook.setProcess(process);
    EasyMock.expect(process.exitValue()).andReturn(7);
    EasyMock.replay(process);
    assert hook.getReturnValue() == 7;
    EasyMock.verify(process);
  }

}
