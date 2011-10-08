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
import javax.annotation.Nullable;

import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.TissueSample;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.utils.StockFileExtensionEnum;
import edu.umn.msi.tropix.persistence.service.ProteomicsRunService;

@ManagedBean
class ProteomicsRunServiceImpl extends ServiceBase implements ProteomicsRunService {

  public ProteomicsRun createProteomicsRun(final String userGridId, final String folderId, final ProteomicsRun run, final String mzxmlFileId, final String sampleId, @Nullable final String sourceFileId) {
    final String runName = run.getName();
    final TropixFile mzxmlFile = getTropixObjectDao().loadTropixObject(mzxmlFileId, TropixFile.class);
    mzxmlFile.setName(getMessageSource().getMessage(MessageConstants.PROTEOMICS_RUN_MZXML_NAME, runName));
    mzxmlFile.setDescription(getMessageSource().getMessage(MessageConstants.PROTEOMICS_RUN_MZXML_DESCRIPTION, runName));
    mzxmlFile.setFileType(getFileType(StockFileExtensionEnum.MZXML)); 
    updateObject(mzxmlFile);
        
    TissueSample sample = null;
    if(sampleId != null) {
      sample = getTropixObjectDao().loadTropixObject(sampleId, TissueSample.class);
    }
    run.setTissueSample(sample);
    run.setMzxml(mzxmlFile);
    
    TropixFile sourceFile = null;
    if(sourceFileId != null) {
      sourceFile = getTropixObjectDao().loadTropixObject(sourceFileId, TropixFile.class);
      final String name = getMessageSource().getMessage(MessageConstants.PROTEOMICS_RUN_SOURCE_NAME, runName, sourceFile.getFileType().getExtension());
      sourceFile.setName(name);
      sourceFile.setDescription(getMessageSource().getMessage(MessageConstants.PROTEOMICS_RUN_SOURCE_DESCRIPTION, runName));
      run.setSource(sourceFile);
    }
        
    saveNewObjectToDestination(run, userGridId, folderId);    
    updateObjectWithParent(mzxmlFile, userGridId, run.getId());
    if(sourceFile != null) {
      updateObjectWithParent(sourceFile, userGridId, run.getId());
    }
    return run;
  }

}
