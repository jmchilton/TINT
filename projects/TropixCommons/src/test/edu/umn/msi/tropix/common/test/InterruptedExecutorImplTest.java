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

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.concurrent.Interruptable;
import edu.umn.msi.tropix.common.concurrent.InterruptedRuntimeException;
import edu.umn.msi.tropix.common.concurrent.impl.InterruptableExecutorImpl;

public class InterruptedExecutorImplTest {

  @Test(groups = "unit", expectedExceptions = IllegalStateException.class)
  public void illegalState() {
    final InterruptableExecutorImpl executor = new InterruptableExecutorImpl();
    executor.setMaxInterruptions(-1);

    executor.execute(new Interruptable() {
      public void run() throws InterruptedException {
      }
    });
  }

  @Test(groups = "unit")
  public void beanOps() {
    final InterruptableExecutorImpl executor = new InterruptableExecutorImpl();
    executor.setMaxInterruptions(4);
    executor.setIgnoreInterruptions(true);
    assert executor.isIgnoreInterruptions();
    assert executor.getMaxInterruptions() == 4;

    executor.setMaxInterruptions(2);
    executor.setIgnoreInterruptions(false);
    assert !executor.isIgnoreInterruptions();
    assert executor.getMaxInterruptions() == 2;
  }

  @Test(groups = "unit")
  public void count() {
    final CountingInterruptable i = new CountingInterruptable();
    final InterruptableExecutorImpl executor = new InterruptableExecutorImpl();
    executor.setMaxInterruptions(3);
    executor.setIgnoreInterruptions(false);
    InterruptedException e = null;
    try {
      executor.execute(i);
    } catch(final InterruptedRuntimeException re) {
      e = re.getInterruptedException();
    }
    assert e != null;
    assert e.getMessage().equals("3");
  }

  static class CountingInterruptable implements Interruptable {
    private int count;

    public void run() throws InterruptedException {
      this.count++;
      throw new InterruptedException("" + this.count);
    }
  }

}
