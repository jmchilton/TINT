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

package edu.umn.msi.tropix.webgui.server.download;

import java.io.OutputStream;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.io.OutputContexts;
import edu.umn.msi.tropix.persistence.service.NoteService;
import edu.umn.msi.tropix.webgui.server.security.UserSession;
import edu.umn.msi.tropix.webgui.server.wiki.WikiPdfExporter;

@ManagedBean
@FileDownloadHandlerType("note")
class NoteDownloadHandlerImpl implements FileDownloadHandler {
  private final WikiPdfExporter wikiPdfExporter;
  private final NoteService noteService;
  private final UserSession userSession;

  @Inject
  NoteDownloadHandlerImpl(final WikiPdfExporter wikiPdfExporter, final NoteService noteService, final UserSession userSession) {
    this.wikiPdfExporter = wikiPdfExporter;
    this.noteService = noteService;
    this.userSession = userSession;
  }

  public void processDownloadRequest(final OutputStream stream, final Function<String, String> accessor) {
    final String id = accessor.apply("id");
    Preconditions.checkNotNull(id);
    final String wikiText = this.noteService.getNoteContents(this.userSession.getGridId(), id);
    final OutputContext outputContext = OutputContexts.forOutputStream(stream);
    this.wikiPdfExporter.exportAsPdf(wikiText, outputContext);
  }

}
