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

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.concurrent.LoopingRunnable;
import edu.umn.msi.tropix.common.concurrent.Timer;

/**
 * An supplier of {@link LoopingRunnable} instances that "wait" between
 * execution via a Timer. {@link LoopingRunnableSupplierImpl} accomplishes
 * this by timed waits instead.
 * 
 * @author John Chilton
 *
 */
public class TimerLoopingRunnableSupplierImpl implements Supplier<LoopingRunnable> {
  private Timer timer;
  private LoopingRunnableConfig loopingRunnableConfig;

  class TimerLoopingRunnableImpl implements LoopingRunnable {
    private Runnable runnable = null;

    public void setBaseRunnable(final Runnable runnable) {
      this.runnable = runnable;
    }

    public void setWaitObject(final Object object) {
      throw new UnsupportedOperationException();
    }

    public void run() {
      boolean exceptionThrown = false;
      try {
        this.runnable.run();
      } catch(final Throwable t) {
        exceptionThrown = true;
        UncaughtExceptionHandlerUtils.handleException(loopingRunnableConfig.getUncaughtExceptionHandler(), t);
      }
      if(exceptionThrown && loopingRunnableConfig.getHaltOnException()) {
        return;
      }
      final TimeUnit timeUnit = loopingRunnableConfig.getWaitTimeUnit();
      final long millisecondsDuration = TimeUnit.MILLISECONDS.convert(loopingRunnableConfig.getWaitTime(), timeUnit);
      timer.schedule(this, millisecondsDuration);
    }

    public void shutdown() {
      // Handled by timer shutdowning
    }
  }

  public LoopingRunnable get() {
    return new TimerLoopingRunnableImpl();
  }

  public void setLoopingRunnableConfig(final LoopingRunnableConfig config) {
    this.loopingRunnableConfig = config;
  }

  public void setTimer(final Timer timer) {
    this.timer = timer;
  }

}
