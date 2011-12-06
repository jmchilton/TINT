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

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.concurrent.Executors;
import edu.umn.msi.tropix.common.concurrent.InterruptableExecutor;
import edu.umn.msi.tropix.common.concurrent.InterruptableExecutors;
import edu.umn.msi.tropix.common.concurrent.Timer;
import edu.umn.msi.tropix.common.concurrent.Timers;
import edu.umn.msi.tropix.common.execution.ExecutionCallback;
import edu.umn.msi.tropix.common.execution.ExecutionConfiguration;
import edu.umn.msi.tropix.common.execution.ExecutionFactory;
import edu.umn.msi.tropix.common.execution.ExecutionHook;
import edu.umn.msi.tropix.common.execution.process.Process;
import edu.umn.msi.tropix.common.execution.process.ProcessFactory;
import edu.umn.msi.tropix.common.execution.process.Processes;
import edu.umn.msi.tropix.common.io.StreamCopier;
import edu.umn.msi.tropix.common.io.StreamCopiers;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;

public class ExecutionFactoryImpl implements ExecutionFactory {
  private static final Log LOG = LogFactory.getLog(ExecutionFactoryImpl.class);
  private Timer timer = Timers.getDefault();
  private InterruptableExecutor interruptableExecutor = InterruptableExecutors.getDefault();
  private Executor executor = Executors.getNewThreadExecutor();
  private StreamCopier asyncStreamCopier = StreamCopiers.getDefaultAsyncStreamCopier();
  private Supplier<ExecutionRunnable> executionRunnableSupplier = new ExecutionRunnableSupplierImpl();
  private ProcessFactory processFactory = Processes.getDefaultFactory();

  public ExecutionHook execute(final ExecutionConfiguration executionConfiguration, final ExecutionCallback executionCallback) {
    final Process process = this.processFactory.createProcess(executionConfiguration);
    final ExecutionHookImpl executionHook = this.buildExecutionHook(executionConfiguration, process);
    this.setupTimeout(executionHook);
    this.setupCallback(executionHook, executionCallback);
    this.setupRedirection(executionConfiguration, process);
    return executionHook;
  }

  private void setupRedirection(final ExecutionConfiguration executionConfiguration, final Process process) {
    if(executionConfiguration.getStandardInputStream() != null) {
      this.redirect(executionConfiguration.getStandardInputStream(), process.getOutputStream(), "Failed to redirect standard input.");
    }
    if(executionConfiguration.getStandardOutputStream() != null) {
      this.redirect(process.getInputStream(), executionConfiguration.getStandardOutputStream(), "Failed to redirect standard output.");
    }
    if(executionConfiguration.getStandardErrorStream() != null) {
      this.redirect(process.getErrorStream(), executionConfiguration.getStandardErrorStream(), "Failed to redirect standard error.");
    }
  }

  private void redirect(final InputStream inputStream, final OutputStream outputStream, final String errorMessage) {
    if(inputStream != null && outputStream != null) {
      try {
        this.asyncStreamCopier.copy(inputStream, outputStream, true);
      } catch(final Throwable t) {
        ExceptionUtils.logQuietly(ExecutionFactoryImpl.LOG, t, "Failed to execute asynchronous stream copy.");
      }
    }
  }

  private ExecutionHookImpl buildExecutionHook(final ExecutionConfiguration executionConfiguration, final Process process) {
    final ExecutionHookImpl executionHook = new ExecutionHookImpl();
    executionHook.setExecutionConfiguration(executionConfiguration);
    executionHook.setProcess(process);
    executionHook.setInterruptableExecutor(this.interruptableExecutor);
    return executionHook;
  }

  private void setupCallback(final ExecutionHookImpl executionHook, final ExecutionCallback executionCallback) {
    if(executionCallback != null) {
      final ExecutionRunnable runnable = this.executionRunnableSupplier.get();
      runnable.setExecutionCallback(executionCallback);
      runnable.setExecutionHook(executionHook);
      this.executor.execute(runnable);
    }
  }

  private void setupTimeout(final ExecutionHookImpl executionHook) {
    final Long timeout = executionHook.getExecutionConfiguration().getTimeout();
    if(timeout != null && timeout >= 0L) {
      this.timer.schedule(new Runnable() {
        public void run() {
          executionHook.timeout();
        }
      }, timeout);
    }
  }

  public void setTimer(final Timer timer) {
    this.timer = timer;
  }

  public void setInterruptableExecutor(final InterruptableExecutor interruptableExecutor) {
    this.interruptableExecutor = interruptableExecutor;
  }

  public void setExecutor(final Executor executor) {
    this.executor = executor;
  }

  public void setAsyncStreamCopier(final StreamCopier asyncStreamCopier) {
    this.asyncStreamCopier = asyncStreamCopier;
  }

  public void setExecutionRunnableSupplier(final Supplier<ExecutionRunnable> executionRunnableSupplier) {
    this.executionRunnableSupplier = executionRunnableSupplier;
  }

  public void setProcessFactory(final ProcessFactory processFactory) {
    this.processFactory = processFactory;
  }

}
