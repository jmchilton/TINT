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

package edu.umn.msi.tropix.persistence.service.test;

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.Provider;
import edu.umn.msi.tropix.models.Request;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.models.utils.StockFileExtensionEnum;
import edu.umn.msi.tropix.persistence.service.DatabaseService;
import edu.umn.msi.tropix.persistence.service.impl.MessageConstants;

public class DatabaseServiceTest extends ServiceTest {
  @Autowired
  private DatabaseService databaseService;

  @Test
  public void createDestinations() {
    final User user = createTempUser();
    for(final String format : new String[] {"FASTA", "FASTQ", "RAW"}) {
      for(final Destination destination : getTestDestinationsWithNull(user)) {
        final Database database = new Database();
        database.setType(format);
        database.setName("Human");

        final TropixFile tropixFile = saveNewUncommitted(new TropixFile(), user);
        final Database returnedDatabase = databaseService.createDatabase(user.getCagridId(), destination.getId(), database, tropixFile.getId());
        
        final Database loadedDatabase = getTropixObjectDao().loadTropixObject(returnedDatabase.getId(), Database.class);
        destination.verifyContains(loadedDatabase);
        assert loadedDatabase.getDatabaseFile() != null;
        //assert loadedDatabase.getCommitted();
        assert loadedDatabase.getDatabaseFile().getId() != null;
        //assert loadedDatabase.getDatabaseFile().getFileId().equals(fileId);
        //assert loadedDatabase.getDatabaseFile().getCommitted();
        assert loadedDatabase.getName().equals("Human");

        destination.validate(new TropixObject[]{loadedDatabase, loadedDatabase.getDatabaseFile()});
        assert loadedDatabase.getPermissionChildren().iterator().next().getId().equals(loadedDatabase.getDatabaseFile().getId());

        final TropixFile loadedFile = loadedDatabase.getDatabaseFile();
        assert loadedFile.getDescription().equals(getMessageSource().getMessage(MessageConstants.DATABASE_FILE_DESCRIPTION, "Human"));
        if(format.equals("FASTA")) {
          assert loadedFile.getName().equals(getMessageSource().getMessage(MessageConstants.DATABASE_FILE_NAME, "Human", ".fasta"));
          assert loadedFile.getFileType().equals(getFileType(StockFileExtensionEnum.FASTA));
        } else if(format.equals("FASTQ")) {
          assert loadedFile.getName().equals(getMessageSource().getMessage(MessageConstants.DATABASE_FILE_NAME, "Human", ".fastq"));
          assert loadedFile.getFileType().equals(getFileType(StockFileExtensionEnum.FASTQ));
        } else {
          assert loadedFile.getName().equals(getMessageSource().getMessage(MessageConstants.DATABASE_FILE_NAME, "Human", ".txt")) : loadedFile.getName();
          assert loadedFile.getFileType().equals(getFileType(StockFileExtensionEnum.TEXT));
        }
      }

    }
  }

  @Test
  public void createForRequest() {
    final User user = createTempUser();
    final Provider provider = createTempProvider();
    provider.getUsers().add(user);
    getDaoFactory().getDao(Provider.class).saveObject(provider);

    final Database database = new Database();
    database.setName("Human");
    database.setType("FASTA");
    final TropixFile tropixFile = saveNewCommitted(new TropixFile(), user);

    final Request request = new Request();
    request.setContents(new HashSet<TropixObject>());
    request.setProvider(provider);
    super.saveNewTropixObject(request);
    provider.getObjects().add(request);
    getDaoFactory().getDao(Provider.class).saveObject(provider);

    databaseService.createDatabase(user.getCagridId(), request.getId(), database, tropixFile.getId());

    assert request.getContents().size() == 1;
    assert provider.getObjects().size() == 3;
    assert provider.getObjects().contains(((Database) request.getContents().iterator().next()).getDatabaseFile());

  }

}
