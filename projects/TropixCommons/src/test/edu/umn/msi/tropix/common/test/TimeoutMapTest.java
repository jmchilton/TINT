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

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.timeout.impl.TimeoutMap;
import edu.umn.msi.tropix.common.timeout.impl.TimeoutTracker;
import edu.umn.msi.tropix.common.timeout.impl.TimeoutTrackerSupplier;

public class TimeoutMapTest {

  @Test(groups = "unit")
  public void executeCalledOnce() {
    this.executeCalledOnce(true);
    this.executeCalledOnce(false);
  }

  public void executeCalledOnce(final boolean addAsMap) {
    final TimeoutMap<String, String> timeoutMap = new TimeoutMap<String, String>();
    final Executor mockExecutor = EasyMock.createMock(Executor.class);
    final Supplier<TimeoutTracker> trackerSupplier = EasyMockUtils.createMockSupplier();
    final TimeoutTracker tracker = EasyMock.createMock(TimeoutTracker.class);
    EasyMock.expect(trackerSupplier.get()).andReturn(tracker);
    EasyMock.expect(trackerSupplier.get()).andReturn(tracker);
    mockExecutor.execute(EasyMock.isA(Runnable.class));

    EasyMock.replay(mockExecutor, trackerSupplier, tracker);
    timeoutMap.setTimeoutTrackerSupplier(trackerSupplier);
    timeoutMap.setLoopingExecutor(mockExecutor);
    if(addAsMap) {
      timeoutMap.put("Key1", "Value1");
      timeoutMap.put("Key2", "Value2");
    } else {
      final HashMap<String, String> addMap = new HashMap<String, String>();
      addMap.put("Key1", "Value1");
      addMap.put("Key2", "Value2");
      timeoutMap.putAll(addMap);
    }
    EasyMock.verify(mockExecutor, trackerSupplier, tracker);
    assert timeoutMap.containsKey("Key1");
    assert timeoutMap.containsKey("Key2");
    assert timeoutMap.containsValue("Value1");
    assert timeoutMap.containsValue("Value2");
    assert timeoutMap.size() == 2;
  }

  @Test(groups = "unit")
  public void timeoutReset() throws InterruptedException {
    final TimeoutTrackerSupplier supplier = new TimeoutTrackerSupplier();
    supplier.setDuration(5);
    supplier.setTimeUnit(TimeUnit.MILLISECONDS);
    final TimeoutTracker tracker = supplier.get();
    Thread.sleep(10);
    assert tracker.isTimedOut();
    tracker.reset();
    assert !tracker.isTimedOut();
  }

  @Test(groups = "unit")
  public void beanOps() {
    final TimeoutTrackerSupplier supplier = new TimeoutTrackerSupplier();
    supplier.setDuration(100);
    assert supplier.getDuration() == 100;
    supplier.setTimeUnit("MILLISECONDS");
    assert supplier.getTimeUnit().equals("MILLISECONDS");
    supplier.setTimeUnit("MICROSECONDS");
    assert supplier.getTimeUnit().equals("MICROSECONDS");
  }

  @Test(groups = "unit")
  public void removal() throws InterruptedException {
    final TimeoutMap<String, String> timeoutMap = new TimeoutMap<String, String>();
    final Object outerLock = new Object();
    timeoutMap.setLoopingExecutor(new Executor() {
      public void execute(final Runnable command) {
        final Thread t = new Thread(new Runnable() {
          public void run() {
            try {
              TimeUnit.MICROSECONDS.sleep(10);
              command.run();
              synchronized(outerLock) {
                outerLock.notify();
              }
            } catch(final InterruptedException e) {
              e.printStackTrace();
            }
          }
        });
        t.start();
      }
    });
    final TimeoutTrackerSupplier supplier = new TimeoutTrackerSupplier();
    supplier.setDuration(1);
    supplier.setTimeUnit(TimeUnit.NANOSECONDS);
    timeoutMap.setTimeoutTrackerSupplier(supplier);
    timeoutMap.put("Hello", "World");
    timeoutMap.put("Hello2", "World2");
    assert timeoutMap.size() == 2;
    synchronized(outerLock) {
      outerLock.wait(10);
    }
    assert timeoutMap.size() == 0;
  }

  @Test(groups = "unit")
  public void keep() throws InterruptedException {
    final TimeoutMap<String, String> timeoutMap = new TimeoutMap<String, String>();
    timeoutMap.setLoopingExecutor(new Executor() {
      public void execute(final Runnable command) {
        try {
          TimeUnit.MICROSECONDS.sleep(5);
          command.run();
        } catch(final InterruptedException e) {
          e.printStackTrace();
        }
      }
    });
    final TimeoutTrackerSupplier supplier = new TimeoutTrackerSupplier();
    supplier.setTimeUnit(TimeUnit.SECONDS);
    supplier.setDuration(1);
    timeoutMap.setTimeoutTrackerSupplier(supplier);
    timeoutMap.put("Hello", "World");
    assert timeoutMap.size() == 1;
    TimeUnit.MILLISECONDS.sleep(1);
    assert timeoutMap.size() == 1;
  }
}
