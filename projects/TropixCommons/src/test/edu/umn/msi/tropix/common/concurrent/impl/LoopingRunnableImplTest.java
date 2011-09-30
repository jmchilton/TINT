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

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.TimeUnit;

import net.jmchilton.concurrent.CountDownLatch;

import org.testng.annotations.Test;

public class LoopingRunnableImplTest {

  static class CountingRunnable implements Runnable {
    private int numRuns = 1;
    private int counter = 0;
    private CountDownLatch latch = new CountDownLatch(1);

    public void run() {
      this.counter++;
      if(this.counter == this.numRuns) {
        latch.countDown();
      }
    }

    public void setNumRuns(final int numRuns) {
      this.numRuns = numRuns;
    }
  }

  @Test(groups = {"unit"}, timeOut=1000)
  public void loops() throws InterruptedException {
    final CountingRunnable countingRunnable = new CountingRunnable();
    countingRunnable.setNumRuns(3);
    final LoopingRunnableImpl loopingRunnable = new LoopingRunnableImpl();
    loopingRunnable.setLoopingRunnableConfig(new LoopingRunnableConfig());
    loopingRunnable.setBaseRunnable(countingRunnable);
    final Thread t = new Thread(loopingRunnable);
    t.start();
    // Verify it loops
    countingRunnable.latch.await();
    loopingRunnable.shutdown();
    // Verify it shutdowns after shutdown
    // is called.
    t.join(500);
  }

  static class ExceptionRunnable implements Runnable {
    private CountDownLatch latch = new CountDownLatch(1);
    public void run() {
      latch.countDown();
      throw new IllegalStateException();
    }
  }

  @Test(groups = {"unit"}, timeOut=1000)
  public void haltOnsException() throws InterruptedException {
    final LoopingRunnableConfig config = new LoopingRunnableConfig();
    config.setHaltOnException(true);
    final LoopingRunnableImpl loopingRunnable = new LoopingRunnableImpl();
    loopingRunnable.setLoopingRunnableConfig(config);
    loopingRunnable.setBaseRunnable(new ExceptionRunnable());
    boolean exceptionCalled = false;
    try {
      loopingRunnable.run();
    } catch(final IllegalStateException e) {
      exceptionCalled = true;
    }
    assert exceptionCalled;
  }

  @Test(groups = {"unit"}, timeOut=1000)
  public void notHault() throws InterruptedException {
    final LoopingRunnableConfig config = new LoopingRunnableConfig();
    config.setHaltOnException(false);
    final LoopingRunnableImpl loopingRunnable = new LoopingRunnableImpl();
    final ExceptionRunnable runnable = new ExceptionRunnable();
    loopingRunnable.setBaseRunnable(runnable);
    loopingRunnable.setLoopingRunnableConfig(config);
    final Thread t = new Thread(loopingRunnable);
    t.start();
    runnable.latch.await();
    loopingRunnable.shutdown();
    t.join(500);
  }

  static class TestHandler implements UncaughtExceptionHandler {
    private boolean called = false;

    public void uncaughtException(final Thread t, final Throwable throwable) {
      this.called = true;
    }
    
    boolean getCalled() {
      return called;
    }
  }

  @Test(groups = "unit", timeOut=1000)
  public void configTest() {
    final LoopingRunnableConfig config = new LoopingRunnableConfig();
    config.setWaitTime(100L, TimeUnit.MICROSECONDS);
    assert config.getWaitTime() == 100L;
    assert config.getWaitTimeUnit().equals(TimeUnit.MICROSECONDS);
    assert config.getWaitTimeUnitAsString().equals("MICROSECONDS");
    config.setWaitTimeUnitAsString("MILLISECONDS");
    assert config.getWaitTimeUnit().equals(TimeUnit.MILLISECONDS);

  }

  @Test(groups = "unit", expectedExceptions = NullPointerException.class, timeOut=1000)
  public void normalWhenHaltOnException() {
    final LoopingRunnableConfig config = new LoopingRunnableConfig();
    config.setHaltOnException(true);
    final LoopingRunnableImpl loopingRunnable = new LoopingRunnableImpl();
    loopingRunnable.setLoopingRunnableConfig(config);
    loopingRunnable.setBaseRunnable(new Runnable() {
      private boolean first = true;

      public void run() {
        if(this.first) {
          this.first = false;
        } else {
          throw new NullPointerException();
        }
      }
    });
    loopingRunnable.run();
  }

  @Test(groups = {"unit"}, timeOut=1000)
  public void exceptionHandler() throws InterruptedException {
    final TestHandler handler = new TestHandler();
    final LoopingRunnableConfig config = new LoopingRunnableConfig();
    config.setHaltOnException(false);
    config.setUncaughtExceptionHandler(handler);
    final LoopingRunnableImpl loopingRunnable = new LoopingRunnableImpl();
    final ExceptionRunnable runnable = new ExceptionRunnable();
    loopingRunnable.setBaseRunnable(runnable);
    loopingRunnable.setLoopingRunnableConfig(config);
    final Thread t = new Thread(loopingRunnable);
    t.start();
    runnable.latch.await();
    loopingRunnable.shutdown();
    t.join(500);
    assert handler.getCalled();
  }

}
