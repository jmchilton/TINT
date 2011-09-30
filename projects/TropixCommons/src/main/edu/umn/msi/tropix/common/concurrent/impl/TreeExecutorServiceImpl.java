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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.WeakHashMap;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.concurrent.Haltable;
import edu.umn.msi.tropix.common.concurrent.LoopingRunnable;

@ManagedResource
public class TreeExecutorServiceImpl extends AbstractExecutorService implements ExecutorService, Haltable {
  private Collection<ExecutorService> childExecutorServices = new LinkedList<ExecutorService>();
  private final WeakHashMap<Thread, ThreadInfo> threads = new WeakHashMap<Thread, ThreadInfo>();
  private final Function<Runnable, Runnable> runnableFunction = Functions.identity();
  private ThreadFactory threadFactory = null;
  private ThreadGroup threadGroup = null;
  private boolean shutdown = false;
  private final AtomicLong threadCount = new AtomicLong();

  static class ThreadInfo {
    private final Runnable runnable;

    ThreadInfo(final Runnable runnable) {
      this.runnable = runnable;
    }

    Runnable getRunnable() {
      return runnable;
    }
  }

  public TreeExecutorServiceImpl() {
    this(null, null);
  }

  public TreeExecutorServiceImpl(final ThreadGroupProvider parentThreadGroupProvider, final String threadGroupName) {
    if(parentThreadGroupProvider != null && threadGroupName != null) {
      this.threadGroup = new ThreadGroup(parentThreadGroupProvider.getThreadGroup(), threadGroupName);
    }
  }

  public void setChildExecutorServices(final Collection<ExecutorService> childExecutorServices) {
    this.childExecutorServices = childExecutorServices;
  }

  public void setThreadFactory(final ThreadFactory threadFactory) {
    this.threadFactory = threadFactory;
    if(this.threadGroup != null && threadFactory instanceof GroupingThreadFactory) {
      ((GroupingThreadFactory) threadFactory).setThreadGroup(this.threadGroup);
    }
  }

  public Collection<ExecutorService> getChildExecutorServices() {
    return this.childExecutorServices;
  }

  public ThreadGroup getThreadGroup() {
    return this.threadGroup;
  }

  public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
    throw new UnsupportedOperationException("awaitTermination not supported");
  }

  @ManagedAttribute
  public boolean isShutdown() {
    return this.shutdown;
  }

  @ManagedAttribute
  public boolean isTerminated() {
    if(!this.shutdown) {
      return false;
    }
    for(final ExecutorService executorService : this.childExecutorServices) {
      if(!executorService.isTerminated()) {
        return false;
      }
    }
    synchronized(this.threads) {
      for(final Thread thread : this.threads.keySet()) {
        if(thread.isAlive()) {
          return false;
        }
      }
    }
    return true;
  }

  private List<Runnable> shutdown(final boolean now) {
    if(this.shutdown) {
      return Collections.emptyList();
    }
    List<Runnable> runnables = null;
    if(now) {
      runnables = new LinkedList<Runnable>();
    }
    for(final ExecutorService executorService : this.childExecutorServices) {
      if(now) {
        runnables.addAll(executorService.shutdownNow());
      } else {
        executorService.shutdown();
      }
    }
    synchronized(this.threads) {
      for(final ThreadInfo threadInfo : this.threads.values()) {
        final Runnable runnable = threadInfo.getRunnable();
        if(runnable instanceof Haltable) {
          ((Haltable) runnable).shutdown();
        }
      }
    }
    this.shutdown = true;
    return runnables;
  }

  @ManagedOperation
  public void shutdown() {
    this.shutdown(false);
  }

  @ManagedOperation
  public List<Runnable> shutdownNow() {
    return this.shutdown(true);
  }

  public void execute(final Runnable command) {
    this.execute(command, null);
  }

  /**
   * @return Total number of threads created by the service.
   */
  @ManagedAttribute
  public long getTotalThreadCount() {
    return this.threadCount.get();
  }

  /**
   * @return Estimate on the number of active threads.
   */
  @ManagedAttribute
  public long getActiveThreadCount() {
    return this.threads.size();
  }

  public void execute(final Runnable command, final String name) {
    if(this.isShutdown()) {
      throw new IllegalStateException("ExecutorService has been shutdown.");
    }
    final Runnable runnable = this.runnableFunction.apply(command);
    final ThreadInfo info = new ThreadInfo(runnable);
    final Thread thread = this.threadFactory.newThread(runnable);
    if(name != null) {
      final String threadPrefix = thread.getName();
      if(threadPrefix != null && !threadPrefix.equals("")) {
        thread.setName(thread.getName() + " - " + name);
      } else {
        thread.setName(name);
      }
    }
    this.threadCount.getAndIncrement();
    synchronized(this.threads) {
      thread.start();
      this.threads.put(thread, info);
    }
  }

  public Executor getExecutor() {
    return this;
  }

  public Executor getExecutor(final String name) {
    return new Executor() {
      public void execute(final Runnable command) {
        TreeExecutorServiceImpl.this.execute(command, name);
      }
    };
  }

  public Executor getLoopingExecutor(final Supplier<LoopingRunnable> loopingRunnableSupplier, final String name) {
    return new Executor() {
      public void execute(final Runnable command) {
        final LoopingRunnable loopingRunnable = loopingRunnableSupplier.get();
        loopingRunnable.setBaseRunnable(command);
        if(name != null) {
          TreeExecutorServiceImpl.this.execute(loopingRunnable, name);
        } else {
          TreeExecutorServiceImpl.this.execute(loopingRunnable);
        }
      }
    };
  }

  public Executor getLoopingExecutor(final Supplier<LoopingRunnable> loopingRunnableSupplier) {
    return this.getLoopingExecutor(loopingRunnableSupplier, null);
  }
}
