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

package edu.umn.msi.tropix.webgui.server.progress;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.EasyMockUtils;

public class ProgressTrackingIOUtilsTimedTest {

  @Test(groups = "unit")
  public void testCopy() throws IOException {
    final ProgressTrackingIOUtilsTimed ioUtils = new ProgressTrackingIOUtilsTimed();
    ioUtils.setBufferSize(1);
    ioUtils.setUpdateDelta(-1);
    
    final byte[] bytes = "this is the string don't ya know".getBytes();
    final ByteArrayInputStream input = new ByteArrayInputStream(bytes);
    final ByteArrayOutputStream output = new ByteArrayOutputStream();

    final ProgressTracker tracker = EasyMock.createMock(ProgressTracker.class);
    final Capture<Float> capture = EasyMockUtils.newCapture();
    tracker.update(EasyMock.capture(capture));
    EasyMock.expectLastCall().anyTimes();
    EasyMock.replay(tracker);
    ioUtils.copy(input, output, bytes.length, tracker);
    // Just testing the copying right now, later should verify progress tracker is being called appropriately
    assert Arrays.equals(bytes, output.toByteArray());
    assert capture.getValues().size() > 0;
    // Assure the percent is increasing
    float lastUpdate = -1;
    for(Float update : capture.getValues()) {
      assert update > lastUpdate;
      lastUpdate = update;
    }
    
  }
  
}
