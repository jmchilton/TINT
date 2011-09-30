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

package edu.umn.msi.tropix.common.concurrent;

import java.util.HashMap;

/**
 * Produces objects implementing the {@link InitializationTracker}
 * interface. 
 * 
 * @author John Chilton
 *
 */
public class InitializationTrackers {

  public static <K> InitializationTracker<K> getDefault() {
    return new InitializationTrackerImpl<K>();
  }
  
  private static class InitializationTrackerImpl<K> implements InitializationTracker<K> {
    enum State {
      UNINITIALIZED, INITIALIZED, FAILED
    }

    private HashMap<K, StateContainer> map = new HashMap<K, StateContainer>();
    private final InterruptableExecutor interruptableExecutor = InterruptableExecutors.getDefault();
    private boolean allInitialized = false;
    private final Object lock = new Object();

    private StateContainer getContainer(final K key) {
      StateContainer container = null;
      if(this.map.containsKey(key)) {
        container = this.map.get(key);
      } else {
        container = new StateContainer();
        container.state = State.UNINITIALIZED;
        this.map.put(key, container);
      }
      return container;
    }

    private void setState(final K key, final State state) {
      if(this.allInitialized) { // For effeciency do a check outside the lock
        return;
      }
      synchronized(this.lock) {
        if(!this.allInitialized) {
          final StateContainer container = this.getContainer(key);
          container.state = state;
          this.lock.notifyAll();
        }
      }
    }

    public void initialize(final K key) {
      this.setState(key, State.INITIALIZED);
    }

    /**
     * PRECONDITION: initializeAll has not been called.
     */
    public void fail(final K key) {
      this.setState(key, State.FAILED);
    }

    public void waitForInitialization(final K key) throws InitializationTracker.InitializationFailedException {
      if(this.allInitialized) {
        return;
      }
      synchronized(this.lock) {
        if(!this.allInitialized) {
          final StateContainer container = this.getContainer(key);
          while(!this.allInitialized && container.state.equals(State.UNINITIALIZED)) {
            this.waitForLock();
          }
          if(container.state.equals(State.FAILED)) {
            throw new InitializationFailedException();
          }
        }
      }
    }

    public void initializeAll() {
      synchronized(this.lock) {
        this.allInitialized = true;
        this.lock.notifyAll();
        this.map = null; // Free map memory
      }
    }

    public void waitForAllInitialization() {
      if(this.allInitialized) {
        return;
      }
      synchronized(this.lock) {
        while(!this.allInitialized) {
          this.waitForLock();
        }
      }
    }

    private void waitForLock() {
      this.interruptableExecutor.execute(new Interruptable() {
        public void run() throws InterruptedException {
          InitializationTrackerImpl.this.lock.wait();
        }
      });
    }

    private static class StateContainer {
      private State state;
    }
  }

}
