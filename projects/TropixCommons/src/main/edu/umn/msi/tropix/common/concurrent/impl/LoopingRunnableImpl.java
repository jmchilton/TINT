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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.umn.msi.tropix.common.concurrent.Haltable;
import edu.umn.msi.tropix.common.concurrent.LoopingRunnable;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;

class LoopingRunnableImpl implements LoopingRunnable, Haltable {
  private static final Log LOG = LogFactory.getLog(LoopingRunnableImpl.class);
  private Runnable baseRunnable;
  private LoopingRunnableConfig config;
  private Object waitObject;
  private boolean shutdown = false;
  private Thread runningThread;

  public void run() {
    this.runningThread = Thread.currentThread();
    while(!this.shutdown) {
      if(this.config.getHaltOnException()) {
        this.baseRunnable.run();
      } else {
        try {
          this.baseRunnable.run();
        } catch(final Throwable throwable) {
          UncaughtExceptionHandlerUtils.handleException(this.config.getUncaughtExceptionHandler(), throwable);
        }
      }
      this.doWait();
    }
  }

  protected void doWait() {
    final long waitTime = this.config.getWaitTime();
    try {
      if(this.waitObject != null && waitTime > 0) {
        synchronized(this.waitObject) {
          this.config.getWaitTimeUnit().timedWait(this.waitObject, waitTime);
        }
      } else if(waitTime > 0) {
        this.config.getWaitTimeUnit().sleep(waitTime);
      } else if(this.waitObject != null) {
        synchronized(this.waitObject) {
          this.waitObject.wait();
        }
      }
    } catch(final InterruptedException e) {
      ExceptionUtils.logQuietly(LOG, e);
    }
  }

  public void setLoopingRunnableConfig(final LoopingRunnableConfig config) {
    this.config = config;
  }

  public void setWaitObject(final Object waitObject) {
    this.waitObject = waitObject;
  }

  public void shutdown() {
    this.shutdown = true;
    if(this.runningThread != null) {
      this.runningThread.interrupt();
    }
  }

  public void setBaseRunnable(final Runnable runnable) {
    this.baseRunnable = runnable;
  }
}
