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

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import edu.umn.msi.tropix.common.concurrent.Interruptable;
import edu.umn.msi.tropix.common.concurrent.InterruptableExecutor;
import edu.umn.msi.tropix.common.concurrent.InterruptedRuntimeException;

/**
 * This class is an {@link InterruptableExecutor} that can be specified to
 * ignore interruptions ({@code ignoreInterruptions}) or fail after a 
 * certain number ({@code maxInterruptions}). These parameters can be configured
 * from JMX if this class is instantiated as a Spring bean.
 * 
 * @author John Chilton
 *
 */
@ManagedResource
public class InterruptableExecutorImpl implements InterruptableExecutor {
  private int maxInterruptions = 1;
  private boolean ignoreInterruptions = true;

  public void execute(final Interruptable interruptable) {
    int numInterruptions = 0;
    InterruptedException lastInterruption = null;
    if(this.maxInterruptions <= 0) {
      throw new IllegalStateException("maxInterruptions must be positive");
    }
    boolean complete = false;
    while(!complete && (this.ignoreInterruptions || numInterruptions < this.maxInterruptions)) {
      try {
        interruptable.run();
        complete = true;
      } catch(final InterruptedException interruptedException) {
        lastInterruption = interruptedException;
        numInterruptions++;
      }
    }
    if(!complete) {
      throw new InterruptedRuntimeException(lastInterruption);
    }
  }

  @ManagedOperation
  public int getMaxInterruptions() {
    return this.maxInterruptions;
  }

  @ManagedOperation
  public void setMaxInterruptions(final int maxInterruptions) {
    this.maxInterruptions = maxInterruptions;
  }

  @ManagedOperation
  public boolean isIgnoreInterruptions() {
    return this.ignoreInterruptions;
  }

  @ManagedOperation
  public void setIgnoreInterruptions(final boolean ignoreInterruptions) {
    this.ignoreInterruptions = ignoreInterruptions;
  }
}
