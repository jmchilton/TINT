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

package edu.umn.msi.tropix.persistence.service.test;

import java.util.Collection;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.Request;
import edu.umn.msi.tropix.models.ScaffoldAnalysis;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.persistence.service.FileService;

public class FileServiceGetFilesTest extends ServiceTest {
  @Autowired
  private FileService fileService;

  private User user;
  private Collection<String> inputIds;
  private Collection<String> expectedFileIds;

  @BeforeMethod
  public void init() {
    user = createTempUser();

    inputIds = Lists.newLinkedList();
    expectedFileIds = Lists.newLinkedList();
  }

  private void check() {
    final TropixFile[] files = fileService.getFiles(user.getCagridId(), inputIds.toArray(new String[] {}));
    assert files.length == expectedFileIds.size();
    for(final TropixFile file : files) {
      assert expectedFileIds.contains(file.getId()) : file.getId() + " not found";
    }
  }

  @Test
  public void simple() {
    final TropixFile file = new TropixFile();
    super.saveNewTropixObject(file, user);

    inputIds.add(file.getId());
    check();

    file.setCommitted(true);
    getTropixObjectDao().saveOrUpdateTropixObject(file);
    expectedFileIds.add(file.getId());
    check();

  }

  @Test
  public void folder() {
    inputIds.add(user.getHomeFolder().getId());
    check();

    final TropixFile file1 = getAndExpectNewFile();
    getTropixObjectDao().addToFolder(user.getHomeFolder().getId(), file1.getId());
    check();

    final TropixFile file2 = getAndExpectNewFile();
    getTropixObjectDao().addToFolder(user.getHomeFolder().getId(), file2.getId());
    check();

    final Folder folder = new Folder();
    folder.setContents(new HashSet<TropixObject>());
    super.saveNewTropixObject(folder, user);
    getTropixObjectDao().addToFolder(user.getHomeFolder().getId(), folder.getId());
    check();

    final TropixFile file3 = getAndExpectNewFile();
    getTropixObjectDao().addToFolder(folder.getId(), file3.getId());
    check();

  }

  @Test
  public void database() {
    final Database database = new Database();
    super.saveNewTropixObject(database, user);
    this.inputIds.add(database.getId());

    final TropixFile dbFile = getAndExpectNewFile();
    database.setDatabaseFile(dbFile);
    super.getTropixObjectDao().saveOrUpdateTropixObject(database);
    check();

  }

  private TropixFile getNewFile() {
    final TropixFile file = new TropixFile();
    file.setCommitted(true);
    super.saveNewTropixObject(file, user);
    return file;
  }

  private TropixFile getAndExpectNewFile() {
    final TropixFile file = getNewFile();
    this.expectedFileIds.add(file.getId());
    return file;
  }

  @Test
  public void proteomicsAnalyses() {
    final IdentificationAnalysis idAnalysis = new IdentificationAnalysis();
    super.saveNewTropixObject(idAnalysis, user);

    this.inputIds.add(idAnalysis.getId());

    final TropixFile output = getAndExpectNewFile();
    idAnalysis.setOutput(output);
    super.getTropixObjectDao().saveOrUpdateTropixObject(idAnalysis);
    check();

    final ScaffoldAnalysis scaffoldAnalysis = new ScaffoldAnalysis();
    super.saveNewTropixObject(scaffoldAnalysis, user);
    this.inputIds.add(scaffoldAnalysis.getId());

    final TropixFile sOutput = getAndExpectNewFile();
    scaffoldAnalysis.setOutputs(sOutput);
    super.getTropixObjectDao().saveOrUpdateTropixObject(sOutput);
    check();
  }

  @Test
  public void request() {
    final Request request = new Request();
    request.setContents(new HashSet<TropixObject>());
    super.saveNewTropixObject(request, user);
    this.inputIds.add(request.getId());
    this.check();

    final TropixFile file = getAndExpectNewFile();
    request.getContents().add(file);
    super.getTropixObjectDao().saveOrUpdateTropixObject(request);
    this.check();
  }

  @Test
  public void virtualFolder() {
    final VirtualFolder root = createTempRootVirtualFolder();
    super.getTropixObjectDao().addVirtualPermissionUser(root.getId(), "write", user.getCagridId());
    this.inputIds.add(root.getId());
    this.check();

    final TropixFile file = getAndExpectNewFile();
    super.getTropixObjectDao().addToVirtualFolder(root.getId(), file.getId());
    this.check();
  }

  @Test
  public void proteomicsRuns() {
    final ProteomicsRun run = new ProteomicsRun();
    super.saveNewTropixObject(run, user);

    inputIds.add(run.getId());
    check();

    final TropixFile mzxml = getAndExpectNewFile();
    run.setMzxml(mzxml);
    getTropixObjectDao().saveOrUpdateTropixObject(run);
    check();

    final TropixFile raw = getAndExpectNewFile();
    run.setSource(raw);
    getTropixObjectDao().saveOrUpdateTropixObject(run);
    check();
  }

}
