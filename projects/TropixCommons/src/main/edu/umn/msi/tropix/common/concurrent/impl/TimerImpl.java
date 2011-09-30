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

import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.umn.msi.tropix.common.concurrent.Timer;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.common.shutdown.ShutdownAware;

/**
 * The default implementation of the Timer interface that wraps a java.util.Timer 
 * and cancels all events upon shutdown.
 * 
 * @author John Chilton
 *
 */
public class TimerImpl implements Timer, ShutdownAware {
  private static final Log LOG = LogFactory.getLog(TimerImpl.class);
  private final java.util.Timer timer = new java.util.Timer(true);

  public synchronized void schedule(final Runnable runnable, final long delay) {
    timer.schedule(new TimerTask() {
      public void run() {
        try {
          runnable.run();
        } catch(final RuntimeException e) {
          ExceptionUtils.logQuietly(LOG, e);
        }
      }
    }, delay);
  }

  public synchronized void destroy() {
    timer.cancel();
  }

}
