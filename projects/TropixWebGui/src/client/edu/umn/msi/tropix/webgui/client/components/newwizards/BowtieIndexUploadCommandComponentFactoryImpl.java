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

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.inject.Inject;
import com.smartgwt.client.widgets.Window;

import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDependency;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescriptions;
import edu.umn.msi.tropix.jobs.activities.descriptions.CommitObjectDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateBowtieIndexDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateTropixFileDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.UploadFileDescription;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.UploadComponent;
import edu.umn.msi.tropix.webgui.client.components.UploadComponentFactory.UploadComponentOptions;
import edu.umn.msi.tropix.webgui.client.components.newwizards.MetadataWizardPageFactory.MetadataWizardPageImpl;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.utils.Lists;
import edu.umn.msi.tropix.webgui.client.utils.Sets;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardCompletionHandler;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardFactoryImpl;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardOptions;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPage;
import edu.umn.msi.tropix.webgui.services.jobs.JobSubmitService;

public class BowtieIndexUploadCommandComponentFactoryImpl extends WizardCommandComponentFactoryImpl {

  class BowtieIndexUploadWizardCommand extends WizardCommand {
    private final UploadComponentOptions uploadOptions = new UploadComponentOptions(new AsyncCallbackImpl<LinkedHashMap<String, String>>()  {
      @Override
      public void handleSuccess() {
        final JobDescription jobDescription = new JobDescription("Create Bowtie index " + metadataPage.getMetadataCanvasSupplier().getName());        
        final UploadFileDescription uploadDescription = ActivityDescriptions.createUploadFileDescription(jobDescription, getResult().values().iterator().next());
        final CreateTropixFileDescription createFileDescription = ActivityDescriptions.createFileFromUpload(uploadDescription, false);
        final CreateBowtieIndexDescription createBowtieIndexDescription = new CreateBowtieIndexDescription();
        ActivityDescriptions.initCommonMetadata(createBowtieIndexDescription, metadataPage.getMetadataCanvasSupplier()); 
        createBowtieIndexDescription.addDependency(ActivityDependency.Builder.on(createFileDescription).produces("objectId").consumes("indexFileId").build());
        final CommitObjectDescription commitDescription = ActivityDescriptions.createCommitDescription(createBowtieIndexDescription);
        JobSubmitService.Util.getInstance().submit(Sets.newHashSet(uploadDescription, createFileDescription, createBowtieIndexDescription, commitDescription), new AsyncCallbackImpl<Void>());
        destroy();
      }
    });
    
    private final UploadWizardPageImpl uploadPage = new UploadWizardPageImpl(uploadComponentFactory.get(uploadOptions), "File", "Specify a Bowtie index file to upload");
    private final MetadataWizardPageImpl metadataPage = getMetadataWizardPageFactory().get(getLocations(), "Bowtie index");

    BowtieIndexUploadWizardCommand(final Collection<TreeItem> locations) {
      super(locations);
      final List<WizardPage> pages = Lists.<WizardPage>newArrayList(metadataPage, uploadPage);
      final WizardOptions options = new WizardOptions();
      options.setTitle("Upload a Bowtie Index");
      setWidget(WizardFactoryImpl.getInstance().getWizard(pages, options, new WizardCompletionHandler() {
        public void onCompletion(final Window wizard) {
          uploadPage.startUpload();
        }
      }));
    }
  }

  public WizardCommand get(final Collection<TreeItem> locations) {
    return new BowtieIndexUploadWizardCommand(locations);
  }

  private ComponentFactory<UploadComponentOptions, ? extends UploadComponent> uploadComponentFactory;

  @Inject
  public void setUploadComponentFactory(final ComponentFactory<UploadComponentOptions, ? extends UploadComponent> uploadComponentFactory) {
    this.uploadComponentFactory = uploadComponentFactory;
  }

}
