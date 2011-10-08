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

import edu.umn.msi.tropix.models.BowtieIndex;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.utils.StockFileExtensionEnum;
import edu.umn.msi.tropix.persistence.service.BowtieIndexService;

@ManagedBean @Named("bowtieIndexService")
class BowtieIndexServiceImpl extends ServiceBase implements BowtieIndexService {

  public BowtieIndex createBowtieIndex(final String userGridId, final String destinationId, final BowtieIndex bowtieIndex, final String fileId) {
    final TropixFile file = getTropixObjectDao().loadTropixObject(fileId, TropixFile.class);
    final String databaseName = bowtieIndex.getName();
    file.setName(getMessageSource().getMessage(MessageConstants.BOWTIE_INDEX_NAME, databaseName));
    file.setDescription(getMessageSource().getMessage(MessageConstants.BOWTIE_INDEX_DESCRIPTION, databaseName));
    file.setFileType(getFileType(StockFileExtensionEnum.BOWTIE_INDEX));
    updateObject(file);
    bowtieIndex.setIndexesFile(file);
    saveNewObjectToDestination(bowtieIndex, userGridId, destinationId);
    updateObjectWithParent(file, userGridId, bowtieIndex.getId());
    return bowtieIndex;
  }

}
