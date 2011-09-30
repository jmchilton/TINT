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

package edu.umn.msi.tropix.common.concurrent;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.concurrent.InitializationTracker.InitializationFailedException;

public class InitializationTrackerImplTest {

  @Test(groups = "unit", timeOut=1000)
  public void waitOneForOne() throws InterruptedException {
    this.wait(false, false, false);
  }

  @Test(groups = "unit", timeOut=1000)
  public void waitOneForAll() throws InterruptedException {
    this.wait(true, false, false);
  }

  @Test(groups = "unit", timeOut=1000)
  public void waitAllForAll() throws InterruptedException {
    this.wait(true, false, true);
  }

  @Test(groups = "unit", timeOut=1000)
  public void initializeTwice() throws InterruptedException {
    this.wait(true, true, false);
  }

  private void wait(final boolean initializeAll, final boolean initializeTwice, final boolean waitAll) throws InterruptedException {
    final InitializationTracker<String> tracker = InitializationTrackers.getDefault();
    final StringBuffer buffer = new StringBuffer("");
    final Runnable runnable = new Runnable() {
      public void run() {
        assert buffer.length() == 0;
        if(waitAll) {
          tracker.waitForAllInitialization();
        } else {
          tracker.waitForInitialization("Hello");
        }
        assert buffer.length() > 0;
      }
    };
    final Thread thread = new Thread(runnable);
    thread.start();
    Thread.sleep(5);
    buffer.append("a");
    if(initializeAll) {
      tracker.initializeAll();
      tracker.initialize("Hello");
    } else {
      tracker.initialize("Hello");
    }    
    thread.join();
  }

  @Test(groups = "unit", timeOut=1000, invocationCount=10)
  public void waitForFail() throws InterruptedException {
    final InitializationTracker<String> tracker = InitializationTrackers.getDefault();
    final StringBuffer buffer = new StringBuffer("");
    final Runnable runnable = new Runnable() {
      public void run() {
        try {
          tracker.waitForInitialization("Hello");
        } catch(final InitializationFailedException exception) {
          buffer.append("b");
        }
      }
    };
    final Thread thread = new Thread(runnable);
    thread.start();
    Thread.sleep(1);
    tracker.fail("Hello");
    while(thread.isAlive()) {
      Thread.sleep(3);
    }
    assert buffer.length() > 0;
  }

}
