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

package edu.umn.msi.tropix.webgui.client.components.newwizards;

import java.util.ArrayList;
import java.util.Collection;

import com.smartgwt.client.widgets.Window;

import edu.umn.msi.tropix.models.Note;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.components.MetadataInputComponent;
import edu.umn.msi.tropix.webgui.client.components.newwizards.MetadataWizardPageFactory.MetadataWizardPageImpl;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator.UpdateEvent;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardCompletionHandler;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardFactoryImpl;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardOptions;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPage;
import edu.umn.msi.tropix.webgui.services.object.NoteService;

public class NoteCommandComponentFactoryImpl extends WizardCommandComponentFactoryImpl {
  @Override
  public WizardCommand get(final Collection<TreeItem> locations) {
    return new WizardCommand(locations) {
      public void execute() {
        final ArrayList<WizardPage> pages = new ArrayList<WizardPage>(1);
        final MetadataWizardPageImpl metadataWizardPage = getMetadataWizardPageFactory().get(locations, "wiki note");
        pages.add(metadataWizardPage);
        final WizardOptions options = new WizardOptions();
        options.setTitle("New Wiki Note");
        WizardFactoryImpl.getInstance().getWizard(pages, options, new WizardCompletionHandler() {
          public void onCompletion(final Window wizard) {
            final Note note = new Note();
            final MetadataInputComponent metadataSupplier = metadataWizardPage.getMetadataCanvasSupplier();
            note.setName(metadataSupplier.getName());
            note.setDescription(metadataSupplier.getDescription());
            final String parentFolderId = metadataSupplier.getDestinationId();
            NoteService.Util.getInstance().createNote(note, parentFolderId, "", new AsyncCallbackImpl<Void>() {
              @Override
              public void onSuccess(final Void result) {
                LocationUpdateMediator.getInstance().onEvent(new UpdateEvent(parentFolderId, null));
              }
            });
            wizard.destroy();
          }
        });
      }
    };
  }

}
