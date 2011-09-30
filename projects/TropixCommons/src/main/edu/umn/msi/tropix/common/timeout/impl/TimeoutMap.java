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

package edu.umn.msi.tropix.common.timeout.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import javax.annotation.concurrent.GuardedBy;

import com.google.common.base.Supplier;
import com.google.common.collect.ForwardingMap;

public class TimeoutMap<K, V> extends ForwardingMap<K, V> {
  private final Object lock = new Object();
  private final Map<K, TimeoutTracker> putTimeMap = new ConcurrentHashMap<K, TimeoutTracker>();
  @GuardedBy("lock") private Executor loopingExecutor = null;
  @GuardedBy("lock") private Runnable maintenanceRunnable = null;
  private Supplier<TimeoutTracker> timeoutTrackerSupplier;
  private Map<K, V> delegate;

  public TimeoutMap() {
    this(new HashMap<K, V>());
  }

  public TimeoutMap(final Map<K, V> delegate) {
    this.delegate = delegate;
  }

  protected void registerKey(final K key) {
    final TimeoutTracker tracker = this.timeoutTrackerSupplier.get();
    this.putTimeMap.put(key, tracker);
    boolean initialized = false;
    synchronized(lock) {
      if(this.maintenanceRunnable == null) {
        this.maintenanceRunnable = new Runnable() {
          public void run() {
            final Iterator<Map.Entry<K, TimeoutTracker>> entryIterator = TimeoutMap.this.putTimeMap.entrySet().iterator();
            while(entryIterator.hasNext()) {
              final Map.Entry<K, TimeoutTracker> entry = entryIterator.next();
              if(entry.getValue().isTimedOut()) {
                final K key = entry.getKey();
                entryIterator.remove();
                TimeoutMap.this.remove(key);
              }
            }
          }
        };
        initialized = true;
      }
    }
    if(initialized) {
      this.loopingExecutor.execute(this.maintenanceRunnable);
    }
  }

  @Override
  public V put(final K key, final V value) {
    this.registerKey(key);
    return super.put(key, value);
  }

  @Override
  public void putAll(final Map<? extends K, ? extends V> map) {
    for(final K key : map.keySet()) {
      this.registerKey(key);
    }
    super.putAll(map);
  }

  public void setLoopingExecutor(final Executor loopingExecutor) {
    this.loopingExecutor = loopingExecutor;
  }

  public void setTimeoutTrackerSupplier(final Supplier<TimeoutTracker> timeoutTrackerSupplier) {
    this.timeoutTrackerSupplier = timeoutTrackerSupplier;
  }

  protected Map<K, V> delegate() {
    return this.delegate;
  }
}
