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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;

import edu.umn.msi.tropix.common.concurrent.Haltable;
import edu.umn.msi.tropix.common.concurrent.LoopingRunnable;

@edu.umd.cs.findbugs.annotations.SuppressWarnings(value={"Wa", "UW", "NN"}, justification="Waiting does not need to occur in loops, because failure should result in failed test case.")
public class TreeExecutorServiceImplTest {

  static class ProbedRunnable implements LoopingRunnable {
    private Runnable baseRunnable;
    private boolean called = false;

    public void run() {
      this.called = true;
      synchronized(this) {
        this.notifyAll();
      }
    }

    public void setBaseRunnable(final Runnable runnable) {
      this.baseRunnable = runnable;
    }

    public void setWaitObject(final Object object) {
    }

    public void shutdown() {
    }
  }

  static class SimpleThreadFactory implements ThreadFactory {
    public Thread newThread(final Runnable r) {
      return new Thread(r);
    }
  }

  static class NoNameThreadFactory implements ThreadFactory {
    public Thread newThread(final Runnable r) {
      final Thread t = new Thread(r);
      // t.setName(null); Doesn't work
      t.setName("");
      return t;
    }
  }

  @Test(groups = "unit")
  public void getChildren() {
    final TreeExecutorServiceImpl impl = new TreeExecutorServiceImpl();
    @SuppressWarnings("unchecked")
    final Collection<ExecutorService> services = EasyMock.createMock(Collection.class);
    impl.setChildExecutorServices(services);
    assert impl.getChildExecutorServices() == services;
  }

  @Test(groups = "unit")
  public void setThreadFactory() {
    final GroupingThreadFactory tf = EasyMock.createMock(GroupingThreadFactory.class);
    final ThreadGroupProvider provider = EasyMock.createMock(ThreadGroupProvider.class);
    final ThreadGroup threadGroup = new ThreadGroup(null);
    EasyMock.expect(provider.getThreadGroup()).andReturn(threadGroup);
    tf.setThreadGroup(EasyMock.isA(ThreadGroup.class));
    EasyMock.replay(tf, provider);
    final TreeExecutorServiceImpl impl2 = new TreeExecutorServiceImpl(provider, "moo");
    assert impl2.getThreadGroup().getParent().equals(threadGroup);
    impl2.setThreadFactory(tf);
    EasyMock.verify(tf, provider);
  }

  @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="Dm", justification="It is known it throws UnsupportedOperationException, that is being tested.")
  @Test(groups = "unit", expectedExceptions = UnsupportedOperationException.class)
  public void await() throws InterruptedException {
    final TreeExecutorServiceImpl impl = new TreeExecutorServiceImpl();
    impl.awaitTermination(100, TimeUnit.MILLISECONDS);
  }

  @Test(groups = "unit")
  public void getLoopingExecutor() throws InterruptedException {
    this.getLoopingExecutor(true);
    this.getLoopingExecutor(false);
  }

  public void getLoopingExecutor(final boolean setName) throws InterruptedException {
    final TreeExecutorServiceImpl impl = new TreeExecutorServiceImpl();
    impl.setThreadFactory(new SimpleThreadFactory());
    final ProbedRunnable loopingRunnable = new ProbedRunnable();
    Executor executor;
    if(setName) {
      executor = impl.getLoopingExecutor(Suppliers.<LoopingRunnable>ofInstance(loopingRunnable), "Moo");
    } else {
      executor = impl.getLoopingExecutor(Suppliers.<LoopingRunnable>ofInstance(loopingRunnable));
    }
    final Runnable runnable = EasyMock.createMock(Runnable.class);
    synchronized(loopingRunnable) {
      executor.execute(runnable);
      loopingRunnable.wait();
    }
    assert loopingRunnable.baseRunnable == runnable;
  }

  @Test(groups = "unit")
  public void getExecutors() throws InterruptedException {
    this.getExecutor(false, true);
    this.getExecutor(false, false);
    this.getExecutor(true, true);
  }

  public void getExecutor(final boolean noname, final boolean named) throws InterruptedException {
    final TreeExecutorServiceImpl impl = new TreeExecutorServiceImpl();
    impl.setThreadFactory(noname ? new NoNameThreadFactory() : new SimpleThreadFactory());
    final ProbedRunnable runnable = new ProbedRunnable();
    synchronized(runnable) {
      if(named) {
        impl.getExecutor("name").execute(runnable);
      } else {
        impl.getExecutor().execute(runnable);
      }
      runnable.wait();
    }
    assert runnable.called;
  }

  @Test(groups = {"unit"})
  public void execute() throws InterruptedException {
    final TreeExecutorServiceImpl impl = new TreeExecutorServiceImpl();
    impl.setThreadFactory(new SimpleThreadFactory());
    for(int i = 0; i < 10; i++) {
      assert impl.getTotalThreadCount() == i;
      assert impl.getActiveThreadCount() <= i;
      final ProbedRunnable runnable = new ProbedRunnable();
      synchronized(runnable) {
        impl.execute(runnable);
        runnable.wait();
      }
      assert runnable.called;
    }
  }

  @Test(groups = {"unit"})
  public void cannotExecuteOnShutdown() {
    final ProbedRunnable runnable = new ProbedRunnable();
    final boolean[] testShutdownNowArr = new boolean[] {false, true};
    for(final boolean testShutdownNow : testShutdownNowArr) {
      final TreeExecutorServiceImpl impl = new TreeExecutorServiceImpl();
      impl.setThreadFactory(new SimpleThreadFactory());
      assert !impl.isShutdown();
      if(testShutdownNow) {
        impl.shutdown();
      } else {
        impl.shutdownNow();
      }
      assert impl.isShutdown();

      boolean exceptionThrown = false;
      try {
        impl.execute(runnable);
      } catch(final Throwable t) {
        exceptionThrown = true;
      }
      assert exceptionThrown;

    }
  }

  static class HaltableRunnable implements Runnable, Haltable {
    private final Object shutdownLock = new Object();

    public void run() {
      synchronized(this.shutdownLock) {
        try {
          this.shutdownLock.wait();
        } catch(final InterruptedException e) {
          assert false;
        }
      }
      synchronized(this) {
        this.notifyAll();
      }
    }

    public void shutdown() {
      synchronized(this.shutdownLock) {
        this.shutdownLock.notifyAll();
      }
    }

  }

  @edu.umd.cs.findbugs.annotations.SuppressWarnings(value="SWL", justification="Needed for test.")
  @Test(groups = {"unit"})
  public void shutdown() throws InterruptedException {
    final boolean[] testShutdownNowArr = new boolean[] {false, true};
    for(final boolean testShutdownNow : testShutdownNowArr) {
      final TreeExecutorServiceImpl impl = new TreeExecutorServiceImpl();
      impl.setThreadFactory(new SimpleThreadFactory());
      final HaltableRunnable runnable = new HaltableRunnable();
      impl.execute(runnable);
      synchronized(runnable) {
        Thread.sleep(5);
        if(testShutdownNow) {
          impl.shutdown();
        } else {
          final List<Runnable> runnables = impl.shutdownNow();
          assert runnables.isEmpty();
        }
        assert impl.isShutdown();
        assert !impl.isTerminated();
        runnable.wait();
      }
      Thread.sleep(5);
      assert impl.isTerminated();
    }
  }

  @Test(groups = {"unit"})
  public void childrenShutdown() {
    final ExecutorService executor1 = EasyMock.createMock(ExecutorService.class);
    final ExecutorService executor2 = EasyMock.createMock(ExecutorService.class);

    executor1.shutdown();
    executor2.shutdown();

    EasyMock.expect(executor1.isTerminated()).andReturn(true);
    EasyMock.expect(executor2.isTerminated()).andReturn(false);

    EasyMock.replay(executor1, executor2);

    final List<ExecutorService> executors = new LinkedList<ExecutorService>();
    executors.add(executor1);
    executors.add(executor2);

    final TreeExecutorServiceImpl impl = new TreeExecutorServiceImpl();
    assert !impl.isTerminated();
    impl.setThreadFactory(new SimpleThreadFactory());
    impl.setChildExecutorServices(executors);

    impl.shutdown();
    assert !impl.isTerminated();
    EasyMock.verify(executor1, executor2);
    assert impl.shutdownNow().isEmpty();
  }

  @Test(groups = {"unit"})
  public void childrenShutdownNow() {
    final ExecutorService executor1 = EasyMock.createMock(ExecutorService.class);
    final ExecutorService executor2 = EasyMock.createMock(ExecutorService.class);

    final ProbedRunnable runnable1 = new ProbedRunnable();
    final ProbedRunnable runnable2 = new ProbedRunnable();
    final List<Runnable> runnables1 = Collections.emptyList();
    final List<Runnable> runnables2 = Arrays.asList(new Runnable[] {runnable1, runnable2});
    EasyMock.expect(executor1.shutdownNow()).andReturn(runnables1);
    EasyMock.expect(executor2.shutdownNow()).andReturn(runnables2);

    EasyMock.expect(executor1.isTerminated()).andReturn(true);
    EasyMock.expect(executor2.isTerminated()).andReturn(true);

    EasyMock.replay(executor1, executor2);

    final List<ExecutorService> executors = new LinkedList<ExecutorService>();
    executors.add(executor1);
    executors.add(executor2);

    final TreeExecutorServiceImpl impl = new TreeExecutorServiceImpl();
    impl.setThreadFactory(new SimpleThreadFactory());
    impl.setChildExecutorServices(executors);
    final List<Runnable> runnables = impl.shutdownNow();
    assert runnables.size() == 2 : "Size not two";
    assert runnables.contains(runnable1) : "Does not contain runnable1";
    assert runnables.contains(runnable2) : "Does not contain runnable2";
    assert impl.isTerminated();
    EasyMock.verify(executor1, executor2);
  }
}
