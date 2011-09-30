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

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import com.google.common.collect.Sets;

import edu.umn.msi.tropix.models.ITraqQuantitationTraining;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.ScaffoldAnalysis;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.service.ITraqQuantitationTrainingService;
import edu.umn.msi.tropix.persistence.service.impl.MessageConstants;
import edu.umn.msi.tropix.persistence.service.impl.StockFileExtensionEnum;

public class ITraqQuantitationTrainingServiceImplTest extends ServiceTest {
  @Autowired
  private ITraqQuantitationTrainingService quantitationService;

  @Test
  public void createQuantitationTraining() {
    final User user = super.createTempUser();
    for(final Destination destination : getTestDestinationsWithNull(user)) {
      final String inputName = "Quantitation Test";

      final ITraqQuantitationTraining input = new ITraqQuantitationTraining();
      input.setName(inputName);

      final TropixFile dataFile = saveNewCommitted(new TropixFile(), user);
      final TropixFile outputFile = saveNewCommitted(new TropixFile(), user);
      
      final ProteomicsRun run1 = saveNewCommitted(new ProteomicsRun(), user);
            
      final IdentificationAnalysis analysis1 = new IdentificationAnalysis();
      analysis1.setRun(run1);
      saveNewTropixObject(analysis1, user);

      final ScaffoldAnalysis scaffold1 = new ScaffoldAnalysis();
      scaffold1.setIdentificationAnalyses(Sets.newHashSet(analysis1));
      saveNewTropixObject(scaffold1, user);

      final ITraqQuantitationTraining returnedTraining = quantitationService.createQuantitationTraining(user.getCagridId(), destination.getId(), input, dataFile.getId(), new String[]{run1.getId()}, outputFile.getId());

      final ITraqQuantitationTraining loadedTraining = getTropixObjectDao().loadTropixObject(returnedTraining.getId(), ITraqQuantitationTraining.class);

      destination.verifyContains(loadedTraining);
      destination.validate(new TropixObject[]{loadedTraining, loadedTraining.getReport(), loadedTraining.getTrainingFile()});

      final TropixFile loadedDataFile = loadedTraining.getReport();
      assert loadedDataFile.getName().equals(getMessageSource().getMessage(MessageConstants.ITRAQ_QUANTITATION_ANALYSIS_DATA_REPORT_NAME, inputName));
      assert loadedDataFile.getDescription().equals(getMessageSource().getMessage(MessageConstants.ITRAQ_QUANTITATION_ANALYSIS_DATA_REPORT_DESCRIPTION, inputName));
      assert loadedDataFile.getFileType().equals(getFileType(StockFileExtensionEnum.TABULAR_XLS));

      final TropixFile loadedOutput = loadedTraining.getTrainingFile();
      assert loadedTraining.getPermissionChildren().contains(loadedOutput);
      assert loadedOutput.getPermissionParents().contains(loadedTraining);
      assert loadedOutput.getName().equals(getMessageSource().getMessage(MessageConstants.ITRAQ_QUANTITATION_TRAINING_OUTPUT_NAME, inputName));
      assert loadedOutput.getDescription().equals(getMessageSource().getMessage(MessageConstants.ITRAQ_QUANTITATION_TRAINING_OUTPUT_DESCRIPTION, inputName));
      assert loadedOutput.getFileType().equals(getFileType(StockFileExtensionEnum.XML));
      
      assert loadedTraining.getRuns().contains(run1); 
    }
  }
  
}
