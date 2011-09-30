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

import java.util.concurrent.TimeUnit;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.concurrent.LoopingRunnable;
import edu.umn.msi.tropix.common.concurrent.Timer;
import edu.umn.msi.tropix.common.test.MockObjectCollection;

public class TimerLoopingRunnableSupplierImplTest {
  private LoopingRunnableConfig loopingRunnableConfig = new LoopingRunnableConfig();;
  private TimerLoopingRunnableSupplierImpl supplier = new TimerLoopingRunnableSupplierImpl();
  private Timer timer;
  private Runnable runnable;
  private final MockObjectCollection mockObjects = new MockObjectCollection();

  @BeforeMethod(groups = "unit")
  public void init() {
    this.loopingRunnableConfig = new LoopingRunnableConfig();
    this.supplier = new TimerLoopingRunnableSupplierImpl();
    this.supplier.setLoopingRunnableConfig(this.loopingRunnableConfig);
    this.timer = this.mockObjects.createMock(Timer.class);
    this.runnable = this.mockObjects.createMock(Runnable.class);
    this.supplier.setTimer(this.timer);
  }

  @Test(groups = "unit", expectedExceptions = UnsupportedOperationException.class)
  public void setWaitObject() {
    this.supplier.get().setWaitObject(new Object());
  }

  @Test(groups = "unit")
  public void timer() {
    this.supplier.get().shutdown();
  }

  @Test(groups = "unit")
  public void simpleLoop() {
    this.looping(false, false);
  }

  @Test(groups = "unit")
  public void exceptionLoop() {
    this.looping(true, false);
  }

  @Test(groups = "unit")
  public void haltingExceptionLoop() {
    this.looping(true, true);
  }

  public void looping(final boolean exception, final boolean haltOnException) {
    this.loopingRunnableConfig.setWaitTimeUnit(TimeUnit.SECONDS);
    this.loopingRunnableConfig.setWaitTime(2L);
    this.loopingRunnableConfig.setHaltOnException(haltOnException);

    final LoopingRunnable loopingRunnable = this.supplier.get();
    loopingRunnable.setBaseRunnable(this.runnable);
    this.runnable.run();
    if(exception) {
      EasyMock.expectLastCall().andThrow(new IllegalStateException());
    }
    if(!exception || !haltOnException) {
      this.timer.schedule(loopingRunnable, 2000L);
    }
    this.mockObjects.replay();
    loopingRunnable.run();
    this.mockObjects.verifyAndReset();
  }

}
