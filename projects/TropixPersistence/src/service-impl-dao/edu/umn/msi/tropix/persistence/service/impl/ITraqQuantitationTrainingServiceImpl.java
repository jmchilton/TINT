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

import edu.umn.msi.tropix.models.ITraqQuantitationTraining;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.utils.StockFileExtensionEnum;
import edu.umn.msi.tropix.persistence.service.ITraqQuantitationTrainingService;

@ManagedBean @Named("iTraqQuantitationTrainingService")
class ITraqQuantitationTrainingServiceImpl extends ServiceBase implements ITraqQuantitationTrainingService {


  public ITraqQuantitationTraining createQuantitationTraining(final String userId, final String destinationId, final ITraqQuantitationTraining quantitationTraining, final String dataReportId, final String[] inputRunIds, final String outputFileId) {
    final String name = quantitationTraining.getName();
    
    final TropixFile report = getTropixObjectDao().loadTropixObject(dataReportId, TropixFile.class);
    report.setName(super.getMessageSource().getMessage(MessageConstants.ITRAQ_QUANTITATION_ANALYSIS_DATA_REPORT_NAME, name));
    report.setDescription(super.getMessageSource().getMessage(MessageConstants.ITRAQ_QUANTITATION_ANALYSIS_DATA_REPORT_DESCRIPTION, name));
    report.setFileType(getFileType(StockFileExtensionEnum.TABULAR_XLS)); //super.getMessageSource().getMessage(MessageConstants.FILE_TYPE_SCAFFOLD_REPORT));
    updateObject(report);

    final TropixFile trainingFile = getTropixObjectDao().loadTropixObject(outputFileId, TropixFile.class);
    trainingFile.setName(super.getMessageSource().getMessage(MessageConstants.ITRAQ_QUANTITATION_TRAINING_OUTPUT_NAME, name));
    trainingFile.setDescription(super.getMessageSource().getMessage(MessageConstants.ITRAQ_QUANTITATION_TRAINING_OUTPUT_DESCRIPTION, name));
    trainingFile.setFileType(getFileType(StockFileExtensionEnum.XML)); //super.getMessageSource().getMessage(MessageConstants.FILE_TYPE_ITRAQ_QUANTITATION_TRAINING_OUTPUT));
    updateObject(trainingFile);
    
    quantitationTraining.setReport(report);
    quantitationTraining.setTrainingFile(trainingFile);
    quantitationTraining.setRuns(Sets.newHashSet(super.getTropixObjectDao().loadTropixObjects(inputRunIds, ProteomicsRun.class)));

    saveNewObjectToDestination(quantitationTraining, userId, destinationId);
    final String quantitationId = quantitationTraining.getId();
    updateObjectWithParent(trainingFile, userId, quantitationId);
    updateObjectWithParent(report, userId, quantitationId);
    return quantitationTraining;
  }

}
