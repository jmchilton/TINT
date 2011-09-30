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

import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import edu.umn.msi.tropix.models.ITraqQuantitationAnalysis;
import edu.umn.msi.tropix.models.ITraqQuantitationTraining;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.ScaffoldAnalysis;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.service.ITraqQuantitationAnalysisService;
import edu.umn.msi.tropix.persistence.service.impl.MessageConstants;
import edu.umn.msi.tropix.persistence.service.impl.StockFileExtensionEnum;

public class ITraqQuantitationAnalysisServiceImplTest extends ServiceTest {
  @Autowired
  private ITraqQuantitationAnalysisService quantitationService;

  @Test(dataProvider="bool1", dataProviderClass=TestNGDataProviders.class)
  public void createQuantificationAnalysis(final boolean useTraining) {
    final User user = super.createTempUser();
    for(final Destination destination : super.getTestDestinationsWithNull(user)) {
      final String inputName = "Quantitation Test";

      final ITraqQuantitationTraining training = saveNewCommitted(new ITraqQuantitationTraining(), user);
      
      final ITraqQuantitationAnalysis input = new ITraqQuantitationAnalysis();
      input.setName(inputName);
      final TropixFile dataFile = saveNewCommitted(new TropixFile(), user);
      final TropixFile outputFile = saveNewCommitted(new TropixFile(), user);
      
      final ProteomicsRun run1 = saveNewCommitted(new ProteomicsRun(), user);
      
      final IdentificationAnalysis analysis1 = new IdentificationAnalysis();
      analysis1.setRun(run1);
      saveNewCommitted(analysis1, user);

      final ScaffoldAnalysis scaffold1 = new ScaffoldAnalysis();
      scaffold1.setIdentificationAnalyses(Sets.newHashSet(analysis1));
      saveNewCommitted(scaffold1, user);

      final ITraqQuantitationAnalysis returnedAnalysis = quantitationService.createQuantitationAnalysis(user.getCagridId(), destination.getId(), input, dataFile.getId(), new String[]{run1.getId() }, useTraining ? training.getId() : null, outputFile.getId());

      final ITraqQuantitationAnalysis loadedAnalysis = getTropixObjectDao().loadTropixObject(returnedAnalysis.getId(), ITraqQuantitationAnalysis.class);      
      destination.verifyContains(loadedAnalysis);
      destination.validate(new TropixObject[]{loadedAnalysis, dataFile, outputFile});
      
      final TropixFile loadedDataFile = loadedAnalysis.getReport();
      assert loadedDataFile.getName().equals(super.getMessageSource().getMessage(MessageConstants.ITRAQ_QUANTITATION_ANALYSIS_DATA_REPORT_NAME, inputName));
      assert loadedDataFile.getDescription().equals(super.getMessageSource().getMessage(MessageConstants.ITRAQ_QUANTITATION_ANALYSIS_DATA_REPORT_DESCRIPTION, inputName));
      assert loadedDataFile.getFileType().equals(getFileType(StockFileExtensionEnum.TABULAR_XLS));

      final TropixFile loadedOutput = loadedAnalysis.getOutput();
      assert loadedAnalysis.getPermissionChildren().contains(loadedOutput);
      assert loadedOutput.getPermissionParents().contains(loadedAnalysis);
      assert loadedOutput.getName().equals(super.getMessageSource().getMessage(MessageConstants.ITRAQ_QUANTITATION_ANALYSIS_OUTPUT_NAME, inputName));
      assert loadedOutput.getDescription().equals(super.getMessageSource().getMessage(MessageConstants.ITRAQ_QUANTITATION_ANALYSIS_OUTPUT_DESCRIPTION, inputName));
      assert loadedOutput.getFileType().equals(getFileType(StockFileExtensionEnum.XML));
      assert loadedAnalysis.getRuns().contains(run1);
      

      if(useTraining) {
        assert loadedAnalysis.getTraining().equals(training);
      } else {
        assert loadedAnalysis.getTraining() == null;
      }

    }    
  }

}
