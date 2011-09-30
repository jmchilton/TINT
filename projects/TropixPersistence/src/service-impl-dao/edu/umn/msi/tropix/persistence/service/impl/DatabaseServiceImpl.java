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

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.service.DatabaseService;

@ManagedBean
@Named("databaseService")
class DatabaseServiceImpl extends ServiceBase implements DatabaseService {

  public Database createDatabase(final String userGridId, final String destinationId, final Database database, final String databaseFileId) {
    final TropixFile databaseFile = getTropixObjectDao().loadTropixObject(databaseFileId, TropixFile.class);
    Preconditions.checkNotNull(databaseFile);
    database.setDatabaseFile(databaseFile);
    final String databaseName = database.getName();
    final String format = database.getType();
    final FileType fileType;
    if(format.equals("FASTA")) {
      fileType = getFileType(StockFileExtensionEnum.FASTA);
      databaseFile.setName(getMessageSource().getMessage(MessageConstants.DATABASE_FILE_NAME, databaseName, fileType.getExtension()));

    } else if(format.equals("FASTQ")) {
      fileType = getFileType(StockFileExtensionEnum.FASTQ);
      databaseFile.setName(getMessageSource().getMessage(MessageConstants.DATABASE_FILE_NAME, databaseName, fileType.getExtension()));
    } else if(format.equals("RAW")) {
      fileType = getFileType(StockFileExtensionEnum.TEXT);
      databaseFile.setName(getMessageSource().getMessage(MessageConstants.DATABASE_FILE_NAME, databaseName, fileType.getExtension()));
    } else {
      throw new IllegalArgumentException("Invalid database format found " + format);
    }
    databaseFile.setFileType(fileType);
    databaseFile.setDescription(getMessageSource().getMessage(MessageConstants.DATABASE_FILE_DESCRIPTION, databaseName));
    updateObject(databaseFile);
    saveNewObjectToDestination(database, userGridId, destinationId);
    updateObjectWithParent(databaseFile, userGridId, database.getId());
    return database;
  }

}
