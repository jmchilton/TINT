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

import java.util.HashSet;

import javax.inject.Inject;

import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.ScaffoldAnalysis;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.models.utils.StockFileExtensionEnum;
import edu.umn.msi.tropix.persistence.service.ScaffoldAnalysisService;
import edu.umn.msi.tropix.persistence.service.impl.MessageConstants;

public class ScaffoldServiceTest extends ServiceTest {
  @Inject
  private ScaffoldAnalysisService scaffoldAnalysisService;

  @Test
  public void createScaffold() {
    final User tempUser = createTempUser();
    for(final Destination destination : getTestDestinationsWithNull(tempUser)) {
      final IdentificationAnalysis[] analyses = getNewIdentificationAnalyses(tempUser);
      final HashSet<String> analysisIds = new HashSet<String>();
      for(final IdentificationAnalysis idAnalysis : analyses) {
        analysisIds.add(idAnalysis.getId());
      }

      final TropixFile inputFile = saveNewUncommitted(new TropixFile(), tempUser);
      final TropixFile outputFile = saveNewUncommitted(new TropixFile(), tempUser);

      final ScaffoldAnalysis scaffoldAnalysis = new ScaffoldAnalysis();
      scaffoldAnalysis.setCommitted(false);
      final String analysisName = "MyAnalysis";
      scaffoldAnalysis.setName(analysisName);

      // final TropixObjectJob job = scaffoldAnalysisService.createScaffoldJob(jobInfo, destination.getId(), scaffoldAnalysis, analysisIds.toArray(new
      // String[0]), inputFile);
      final ScaffoldAnalysis returnedAnalysis = scaffoldAnalysisService.createScaffoldJob(tempUser.getCagridId(), destination.getId(),
          scaffoldAnalysis, Iterables.toArray(analysisIds, String.class), inputFile.getId(), outputFile.getId(), "V3");

      final ScaffoldAnalysis analysis = getTropixObjectDao().loadTropixObject(returnedAnalysis.getId(), ScaffoldAnalysis.class);
      assert !analysis.getCommitted();
      assert analysis.getName().equals(analysisName);
      final HashSet<String> loadedIds = new HashSet<String>();
      for(final IdentificationAnalysis idAnalysis : analysis.getIdentificationAnalyses()) {
        loadedIds.add(idAnalysis.getId());
      }

      assert loadedIds.equals(analysisIds);
      assert analysis.getPermissionChildren().containsAll(Lists.newArrayList(analysis.getInput(), analysis.getOutputs()));
      // assert analysis.getPermissionChildren().iterator().next().equals(analysis.getInput());
      // assert analysis.getPermissionChildren().iterator().next().equals(analysis.getOutputs());
      destination.validate(new TropixObject[] {analysis, analysis.getInput(), analysis.getOutputs()});
      assert analysis.getInput() != null;
      assert analysis.getInput().getId() != null;
      assert analysis.getInput().getFileType().equals(getFileType(StockFileExtensionEnum.XML));
      assert analysis.getInput().getName().equals(getMessageSource().getMessage(MessageConstants.SCAFFOLD_ANALYSIS_INPUT_NAME, analysisName));
      assert analysis.getInput().getDescription()
          .equals(getMessageSource().getMessage(MessageConstants.SCAFFOLD_ANALYSIS_INPUT_DESCRIPTION, analysisName));

      assert analysis.getOutputs().getFileType().equals(getFileType(StockFileExtensionEnum.SCAFFOLD3_REPORT));
      assert analysis.getOutputs().getName().equals(getMessageSource().getMessage(MessageConstants.SCAFFOLD_ANALYSIS_OUTPUT_NAME, analysisName, ".sf3"));
      assert analysis.getOutputs().getDescription()
          .equals(getMessageSource().getMessage(MessageConstants.SCAFFOLD_ANALYSIS_OUTPUT_DESCRIPTION, analysisName));

      assert !analysis.getCommitted();
      assert !analysis.getInput().getCommitted();
    }
  }

  private IdentificationAnalysis[] getNewIdentificationAnalyses(final User owner) {
    final int numAnalyses = 10;
    final IdentificationAnalysis[] analyses = new IdentificationAnalysis[numAnalyses];
    for(int i = 0; i < numAnalyses; i++) {
      final IdentificationAnalysis analysis = new IdentificationAnalysis();
      analysis.setScaffoldAnalyses(new HashSet<ScaffoldAnalysis>());
      saveNewTropixObject(analysis, owner);
      analyses[i] = analysis;
    }
    return analyses;
  }
}
