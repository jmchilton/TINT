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

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.umn.msi.tropix.common.concurrent.GrouppedExecutor;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.common.shutdown.ShutdownException;

class GrouppedFifoExecutorImpl<G> implements GrouppedExecutor<G> {
  private static final Log LOG = LogFactory.getLog(GrouppedFifoExecutorImpl.class);
  private final ConcurrentHashMap<G, Entry> entries = new ConcurrentHashMap<G, Entry>();
  private Executor executor;

  private static Runnable composeRunnables(final Runnable r1, final Runnable r2) {
    return new Runnable() {
      public void run() {
        try {
          r1.run();
        } catch(final ShutdownException se) {
          throw se;
        } catch(final RuntimeException e) {
          ExceptionUtils.logQuietly(LOG, e);
        }
        r2.run();
      }
    };
  }

  private class Entry implements Runnable {
    private final Queue<Runnable> runnables = new LinkedList<Runnable>();
    private boolean active = false;

    Entry(final G group) {
    }

    public void addRunnable(final Runnable runnable) {
      runnables.add(runnable);
    }

    public void run() {
      if(!this.active && !this.runnables.isEmpty()) {
        final Runnable baseRunnable = this.runnables.remove();
        this.active = true;
        executor.execute(GrouppedFifoExecutorImpl.composeRunnables(baseRunnable, new Runnable() {
          public void run() {
            synchronized(Entry.this) {
              active = false;
              Entry.this.run();
            }
          }
        }));
      }
    }
  }

  public void execute(final G group, final Runnable runnable) {
    final Entry newEntryIfNeeded = new Entry(group);
    Entry entry = entries.putIfAbsent(group, newEntryIfNeeded);
    if(entry == null) {
      entry = newEntryIfNeeded;
    }
    synchronized(entry) {
      entry.addRunnable(runnable);
      entry.run();
    }
  }

  public void setExecutor(final Executor executor) {
    this.executor = executor;
  }

}
