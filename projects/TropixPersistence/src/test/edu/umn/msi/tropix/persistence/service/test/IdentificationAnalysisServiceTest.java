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

import java.util.Random;

import javax.inject.Inject;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.IdentificationParameters;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.service.IdentificationAnalysisService;
import edu.umn.msi.tropix.persistence.service.impl.MessageConstants;
import edu.umn.msi.tropix.persistence.service.impl.StockFileExtensionEnum;
import edu.umn.msi.tropix.persistence.service.impl.StockFileExtensionI;

// TODO: Fill out commit tests a little more (check more attributes)
public class IdentificationAnalysisServiceTest extends ServiceTest {
  private static final Random RANDOM = new Random();
  
  @Inject
  private IdentificationAnalysisService idService;
  
  @Test
  public void createIDAnalysisSequest() {
    createIDAnalysisWithParams(AnalysisType.SEQUEST, false, true);
  }

  @Test
  public void createIDAnalysisMascot() {
    createIDAnalysisWithParams(AnalysisType.MASCOT, false, true);
  }

  @Test
  public void createIDAnalysisXTandem() {
    createIDAnalysisWithParams(AnalysisType.XTANDEM, false, true);
  }

  @Test
  public void createIDAnalysisOmssaWithParams() {
    createIDAnalysisWithParams(AnalysisType.OMSSA, true, true);
  }

  @Test
  public void createIDAnalysisOmssaWithParamsNoRun() {
    createIDAnalysisWithParams(AnalysisType.OMSSA, true, false);
  }
  

  @Test
  public void createIDAnalysisSequestWithParams() {
    createIDAnalysisWithParams(AnalysisType.SEQUEST, true, true);
  }

  @Test
  public void createIDAnalysisXTandemWithParams() {
    createIDAnalysisWithParams(AnalysisType.XTANDEM, true, true);
  }

  @Test
  public void createIDAnalysisOmssa() {
    createIDAnalysisWithParams(AnalysisType.OMSSA, false, true);
  }


  
  public void createIDAnalysisWithParams(final AnalysisType type, final boolean useParams, final boolean useRun) {
    final User tempUser = createTempUser();
    
    for(final Destination destination : super.getTestDestinationsWithNull(tempUser)) {
      final ProteomicsRun proteomicsRun = useRun ? new ProteomicsRun() : null;
      if(useRun) {
        saveNewTropixObject(proteomicsRun, tempUser);
      }

      final Database database = new Database();
      saveNewTropixObject(database, tempUser);

      final IdentificationParameters parameters = useParams ? new IdentificationParameters() : null;
      if(useParams) {
        saveNewTropixObject(parameters, tempUser);
        if(type.equals(AnalysisType.OMSSA)) {
          final TropixFile omssaParams = new TropixFile();
          saveNewTropixObject(omssaParams, tempUser);
          parameters.setParametersId(omssaParams.getId());
        }
      }
      
      final String analysisName = "Moo";
      final IdentificationAnalysis idAnalysis = new IdentificationAnalysis();
      
      idAnalysis.setCommitted(RANDOM.nextBoolean());
      idAnalysis.setName(analysisName);
      idAnalysis.setIdentificationProgram(type.getIdentificationProgram());

      final TropixFile tropixFile = new TropixFile();
      saveNewTropixObject(tropixFile, tempUser);
      
      final IdentificationAnalysis returnedAnalysis = idService.createIdentificationAnalysis(tempUser.getCagridId(), destination.getId(), idAnalysis, type.identificationProgram, tropixFile.getId(), useRun ? proteomicsRun.getId() : null, database.getId(), useParams ? parameters.getId() : null);
      final IdentificationAnalysis loadedAnalysis = getTropixObjectDao().loadTropixObject(returnedAnalysis.getId(), IdentificationAnalysis.class);
      destination.verifyContains(loadedAnalysis);
      
      //assert loadedAnalysis.getCommitted();
      if(useParams) {
        assert loadedAnalysis.getParameters() != null;
        assert loadedAnalysis.getParameters().getName().equals(getMessageSource().getMessage(MessageConstants.IDENTIFICATION_ANALYSIS_PARAMS_NAME, analysisName));
        assert loadedAnalysis.getParameters().getDescription().equals(getMessageSource().getMessage(MessageConstants.IDENTIFICATION_ANALYSIS_PARAMS_DESCRIPTION, analysisName));
        if(type.equals(AnalysisType.OMSSA)) {
          final TropixFile omssaParams = getTropixObjectDao().loadTropixObject(loadedAnalysis.getParameters().getParametersId(), TropixFile.class);
          assert omssaParams.getName().equals(getMessageSource().getMessage(MessageConstants.IDENTIFICATION_ANALYSIS_INPUT_NAME, analysisName));
          assert omssaParams.getDescription().equals(getMessageSource().getMessage(MessageConstants.IDENTIFICATION_ANALYSIS_INPUT_DESCRIPTION, analysisName));
          assert omssaParams.getFileType().equals(getFileType(StockFileExtensionEnum.XML));
        }
      } else {
        assert loadedAnalysis.getParameters() == null;
      }

      assert loadedAnalysis.getDatabase().getId().equals(database.getId());
      if(useRun) {
        assert loadedAnalysis.getRun().getId().equals(proteomicsRun.getId());
      }
      assert loadedAnalysis.getOutput() != null;

      destination.validate(new TropixObject[]{loadedAnalysis, loadedAnalysis.getOutput()});
      if(useParams) {
        destination.validate(new TropixObject[]{loadedAnalysis.getParameters()});        
        assert loadedAnalysis.getParameters().getPermissionParents().iterator().next().equals(loadedAnalysis);
        if(type.equals(AnalysisType.OMSSA)) {
          destination.validate(new TropixObject[]{getTropixObjectDao().loadTropixObject(loadedAnalysis.getParameters().getParametersId())});         
        }
      }

      assert loadedAnalysis.getOutput().getName().equals(getMessageSource().getMessage(MessageConstants.IDENTIFICATION_ANALYSIS_OUTPUT_NAME, analysisName, getFileType(type.getOutputType()).getExtension())) : loadedAnalysis.getOutput().getName();
      assert loadedAnalysis.getOutput().getDescription().equals(getMessageSource().getMessage(MessageConstants.IDENTIFICATION_ANALYSIS_OUTPUT_DESCRIPTION, analysisName));
      assert loadedAnalysis.getOutput().getFileType().equals(getFileType(type.getOutputType()));

      assert loadedAnalysis.getOutput().getPermissionParents().iterator().next().equals(loadedAnalysis);

    }

  }

  

  private enum AnalysisType {
    SEQUEST(StockFileExtensionEnum.ZIP, "SequestBean"), XTANDEM(StockFileExtensionEnum.XML, "XTandemBean"), OMSSA(StockFileExtensionEnum.OMSSA_OUTPUT, "OmssaXml"), MASCOT(StockFileExtensionEnum.MASCOT_OUTPUT, "Mascot");

    private final String identificationProgram;
    private final StockFileExtensionI fileExtension;

    private AnalysisType(final StockFileExtensionI fileExtension, final String identificationProgram) {
      this.fileExtension = fileExtension;
      this.identificationProgram = identificationProgram;
    }

    public StockFileExtensionI getOutputType() {
      return fileExtension;
    }

    public String getIdentificationProgram() {
      return identificationProgram;
    }
  }

}
