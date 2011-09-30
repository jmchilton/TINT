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

package edu.umn.msi.tropix.webgui.server.storage.impl;

import java.io.File;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.concurrent.Timer;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.webgui.server.storage.TempFileStore;

public class TempFileStoreImplTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  
  @Test(groups = "unit")
  public void testTempFileStore() {
    final Timer timer = EasyMock.createMock(Timer.class);
    final Capture<Runnable> fileCapture = new Capture<Runnable>();
    assert timer != null;
    assert fileCapture != null;
    timer.schedule(EasyMock.capture(fileCapture), EasyMock.anyLong());
    EasyMock.replay(timer);
    final TempFileStoreImpl store = new TempFileStoreImpl(timer);
    final TempFileStore.TempFileInfo tempFileInfo = store.getTempFileInfo("moo.cow");
    assert tempFileInfo.toString().contains("moo.cow");
    final File tempLocation = tempFileInfo.getTempLocation();
    try {
      FILE_UTILS.touch(tempLocation);
      assert tempLocation.exists();
      
      final TempFileStore.TempFileInfo recoveredInfo = store.recoverTempFileInfo(tempFileInfo.getId());
      assert recoveredInfo.getFileName().equals("moo.cow");
      assert recoveredInfo.getTempLocation().equals(tempLocation);
            
      fileCapture.getValue().run();
      assert !tempLocation.exists();
    } finally {
      FILE_UTILS.deleteQuietly(tempLocation);
    }
  }
}
