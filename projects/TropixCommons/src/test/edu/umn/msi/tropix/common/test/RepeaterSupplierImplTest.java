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

package edu.umn.msi.tropix.common.test;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.jmchilton.concurrent.CountDownLatch;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.concurrent.Repeater;
import edu.umn.msi.tropix.common.concurrent.impl.RepeaterSupplierImpl;
import edu.umn.msi.tropix.common.shutdown.ShutdownException;

public class RepeaterSupplierImplTest {

  @Test(groups = "unit", timeOut=1000)
  public void succeed() {
    final Runnable runnable = new Runnable() {
      public void run() {
      }
    };

    final RepeaterSupplierImpl<Runnable> supplier = new RepeaterSupplierImpl<Runnable>();
    final Repeater<Runnable> repeater = supplier.get();
    repeater.setBaseRunnable(runnable);
    repeater.run();
  }

  static class CountingRunnable implements Runnable {
    static final String STRING = "COUNTINGRUNNABLESTRING";
    private final CountDownLatch latch;
    private long count = 0;
    
    CountingRunnable() {
      this(new CountDownLatch(1));
    }

    CountingRunnable(final CountDownLatch latch) {
      this.latch = latch;
    }

    public void run() {
      this.count++;
      throw new RuntimeException();
    }

    public long getCount() {
      return this.count;
    }

    public String toString() {
      return CountingRunnable.STRING;
    }
  };

  @Test(groups = "unit", timeOut=1000)
  public void beanOps() {
    final RepeaterSupplierImpl<Runnable> supplier = new RepeaterSupplierImpl<Runnable>();
    supplier.setSleepTimeUnit("MICROSECONDS");
    supplier.getSleepTimeUnit().equals(TimeUnit.NANOSECONDS.toString());
    supplier.setMaxAttempts(145L);
    assert supplier.getMaxAttempts() == 145L;

    supplier.setSleepDurationDelta(1004L);
    assert supplier.getSleepDurationDelta() == 1004L;

    supplier.setMaxSleepDuration(10045L);
    assert supplier.getMaxSleepDuration() == 10045L;

    supplier.setInitialSleepDuration(3L);
    assert supplier.getInitialSleepDuration() == 3L;
  }

  @Test(groups = "unit", timeOut=1000)
  public void interruptions() throws InterruptedException {
    final Runnable runnable = new Runnable() {
      private boolean first = true;

      public void run() {
        if(this.first) {
          this.first = false;
          throw new IllegalStateException();
        }
      }
    };
    final RepeaterSupplierImpl<Runnable> supplier = new RepeaterSupplierImpl<Runnable>();
    supplier.setSleepDurationDelta(10000);
    supplier.setSleepTimeUnit(TimeUnit.MILLISECONDS);

    final Repeater<Runnable> repeater = supplier.get();
    repeater.setBaseRunnable(runnable);
    repeater.run();
    final Thread thread = new Thread(repeater);
    final long start = System.currentTimeMillis();
    thread.start();
    Thread.sleep(50);
    thread.interrupt();
    thread.join();
    final long end = System.currentTimeMillis();
    assert end - start < 5000;
  }

  @Test(groups = "unit")
  public void fails() {
    final CountingRunnable countingRunnable = new CountingRunnable();
    final RepeaterSupplierImpl<CountingRunnable> supplier = new RepeaterSupplierImpl<CountingRunnable>();
    supplier.setSleepTimeUnit(TimeUnit.NANOSECONDS);
    supplier.setMaxAttempts(3);
    final Repeater<CountingRunnable> repeater = supplier.get();
    repeater.setBaseRunnable(countingRunnable);
    boolean failed = false;
    try {
      repeater.run();
    } catch(final RuntimeException e) {
      failed = true;
    }
    assert failed;
    assert countingRunnable.getCount() == 3;
  }

  class Increasing implements Runnable {
    private long lastDelta = -1;
    private long lastTime = -1;
    private boolean decreased = false;
    private long maxWait;

    public void run() {
      final long currentTime = System.currentTimeMillis();
      if(this.lastTime != -1) {
        final long currentDelta = currentTime - this.lastTime;
        this.maxWait = currentDelta > this.maxWait ? currentDelta : this.maxWait;
        if(this.lastDelta != -1) {
          this.decreased = this.decreased || this.lastDelta >= currentDelta;
        }
        this.lastDelta = currentDelta;
      }
      this.lastTime = currentTime;
      throw new IllegalStateException();
    }
  }

  @Test
  public void increasing() {
    final Increasing runnable = new Increasing();
    final RepeaterSupplierImpl<Increasing> supplier = new RepeaterSupplierImpl<Increasing>();
    supplier.setSleepTimeUnit(TimeUnit.MILLISECONDS);
    supplier.setInitialSleepDuration(2);
    supplier.setSleepDurationDelta(100);
    supplier.setMaxAttempts(5);
    final Repeater<Increasing> repeater = supplier.get();
    repeater.setBaseRunnable(runnable);
    Exception exception = null;
    try {
      repeater.run();
    } catch(final Exception e) {
      exception = e;
    }
    assert exception != null && exception.getClass().equals(IllegalStateException.class);
    assert !runnable.decreased;
  }

  @Test(groups = "unit", timeOut=1000)
  public void maxWait() {
    final Increasing runnable = new Increasing();
    final RepeaterSupplierImpl<Increasing> supplier = new RepeaterSupplierImpl<Increasing>();
    supplier.setSleepTimeUnit(TimeUnit.MILLISECONDS);
    supplier.setInitialSleepDuration(2);
    supplier.setSleepDurationDelta(30);
    supplier.setMaxAttempts(20);
    supplier.setMaxSleepDuration(50);
    final Repeater<Increasing> repeater = supplier.get();
    repeater.setBaseRunnable(runnable);
    Exception exception = null;
    try {
      repeater.run();
    } catch(final Exception e) {
      exception = e;
    }
    assert exception != null && exception.getClass().equals(IllegalStateException.class);
    assert runnable.maxWait < 110;
  }

  static class ExceptionRunnable implements Runnable {
    private Exception exception = null;
    private Runnable runnable;

    public void run() {
      try {
        this.runnable.run();
      } catch(final Exception exception) {
        this.exception = exception;
      }
    }
  }

  @Test(groups = "unit", timeOut=1000)
  public void shutdown() throws Exception {
    final RepeaterSupplierImpl<CountingRunnable> supplier = new RepeaterSupplierImpl<CountingRunnable>();
    supplier.setInitialSleepDuration(10);
    supplier.setSleepTimeUnit(TimeUnit.MILLISECONDS);
    final Repeater<CountingRunnable> repeater = supplier.get();
    final ExceptionRunnable exceptionRunnable = new ExceptionRunnable();
    repeater.setBaseRunnable(new CountingRunnable());
    exceptionRunnable.runnable = repeater;
    final Thread t = new Thread(exceptionRunnable);
    t.start();
    Thread.sleep(50);
    supplier.destroy();
    t.join(50);
    assert exceptionRunnable.exception instanceof ShutdownException;
  }

  @Test(groups = "unit", timeOut=1000, invocationCount=10)
  public void managedOperations() throws InterruptedException {
    final CountingRunnable countingRunnable = new CountingRunnable();
    final RepeaterSupplierImpl<CountingRunnable> supplier = new RepeaterSupplierImpl<CountingRunnable>();
    supplier.setSleepTimeUnit(TimeUnit.NANOSECONDS);
    supplier.setMaxAttempts(-1);
    supplier.setMaxSleepDuration(10000);
    supplier.setSleepDurationDelta(0);
    supplier.setInitialSleepDuration(100);
    final List<Thread> threads = new LinkedList<Thread>();
    for(int i = 0; i < 10; i++) {
      final Repeater<CountingRunnable> repeater = supplier.get();
      repeater.setBaseRunnable(countingRunnable);
      final Thread thread = new Thread(repeater);
      thread.start();
      threads.add(thread);
    }
    supplier.activeRepeaters(); // Tests that this doesn't cause class to hang.
    Thread.sleep(10);
    boolean first = true;
    for(final Thread thread : threads) {
      if(first) {
        first = false;
      } else {
        assert thread.isAlive();
        supplier.killRepeater(thread.getId());
      }
    }
    Thread.sleep(10);
    first = true;
    for(final Thread thread : threads) {
      if(first) {
        first = false;
      } else {
        thread.join(100);
      }
    }
    final Thread firstThread = threads.get(0);
    assert firstThread.isAlive();
    final String repeaters = supplier.activeRepeaters();
    assert repeaters.contains("" + firstThread.getId());
    assert repeaters.contains(CountingRunnable.STRING);
    assert firstThread.isAlive();
    supplier.killRepeater(firstThread.getId());
    // firstThread should eventually die, leaving this loop. Else 
    // test will fail with timeout.
    while(firstThread.isAlive()) {
      Thread.sleep(10);
    }
  }

}
