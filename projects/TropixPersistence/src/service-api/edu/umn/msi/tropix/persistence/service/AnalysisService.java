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

package edu.umn.msi.tropix.persistence.service;

import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.persistence.aop.PersistenceMethod;
import edu.umn.msi.tropix.persistence.aop.Reads;
import edu.umn.msi.tropix.persistence.aop.UserId;

public interface AnalysisService {
  /**
   * Needed for scaffold pipeline.
   * 
   * @param analysesIds
   *          IdentificationAnalysis ids to fetch databases for
   * @return Array of databases associated with given analyses
   */
  @PersistenceMethod Database[] getIdentificationDatabases(@UserId String gridId, @Reads String[] analysesIds);

  //@PersistenceMethod ProteomicsRun[] getRunsFromScaffoldAnalysis(@UserId String gridId, @Reads String scaffoldAnalysisId);

  /**
   * 
   * @param gridId User id of requester.
   * @param objectIds Ids corresponding to a folder, a sample, or a list of runs.
   * @return The list of run ids in the folder, associated with the sample, or the input run ids.
   */
  @PersistenceMethod
  ProteomicsRun[] getRuns(@UserId String gridId, @Reads String[] objectIds);

}
