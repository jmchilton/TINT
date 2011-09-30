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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.execution.ExecutionCallback;
import edu.umn.msi.tropix.common.execution.ExecutionHook;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;

class ExecutionRunnableSupplierImpl implements Supplier<ExecutionRunnable> {
  private static final Log LOG = LogFactory.getLog(ExecutionRunnableSupplierImpl.class);

  public ExecutionRunnable get() {
    return new ExecutionRunnableImpl();
  }

  static class ExecutionRunnableImpl implements ExecutionRunnable {
    private ExecutionCallback executionCallback;
    private ExecutionHook executionHook;

    public void run() {
      RuntimeException exception = null;
      try {
        this.executionHook.waitFor();
      } catch(final RuntimeException runtimeException) {
        exception = runtimeException;
        ExceptionUtils.logQuietly(LOG, runtimeException, "Exception thrown while executing waitFor on ExecutionHook " + executionHook);
      }

      try {
        this.executionCallback.onCompletion(this.executionHook);
      } catch(final RuntimeException runtimeException) {
        exception = runtimeException;
        ExceptionUtils.logQuietly(LOG, runtimeException, "Exception where executing onCompletion on callback " + executionCallback);
      }

      if(exception != null) {
        throw exception;
      }
    }

    public void setExecutionCallback(final ExecutionCallback executionCallback) {
      this.executionCallback = executionCallback;
    }

    public void setExecutionHook(final ExecutionHook executionHook) {
      this.executionHook = executionHook;
    }
  }

}
