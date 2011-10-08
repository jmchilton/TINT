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

import edu.umn.msi.tropix.models.ITraqQuantitationAnalysis;
import edu.umn.msi.tropix.models.ITraqQuantitationTraining;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.utils.StockFileExtensionEnum;
import edu.umn.msi.tropix.persistence.service.ITraqQuantitationAnalysisService;

@ManagedBean @Named("iTraqQuantitationAnalysisService")
class ITraqQuantitationAnalysisServiceImpl extends ServiceBase implements ITraqQuantitationAnalysisService {

  public ITraqQuantitationAnalysis createQuantitationAnalysis(final String userId, final String destinationId, final ITraqQuantitationAnalysis quantitationAnalysis, final String dataReportId, final String[] inputRunIds, final String trainingId, final String outputFileId) {
    final String name = quantitationAnalysis.getName();
    
    quantitationAnalysis.setRuns(Sets.newHashSet(super.getTropixObjectDao().loadTropixObjects(inputRunIds, ProteomicsRun.class)));
    if(trainingId != null) {
      quantitationAnalysis.setTraining(getTropixObjectDao().loadTropixObject(trainingId, ITraqQuantitationTraining.class));
    }
    final TropixFile dataReport = getTropixObjectDao().loadTropixObject(dataReportId, TropixFile.class);
    dataReport.setName(super.getMessageSource().getMessage(MessageConstants.ITRAQ_QUANTITATION_ANALYSIS_DATA_REPORT_NAME, name));
    dataReport.setDescription(super.getMessageSource().getMessage(MessageConstants.ITRAQ_QUANTITATION_ANALYSIS_DATA_REPORT_DESCRIPTION, name));
    dataReport.setFileType(getFileType(StockFileExtensionEnum.TABULAR_XLS));
    updateObject(dataReport);
    
    final TropixFile outputFile = getTropixObjectDao().loadTropixObject(outputFileId, TropixFile.class);
    outputFile.setName(super.getMessageSource().getMessage(MessageConstants.ITRAQ_QUANTITATION_ANALYSIS_OUTPUT_NAME, name));
    outputFile.setDescription(super.getMessageSource().getMessage(MessageConstants.ITRAQ_QUANTITATION_ANALYSIS_OUTPUT_DESCRIPTION, name));
    outputFile.setFileType(getFileType(StockFileExtensionEnum.XML)); //super.getMessageSource().getMessage(MessageConstants.FILE_TYPE_ITRAQ_QUANTITATION_OUTPUT));
    updateObject(outputFile);
    
    quantitationAnalysis.setReport(dataReport);
    quantitationAnalysis.setOutput(outputFile);

    saveNewObjectToDestination(quantitationAnalysis, userId, destinationId);
    final String quantitationId = quantitationAnalysis.getId();
    super.updateObjectWithParent(dataReport, userId, quantitationId);
    super.updateObjectWithParent(outputFile, userId, quantitationId);    
    return quantitationAnalysis;
  }

}
