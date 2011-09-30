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

package edu.umn.msi.tropix.persistence.dao.hibernate.test;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.persistence.dao.NoteRevisionDao;

public class NoteRevisionDaoImplTest extends DaoTest {
  @Autowired
  private NoteRevisionDao noteRevisionDao;

  @Test
  public void findLatestRevision() {
    final String noteId = newId();
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO TROPIX_OBJECT(ID) VALUES ('" + noteId + "')");
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO NOTE(OBJECT_ID) VALUES ('" + noteId + "')");

    final String rId1 = newId(), rId2 = newId(), rId3 = newId();
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO NOTE_REVISION(ID, NOTE_ID, REVISION_NUM) VALUES ('" + rId1 + "','" + noteId + "','0')");
    flush();
    assert noteRevisionDao.getLatestNoteRevision(noteId).getId().equals(rId1);
    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO NOTE_REVISION(ID, NOTE_ID, REVISION_NUM) VALUES ('" + rId2 + "','" + noteId + "','1')");
    flush();
    assert noteRevisionDao.getLatestNoteRevision(noteId).getId().equals(rId2);

    simpleJdbcTemplate.getJdbcOperations().execute("INSERT INTO NOTE_REVISION(ID, NOTE_ID, REVISION_NUM) VALUES ('" + rId3 + "','" + noteId + "','2')");
    flush();
    assert noteRevisionDao.getLatestNoteRevision(noteId).getId().equals(rId3);
  }
}
