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

import java.util.Collection;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.ScaffoldAnalysis;
import edu.umn.msi.tropix.models.TissueSample;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.persistence.service.AnalysisService;
import edu.umn.msi.tropix.persistence.service.FolderService;

@ManagedBean @Named("analysisService")
class AnalysisServiceImpl extends ServiceBase implements AnalysisService {
  private final FolderService folderService;
    
  @Inject
  AnalysisServiceImpl(final FolderService folderService) {
    this.folderService = folderService;
  }

  public Database[] getIdentificationDatabases(final String userGridId, final String[] analysesIds) {
    final List<TropixObject> analysesObjects = getTropixObjectDao().loadTropixObjects(analysesIds);
    final Database[] databases = new Database[analysesIds.length];
    for(int i = 0; i < analysesIds.length; i++) {
      final IdentificationAnalysis analysis = (IdentificationAnalysis) analysesObjects.get(i);
      //final IdentificationParameters getParameters = analysis.getParameters();
      final Database database = analysis.getDatabase();
      databases[i] = database;
    }
    for(final Database database : databases) {
      if(super.filterObject(database, userGridId) == null) {
        throw new IllegalStateException("Failed to load databases corresponding to one or more analyses");
      }
    }
    return databases;
  }

  public ProteomicsRun[] getRuns(final String gridId, final String[] objectIds) {
    final List<ProteomicsRun> runs = Lists.newLinkedList();
    for(String objectId : objectIds) {
      TropixObject tropixObject = getTropixObjectDao().loadTropixObject(objectId);
      if(tropixObject instanceof TissueSample) {
        Collection<ProteomicsRun> objectsRuns = ((TissueSample) tropixObject).getProteomicsRuns();
        if(objectsRuns != null) {
          runs.addAll(objectsRuns);
        }
      } else if(tropixObject instanceof ProteomicsRun) {
        runs.add((ProteomicsRun) tropixObject);
      } else if(tropixObject instanceof Folder) {
        for(TropixObject object : folderService.getFolderContents(gridId, tropixObject.getId(), new TropixObjectType[] {TropixObjectTypeEnum.PROTEOMICS_RUN})) {
          final ProteomicsRun run = (ProteomicsRun) object;
          runs.add(run);
        }
      } else if(tropixObject instanceof ScaffoldAnalysis) {
        runs.addAll(getTropixObjectDao().getRunsFromScaffoldAnalysis(objectId));
      }
    }
    return filter(runs, ProteomicsRun.class, gridId);
  }

}
