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

import java.lang.Thread.UncaughtExceptionHandler;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.concurrent.impl.ThreadFactoryImpl;

public class ThreadFactoryImplTest {

  class TestUncaughtExceptionHandler implements UncaughtExceptionHandler {
    private boolean called = false;

    public void uncaughtException(final Thread t, final Throwable e) {
      this.called = true;
    }
  }

  static class TestRunnable implements Runnable {
    private boolean called = false;

    public void run() {
      this.called = true;
    }
  }

  static class ExceptionRunnable implements Runnable {
    public void run() {
      throw new IllegalStateException();
    }
  }

  @Test(groups = {"unit"})
  public void groupingThreadFactory() throws InterruptedException {
    this.threadFactory(false);
  }

  @Test(groups = {"unit"})
  public void nongroupingThreadFactory() throws InterruptedException {
    this.threadFactory(true);
  }

  public void threadFactory(final boolean nullThreadGroup) throws InterruptedException {
    final ThreadFactoryImpl threadFactory = new ThreadFactoryImpl();
    final ThreadGroup threadGroup = nullThreadGroup ? null : new ThreadGroup("Group");
    threadFactory.setName("TestThread");
    threadFactory.setDaemon(false);
    threadFactory.setPriority(Thread.MAX_PRIORITY);
    threadFactory.setThreadGroup(threadGroup);
    if(nullThreadGroup) {
      assert threadFactory.getThreadGroup() == null;
    } else {
      assert threadFactory.getThreadGroup() != null;
    }
    final TestUncaughtExceptionHandler eh = new TestUncaughtExceptionHandler();
    threadFactory.setUncaughtExceptionHandler(eh);
    final TestRunnable testRunnable = new TestRunnable();
    final Thread thread1 = threadFactory.newThread(testRunnable);
    if(!nullThreadGroup) {
      assert thread1.getThreadGroup().equals(threadGroup);
    }
    thread1.start();
    thread1.join();
    assert testRunnable.called;
    assert thread1.getPriority() == Thread.MAX_PRIORITY;
    assert thread1.getName().equals("TestThread");
    assert !thread1.isDaemon();
    assert !thread1.isAlive();
    assert !eh.called;
    threadFactory.setPriority(Thread.MIN_PRIORITY);
    threadFactory.setName("EThread");
    final ExceptionRunnable exceptionRunnable = new ExceptionRunnable();
    final Thread eThread = threadFactory.newThread(exceptionRunnable);
    assert !eh.called;
    eThread.start();
    try {
      eThread.join();
    } catch(final InterruptedException e) {
      assert false;
    }
    assert eh.called;
    assert eThread.getName().equals("EThread");
    assert eThread.getPriority() == Thread.MIN_PRIORITY;
  }
}
