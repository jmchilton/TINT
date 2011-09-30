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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.common.collect.Collections;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.IdentificationParameters;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.ScaffoldAnalysis;
import edu.umn.msi.tropix.models.TissueSample;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.models.utils.ModelFunctions;
import edu.umn.msi.tropix.persistence.service.AnalysisService;

public class AnalysisServiceTest extends ServiceTest {
  @Autowired
  private AnalysisService analysisService;

  @Test
  public void getRuns() {
    final User user = createTempUser();
    
    final ProteomicsRun run1 = new ProteomicsRun();
    run1.setCommitted(true);
    super.saveNewTropixObject(run1, user);

    final IdentificationAnalysis analysis1 = new IdentificationAnalysis();
    analysis1.setCommitted(true);
    analysis1.setRun(run1);
    super.saveNewTropixObject(analysis1, user);

    final IdentificationAnalysis analysis1Bad = new IdentificationAnalysis();
    analysis1Bad.setCommitted(true);
    analysis1Bad.setRun(run1);
    analysis1Bad.setDeletedTime("" + System.currentTimeMillis());
    super.saveNewTropixObject(analysis1Bad, user);

    final ScaffoldAnalysis scaffold1 = new ScaffoldAnalysis();
    scaffold1.setCommitted(true);
    scaffold1.setIdentificationAnalyses(Sets.newHashSet(analysis1));
    super.saveNewTropixObject(scaffold1, user);

    final ProteomicsRun run2 = new ProteomicsRun();
    run2.setCommitted(true);
    super.saveNewTropixObject(run2, user);

    final IdentificationAnalysis analysis2 = new IdentificationAnalysis();
    analysis2.setCommitted(true);
    analysis2.setRun(run2);
    super.saveNewTropixObject(analysis2, user);

    final ScaffoldAnalysis scaffold2 = new ScaffoldAnalysis();
    scaffold2.setCommitted(true);
    scaffold2.setIdentificationAnalyses(Sets.newHashSet(analysis2));
    super.saveNewTropixObject(scaffold2, user);

    assert analysisService.getRuns(user.getCagridId(), new String[]{scaffold1.getId()})[0].getId().equals(run1.getId());

  }

  @Test(expectedExceptions = RuntimeException.class)
  public void noPermission() {
    final User user = createTempUser(), otherUser = createTempUser();

    final Database database1 = new Database();
    database1.setCommitted(true);
    database1.setDeletedTime("" + 123L);
    saveNewTropixObject(database1, otherUser);

    //final IdentificationParameters parameters1 = new IdentificationParameters();
    //parameters1.setDatabase(database1);
    //saveNewTropixObject(parameters1, user);

    final IdentificationAnalysis analysis1 = new IdentificationAnalysis();
    analysis1.setDatabase(database1);
    //analysis1.setParameters(parameters1);
    saveNewTropixObject(analysis1, otherUser);

    super.getTropixObjectDao().addRole(analysis1.getId(), "read", user);
    analysisService.getIdentificationDatabases(user.getCagridId(), new String[] {analysis1.getId()});
  }

  @Test(expectedExceptions = RuntimeException.class)
  public void deleted() {
    final User user = createTempUser();

    final Database database1 = new Database();
    database1.setCommitted(true);
    database1.setDeletedTime("" + 123L);
    saveNewTropixObject(database1, user);

    final IdentificationParameters parameters1 = new IdentificationParameters();
    //parameters1.setDatabase(database1);
    saveNewTropixObject(parameters1, user);

    final IdentificationAnalysis analysis1 = new IdentificationAnalysis();
    analysis1.setDatabase(database1);
    analysis1.setParameters(parameters1);
    saveNewTropixObject(analysis1, user);

    analysisService.getIdentificationDatabases(user.getCagridId(), new String[] {analysis1.getId()});
  }

  @Test(expectedExceptions = RuntimeException.class)
  public void notCommitted() {
    final User user = createTempUser();

    final Database database1 = new Database();
    database1.setCommitted(false);
    saveNewTropixObject(database1, user);

    final IdentificationParameters parameters1 = new IdentificationParameters();
    //parameters1.setDatabase(database1);
    saveNewTropixObject(parameters1, user);

    final IdentificationAnalysis analysis1 = new IdentificationAnalysis();
    analysis1.setDatabase(database1);
    analysis1.setParameters(parameters1);
    saveNewTropixObject(analysis1, user);

    analysisService.getIdentificationDatabases(user.getCagridId(), new String[] {analysis1.getId()});
  }

  @Test
  public void databaseIds() {
    final User user = createTempUser();
    final TropixFile file1 = new TropixFile();
    final TropixFile file2 = new TropixFile();

    file1.setFileId(newId());
    file2.setFileId(newId());

    saveNewTropixObject(file1);
    saveNewTropixObject(file2);

    final Database database1 = new Database();
    database1.setCommitted(true);
    final Database database2 = new Database();
    database2.setCommitted(true);

    database1.setDatabaseFile(file1);
    database2.setDatabaseFile(file2);

    saveNewTropixObject(database1, user);
    saveNewTropixObject(database2, user);

    final IdentificationParameters parameters1 = new IdentificationParameters();
    final IdentificationParameters parameters2 = new IdentificationParameters();

    saveNewTropixObject(parameters1);
    saveNewTropixObject(parameters2);

    final IdentificationAnalysis analysis1 = new IdentificationAnalysis();
    final IdentificationAnalysis analysis2 = new IdentificationAnalysis();

    analysis1.setDatabase(database1);
    analysis2.setDatabase(database2);

    analysis1.setIdentificationProgram("SequestBean");
    analysis2.setIdentificationProgram("SequestBean");

    analysis1.setParameters(parameters1);
    analysis2.setParameters(parameters2);

    saveNewTropixObject(analysis1);
    saveNewTropixObject(analysis2);

    getTropixObjectDao().setOwner(analysis1.getId(), user);
    getTropixObjectDao().setOwner(analysis2.getId(), user);

    final Database[] result = analysisService.getIdentificationDatabases(user.getCagridId(), new String[] {analysis1.getId(), analysis2.getId()});

    assert result.length == 2;
    assert result[0].getDatabaseFile().getFileId().equals(file1.getFileId());
    assert result[1].getDatabaseFile().getFileId().equals(file2.getFileId());
  }
  
  @Test
  public void getRunIds() {
    final User user = createTempUser();
    
    final ProteomicsRun run1 = saveNewCommitted(new ProteomicsRun(), user), run2 = saveNewCommitted(new ProteomicsRun(), user);
    // Give the user a run that is not expected to come up
    saveNewCommitted(new ProteomicsRun(), user);
    // Make sure one run exists this user doesn't own.
    saveNewCommitted(new ProteomicsRun(), createTempUser());
    final List<String> expectedIds = Lists.newArrayList(run1.getId(), run2.getId());
    
    final TissueSample tissueSample = saveNewCommitted(new TissueSample(), user);
 
    assert analysisService.getRuns(user.getCagridId(), new String[]{tissueSample.getId()}).length == 0;
        
    tissueSample.setProteomicsRuns(Sets.newHashSet(run1, run2));
    getTropixObjectDao().saveOrUpdateTropixObject(tissueSample);

    
    List<ProteomicsRun> runs = Lists.newArrayList(analysisService.getRuns(user.getCagridId(), new String[]{tissueSample.getId()}));
    assert runs.size() == 2;
    assert Collections.transform(runs, ModelFunctions.getIdFunction()).containsAll(expectedIds);

    runs = Lists.newArrayList(analysisService.getRuns(user.getCagridId(), new String[] {run1.getId(), run2.getId()}));
    assert runs.size() == 2;
    assert Collections.transform(runs, ModelFunctions.getIdFunction()).containsAll(expectedIds);
    
    final Folder folder = saveNewCommitted(new Folder(), user);
    folder.setContents(Sets.<TropixObject>newHashSet());
    getTropixObjectDao().addToFolder(folder.getId(), run1.getId());
    getTropixObjectDao().addToFolder(folder.getId(), run2.getId());
    getTropixObjectDao().addToFolder(folder.getId(), tissueSample.getId());

    runs = Lists.newArrayList(analysisService.getRuns(user.getCagridId(), new String[] {folder.getId()}));
    assert runs.size() == 2 : runs.size();
    assert Collections.transform(runs, ModelFunctions.getIdFunction()).containsAll(expectedIds);
    
  }
  

}
