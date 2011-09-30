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

package edu.umn.msi.tropix.common.logging;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.NoOpLog;

public class IntermittentLogs {
  private static final Log NOP_LOG = new NoOpLog();

  public static IntermittentLog createIntermittentLog(final Log log) {
    return new IntermittentLogImpl(log);
  }

  private static class IntermittentLogImpl implements IntermittentLog {
    private final Log baseLog;
    private Log activeLog;

    IntermittentLogImpl(final Log baseLog) {
      this.baseLog = baseLog;
      this.reset();
    }

    public Log getLog() {
      final Log log = this.activeLog;
      this.activeLog = IntermittentLogs.NOP_LOG;
      return log;
    }

    public void reset() {
      this.activeLog = this.baseLog;
    }
  }

}
