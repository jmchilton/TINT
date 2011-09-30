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

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.concurrent.TimeUnit;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * This class is more or less a simple bean describing the behavior a LoopingRunnable
 * should exhibit. How long to wait between executions, how to handle execptions, objects
 * to wait on, etc... can be configured via this class.
 * 
 * @author John Chilton
 *
 */
@ManagedResource
public class LoopingRunnableConfig {
  private static final TimeUnit DEFAULT_WAIT_TIME_UNIT = TimeUnit.MILLISECONDS;
  private static final long DEFAULT_WAIT_TIME = 100;
  private boolean haltOnException = false;
  private UncaughtExceptionHandler uncaughtExceptionHandler = null;
  private TimeUnit waitTimeUnit = LoopingRunnableConfig.DEFAULT_WAIT_TIME_UNIT;
  private long waitTime = LoopingRunnableConfig.DEFAULT_WAIT_TIME;

  public void setWaitTime(final long waitTime, final TimeUnit waitTimeUnit) {
    this.setWaitTime(waitTime);
    this.setWaitTimeUnit(waitTimeUnit);
  }

  public boolean getHaltOnException() {
    return this.haltOnException;
  }

  public void setHaltOnException(final boolean haltOnException) {
    this.haltOnException = haltOnException;
  }

  public UncaughtExceptionHandler getUncaughtExceptionHandler() {
    return this.uncaughtExceptionHandler;
  }

  public void setUncaughtExceptionHandler(final UncaughtExceptionHandler uncaughtExceptionHandler) {
    this.uncaughtExceptionHandler = uncaughtExceptionHandler;
  }

  public TimeUnit getWaitTimeUnit() {
    return this.waitTimeUnit;
  }

  public void setWaitTimeUnit(final TimeUnit waitTimeUnit) {
    this.waitTimeUnit = waitTimeUnit;
  }

  @ManagedAttribute
  public String getWaitTimeUnitAsString() {
    return this.waitTimeUnit.toString();
  }

  @ManagedAttribute
  public void setWaitTimeUnitAsString(final String waitTimeUnitAsString) {
    this.waitTimeUnit = TimeUnit.valueOf(waitTimeUnitAsString);
  }

  @ManagedAttribute
  public long getWaitTime() {
    return this.waitTime;
  }

  @ManagedAttribute
  public void setWaitTime(final long waitTime) {
    this.waitTime = waitTime;
  }
}
