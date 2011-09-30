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

import javax.annotation.Nullable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import edu.umn.msi.tropix.common.logging.ExceptionUtils;

class UncaughtExceptionHandlerUtils {
  private static final Log LOG = LogFactory.getLog(UncaughtExceptionHandlerUtils.class);

  public static void handleException(@Nullable final UncaughtExceptionHandler handler, final Throwable t) {
    final Thread currentThread = Thread.currentThread();
    UncaughtExceptionHandler eh;
    if(handler != null) {
      eh = handler;
    } else {
      eh = currentThread.getUncaughtExceptionHandler();
    }
    try {
      eh.uncaughtException(currentThread, t);
    } catch(final Throwable exceptionThrowable) {
      ExceptionUtils.logQuietly(LOG, exceptionThrowable);
    }
  }
}
