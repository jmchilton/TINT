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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;

import javax.annotation.PostConstruct;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.concurrent.Interruptable;
import edu.umn.msi.tropix.common.concurrent.InterruptableExecutor;
import edu.umn.msi.tropix.common.concurrent.InterruptableExecutors;
import edu.umn.msi.tropix.common.concurrent.LoopingRunnable;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;

abstract class CachedSupport<T> {
  private static final Log LOG = LogFactory.getLog(CachedSupport.class);
  private final CountDownLatch isInitialized = new CountDownLatch(1);
  private InterruptableExecutor interruptableExecutor = InterruptableExecutors.getDefault();
  private T cachedInstance = null;
  private Supplier<LoopingRunnable> loopingRunnableSupplier;
  private Executor executor;

  @PostConstruct
  public void init() {
    final Runnable runnable = new Runnable() {
      public void run() {
        T newInstance = null;
        try {
          newInstance = getInstance();
        } catch(final Throwable t) {
          ExceptionUtils.logQuietly(CachedSupport.LOG, t, "Failed to obtain object to cache.");
        }
        if(newInstance != null) {
          cachedInstance = newInstance;
        }
        isInitialized.countDown();
      }
    };
    final LoopingRunnable loopingRunnable = this.loopingRunnableSupplier.get();
    loopingRunnable.setBaseRunnable(runnable);
    executor.execute(loopingRunnable);
  }

  protected abstract T getInstance();

  protected T getCachedInstance() {
    interruptableExecutor.execute(new Interruptable() {
      public void run() throws InterruptedException {
        isInitialized.await();
      }
    });
    return this.cachedInstance;
  }

  public void setLoopingRunnableSupplier(final Supplier<LoopingRunnable> loopingRunnableSupplier) {
    this.loopingRunnableSupplier = loopingRunnableSupplier;
  }

  public void setInterruptableExecutor(final InterruptableExecutor interruptableExecutor) {
    this.interruptableExecutor = interruptableExecutor;
  }

  public void setExecutor(final Executor executor) {
    this.executor = executor;
  }

}
