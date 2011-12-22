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
import java.util.LinkedHashMap;

import com.google.inject.Inject;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDependency;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescriptions;
import edu.umn.msi.tropix.jobs.activities.descriptions.CommitObjectDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateDatabaseDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateTropixFileDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.UploadFileDescription;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.UploadComponent;
import edu.umn.msi.tropix.webgui.client.components.UploadComponentFactory.UploadComponentOptions;
import edu.umn.msi.tropix.webgui.client.components.newwizards.MetadataWizardPageFactory.MetadataWizardPageImpl;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.utils.Sets;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardCompletionHandler;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardFactoryImpl;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardOptions;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPage;
import edu.umn.msi.tropix.webgui.services.jobs.JobSubmitService;

public class SequenceDatabaseCommandComponentFactoryImpl extends WizardCommandComponentFactoryImpl {

  class SequenceDatabaseWizardCommand extends WizardCommand {
    private final MetadataWizardPageImpl metadataPage = getMetadataWizardPageFactory().get(getLocations(), "Sequence database");
    private DynamicForm formatForm;

    private final UploadComponentOptions uploadOpts = new UploadComponentOptions(new AsyncCallbackImpl<LinkedHashMap<String, String>>() {
      @Override
      public void handleSuccess() {
        try {
          final JobDescription jobDescription = new JobDescription("Create sequence database " + metadataPage.getMetadataCanvasSupplier().getName());
          final UploadFileDescription uploadDescription = ActivityDescriptions.createUploadFileDescription(jobDescription, getResult().values().iterator().next());
          final CreateTropixFileDescription createDatabaseFileDescription = ActivityDescriptions.createFileFromUpload(uploadDescription, false);
          final CreateDatabaseDescription createDatabaseDescription = new CreateDatabaseDescription();
          createDatabaseDescription.setJobDescription(jobDescription);
          ActivityDescriptions.initCommonMetadata(createDatabaseDescription, metadataPage.getMetadataCanvasSupplier());
          createDatabaseDescription.addDependency(ActivityDependency.Builder.on(createDatabaseFileDescription).produces("objectId").consumes("databaseFileId").build());
          final String databaseFormat = formatForm.getValueAsString("format");
          createDatabaseDescription.setDatabaseType(databaseFormat);
          final CommitObjectDescription commitDescription = ActivityDescriptions.createCommitDescription(createDatabaseDescription);
          JobSubmitService.Util.getInstance().submit(Sets.newHashSet(uploadDescription, createDatabaseFileDescription, createDatabaseDescription, commitDescription), new AsyncCallbackImpl<Void>());
        } finally {
          destroy();
        }
      }
    });
    private final UploadComponent uploadComponent = uploadComponentFactory.get(uploadOpts);

    class UploadWizardPageImpl extends FileSourceWizardPageImpl {
            
      UploadWizardPageImpl() {
        super(uploadComponent);
        formatForm = new DynamicForm();
        formatForm.setWidth100();
        final SelectItem selectItem = new SelectItem();
        selectItem.setName("format");
        selectItem.setTitle("Format");
        selectItem.setValueMap("FASTA", "FASTQ", "RAW");
        selectItem.setValue("FASTA");
        formatForm.setItems(selectItem);
        formatForm.setHeight(25);
        this.setTitle("File");
        this.setDescription("Specify a sequence database file to upload");
        final VLayout layout = new VLayout();
        layout.setHeight100();
        layout.setWidth100();
        this.setCanvas(layout);
        setValid(true);
        layout.addMember(formatForm);
        layout.addMember(uploadComponent.get());
      }
    }

    SequenceDatabaseWizardCommand(final Collection<TreeItem> locations) {
      super(locations);

      final UploadWizardPageImpl uploadWizardPage = new UploadWizardPageImpl();
      final ArrayList<WizardPage> pages = new ArrayList<WizardPage>(2);
      pages.add(metadataPage);
      pages.add(uploadWizardPage);
      final WizardOptions options = new WizardOptions();
      options.setTitle("Upload a Sequence Database");
      setWidget(WizardFactoryImpl.getInstance().getWizard(pages, options, new WizardCompletionHandler() {
        public void onCompletion(final Window wizard) {
          uploadComponent.startUpload();
        }
      }));
    }
  }

  public WizardCommand get(final Collection<TreeItem> locations) {
    return new SequenceDatabaseWizardCommand(locations);
  }

  private ComponentFactory<UploadComponentOptions, ? extends UploadComponent> uploadComponentFactory;

  @Inject
  public void setUploadComponentFactory(final ComponentFactory<UploadComponentOptions, ? extends UploadComponent> uploadComponentFactory) {
    this.uploadComponentFactory = uploadComponentFactory;
  }  

}
