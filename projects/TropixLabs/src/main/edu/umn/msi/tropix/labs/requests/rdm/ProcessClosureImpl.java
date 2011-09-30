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

package edu.umn.msi.tropix.labs.requests.rdm;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;

public class ProcessClosureImpl implements Closure<File> {
  private static final Log LOG = LogFactory.getLog(ProcessClosureImpl.class);
  private ProcessTracker processTracker;
  private Closure<File> uploadClosure;
  private long waitTime;

  public void apply(final File input) {
    try {
      if(processTracker.beenProcessed(input)) {
        return;
      }
      final long lastModified = input.lastModified();
      final long now = System.currentTimeMillis();
      if(lastModified + waitTime > now) {
        return;
      }
      uploadClosure.apply(input);
      try {
        processTracker.process(input);
      } catch(final Throwable t) {
        ExceptionUtils.logQuietly(LOG, t, "Failed to tell process tracker that file " + input + " has been processed, it will likely be reprocessed.");
      }
    } catch(final RuntimeException e) {
      ExceptionUtils.logQuietly(LOG, e);
    }
  }

  public void setProcessTracker(final ProcessTracker processTracker) {
    this.processTracker = processTracker;
  }

  public void setWaitTime(final long waitTime) {
    this.waitTime = waitTime;
  }

  public void setUploadClosure(final Closure<File> uploadClosure) {
    this.uploadClosure = uploadClosure;
  }

}
