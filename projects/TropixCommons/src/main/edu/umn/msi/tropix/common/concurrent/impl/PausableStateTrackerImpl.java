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
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import edu.umn.msi.tropix.common.concurrent.PausableCallback;
import edu.umn.msi.tropix.common.concurrent.PausableStateTracker;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;

/**
 * This is the default implementation of the {@link PausableStateTracker} interface.
 * Startup and shutdowns can be signaled from JMX when this class is instatiated 
 * as a Spring bean.
 * 
 * @author John Chilton
 *
 */
@ManagedResource
public class PausableStateTrackerImpl implements PausableStateTracker {
  private static final Log LOG = LogFactory.getLog(PausableStateTrackerImpl.class);
  private boolean paused = false;
  private String id = "<Anonymous>";
  private final List<PausableCallback> callbacks = new LinkedList<PausableCallback>();

  public PausableStateTrackerImpl() {
    this(false);
  }

  public PausableStateTrackerImpl(final boolean paused) {
    this.paused = paused;
  }

  @ManagedAttribute
  public boolean isPaused() {
    return this.paused;
  }

  @ManagedOperation
  public synchronized void startup() {
    if(!this.paused) {
      LOG.info("startup() called on tracker which was already started. This will be ignored!");
      return;
    }
    LOG.debug("StateTracker " + this.id + " starting up");
    this.paused = false;
    for(final PausableCallback callback : this.callbacks) {
      try {
        callback.onStartup();
      } catch(final Throwable t) {
        ExceptionUtils.logQuietly(PausableStateTrackerImpl.LOG, t, "Executing onStartup() on PausableCallback " + callback);
      }
    }
  }

  @ManagedOperation
  public synchronized void shutdown() {
    if(this.paused) {
      PausableStateTrackerImpl.LOG.info("shutdown() called on tracker which was already shutdown. This will be ignored!");
      return;
    }
    PausableStateTrackerImpl.LOG.info("StateTracker " + this.id + " shuting down up");
    this.paused = true;
    for(final PausableCallback callback : this.callbacks) {
      try {
        callback.onShutdown();
      } catch(final Throwable t) {
        ExceptionUtils.logQuietly(PausableStateTrackerImpl.LOG, t, "Executing onShutdown() on PausableCallback " + callback);
      }
    }
  }

  public void registerPausableCallback(final PausableCallback callback) {
    this.callbacks.add(callback);
  }

  public void unregisterPausableCallback(final PausableCallback callback) {
    this.callbacks.remove(callback);
  }

  @ManagedAttribute
  public void setId(final String id) {
    this.id = id;
  }

  @ManagedAttribute
  public String getId() {
    return this.id;
  }
}
