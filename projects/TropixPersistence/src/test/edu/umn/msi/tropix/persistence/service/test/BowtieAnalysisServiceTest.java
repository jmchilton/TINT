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

import javax.inject.Inject;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.BowtieAnalysis;
import edu.umn.msi.tropix.models.BowtieIndex;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.models.utils.StockFileExtensionEnum;
import edu.umn.msi.tropix.persistence.service.BowtieAnalysisService;
import edu.umn.msi.tropix.persistence.service.impl.MessageConstants;

public class BowtieAnalysisServiceTest extends ServiceTest {
  @Inject
  private BowtieAnalysisService bowtieAnalysisService;

  @Test
  public void createBowtieAnalysis() {
    final User user = super.createTempUser();
    for(final Destination destination : getTestDestinationsWithNull(user)) {
      final String inputName = "Bowtie Analysis Test";
      final BowtieAnalysis input = new BowtieAnalysis();
      input.setName(inputName);

      final Database database = saveNewCommitted(new Database(), user);
      final BowtieIndex index = saveNewCommitted(new BowtieIndex(), user);
      final TropixFile outputFile = saveNewCommitted(new TropixFile(), user);
      final BowtieAnalysis returnedAnalysis = bowtieAnalysisService.createBowtieAnalysis(user.getCagridId(), destination.getId(), index.getId(), input, new String[] {database.getId()}, outputFile.getId());
      final BowtieAnalysis loadedAnalysis = getTropixObjectDao().loadTropixObject(returnedAnalysis.getId(), BowtieAnalysis.class);

      destination.validate(new TropixObject[] {loadedAnalysis, outputFile});
      destination.verifyContains(loadedAnalysis);

      assert loadedAnalysis.getDatabases().size() == 1;
 
      final TropixFile loadedOutput = loadedAnalysis.getOutput();
      assert loadedAnalysis.getPermissionChildren().contains(loadedOutput);
      assert loadedOutput.getPermissionParents().contains(loadedAnalysis);
      assert loadedOutput.getName().equals(super.getMessageSource().getMessage(MessageConstants.BOWTIE_ANALYSIS_OUTPUT_NAME, inputName));
      assert loadedOutput.getDescription().equals(super.getMessageSource().getMessage(MessageConstants.BOWTIE_ANALYSIS_OUTPUT_DESCRIPTION, inputName));
      assert loadedOutput.getFileType().equals(super.getFileType(StockFileExtensionEnum.TEXT));
    }
  }

}
