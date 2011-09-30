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

package edu.umn.msi.tropix.common.test;

import org.apache.commons.logging.Log;
import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.logging.IntermittentLog;
import edu.umn.msi.tropix.common.logging.IntermittentLogs;

public class IntermittentLogsTest {

  @Test(groups = "unit")
  public void getLog() {
    final Log log = EasyMock.createMock(Log.class);

    final IntermittentLog iLog = IntermittentLogs.createIntermittentLog(log);

    log.debug("Hello");
    EasyMock.replay(log);
    iLog.getLog().debug("Hello");
    iLog.getLog().debug("World");
    EasyMock.verify(log);
  }

  @Test(groups = "unit")
  public void reset() {
    final Log log = EasyMock.createMock(Log.class);

    final IntermittentLog iLog = IntermittentLogs.createIntermittentLog(log);

    log.debug("Hello");
    log.debug("World");
    EasyMock.replay(log);
    iLog.getLog().debug("Hello");
    iLog.reset();
    iLog.getLog().debug("World");
    EasyMock.verify(log);
  }

}
