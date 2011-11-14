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
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.common.concurrent.LoopingRunnable;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.test.MockObjectCollection;

public class DirectoryMonitorTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();

  private DirectoryMonitorImpl rdm = new DirectoryMonitorImpl();
  private SpringDirectoryMonitor monitor = new SpringDirectoryMonitor();

  private Executor executor;
  private LoopingRunnable loopingRunnable;
  private Supplier<LoopingRunnable> loopingRunnableSupplier;
  private Closure<File> closure;
  private Capture<Runnable> runnableCapture;
  private MockObjectCollection mockObjects;

  @BeforeMethod(groups = "unit")
  public void init() {
    executor = EasyMock.createMock(Executor.class);
    loopingRunnable = EasyMock.createMock(LoopingRunnable.class);
    loopingRunnableSupplier = Suppliers.ofInstance(loopingRunnable);
    closure = EasyMock.createMock(Closure.class);
    runnableCapture = new Capture<Runnable>();

    rdm.setExecutor(executor);
    rdm.setFileClosure(closure);
    rdm.setLoopingRunnableSupplier(loopingRunnableSupplier);

    monitor.setDirectoryMonitor(rdm);
    mockObjects = MockObjectCollection.fromObjects(executor, loopingRunnable, closure);
  }

  @Test(groups = "unit")
  public void directory() {
    final File directory = FILE_UTILS.createTempDirectory();
    try {
      loopingRunnable.setBaseRunnable(EasyMock.capture(runnableCapture));
      executor.execute(loopingRunnable);
      mockObjects.replay();

      monitor.setDirectory(directory);
      monitor.init();
      mockObjects.verifyAndReset();

      mockObjects.replay();
      final Runnable checker = runnableCapture.getValue();
      checker.run();
      mockObjects.verifyAndReset();

      final File file1 = new File(directory, "moo.txt");
      FILE_UTILS.touch(file1);
      this.closure.apply(file1);
      mockObjects.replay();
      checker.run();
      mockObjects.verifyAndReset();

      final File subDir = new File(directory, "subdir");
      FILE_UTILS.mkdir(subDir);
      final File file2 = new File(subDir, "file2.exe");
      FILE_UTILS.touch(file2);

      this.closure.apply(file1);
      this.closure.apply(file2);
      mockObjects.replay();
      checker.run();
      mockObjects.verifyAndReset();
    } finally {
      FILE_UTILS.deleteDirectory(directory);
    }
  }

}
