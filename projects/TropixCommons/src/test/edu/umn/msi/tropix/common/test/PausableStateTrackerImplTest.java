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

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.concurrent.PausableCallback;
import edu.umn.msi.tropix.common.concurrent.PausableStateTracker;
import edu.umn.msi.tropix.common.concurrent.PausableStateTrackers;
import edu.umn.msi.tropix.common.concurrent.impl.PausableStateTrackerImpl;

public class PausableStateTrackerImplTest {

  @Test(groups = "unit", timeOut=1000)
  public void shutdown() {
    final PausableStateTrackerImpl tracker = new PausableStateTrackerImpl();
    assert !tracker.isPaused();
    tracker.shutdown();
    assert tracker.isPaused();
  }

  @Test(groups = "unit", timeOut=1000)
  public void startup() {
    final PausableStateTrackerImpl tracker = new PausableStateTrackerImpl(true);
    assert tracker.isPaused();
    tracker.startup();
    assert !tracker.isPaused();
  }

  @Test(groups = "unit", timeOut=1000)
  public void beanOps() {
    final PausableStateTrackerImpl tracker = new PausableStateTrackerImpl(true);
    tracker.setId("1234");
    assert tracker.getId().equals("1234");
  }

  @Test(groups = "unit", timeOut=1000)
  public void ignoreInvalidRequest() {
    final PausableCallback callback1 = EasyMock.createMock(PausableCallback.class);
    final PausableStateTracker tracker = PausableStateTrackers.get();
    EasyMock.replay(callback1);
    tracker.registerPausableCallback(callback1);
    tracker.startup();
    assert !tracker.isPaused();
    EasyMock.verify(callback1);
    EasyMock.reset(callback1);
    callback1.onShutdown();
    EasyMock.replay(callback1);
    tracker.shutdown();
    assert tracker.isPaused();
    EasyMock.verify(callback1);
    EasyMock.reset(callback1);
    EasyMock.replay(callback1);
    tracker.shutdown();
    assert tracker.isPaused();
    EasyMock.verify(callback1);
  }

  /*
   * This tests makes sure the callbacks throwing exceptions don't prevent the other callbacks from running or the state from changing.
   */
  @Test(groups = "unit", timeOut=1000)
  public void listenerStartupExceptions() {
    this.listenerExceptions(true);
  }

  @Test(groups = "unit", timeOut=1000)
  public void listenerShutdownExceptions() {
    this.listenerExceptions(false);
  }

  public void listenerExceptions(final boolean onStartup) {
    final PausableCallback callback1 = EasyMock.createMock(PausableCallback.class);
    final PausableCallback callback2 = EasyMock.createMock(PausableCallback.class);

    if(onStartup) {
      callback1.onStartup();
    } else {
      callback1.onShutdown();
    }
    EasyMock.expectLastCall().andThrow(new IllegalStateException());
    if(onStartup) {
      callback2.onStartup();
    } else {
      callback2.onShutdown();
    }

    EasyMock.expectLastCall().andThrow(new NullPointerException());

    EasyMock.replay(callback1, callback2);
    final PausableStateTrackerImpl tracker = new PausableStateTrackerImpl();
    if(onStartup) {
      tracker.shutdown();
    }
    tracker.registerPausableCallback(callback1);
    tracker.registerPausableCallback(callback2);

    if(onStartup) {
      tracker.startup();
    } else {
      tracker.shutdown();
    }
    EasyMock.verify(callback1, callback2);
  }

  @Test(groups = "unit", timeOut=1000)
  public void listeners() {
    final PausableCallback callback1 = EasyMock.createMock(PausableCallback.class);
    final PausableCallback callback2 = EasyMock.createMock(PausableCallback.class);

    callback1.onShutdown();
    callback1.onStartup();

    EasyMock.replay(callback1, callback2);

    final PausableStateTrackerImpl tracker = new PausableStateTrackerImpl();
    tracker.registerPausableCallback(callback1);

    tracker.shutdown();
    tracker.startup();

    EasyMock.verify(callback1, callback2);
    EasyMock.reset(callback1, callback2);

    callback1.onShutdown();
    callback1.onStartup();
    callback2.onShutdown();
    callback2.onStartup();

    EasyMock.replay(callback1, callback2);

    tracker.registerPausableCallback(callback2);

    tracker.shutdown();
    tracker.startup();

    EasyMock.verify(callback1, callback2);
    EasyMock.reset(callback1, callback2);

    callback2.onShutdown();

    EasyMock.replay(callback1, callback2);

    tracker.unregisterPausableCallback(callback1);
    tracker.shutdown();

    EasyMock.verify(callback1, callback2);
    EasyMock.reset(callback1, callback2);

    EasyMock.replay(callback1, callback2);
    tracker.unregisterPausableCallback(callback2);
    tracker.startup();

    EasyMock.verify(callback1, callback2);
  }

}
