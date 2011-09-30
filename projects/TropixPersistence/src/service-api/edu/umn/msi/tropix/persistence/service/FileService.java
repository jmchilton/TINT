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

import edu.umn.msi.tropix.models.PhysicalFile;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.aop.PersistenceMethod;
import edu.umn.msi.tropix.persistence.aop.Reads;
import edu.umn.msi.tropix.persistence.aop.UserId;

public interface FileService {

  @PersistenceMethod
  boolean canReadFile(@UserId String userId, String fileId);

  @PersistenceMethod
  boolean canDeleteFile(@UserId String userId, String fileId);

  @PersistenceMethod
  boolean canWriteFile(@UserId String userId, String fileId);

  @PersistenceMethod
  TropixFile[] getFiles(@UserId String userId, @Reads String[] idsArray);

  /**
   * Used by the storage service to record files length.
   * 
   * @param fileId
   *          - FileId of the file that has been uploaded.
   * @param length
   *          - Length in bytes of the file that has been uploaded.
   */
  void recordLength(String fileId, long length);

  PhysicalFile loadPhysicalFile(final String fileId);

  /**
   * 
   * Used by the storage service to commit files.
   * 
   * @param fileId
   */
  void commit(String fileId);

  boolean fileExists(String fileId);

}
