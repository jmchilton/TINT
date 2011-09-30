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

import org.testng.annotations.Test;

public class TimerTest {

  static class NotifingRunnable implements Runnable {
    private final Object object = new Object();

    public void run() {
      synchronized(this.object) {
        this.object.notify();
      }
    }
  }

  @Test(groups = "unit")
  public void timerImpl() throws InterruptedException {
    final TimerImpl timer = new TimerImpl();
    final NotifingRunnable runnable = new NotifingRunnable();
    final long before = System.currentTimeMillis();
    synchronized(runnable.object) {
      timer.schedule(runnable, 10);
      runnable.object.wait();
    }
    final long after = System.currentTimeMillis();
    assert after - before >= 10;
  }

  static class RecordRunnable implements Runnable {
    private boolean called = false;
    private CountDownLatch latch = new CountDownLatch(1);

    public void run() {
      latch.countDown();
      this.called = true;
    }
  }

  @Test(groups = "unit")
  public void cancel() throws Exception {
    RecordRunnable runnable = new RecordRunnable();
    final TimerImpl timer = new TimerImpl();
    timer.schedule(runnable, 5);
    assert !runnable.called; // Make sure its not executed immediately, this is a little brittle.
    runnable.latch.await(); // Make sure its eventually executed.
    
    runnable = new RecordRunnable();
    timer.schedule(runnable, 5);
    timer.destroy();
    Thread.sleep(10);
    assert !runnable.called;
  }

}
