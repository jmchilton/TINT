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

import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import edu.umn.msi.tropix.common.concurrent.Timer;
import edu.umn.msi.tropix.models.Note;
import edu.umn.msi.tropix.models.NoteRevision;
import edu.umn.msi.tropix.persistence.dao.NoteRevisionDao;
import edu.umn.msi.tropix.persistence.service.NoteService;

@ManagedBean @Named("noteService")
class NoteServiceImpl extends ServiceBase implements NoteService {
  private static final long DEFAULT_TIMEOUT = 15 * 60 * 1000;
  private final HashMap<String, String> lockMap = new HashMap<String, String>();
  private NoteRevisionDao noteRevisionDao;
  private Timer timer;

  private long timeout = DEFAULT_TIMEOUT;

  public boolean canEditContents(final String gridId, final String noteId) {
    return getSecurityProvider().canModify(noteId, gridId);
  }

  public void createNote(final String userId, final String folderId, final Note note, final String contents) {
    final HashSet<NoteRevision> revisions = new HashSet<NoteRevision>();
    note.setRevisions(revisions);
    note.setCommitted(true);
    super.saveNewObjectToDestination(note, userId, folderId);
    final NoteRevision revision = new NoteRevision();
    revision.setContents(contents);
    revision.setRevisionNum(0);
    revision.setCreationTime("" + System.currentTimeMillis());
    revision.setNote(note);

    noteRevisionDao.saveObject(revision);

    revisions.add(revision);
    getTropixObjectDao().saveOrUpdateTropixObject(note);
  }

  public String getNoteContents(final String userId, final String noteId) {
    final NoteRevision noteRevision = noteRevisionDao.getLatestNoteRevision(noteId);
    return noteRevision.getContents();
  }

  public String getNoteLock(final String userId, final String noteId) {
    String lock = null;
    synchronized(lockMap) {
      if(!lockMap.containsKey(noteId)) {
        lock = UUID.randomUUID().toString();
        lockMap.put(noteId, lock);
      }
      timer.schedule(new Runnable() {
        public void run() {
          synchronized(lockMap) {
            lockMap.remove(noteId);
          }
        }
      }, timeout);
    }
    return lock;
  }

  // TODO: This method has race conditions, though they will never be exhibited in practice. None
  // this less this method should be modified.
  public void updateNoteContents(final String userId, final String noteId, final String lock, final String updatedContents) {
    final Note note = getTropixObjectDao().loadTropixObject(noteId, Note.class);
    final NoteRevision newRevision = new NoteRevision();
    newRevision.setCreationTime("" + System.currentTimeMillis());
    newRevision.setNote(note);
    newRevision.setContents(updatedContents);

    synchronized(lockMap) {
      final String actualLock = lockMap.get(noteId);
      if(actualLock == null || !actualLock.equals(lock)) {
        throw new IllegalStateException("Lock " + lock + " no longer valid for note " + noteId);
      } else {
        lockMap.remove(noteId);
      }
    }
    final NoteRevision lastRevision = noteRevisionDao.getLatestNoteRevision(noteId);
    newRevision.setRevisionNum(lastRevision.getRevisionNum() + 1);
    noteRevisionDao.saveObject(newRevision);
    note.getRevisions().add(newRevision);
    getTropixObjectDao().saveOrUpdateTropixObject(note);
  }

  @Inject
  public void setNoteRevisionDao(final NoteRevisionDao noteRevisionDao) {
    this.noteRevisionDao = noteRevisionDao;
  }

  public void setTimeout(final long timeout) {
    this.timeout = timeout;
  }

  @Inject
  public void setTimer(final Timer timer) {
    this.timer = timer;
  }
}
