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
import java.io.IOException;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.test.MockObjectCollection;

public class ProcessClosureImplTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private ProcessClosureImpl processClosure;
  private ProcessTracker processTracker;
  private MockObjectCollection mockObjects;
  private Closure<File> uploadClosure;

  @BeforeMethod(groups = "unit")
  public void init() {
    processTracker = EasyMock.createMock(ProcessTracker.class);
    uploadClosure = EasyMock.createMock(Closure.class);

    processClosure = new ProcessClosureImpl();
    processClosure.setProcessTracker(processTracker);
    processClosure.setUploadClosure(uploadClosure);

    mockObjects = MockObjectCollection.fromObjects(processTracker, uploadClosure);
  }

  @Test(groups = "unit")
  public void hasBeenProcessed() {
    final File file = new File("moo");
    EasyMock.expect(processTracker.beenProcessed(file)).andReturn(true);
    mockObjects.replay();
    processClosure.apply(file);
    mockObjects.verifyAndReset();
  }

  @Test(groups = "unit")
  public void hasBeenProcessedOldEnough() throws IOException, InterruptedException {
    final File file = File.createTempFile("tpx", "moo");
    try {
      Thread.sleep(1);
      EasyMock.expect(processTracker.beenProcessed(file)).andReturn(true);
      mockObjects.replay();
      processClosure.setWaitTime(1L);
      processClosure.apply(file);
      mockObjects.verifyAndReset();
    } finally {
      FILE_UTILS.deleteQuietly(file);
    }
  }

  @Test(groups = "unit")
  public void tooNew() throws IOException {
    final File file = File.createTempFile("tpx", "moo");
    try {
      processClosure.setWaitTime(15 * 60 * 1000L);
      EasyMock.expect(processTracker.beenProcessed(file)).andReturn(false);
      mockObjects.replay();
      processClosure.apply(file);
      mockObjects.verifyAndReset();
    } finally {
      FILE_UTILS.deleteQuietly(file);
    }
  }

  @Test(groups = "unit")
  public void processPersistFailure() throws IOException, InterruptedException {
    final File file = File.createTempFile("tpx", "moo");
    try {
      Thread.sleep(1);
      EasyMock.expect(processTracker.beenProcessed(file)).andReturn(false);
      uploadClosure.apply(file);
      processTracker.process(file);
      EasyMock.expectLastCall().andThrow(new IllegalStateException("Database problem..."));
      mockObjects.replay();
      processClosure.setWaitTime(0L);
      processClosure.apply(file);
      mockObjects.verifyAndReset();
    } finally {
      FILE_UTILS.deleteQuietly(file);
    }
  }

  @Test(groups = "unit")
  public void processNormally() throws IOException, InterruptedException {
    final File file = File.createTempFile("tpx", "moo");
    try {
      Thread.sleep(1);
      EasyMock.expect(processTracker.beenProcessed(file)).andReturn(false);
      uploadClosure.apply(file);
      processTracker.process(file);
      mockObjects.replay();
      processClosure.setWaitTime(0L);
      processClosure.apply(file);
      mockObjects.verifyAndReset();
    } finally {
      FILE_UTILS.deleteQuietly(file);
    }
  }

  @Test(groups = "unit")
  public void uploadException() throws IOException, InterruptedException {
    final File file = File.createTempFile("tpx", "moo");
    try {
      Thread.sleep(1);
      EasyMock.expect(processTracker.beenProcessed(file)).andReturn(false);
      uploadClosure.apply(file);
      EasyMock.expectLastCall().andThrow(new RuntimeException());
      mockObjects.replay();
      processClosure.setWaitTime(0L);
      processClosure.apply(file);
      mockObjects.verifyAndReset();
    } finally {
      FILE_UTILS.deleteQuietly(file);
    }

  }

}
