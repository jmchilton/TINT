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

import java.util.Set;

import javax.annotation.ManagedBean;
import javax.inject.Named;

import com.google.common.collect.Sets;

import edu.umn.msi.tropix.models.BowtieAnalysis;
import edu.umn.msi.tropix.models.BowtieIndex;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.utils.StockFileExtensionEnum;
import edu.umn.msi.tropix.persistence.service.BowtieAnalysisService;

@ManagedBean @Named("bowtieAnalysisService")
class BowtieAnalysisServiceImpl extends ServiceBase implements BowtieAnalysisService {

  public BowtieAnalysis createBowtieAnalysis(final String userGridId, final String destinationId, final String bowtieIndex, final BowtieAnalysis analysis, final String[] databaseIds, final String outputFileId) {
    final BowtieIndex index = getTropixObjectDao().loadTropixObject(bowtieIndex, BowtieIndex.class);
    analysis.setIndex(index);

    final Set<Database> databases = Sets.newHashSet(getTropixObjectDao().loadTropixObjects(databaseIds, Database.class));
    analysis.setDatabases(databases);

    final TropixFile output = getTropixObjectDao().loadTropixObject(outputFileId, TropixFile.class);
    output.setName(super.getMessageSource().getMessage(MessageConstants.BOWTIE_ANALYSIS_OUTPUT_NAME, analysis.getName()));
    output.setDescription(super.getMessageSource().getMessage(MessageConstants.BOWTIE_ANALYSIS_OUTPUT_DESCRIPTION, analysis.getName()));
    output.setFileType(super.getFileType(StockFileExtensionEnum.TEXT));
    updateObject(output);
    
    analysis.setOutput(output);
    saveNewObjectToDestination(analysis, userGridId, destinationId);
    updateObjectWithParent(output, userGridId, analysis.getId());
    return analysis;
  }

}
