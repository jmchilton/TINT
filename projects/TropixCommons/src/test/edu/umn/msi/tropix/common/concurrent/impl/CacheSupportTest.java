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

import java.util.concurrent.Executor;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;

import edu.umn.msi.tropix.common.concurrent.LoopingRunnable;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.EasyMockUtils.Reference;

public class CacheSupportTest {

  static class PublicCachedSupport extends CachedSupport<Object> {
    private int called = 0;
    private Object instance;

    public Object getInstance() {
      this.called++;
      return this.instance;
    }

    @Override
    public Object getCachedInstance() {
      return super.getCachedInstance();
    }
  }

  static class GetCachedInstanceRunnable implements Runnable {
    private PublicCachedSupport cacher;
    private boolean finished = false;

    public void run() {
      try {
        this.cacher.getCachedInstance();
      } catch(final Throwable t) {
        t.printStackTrace();
      }
      this.finished = true;
    }

  }

  @Test(groups = "unit", timeOut=1000)
  public void cacheTest() throws InterruptedException {
    final Executor executor = EasyMock.createMock(Executor.class);
    final LoopingRunnable loopingRunnable = EasyMock.createMock(LoopingRunnable.class);
    final PublicCachedSupport cacher = new PublicCachedSupport();
    final GetCachedInstanceRunnable runnable = new GetCachedInstanceRunnable();
    runnable.cacher = cacher;
    final Thread t = new Thread(runnable);
    t.start();
    assert !runnable.finished;
    assert t.isAlive();
    cacher.setExecutor(executor);
    cacher.setLoopingRunnableSupplier(Suppliers.ofInstance(loopingRunnable));
    final Reference<Runnable> runnableReference = EasyMockUtils.newReference();
    loopingRunnable.setBaseRunnable(EasyMockUtils.record(runnableReference));
    executor.execute(loopingRunnable);
    EasyMock.replay(executor, loopingRunnable);
    cacher.init();
    assert !runnable.finished;
    assert t.isAlive();
    EasyMockUtils.verifyAndReset(executor, loopingRunnable);
    Object object = new Object();
    cacher.instance = object;
    runnableReference.get().run();
    assert cacher.called == 1;
    assert cacher.getCachedInstance() == object;
    t.join();
    assert runnable.finished;
    object = new Object();
    assert cacher.getCachedInstance() != object;
    cacher.instance = object;
    runnableReference.get().run();
    assert cacher.called == 2;
    assert cacher.getCachedInstance() == object;

  }
}
