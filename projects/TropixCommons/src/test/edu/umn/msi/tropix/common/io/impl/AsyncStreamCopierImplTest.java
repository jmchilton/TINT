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

package edu.umn.msi.tropix.common.io.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.Executor;

import org.apache.commons.io.output.ProxyOutputStream;
import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.EasyMockUtils.Reference;

public class AsyncStreamCopierImplTest {

  @Test(groups = "unit", timeOut=1000)
  public void mockTests() {
    this.mockTest(true, true);
    this.mockTest(true, false);
    this.mockTest(false, true);
    this.mockTest(false, false);
  }

  public void mockTest(final boolean close, final boolean exception) {
    final AsyncStreamCopierImpl copier = new AsyncStreamCopierImpl();
    final Executor executor = EasyMock.createMock(Executor.class);
    final IOUtils ioUtils = EasyMock.createMock(IOUtils.class);
    final Reference<Runnable> runnableReference = EasyMockUtils.newReference();
    executor.execute(EasyMockUtils.record(runnableReference));
    copier.setExecutor(executor);
    copier.setIOUtils(ioUtils);
    EasyMock.replay(executor);
    final InputStream input = new ByteArrayInputStream("moo".getBytes());
    final OutputStream output = new ByteArrayOutputStream();
    copier.copy(input, output, close);
    EasyMock.verify(executor);

    ioUtils.copy(EasyMock.same(input), EasyMock.same(output));
    if(exception) {
      EasyMock.expectLastCall().andThrow(new NullPointerException());
    } else {
      EasyMock.expectLastCall().andReturn(14);
    }
    if(close) {
      ioUtils.closeQuietly(output);
    }
    EasyMock.replay(ioUtils);
    Exception e = null;
    try {
      runnableReference.get().toString();
      runnableReference.get().run();
    } catch(final Exception ie) {
      e = ie;
    }
    if(exception) {
      assert e != null;
    }
    EasyMock.verify(ioUtils);
  }

  @Test(groups = "unit", timeOut=1000)
  public void synchronousCopyClose() {
    this.testSimpleCopy(true);
  }

  @Test(groups = "unit", timeOut=1000)
  public void synchronousCopyNoClose() {
    this.testSimpleCopy(false);
  }

  private static class TrackedOutputStream extends ProxyOutputStream {
    private boolean closed = false;

    public TrackedOutputStream(final OutputStream proxy) {
      super(proxy);
    }

    public void close() throws IOException {
      this.closed = true;
      super.close();
    }
  }

  private void testSimpleCopy(final boolean close) {
    final AsyncStreamCopierImpl copier = new AsyncStreamCopierImpl();
    final ByteArrayInputStream inputStream = new ByteArrayInputStream("Hello".getBytes());
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    final TrackedOutputStream trackedOutputStream = new TrackedOutputStream(outputStream);
    copier.setExecutor(new Executor() {
      public void execute(final Runnable runnable) {
        runnable.run();
      }
    });
    copier.copy(inputStream, trackedOutputStream, close);
    assert new String(outputStream.toByteArray()).equals("Hello");
    assert trackedOutputStream.closed == close;
  }

  @Test(groups = "unit", timeOut=1000, invocationCount=10)
  public void close() throws IOException, InterruptedException {
    final AsyncStreamCopierImpl copier = new AsyncStreamCopierImpl();
    final Reference<Thread> threadReference = new Reference<Thread>();
    final Reference<Throwable> throwableReference = new Reference<Throwable>();
    copier.setExecutor(new Executor() {
      public void execute(final Runnable runnable) {
        final Thread thread = new Thread(runnable);
        threadReference.set(thread);
        thread.start();
        thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
          public void uncaughtException(final Thread arg0, final Throwable throwable) {
            throwableReference.set(throwable);
          }
        });
      }
    });
    final PipedOutputStream pipedOutputStream = new PipedOutputStream();
    final PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);
    final ByteArrayOutputStream copiedStream = new ByteArrayOutputStream();
    copier.copy(pipedInputStream, copiedStream, true);
    Thread.sleep(3);
    assert new String(copiedStream.toByteArray()).equals("");
    pipedOutputStream.write("Hello ".getBytes());
    pipedOutputStream.flush();
    while(!new String(copiedStream.toByteArray()).equals("Hello ")) {
      Thread.sleep(1);
    }
    pipedOutputStream.write("World!".getBytes());
    pipedOutputStream.flush();
    while(!new String(copiedStream.toByteArray()).equals("Hello World!")) {
      Thread.sleep(1);
    }
    assert threadReference.get().isAlive();
    pipedOutputStream.close();
    while(threadReference.get().isAlive()) {
      Thread.sleep(1);
    }
    assert throwableReference.get() == null;
  }
}
