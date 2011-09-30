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

package edu.umn.msi.tropix.persistence.dao.hibernate;

import javax.annotation.ManagedBean;

import org.hibernate.Query;

import edu.umn.msi.tropix.models.NoteRevision;
import edu.umn.msi.tropix.persistence.dao.NoteRevisionDao;

@ManagedBean
class NoteRevisionDaoImpl extends GenericDaoImpl<NoteRevision> implements NoteRevisionDao {

  public NoteRevision getLatestNoteRevision(final String noteId) {
    final Query countQuery = createQuery("select count(*) from NoteRevision nr where nr.note.id = :noteId");
    countQuery.setParameter("noteId", noteId);
    final Long count = (Long) countQuery.uniqueResult();

    final Query revisionQuery = createQuery("select nr from NoteRevision nr where nr.note.id = :noteId and nr.revisionNum = :revisionNum");
    revisionQuery.setParameter("revisionNum", Integer.parseInt(Long.toString(count - 1L)));
    revisionQuery.setParameter("noteId", noteId);
    final NoteRevision noteRevision = (NoteRevision) revisionQuery.uniqueResult();
    return noteRevision;
  }

}
