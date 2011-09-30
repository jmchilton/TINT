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

package edu.umn.msi.tropix.persistence.service.impl;

import java.util.HashSet;

import javax.annotation.ManagedBean;
import javax.inject.Named;

import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.IdentificationParameters;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.ScaffoldAnalysis;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.service.IdentificationAnalysisService;

@ManagedBean
@Named("identificationAnalysisService")
class IdentificationAnalysisServiceImpl extends ServiceBase implements IdentificationAnalysisService {

  private FileType getFileType(final AnalysisType analysisType) {
    FileType type = null;
    switch(analysisType) {
    case SEQUEST_ANALYSIS:
      type = getFileType(StockFileExtensionEnum.ZIP);
      break;
    case XTANDEM_ANALYSIS:
      type = getFileType(StockFileExtensionEnum.XML);
      break;
    case OMSSA_ANALYSIS:
      type = getFileType(StockFileExtensionEnum.OMSSA_OUTPUT);
      break;
    case MYRIMATCH_ANALYSIS:
    case TAGRECON_ANALYSIS:
      type = getFileType(StockFileExtensionEnum.PEPXML);
      break;
    case MASCOT_ANALYSIS:
      type = getFileType(StockFileExtensionEnum.MASCOT_OUTPUT);
      break;
    default:
      throw new IllegalStateException("Unknown identification analysis type");
    }
    return type;
  }

  private enum AnalysisType {
    SEQUEST_ANALYSIS("SequestBean"), XTANDEM_ANALYSIS("XTandemBean"), OMSSA_ANALYSIS("OmssaXml", true), MASCOT_ANALYSIS("Mascot"), MYRIMATCH_ANALYSIS(
        "MyriMatch", true), TAGRECON_ANALYSIS("TagRecon", true);
    private final String parameterType;
    private boolean hasParametersFile = false;

    AnalysisType(final String parameterType, final boolean hasParameterFile) {
      this.parameterType = parameterType;
      this.hasParametersFile = hasParameterFile;
    }

    AnalysisType(final String parameterType) {
      this.parameterType = parameterType;
    }

    static AnalysisType fromParameterType(final String parameterType) {
      for(final AnalysisType type : AnalysisType.values()) {
        if(type.parameterType.equals(parameterType)) {
          return type;
        }
      }
      throw new IllegalStateException("No enum with parameterType " + parameterType + " exists.");
    }

  }

  public IdentificationAnalysis createIdentificationAnalysis(final String userGridId, final String folderId, final IdentificationAnalysis analysis,
      final String parameterType, final String analysisFileId, final String runId, final String databaseId, final String parametersId) {
    analysis.setScaffoldAnalyses(new HashSet<ScaffoldAnalysis>());
    final AnalysisType analysisType = AnalysisType.fromParameterType(parameterType);
    ProteomicsRun run = null;
    if(runId != null) {
      run = getTropixObjectDao().loadTropixObject(runId, ProteomicsRun.class);
    }
    analysis.setRun(run);

    final String analysisName = analysis.getName();
    IdentificationParameters parameters = null;
    if(parametersId != null) {
      parameters = getTropixObjectDao().loadTropixObject(parametersId, IdentificationParameters.class);
      parameters.setName(getMessageSource().getMessage(MessageConstants.IDENTIFICATION_ANALYSIS_PARAMS_NAME, analysisName));
      parameters.setDescription(getMessageSource().getMessage(MessageConstants.IDENTIFICATION_ANALYSIS_PARAMS_DESCRIPTION, analysisName));
      updateObject(parameters);
      if(analysisType.hasParametersFile) {
        final String fileId = parameters.getParametersId();
        if(fileId != null) {
          final TropixFile paramFile = getTropixObjectDao().loadTropixObject(fileId, TropixFile.class);
          paramFile.setName(getMessageSource().getMessage(MessageConstants.IDENTIFICATION_ANALYSIS_INPUT_NAME, analysisName));
          paramFile.setDescription(getMessageSource().getMessage(MessageConstants.IDENTIFICATION_ANALYSIS_INPUT_DESCRIPTION, analysisName));
          paramFile.setFileType(getFileType(StockFileExtensionEnum.XML));
          updateObject(paramFile);
        }
      }
    }
    analysis.setParameters(parameters);
    if(databaseId != null) {
      analysis.setDatabase(getTropixObjectDao().loadTropixObject(databaseId, Database.class));
    }

    final TropixFile analysisFile = getTropixObjectDao().loadTropixObject(analysisFileId, TropixFile.class);
    analysisFile.setName(getMessageSource().getMessage(MessageConstants.IDENTIFICATION_ANALYSIS_OUTPUT_NAME, analysisName,
        getFileType(analysisType).getExtension()));
    analysisFile.setDescription(getMessageSource().getMessage(MessageConstants.IDENTIFICATION_ANALYSIS_OUTPUT_DESCRIPTION, analysisName));
    analysisFile.setFileType(getFileType(analysisType));
    updateObject(analysisFile);
    analysis.setOutput(analysisFile);
    saveNewObjectToDestination(analysis, userGridId, folderId);

    updateObjectWithParent(analysisFile, userGridId, analysis.getId());
    if(parameters != null) {
      updateObjectWithParent(parameters, userGridId, analysis.getId());
      if(analysisType.hasParametersFile) {
        updateObjectWithParent(getTropixObjectDao().loadTropixObject(parameters.getParametersId()), userGridId, analysis.getId());
      }
    }
    return analysis;
  }

}
