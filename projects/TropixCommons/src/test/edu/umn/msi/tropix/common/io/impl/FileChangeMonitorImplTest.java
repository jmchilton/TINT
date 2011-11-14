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

package edu.umn.msi.tropix.common.io.impl;

import java.io.File;
import java.util.concurrent.Executor;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.test.EasyMockUtils;

public class FileChangeMonitorImplTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();

  @Test(groups = "unit")
  public void monitorChanges() throws InterruptedException {
    final FileChangeMonitorImpl monitor = new FileChangeMonitorImpl();
    final Executor executor = EasyMock.createMock(Executor.class);
    monitor.setLoopingExecutor(executor);

    final Capture<Runnable> runnableCapture = EasyMockUtils.newCapture();
    executor.execute(EasyMock.capture(runnableCapture));
    EasyMock.replay(executor);

    final File tempFile = FILE_UTILS.createTempFile();
    tempFile.deleteOnExit();
    FILE_UTILS.touch(tempFile);
    @SuppressWarnings("unchecked")
    final Closure<File> closure = EasyMock.createMock(Closure.class);
    monitor.registerChangeListener(tempFile, closure);

    EasyMockUtils.verifyAndReset(executor);
    // Make sure closure isn't called
    runnableCapture.getValue().run();
    Thread.sleep(1000); // Without this sleep, Ubuntu on my workstation doesn't seem to recognize it as a different time.
    FILE_UTILS.touch(tempFile);
    closure.apply(tempFile);
    EasyMock.replay(closure);
    runnableCapture.getValue().run();
    EasyMockUtils.verifyAndReset(closure);

  }

}
