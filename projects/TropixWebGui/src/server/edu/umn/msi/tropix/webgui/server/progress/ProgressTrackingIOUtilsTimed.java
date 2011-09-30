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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProgressTrackingIOUtilsTimed implements ProgressTrackingIOUtils {
  private int bufferSize = 4098;
  private long updateDelta;

  public void setBufferSize(final int bufferSize) {
    this.bufferSize = bufferSize;
  }

  public void setUpdateDelta(final long updateDelta) {
    this.updateDelta = updateDelta;
  }

  public long copy(final InputStream inputStream, final OutputStream outputStream, final long size, final ProgressTracker tracker) throws IOException {
    return copy(inputStream, outputStream, 0, size, tracker);
  }

  public long copy(final InputStream inputStream, final OutputStream outputStream, final long start, final long end, final ProgressTracker tracker) throws IOException {
    tracker.update((float) (((double) start) / ((double) end)));
    long lastUpdate = System.currentTimeMillis();
    final byte[] buffer = new byte[this.bufferSize];
    long totalBytesWritten = start;
    int bytesWritten;
    int iteration = 0;
    while((bytesWritten = inputStream.read(buffer)) > 0) {
      outputStream.write(buffer, 0, bytesWritten);
      totalBytesWritten += bytesWritten;
      // Its overkill to hit the system clock each iteration, so only check
      // every few iterations...
      iteration = (iteration + 1) % 5;
      if(iteration == 0) {
        final long curTime = System.currentTimeMillis();
        //LOG.trace("Checking time difference  " + (curTime - lastUpdate) + " > " + this.updateDelta);
        if((curTime - lastUpdate) > this.updateDelta) {
          //LOG.trace("Trying to update tracker");
          tracker.update(((float) totalBytesWritten) / end);
          lastUpdate = curTime;
        }
      }
    }
    return totalBytesWritten;
  }

}
