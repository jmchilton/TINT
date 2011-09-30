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

import java.util.concurrent.TimeUnit;

import org.springframework.jmx.export.annotation.ManagedAttribute;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.google.common.base.Supplier;

@ManagedResource
public class TimeoutTrackerSupplier implements Supplier<TimeoutTracker> {
  private TimeUnit timeUnit = TimeUnit.MILLISECONDS;
  private long duration = 60 * 1000;
  private long maxDuration;

  public TimeoutTrackerSupplier() {
    this.resetMaxDuration();
  }

  private void resetMaxDuration() {
    this.maxDuration = TimeUnit.NANOSECONDS.convert(this.duration, this.timeUnit);
  }

  public void setTimeUnit(final TimeUnit timeUnit) {
    this.timeUnit = timeUnit;
    this.resetMaxDuration();
  }

  @ManagedAttribute
  public String getTimeUnit() {
    return this.timeUnit.toString();
  }

  @ManagedAttribute
  public void setTimeUnit(final String timeUnitString) {
    this.timeUnit = TimeUnit.valueOf(timeUnitString);
  }

  @ManagedAttribute
  public void setDuration(final long duration) {
    this.duration = duration;
    this.resetMaxDuration();
  }

  @ManagedAttribute
  public long getDuration() {
    return this.duration;
  }

  public TimeoutTracker get() {
    return new TimeoutTrackerImpl();
  }

  class TimeoutTrackerImpl implements TimeoutTracker {
    private long since = System.nanoTime();

    public boolean isTimedOut() {
      return System.nanoTime() - this.since > TimeoutTrackerSupplier.this.maxDuration;
    }

    public void reset() {
      this.since = System.nanoTime();
    }
  }
}
