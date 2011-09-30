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

import edu.umn.msi.tropix.models.Note;
import edu.umn.msi.tropix.models.NoteRevision;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.dao.Dao;
import edu.umn.msi.tropix.persistence.service.NoteService;

public class NoteServiceTest extends ServiceTest {

  @Autowired
  private NoteService noteService;

  @Test
  public void canEditContents() {
    final Note note = new Note();
    note.setName("NoteName");
    note.setDescription("NoteDescription");

    final User user = createTempUser();
    saveNewTropixObject(note, user);
    assert noteService.canEditContents(user.getCagridId(), note.getId());

    final User otherUser = createTempUser();
    // assert ! noteService.canEditContents(otherUser.getCagridId(), note.getId());

    getTropixObjectDao().addRole(note.getId(), "read", otherUser);
    assert !noteService.canEditContents(otherUser.getCagridId(), note.getId());

    getTropixObjectDao().addRole(note.getId(), "write", otherUser);
    assert noteService.canEditContents(otherUser.getCagridId(), note.getId());
  }

  @Test
  public void create() {
    final Note note = new Note();
    note.setName("NoteName");
    note.setDescription("NoteDescription");

    final User user = createTempUser();
    noteService.createNote(user.getCagridId(), user.getHomeFolder().getId(), note, "'''Contents'''");

    final Note loadedNote = getTropixObjectDao().loadTropixObject(note.getId(), Note.class);
    assert getTropixObjectDao().getOwnerId(loadedNote.getId()).equals(user.getCagridId());
    assert loadedNote.getRevisions().size() == 1;
    final NoteRevision revision = loadedNote.getRevisions().iterator().next();
    assert revision.getContents().equals("'''Contents'''");
    assert loadedNote.getDeletedTime() == null;
    assert loadedNote.getCommitted();
  }

  @Test
  public void lockFailure() {
    final Note note = new Note();
    note.setRevisions(new HashSet<NoteRevision>());
    note.setName("Note");
    note.setDescription("NoteD");

    final User user = createTempUser();
    saveNewTropixObject(note, user);

    final NoteRevision noteRevision = new NoteRevision();
    noteRevision.setContents("OldContents");
    noteRevision.setNote(note);
    noteRevision.setRevisionNum(0);

    final Dao<NoteRevision> revisionDao = getDaoFactory().getDao(NoteRevision.class);
    revisionDao.saveObject(noteRevision);

    String lock = noteService.getNoteLock(user.getCagridId(), note.getId());
    assert lock != null;
    lock = noteService.getNoteLock(user.getCagridId(), note.getId());
    assert lock == null;
  }

  @Test
  public void update() {
    final Note note = new Note();
    note.setRevisions(new HashSet<NoteRevision>());
    note.setName("Note");
    note.setDescription("NoteD");

    final User user = createTempUser();
    saveNewTropixObject(note, user);

    final NoteRevision noteRevision = new NoteRevision();
    noteRevision.setContents("OldContents");
    noteRevision.setNote(note);
    noteRevision.setRevisionNum(0);
    note.getRevisions().add(noteRevision);

    final Dao<NoteRevision> revisionDao = getDaoFactory().getDao(NoteRevision.class);
    revisionDao.saveObject(noteRevision);

    final String lock = noteService.getNoteLock(user.getCagridId(), note.getId());
    noteService.updateNoteContents(user.getCagridId(), note.getId(), lock, "NewContents");

    final Note loadedNote = getTropixObjectDao().loadTropixObject(note.getId(), Note.class);
    assert loadedNote.getRevisions().size() == 2 : loadedNote.getRevisions().size();
    final NoteRevision newRevision = findUniqueResult("from NoteRevision where revisionNum = 1 and note.id = '" + note.getId() + "'");
    assert newRevision != null;
    assert newRevision.getContents().equals("NewContents");
  }

  @Test
  public void get() {
    final Note note = new Note();
    note.setRevisions(new HashSet<NoteRevision>());
    note.setName("Note");
    note.setDescription("NoteD");

    final User user = createTempUser();
    saveNewTropixObject(note, user);

    final NoteRevision noteRevision0 = new NoteRevision();
    noteRevision0.setContents("OldContents");
    noteRevision0.setNote(note);
    noteRevision0.setRevisionNum(0);

    final NoteRevision noteRevision1 = new NoteRevision();
    noteRevision1.setContents("NewContents");
    noteRevision1.setNote(note);
    noteRevision1.setRevisionNum(1);

    final Dao<NoteRevision> revisionDao = getDaoFactory().getDao(NoteRevision.class);
    revisionDao.saveObject(noteRevision0);
    revisionDao.saveObject(noteRevision1);

    assert noteService.getNoteContents(user.getCagridId(), note.getId()).equals("NewContents");
  }

}
