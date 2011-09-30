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

package edu.umn.msi.tropix.webgui.server;

import javax.annotation.ManagedBean;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.base.Function;

import edu.umn.msi.tropix.models.Note;
import edu.umn.msi.tropix.persistence.service.NoteService;
import edu.umn.msi.tropix.webgui.server.aop.ServiceMethod;
import edu.umn.msi.tropix.webgui.server.security.UserSession;

@ManagedBean
public class NoteServiceImpl implements edu.umn.msi.tropix.webgui.services.object.NoteService {
  public static class NoteLock {
    private String lock = null;

    public String getLock() {
      return this.lock;
    }

    public void setLock(final String lock) {
      this.lock = lock;
    }
  }
  
  NoteServiceImpl() {
  }

  private NoteService noteService;
  private Function<String, String> wikiParser;
  private UserSession userSession;
  private NoteLock noteLock;

  @ServiceMethod(readOnly = true)
  public String getNoteHTML(final String noteId) {
    final String wikiText = this.noteService.getNoteContents(this.userSession.getGridId(), noteId);
    return this.wikiParser.apply(wikiText);
  }

  @ServiceMethod
  public void createNote(final Note note, final String folderId, @Nullable final String contents) {
    this.noteService.createNote(this.userSession.getGridId(), folderId, note, contents);
  }

  @ServiceMethod
  public String getNoteContents(final String noteId) {
    final String contents = this.noteService.getNoteContents(this.userSession.getGridId(), noteId);
    return contents;
  }

  @ServiceMethod
  public synchronized String attemptEdit(final String noteId) {
    final String lock = this.noteService.getNoteLock(this.userSession.getGridId(), noteId);
    if(lock == null) {
      return null;
    }
    this.noteLock.setLock(lock);
    final String contents = this.noteService.getNoteContents(this.userSession.getGridId(), noteId);
    return contents;
  }

  @ServiceMethod
  public void updateContents(final String noteId, final String contents) {
    this.noteService.updateNoteContents(this.userSession.getGridId(), noteId, this.noteLock.getLock(), contents);
  }

  @ServiceMethod(readOnly = true)
  public boolean canEditContents(final String noteId) {
    return noteService.canEditContents(userSession.getGridId(), noteId);
  }

  @Inject
  public void setNoteService(final NoteService noteService) {
    this.noteService = noteService;
  }

  @Inject
  public void setUserSession(final UserSession userSession) {
    this.userSession = userSession;
  }

  @Inject
  public void setWikiParser(@Named("wikiParserFunction") final Function<String, String> wikiParser) {
    this.wikiParser = wikiParser;
  }

  @Inject
  public void setNoteLock(final NoteLock noteLock) {
    this.noteLock = noteLock;
  }

}
