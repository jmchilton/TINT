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

import edu.umn.msi.tropix.common.execution.ExecutionCallback;
import edu.umn.msi.tropix.common.execution.ExecutionHook;

public class ExecutionRunnableSupplierImplTest {

  @Test(groups = "unit", timeOut=1000)
  public void normal() {
    final ExecutionRunnableSupplierImpl supplier = new ExecutionRunnableSupplierImpl();

    final ExecutionHook executionHook = EasyMock.createMock(ExecutionHook.class);
    final ExecutionCallback callback = EasyMock.createMock(ExecutionCallback.class);

    executionHook.waitFor();
    callback.onCompletion(executionHook);

    EasyMock.replay(executionHook, callback);
    final ExecutionRunnable runnable = supplier.get();
    runnable.setExecutionCallback(callback);
    runnable.setExecutionHook(executionHook);
    runnable.run();

    EasyMock.verify(executionHook, callback);
  }

  @Test(groups = {"unit"}, expectedExceptions = IllegalStateException.class, timeOut=1000)
  public void waitException() {
    final ExecutionRunnableSupplierImpl supplier = new ExecutionRunnableSupplierImpl();

    final ExecutionHook executionHook = EasyMock.createMock(ExecutionHook.class);
    final ExecutionCallback callback = EasyMock.createMock(ExecutionCallback.class);

    executionHook.waitFor();
    EasyMock.expectLastCall().andThrow(new IllegalStateException());
    callback.onCompletion(executionHook);

    EasyMock.replay(executionHook, callback);
    final ExecutionRunnable runnable = supplier.get();
    runnable.setExecutionCallback(callback);
    runnable.setExecutionHook(executionHook);
    try {
      runnable.run();
    } finally {
      EasyMock.verify(executionHook, callback);
    }
  }

  @Test(groups = {"unit"}, expectedExceptions = IllegalStateException.class, timeOut=1000)
  public void completionException() {
    final ExecutionRunnableSupplierImpl supplier = new ExecutionRunnableSupplierImpl();

    final ExecutionHook executionHook = EasyMock.createMock(ExecutionHook.class);
    final ExecutionCallback callback = EasyMock.createMock(ExecutionCallback.class);

    executionHook.waitFor();
    callback.onCompletion(executionHook);
    EasyMock.expectLastCall().andThrow(new IllegalStateException());

    EasyMock.replay(executionHook, callback);
    final ExecutionRunnable runnable = supplier.get();
    runnable.setExecutionCallback(callback);
    runnable.setExecutionHook(executionHook);
    try {
      runnable.run();
    } finally {
      EasyMock.verify(executionHook, callback);
    }
  }

}