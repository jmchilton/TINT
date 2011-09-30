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

import edu.umn.msi.tropix.common.concurrent.Interruptable;
import edu.umn.msi.tropix.common.concurrent.InterruptableExecutor;
import edu.umn.msi.tropix.common.execution.ExecutionConfiguration;
import edu.umn.msi.tropix.common.execution.ExecutionHook;
import edu.umn.msi.tropix.common.execution.process.Process;

class ExecutionHookImpl implements ExecutionHook {
  // Managed internally
  private boolean killed = false;
  private boolean timedout = false;
  private RuntimeException runtimeException = null;

  // Must be set
  private Process process = null;
  private ExecutionConfiguration executionConfiguration = null;
  private InterruptableExecutor interruptableExecutor = null;

  public boolean getComplete() {
    return this.process.isComplete();
  }

  public ExecutionConfiguration getExecutionConfiguration() {
    return this.executionConfiguration;
  }

  public boolean getKilled() {
    return this.killed;
  }

  public int getReturnValue() {
    return this.process.exitValue();
  }

  public boolean getTimedOut() {
    return this.timedout;
  }

  public RuntimeException getException() {
    return this.runtimeException;
  }

  public void timeout() {
    this.timedout = true;
    this.kill();
  }

  public void kill() {
    this.killed = true;
    this.process.destroy();
  }

  public void waitFor() {
    try {
      this.interruptableExecutor.execute(new Interruptable() {
        public void run() throws InterruptedException {
          ExecutionHookImpl.this.process.waitFor();
        }
      });
    } catch(final RuntimeException runtimeException) {
      this.runtimeException = runtimeException;
      throw runtimeException;
    }
  }

  public void setProcess(final Process process) {
    this.process = process;
  }

  public void setInterruptableExecutor(final InterruptableExecutor interruptableExecutor) {
    this.interruptableExecutor = interruptableExecutor;
  }

  public void setExecutionConfiguration(final ExecutionConfiguration executionConfiguration) {
    this.executionConfiguration = executionConfiguration;
  }
}
