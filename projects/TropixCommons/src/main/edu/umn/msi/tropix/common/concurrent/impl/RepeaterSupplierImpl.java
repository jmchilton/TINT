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

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.concurrent.Repeater;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.common.shutdown.ShutdownAware;
import edu.umn.msi.tropix.common.shutdown.ShutdownException;

/**
 * This class supplies instances of the {@link Repeater} interface. Many different
 * options for how to configuration repitition can be configured via the setters in 
 * this class. Additionally, when instatitated as a Spring bean these options may be
 * altered at Runtime via JMX.
 * 
 * @author John Chilton
 *
 * @param <T> Type parameters of Repeaters to create.
 */
@ManagedResource
public class RepeaterSupplierImpl<T extends Runnable> implements Supplier<Repeater<T>>, ShutdownAware {
  private static final Log LOG = LogFactory.getLog(RepeaterSupplierImpl.class);
  private long maxAttempts = -1;
  private long maxSleepDuration = Long.MAX_VALUE;
  private long sleepDurationDelta = 1;
  private long initialSleepDuration = 1;
  private TimeUnit sleepTimeUnit = TimeUnit.SECONDS;
  private final ConcurrentHashMap<Long, RepeaterImpl> repeaterMap = new ConcurrentHashMap<Long, RepeaterImpl>();
  private boolean shutdown = false;

  @ManagedOperation
  public void killRepeater(final long threadId) {
    final RepeaterImpl repeater = this.repeaterMap.get(threadId);
    if(repeater != null) {
      repeater.kill();
    }
  }

  @ManagedOperation
  public String activeRepeaters() {
    final StringBuilder activeRepeaters = new StringBuilder();
    for(final Entry<Long, RepeaterImpl> entry : this.repeaterMap.entrySet()) {
      activeRepeaters.append(entry.getKey() + "[" + entry.getValue() + "]");
    }
    return activeRepeaters.toString();
  }

  public Repeater<T> get() {
    final RepeaterImpl repeaterImpl = new RepeaterImpl();
    return repeaterImpl;
  }

  class RepeaterImpl implements Repeater<T> {
    private T baseRunnable;
    private boolean succeeded = false;
    private long sleepDuration = RepeaterSupplierImpl.this.initialSleepDuration;
    private boolean killed;

    public void kill() {
      this.killed = true;
    }

    public void setBaseRunnable(final T baseRunnable) {
      this.baseRunnable = baseRunnable;
    }

    private void doSleep() {
      try {
        RepeaterSupplierImpl.this.sleepTimeUnit.sleep(this.sleepDuration);
      } catch(final InterruptedException e) {
        ExceptionUtils.logQuietly(LOG, e);
      }
      this.sleepDuration += RepeaterSupplierImpl.this.sleepDurationDelta;
      this.sleepDuration = (this.sleepDuration > RepeaterSupplierImpl.this.maxSleepDuration) ? RepeaterSupplierImpl.this.maxSleepDuration : this.sleepDuration;
    }

    public boolean done(final long attempts) {
      return RepeaterSupplierImpl.this.shutdown || this.succeeded || this.killed || (RepeaterSupplierImpl.this.maxAttempts != -1 && attempts >= RepeaterSupplierImpl.this.maxAttempts);
    }

    public void run() {
      long attempts = 0;
      RuntimeException lastException = null;
      RepeaterSupplierImpl.this.repeaterMap.put(Thread.currentThread().getId(), this);
      try {
        boolean firstAttempt = true;
        while(!this.done(attempts)) {
          if(firstAttempt) {
            firstAttempt = false;
          } else {
            this.doSleep();
          }
          try {
            this.baseRunnable.run();
            this.succeeded = true;
          } catch(final Throwable t) {
            lastException = ExceptionUtils.convertException(t);
          }
          attempts++;
        }
      } finally {
        RepeaterSupplierImpl.this.repeaterMap.remove(Thread.currentThread().getId());
      }
      if(!this.succeeded) {
        if(RepeaterSupplierImpl.this.shutdown) {
          throw new ShutdownException();
        } else {
          throw lastException;
        }
      }
    }

    public String toString() {
      return this.baseRunnable.toString();
    }

  }

  @PreDestroy
  public void destroy() {
    this.shutdown = true;
  }

  public void setSleepTimeUnit(final TimeUnit sleepTimeUnit) {
    this.sleepTimeUnit = sleepTimeUnit;
  }

  @ManagedAttribute
  public void setSleepTimeUnit(final String sleepTimeUnitString) {
    final TimeUnit sleepTimeUnit = Enum.valueOf(TimeUnit.class, sleepTimeUnitString);
    if(sleepTimeUnit != null) {
      this.setSleepTimeUnit(sleepTimeUnit);
    }
  }

  @ManagedAttribute
  public void setMaxAttempts(final long maxAttempts) {
    this.maxAttempts = maxAttempts;
  }

  @ManagedAttribute
  public void setMaxSleepDuration(final long maxSleepDuration) {
    this.maxSleepDuration = maxSleepDuration;
  }

  @ManagedAttribute
  public void setSleepDurationDelta(final long sleepDurationDelta) {
    this.sleepDurationDelta = sleepDurationDelta;
  }

  @ManagedAttribute
  public void setInitialSleepDuration(final long initialSleepDuration) {
    this.initialSleepDuration = initialSleepDuration;
  }

  @ManagedAttribute
  public long getMaxAttempts() {
    return this.maxAttempts;
  }

  @ManagedAttribute
  public long getMaxSleepDuration() {
    return this.maxSleepDuration;
  }

  @ManagedAttribute
  public long getSleepDurationDelta() {
    return this.sleepDurationDelta;
  }

  @ManagedAttribute
  public long getInitialSleepDuration() {
    return this.initialSleepDuration;
  }

  @ManagedAttribute
  public String getSleepTimeUnit() {
    return this.sleepTimeUnit.name();
  }

}