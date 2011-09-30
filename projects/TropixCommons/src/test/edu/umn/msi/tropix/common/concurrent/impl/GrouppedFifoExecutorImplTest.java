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

package edu.umn.msi.tropix.common.concurrent.impl;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.concurrent.Executors;
import edu.umn.msi.tropix.common.shutdown.ShutdownException;

public class GrouppedFifoExecutorImplTest {

  private static class LatchRunnable implements Runnable {
    private final CountDownLatch endRunLatch = new CountDownLatch(1);
    private final CountDownLatch startRunLatch = new CountDownLatch(1);
    private volatile boolean running = false;
    private volatile RuntimeException exception = null;

    public void run() {
      running = true;
      startRunLatch.countDown();
      try {
        endRunLatch.await();
        if(exception != null) {
          throw exception;
        }
        running = false;
      } catch(final InterruptedException e) {
        throw new RuntimeException(e);
      }
    }

    boolean isRunning() {
      return running;
    }
  }

  /**
   * Tests shutdown exception causes next runnable to not run.
   */
  @Test(groups = "unit")
  public void shutdownExecution() throws InterruptedException {
    final Executor executor = Executors.getNewThreadExecutor();
    final GrouppedFifoExecutorImpl<String> gExecutor = new GrouppedFifoExecutorImpl<String>();
    gExecutor.setExecutor(executor);

    final LatchRunnable a1 = new LatchRunnable(), a2 = new LatchRunnable();

    gExecutor.execute("a", a1);
    gExecutor.execute("a", a2);
    a1.startRunLatch.await();
    a1.exception = new ShutdownException();
    a1.endRunLatch.countDown();

    assert !a2.startRunLatch.await(50, TimeUnit.MILLISECONDS);
  }

  /**
   * Tests non shutdown exception doesn't prevent next runnable from running.
   */
  @Test(groups = "unit")
  public void otherExecution() throws InterruptedException {
    final Executor executor = Executors.getNewThreadExecutor();
    final GrouppedFifoExecutorImpl<String> gExecutor = new GrouppedFifoExecutorImpl<String>();
    gExecutor.setExecutor(executor);

    final LatchRunnable a1 = new LatchRunnable(), a2 = new LatchRunnable();

    gExecutor.execute("a", a1);
    gExecutor.execute("a", a2);
    a1.startRunLatch.await();
    a1.exception = new IllegalStateException();
    a1.endRunLatch.countDown();
    a2.startRunLatch.await();
    assert a2.isRunning();
    a2.endRunLatch.countDown();
  }

  @Test(groups = "unit")
  public void normalExecution() throws InterruptedException {
    final Executor executor = Executors.getNewThreadExecutor();
    final GrouppedFifoExecutorImpl<String> gExecutor = new GrouppedFifoExecutorImpl<String>();
    gExecutor.setExecutor(executor);

    final LatchRunnable a1 = new LatchRunnable(), a2 = new LatchRunnable(), b1 = new LatchRunnable();

    gExecutor.execute("a", a1);
    gExecutor.execute("a", a2);
    a1.startRunLatch.await();
    assert a1.isRunning();
    assert !a2.isRunning();

    gExecutor.execute("b", b1);
    b1.startRunLatch.await();
    assert a1.isRunning();
    assert !a2.isRunning();
    assert b1.isRunning();

    a1.endRunLatch.countDown();
    a2.startRunLatch.await();
    assert !a1.isRunning();
    assert a2.isRunning();
    assert b1.isRunning();

    a2.endRunLatch.countDown();
    b1.endRunLatch.countDown();
  }
}
