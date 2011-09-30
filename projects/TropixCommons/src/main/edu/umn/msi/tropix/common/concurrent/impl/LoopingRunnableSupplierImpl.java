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

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.concurrent.LoopingRunnable;
import edu.umn.msi.tropix.common.shutdown.ShutdownAware;

/**
 * Given a {@link LoopingRunnableConfig}, this class will supply
 * instances of LoopingRunnable that will conform to that configuration.
 * Additionally, if this class is instantiated in a container that
 * respects the {@link ShutdownAware} interface (i.e. Spring), this class 
 * will shutdown any LoopingRunnables that haven't been GC'ed upon container 
 * shutdown. 
 * 
 * @author John Chilton
 *
 */
public class LoopingRunnableSupplierImpl implements Supplier<LoopingRunnable>, ShutdownAware {
  private LoopingRunnableConfig config;
  private final WeakHashMap<LoopingRunnableImpl, WeakReference<LoopingRunnableImpl>> runnables = new WeakHashMap<LoopingRunnableImpl, WeakReference<LoopingRunnableImpl>>();

  public LoopingRunnable get() {
    final LoopingRunnableImpl loopingRunnable = new LoopingRunnableImpl();
    loopingRunnable.setLoopingRunnableConfig(config);
    runnables.put(loopingRunnable, new WeakReference<LoopingRunnableImpl>(loopingRunnable));
    return loopingRunnable;
  }

  public void setLoopingRunnableConfig(final LoopingRunnableConfig config) {
    this.config = config;
  }

  public void destroy() throws Exception {
    for(final LoopingRunnableImpl impl : runnables.keySet()) {
      impl.shutdown();
    }
  }

}
