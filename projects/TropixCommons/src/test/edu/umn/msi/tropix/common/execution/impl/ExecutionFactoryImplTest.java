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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executor;

import net.jmchilton.concurrent.CountDownLatch;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.concurrent.Interruptable;
import edu.umn.msi.tropix.common.concurrent.InterruptableExecutor;
import edu.umn.msi.tropix.common.concurrent.Timer;
import edu.umn.msi.tropix.common.execution.ExecutionCallback;
import edu.umn.msi.tropix.common.execution.ExecutionConfiguration;
import edu.umn.msi.tropix.common.execution.ExecutionHook;
import edu.umn.msi.tropix.common.execution.process.Process;
import edu.umn.msi.tropix.common.execution.process.ProcessConfiguration;
import edu.umn.msi.tropix.common.execution.process.ProcessFactory;
import edu.umn.msi.tropix.common.io.StreamCopier;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.EasyMockUtils.Reference;

public class ExecutionFactoryImplTest {

  @Test(groups = "unit", timeOut=1000)
  public void setInterruptableExecutor() {
    final ExecutionFactoryImpl factory = new ExecutionFactoryImpl();
    final ProcessFactory processFactory = EasyMock.createMock(ProcessFactory.class);
    final Process process = EasyMock.createMock(Process.class);
    final ExecutionConfiguration executionConfiguration = new ExecutionConfiguration();
    final Reference<ProcessConfiguration> configRef = EasyMockUtils.newReference();
    EasyMock.expect(processFactory.createProcess(EasyMockUtils.record(configRef))).andReturn(process);
    final InterruptableExecutor executor = EasyMock.createMock(InterruptableExecutor.class);
    EasyMock.replay(processFactory, process, executor);
    factory.setProcessFactory(processFactory);
    factory.setInterruptableExecutor(executor);
    final ExecutionHook hook = factory.execute(executionConfiguration, null);
    EasyMockUtils.verifyAndReset(processFactory, process, executor);
    executor.execute(EasyMock.isA(Interruptable.class));
    EasyMock.replay(processFactory, process, executor);
    hook.waitFor();
    EasyMockUtils.verifyAndReset(processFactory, process, executor);
  }

  @Test(groups = "unit", timeOut=1000)
  public void asyncFailQuietly() {
    final ExecutionFactoryImpl factory = new ExecutionFactoryImpl();
    final StreamCopier streamCopier = EasyMock.createMock(StreamCopier.class);
    EasyMock.makeThreadSafe(streamCopier, true);
    factory.setAsyncStreamCopier(streamCopier);

    final InputStream inputStream = new ByteArrayInputStream(new byte[] {});
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    final ExecutionConfiguration executionConfiguration = new ExecutionConfiguration();
    executionConfiguration.setStandardErrorStream(outputStream);
    final ExecutionCallback callback = EasyMock.createMock(ExecutionCallback.class);

    final ProcessFactory processFactory = EasyMock.createMock(ProcessFactory.class);
    final Process process = EasyMock.createMock(Process.class);
    EasyMock.expect(process.getErrorStream()).andReturn(inputStream);
    final Reference<ProcessConfiguration> configRef = EasyMockUtils.newReference();
    EasyMock.expect(processFactory.createProcess(EasyMockUtils.record(configRef))).andReturn(process);
    streamCopier.copy(inputStream, outputStream, true);
    // streamCopier.copy(null, null, false);
    EasyMock.expectLastCall().andThrow(new IllegalStateException());
    EasyMock.replay(processFactory, process, streamCopier);
    factory.setProcessFactory(processFactory);
    factory.execute(executionConfiguration, callback);
    EasyMock.verify(processFactory, process, streamCopier);
    EasyMock.reset(processFactory, process, streamCopier);
  }

  @Test(groups = "unit", timeOut=1000)
  public void testParameters() {
    final ExecutionFactoryImpl factory = new ExecutionFactoryImpl();
    final ExecutionConfiguration executionConfiguration = new ExecutionConfiguration();
    executionConfiguration.setApplication("/bin/sh");
    executionConfiguration.setArguments(Arrays.asList("echo"));
    executionConfiguration.setDirectory(new File(UUID.randomUUID().toString()));
    executionConfiguration.setTimeout(null);
    final Map<String, String> env = new HashMap<String, String>();
    env.put("PATH", "/java");
    executionConfiguration.setEnvironment(env);

    final ProcessFactory processFactory = EasyMock.createMock(ProcessFactory.class);
    final Reference<ProcessConfiguration> configRef = EasyMockUtils.newReference();
    final Process process = EasyMock.createMock(Process.class);
    processFactory.createProcess(EasyMockUtils.record(configRef));
    EasyMock.expectLastCall().andReturn(process);
    EasyMock.replay(processFactory, process);

    factory.setProcessFactory(processFactory);
    final ExecutionHook hook = factory.execute(executionConfiguration, null);

    EasyMock.verify(processFactory, process);

    final ProcessConfiguration processConfig = configRef.get();
    assert processConfig.getApplication().equals(executionConfiguration.getApplication());
    assert processConfig.getArguments().equals(executionConfiguration.getArguments());
    assert processConfig.getDirectory().equals(executionConfiguration.getDirectory());
    assert processConfig.getEnvironment().equals(executionConfiguration.getEnvironment());

    assert hook.getExecutionConfiguration().equals(executionConfiguration);
  }

  @Test(groups = "unit", timeOut=1000)
  public void testTimeout() {
    final ExecutionConfiguration executionConfiguration = new ExecutionConfiguration();
    executionConfiguration.setTimeout(10L);

    final Timer timer = EasyMock.createMock(Timer.class);
    final Reference<Runnable> runnableReference = EasyMockUtils.newReference();
    timer.schedule(EasyMockUtils.record(runnableReference), EasyMock.eq(10L));

    final ProcessFactory processFactory = EasyMock.createMock(ProcessFactory.class);
    final Process process = EasyMock.createMock(Process.class);
    EasyMock.expect(processFactory.createProcess(EasyMock.isA(ProcessConfiguration.class))).andReturn(process);

    final ExecutionFactoryImpl factory = new ExecutionFactoryImpl();
    factory.setTimer(timer);
    factory.setProcessFactory(processFactory);

    EasyMock.replay(timer, processFactory, process);
    final ExecutionHook hook = factory.execute(executionConfiguration, null);
    EasyMock.verify(timer, processFactory, process);
    assert !hook.getTimedOut();
    assert !hook.getKilled();
    EasyMock.reset(timer, processFactory, process);
    process.destroy();
    EasyMock.replay(timer, processFactory, process);
    runnableReference.get().run();
    EasyMock.verify(timer, processFactory, process);
    assert hook.getTimedOut();
    assert hook.getKilled();
  }

  static class WaitingProcess implements Process {
    private InputStream errorStream;
    private InputStream inputStream;
    private OutputStream outputStream;
    private CountDownLatch latch = new CountDownLatch(1);

    public void destroy() {
    }

    public int exitValue() {
      return 0;
    }

    public InputStream getErrorStream() {
      return this.errorStream;
    }

    public InputStream getInputStream() {
      return this.inputStream;
    }

    public OutputStream getOutputStream() {
      return this.outputStream;
    }

    public boolean isComplete() {
      return false;
    }

    public int waitFor() throws InterruptedException {
      latch.await();
      return 0;
    }
  }

  @Test(groups = "unit", timeOut=1000)
  public void testRedirection() throws InterruptedException, IOException {
    final ExecutionConfiguration executionConfiguration = new ExecutionConfiguration();
    final byte[] bytes = "Hello Input!".getBytes();
    final ByteArrayInputStream stdIn = new ByteArrayInputStream(bytes);
    final ByteArrayOutputStream stdOut = new ByteArrayOutputStream();
    final ByteArrayOutputStream stdErr = new ByteArrayOutputStream();
    executionConfiguration.setStandardOutputStream(stdOut);
    executionConfiguration.setStandardErrorStream(stdErr);
    executionConfiguration.setStandardInputStream(stdIn);

    final WaitingProcess waitingProcess = new WaitingProcess();
    final byte[] errorBytes = "Hello Error!".getBytes();
    final byte[] outBytes = "Hello Out!".getBytes();

    waitingProcess.errorStream = new ByteArrayInputStream(errorBytes);
    waitingProcess.inputStream = new ByteArrayInputStream(outBytes);
    final ByteArrayOutputStream processOutputStream = new ByteArrayOutputStream();
    waitingProcess.outputStream = processOutputStream;
    
    final ProcessFactory processFactory = EasyMock.createMock(ProcessFactory.class);
    EasyMock.expect(processFactory.createProcess(EasyMock.isA(ProcessConfiguration.class))).andReturn(waitingProcess);
    EasyMock.replay(processFactory);
    final ExecutionFactoryImpl factory = new ExecutionFactoryImpl();
    factory.setProcessFactory(processFactory);
    factory.execute(executionConfiguration, null);

    Thread.sleep(10);
    waitingProcess.latch.countDown();
    Thread.sleep(10);
    assert new String(stdErr.toByteArray()).equals("Hello Error!");
    assert new String(stdOut.toByteArray()).equals("Hello Out!");
    assert new String(processOutputStream.toByteArray()).equals("Hello Input!");
    EasyMock.verify(processFactory);
  }

  @Test(groups = "unit", timeOut=1000)
  public void testCallback() {
    final ExecutionConfiguration executionConfiguration = new ExecutionConfiguration();

    final ProcessFactory processFactory = EasyMock.createMock(ProcessFactory.class);

    final WaitingProcess process = new WaitingProcess();
    EasyMock.expect(processFactory.createProcess(EasyMock.isA(ProcessConfiguration.class))).andReturn(process);

    final Executor executor = EasyMock.createMock(Executor.class);
    final ExecutionCallback callback = EasyMock.createMock(ExecutionCallback.class);
    @SuppressWarnings("unchecked")
    final Supplier<ExecutionRunnable> executionRunnableSupplier = EasyMock.<Supplier>createMock(Supplier.class);
    final ExecutionRunnable runnable = EasyMock.createMock(ExecutionRunnable.class);
    EasyMock.expect(executionRunnableSupplier.get()).andReturn(runnable);
    final Reference<ExecutionHook> executionHookReference = EasyMockUtils.newReference();
    final Reference<ExecutionCallback> executionCallbackReference = EasyMockUtils.newReference();
    runnable.setExecutionCallback(EasyMockUtils.record(executionCallbackReference));
    runnable.setExecutionHook(EasyMockUtils.record(executionHookReference));
    executor.execute(runnable);
    EasyMock.replay(processFactory, executionRunnableSupplier, runnable, executor, callback);

    final ExecutionFactoryImpl factory = new ExecutionFactoryImpl();
    factory.setProcessFactory(processFactory);
    factory.setExecutionRunnableSupplier(executionRunnableSupplier);
    factory.setExecutor(executor);
    final ExecutionHook executionHook = factory.execute(executionConfiguration, callback);

    EasyMock.verify(processFactory, executionRunnableSupplier, runnable, callback);
    assert callback == executionCallbackReference.get();
    assert executionHookReference.get() == executionHook;
  }
}
