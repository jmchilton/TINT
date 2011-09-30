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

import java.util.UUID;

import javax.inject.Inject;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.TissueSample;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.service.ProteomicsRunService;
import edu.umn.msi.tropix.persistence.service.impl.MessageConstants;
import edu.umn.msi.tropix.persistence.service.impl.StockFileExtensionEnum;

public class ProteomicsRunServiceTest extends ServiceTest {
  @Inject
  private ProteomicsRunService proteomicsRunService;

  private User tempUser; 
  private Destination currentDestination;
  private String runName;
  
  private ProteomicsRun unsavedRun;
  private ProteomicsRun savedRun;

  private TissueSample tissueSample;
  private TropixFile sourceFile;
  
  @BeforeMethod
  public void init() {
    tempUser = createTempUser();
    runName = UUID.randomUUID().toString();
    
    unsavedRun = null;
    savedRun = null;
    tissueSample = null;
    sourceFile = null;
  }
  
  @Test
  public void createProteomicsRunWithSampleAndSource() {
    for(final Destination destination : getTestDestinationsWithNull(tempUser)) {
      this.currentDestination = destination;
      initializeSample();
      initializeSourceFile();
      runTestAndCheckResults();
    }    
  }

  @Test
  public void createProteomicsRunWithNoSampleOrSource() {
    for(final Destination destination : getTestDestinationsWithNull(tempUser)) {
      this.currentDestination = destination;
      runTestAndCheckResults();
    }    
  }

  private void initializeSourceFile() {
    final TropixFile sourceFile = new TropixFile();
    sourceFile.setFileType(getFileType(StockFileExtensionEnum.THERMO_RAW));
    this.sourceFile = saveNewUncommitted(sourceFile, tempUser);
  }
  
  private void runTestAndCheckResults() {
    final TropixFile mzxmlFile = saveNewUncommitted(new TropixFile(), tempUser);
    initializeUnsavedRun(new ProteomicsRun());
    initializeSavedRun(proteomicsRunService.createProteomicsRun(tempUser.getCagridId(), 
                                                                currentDestination.getId(), 
                                                                unsavedRun, 
                                                                mzxmlFile.getId(), 
                                                                getSampleId(), 
                                                                getSourceFileId()));

    currentDestination.validate(new TropixObject[]{savedRun});
    verifyMzxml();
    verifySample();
    verifySourceFile();
  }

  private void initializeUnsavedRun(final ProteomicsRun unsavedRun) {
    this.unsavedRun = unsavedRun;

    unsavedRun.setCommitted(false);
    unsavedRun.setName(runName);    
  }
  
  private void initializeSavedRun(final ProteomicsRun returnedRun) {
    this.savedRun = getTropixObjectDao().loadTropixObject(returnedRun.getId(), ProteomicsRun.class);
  }

  private void verifyMzxml() {
    assert savedRun.getMzxml() != null;
    assert savedRun.getMzxml().getName().equals(getMessageSource().getMessage(MessageConstants.PROTEOMICS_RUN_MZXML_NAME, runName));
    assert savedRun.getMzxml().getDescription().equals(getMessageSource().getMessage(MessageConstants.PROTEOMICS_RUN_MZXML_DESCRIPTION, runName));
    assert savedRun.getMzxml().getFileType().equals(getFileType(StockFileExtensionEnum.MZXML));
    currentDestination.validate(savedRun.getMzxml());
    assert savedRun.getMzxml().getPermissionParents().iterator().next().equals(savedRun);
  }
  
  private void verifySourceFile() {
    if(sourceFile != null) {
      final TropixFile loadedSourceFile = savedRun.getSource();
      assert sourceFile != null;
      assert loadedSourceFile.getName().equals(getMessageSource().getMessage(MessageConstants.PROTEOMICS_RUN_SOURCE_NAME, runName, sourceFile.getFileType().getExtension()));
      assert loadedSourceFile.getDescription().equals(getMessageSource().getMessage(MessageConstants.PROTEOMICS_RUN_SOURCE_DESCRIPTION, runName));
      assert loadedSourceFile.getFileType().equals(sourceFile.getFileType());
      currentDestination.validate(loadedSourceFile);
      assert loadedSourceFile.getPermissionParents().iterator().next().equals(savedRun);
    }
  }
  
  private void initializeSample() {
    tissueSample = saveNewCommitted(new TissueSample(), tempUser);
  }
  
  private void verifySample() {
    if(tissueSample != null) {
      assert savedRun.getTissueSample().getId().equals(tissueSample.getId());
    }
  }
  
  private String getSampleId() {
    return tissueSample == null ? null : tissueSample.getId();
  }
  
  private String getSourceFileId() {
    return sourceFile == null ? null : sourceFile.getId();
  }
  
}