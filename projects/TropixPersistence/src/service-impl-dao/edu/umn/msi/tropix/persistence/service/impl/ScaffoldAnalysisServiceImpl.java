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

import javax.annotation.ManagedBean;
import javax.inject.Named;

import com.google.common.collect.Sets;

import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.ScaffoldAnalysis;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.utils.StockFileExtensionEnum;
import edu.umn.msi.tropix.persistence.service.ScaffoldAnalysisService;

@ManagedBean
@Named("scaffoldAnalysisService")
class ScaffoldAnalysisServiceImpl extends ServiceBase implements ScaffoldAnalysisService {

  public ScaffoldAnalysis createScaffoldJob(final String userId, final String destinationId, final ScaffoldAnalysis scaffoldAnalysis,
      final String[] idAnalysisIds, final String inputFileId, final String outputFileId, final String scaffoldVersion) {
    scaffoldAnalysis.setIdentificationAnalyses(Sets.newHashSet(getTropixObjectDao().loadTropixObjects(idAnalysisIds, IdentificationAnalysis.class)));

    FileType fileType;
    if("V3".equals(scaffoldVersion)) {
      fileType = getFileType(StockFileExtensionEnum.SCAFFOLD3_REPORT);
    } else {
      fileType = getFileType(StockFileExtensionEnum.SCAFFOLD_REPORT);
    }

    final String analysisName = scaffoldAnalysis.getName();
    final TropixFile inputFile = getTropixObjectDao().loadTropixObject(inputFileId, TropixFile.class);
    inputFile.setName(getMessageSource().getMessage(MessageConstants.SCAFFOLD_ANALYSIS_INPUT_NAME, analysisName));
    inputFile.setDescription(getMessageSource().getMessage(MessageConstants.SCAFFOLD_ANALYSIS_INPUT_DESCRIPTION, analysisName));
    inputFile.setFileType(getFileType(StockFileExtensionEnum.XML)); // getMessageSource().getMessage(MessageConstants.FILE_TYPE_SCAFFOLD_INPUT));
    updateObject(inputFile);
    scaffoldAnalysis.setInput(inputFile);

    final TropixFile outputFile = getTropixObjectDao().loadTropixObject(outputFileId, TropixFile.class);
    outputFile.setName(getMessageSource().getMessage(MessageConstants.SCAFFOLD_ANALYSIS_OUTPUT_NAME, analysisName, fileType.getExtension()));
    outputFile.setDescription(getMessageSource().getMessage(MessageConstants.SCAFFOLD_ANALYSIS_OUTPUT_DESCRIPTION, analysisName));
    outputFile.setFileType(fileType);
    updateObject(outputFile);
    scaffoldAnalysis.setOutputs(outputFile);

    super.saveNewObjectToDestination(scaffoldAnalysis, userId, destinationId);
    updateObjectWithParent(inputFile, userId, scaffoldAnalysis.getId());
    updateObjectWithParent(outputFile, userId, scaffoldAnalysis.getId());
    return scaffoldAnalysis;
  }

}
