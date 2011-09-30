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

import net.jmchilton.concurrent.CountDownLatch;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.concurrent.LoopingRunnable;

public class LoopingRunnableSupplierTest {
  private final LoopingRunnableSupplierImpl supplier = new LoopingRunnableSupplierImpl();
  private LoopingRunnable loopingRunnable = null;
  private Thread t;

  @BeforeMethod(alwaysRun = true)
  public void init() {
    this.t = null;
    this.loopingRunnable = null;
  }

  public void start() {
    this.t = new Thread(this.loopingRunnable);
    this.t.start();
  }

  public void stop() throws InterruptedException {
    this.loopingRunnable.shutdown();
    this.t.join();
  }

  @Test(groups = "unit", expectedExceptions = IllegalStateException.class, timeOut = 1000)
  public void haltOnException() {
    final LoopingRunnableConfig config = new LoopingRunnableConfig();
    config.setHaltOnException(true);

    this.supplier.setLoopingRunnableConfig(config);
    final LoopingRunnable loopingRunnable = this.supplier.get();

    final CountingRunnable runnable1 = new CountingRunnable();
    runnable1.throwsException = true;

    loopingRunnable.setBaseRunnable(runnable1);
    loopingRunnable.run();
  }

  @Test(groups = "unit", timeOut = 1000)
  public void noHaltOnException() throws InterruptedException {
    final LoopingRunnableConfig config = new LoopingRunnableConfig();
    config.setWaitTime(0);
    config.setHaltOnException(false);
    this.supplier.setLoopingRunnableConfig(config);
    this.loopingRunnable = this.supplier.get();
    final CountingRunnable runnable1 = new CountingRunnable();
    runnable1.throwsException = true;
    this.loopingRunnable.setBaseRunnable(runnable1);
    this.start();
    // Test is a little brittle because of this
    Thread.sleep(25);
    this.stop();
    assert runnable1.count > 1 : runnable1.count;
  }

  static class CountingRunnable implements Runnable {

    CountingRunnable() {
      this(1);
    }

    CountingRunnable(final int count) {
      latch = new CountDownLatch(count);
    }

    private CountDownLatch latch;
    private boolean throwsException = false;
    private long count = 0L;

    CountDownLatch getLatch() {
      return latch;
    }

    public void run() {
      latch.countDown();
      this.count++;
      if(this.throwsException) {
        throw new IllegalStateException();
      }
    }

  }

  @Test(groups = "unit", timeOut = 1000)
  public void waitObject() throws InterruptedException {

    final LoopingRunnableConfig config = new LoopingRunnableConfig();
    config.setWaitTime(0);
    this.supplier.setLoopingRunnableConfig(config);
    this.loopingRunnable = this.supplier.get();

    final Object waitObject = new Object();
    this.loopingRunnable.setWaitObject(waitObject);
    final CountingRunnable countingRunnable = new CountingRunnable();

    this.loopingRunnable.setBaseRunnable(countingRunnable);
    synchronized(waitObject) {
      this.start();
      Thread.sleep(25);
      assert countingRunnable.count == 1;
    }
    Thread.sleep(25);
    synchronized(waitObject) {
      assert countingRunnable.count == 1;
      waitObject.notify();
    }
    // Test will fail if notify issued previously
    // doesn't cause count to increase.
    while(countingRunnable.count == 1) {
      Thread.sleep(5);
    }
  }

  @Test(groups = "unit", timeOut = 1000)
  public void timedWaitObject() throws InterruptedException {
    final LoopingRunnableConfig config = new LoopingRunnableConfig();
    config.setWaitTime(2);
    this.supplier.setLoopingRunnableConfig(config);
    this.loopingRunnable = this.supplier.get();

    final Object waitObject = new Object();
    this.loopingRunnable.setWaitObject(waitObject);
    // Make sure multiple countdowns happen, despite no notifies.
    final CountingRunnable countingRunnable = new CountingRunnable(3);

    this.loopingRunnable.setBaseRunnable(countingRunnable);
    this.start();
    countingRunnable.getLatch().await();
  }

}
