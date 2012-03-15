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
import java.util.List;
import java.util.Set;

import com.google.inject.Inject;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDependency;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescriptions;
import edu.umn.msi.tropix.jobs.activities.descriptions.CommitObjectDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateDatabaseDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateTropixFileDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.FileSourceHolder;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.UploadFileDescription;
import edu.umn.msi.tropix.models.utils.StockFileExtensionEnum;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.DynamicUploadComponent;
import edu.umn.msi.tropix.webgui.client.components.UploadComponentFactory.FileSource;
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

    private final UploadComponentOptions uploadOpts = new UploadComponentOptions(false, new AsyncCallbackImpl<List<FileSource>>() {
      @Override
      public void handleSuccess() {
        try {
          final Set<ActivityDescription> descriptions = Sets.newHashSet();
          final JobDescription jobDescription = new JobDescription("Create sequence database " + metadataPage.getMetadataCanvasSupplier().getName());
          final FileSource fileSource = getResult().get(0);
          final FileSourceHolder fileSourceHolder;
          if(fileSource.isUpload()) {
            final UploadFileDescription uploadDescription = ActivityDescriptions.createUploadFileDescription(jobDescription, fileSource.getId());
            final CreateTropixFileDescription createDatabaseFileDescription = ActivityDescriptions.createFileFromUpload(uploadDescription, false);
            fileSourceHolder = new FileSourceHolder(createDatabaseFileDescription);
            descriptions.add(uploadDescription);
            descriptions.add(createDatabaseFileDescription);
          } else {
            fileSourceHolder = new FileSourceHolder(fileSource.getId());
          }
          final CreateDatabaseDescription createDatabaseDescription = new CreateDatabaseDescription();
          createDatabaseDescription.setJobDescription(jobDescription);
          ActivityDescriptions.initCommonMetadata(createDatabaseDescription, metadataPage.getMetadataCanvasSupplier());
          if(fileSourceHolder.hasExistingId()) {
            createDatabaseDescription.setDatabaseFileId(fileSourceHolder.getTropixFileObjectId());
          } else {
            createDatabaseDescription.addDependency(ActivityDependency.Builder.on(fileSourceHolder.getCreateTropixFileDescription()).produces("objectId").consumes("databaseFileId").build());            
          }
          final String databaseFormat = formatForm.getValueAsString("format");
          createDatabaseDescription.setDatabaseType(databaseFormat);
          final CommitObjectDescription commitDescription = ActivityDescriptions.createCommitDescription(createDatabaseDescription);
          
          descriptions.add(createDatabaseDescription);
          descriptions.add(commitDescription);
          JobSubmitService.Util.getInstance().submit(descriptions, new AsyncCallbackImpl<Void>());
        } finally {
          destroy();
        }
      }
    });
    
    private String databaseType = "FASTA";
    private void updateSourceType() {
      if("FASTA".equals(databaseType)) {
        uploadOpts.setTypes("*.fasta");
        uploadOpts.setTypesDescription("FASTA Sequence File");
        uploadOpts.setExtension(StockFileExtensionEnum.FASTA.getExtension());
      } else if("FASTQ".equals(databaseType)) {
        uploadOpts.setTypes(null);
        uploadOpts.setTypesDescription(null);
        uploadOpts.setExtension(StockFileExtensionEnum.FASTQ.getExtension());
      } else if("RAW".equals(databaseType)) {
        uploadOpts.setTypes(null);
        uploadOpts.setTypesDescription(null);
        uploadOpts.setExtension(StockFileExtensionEnum.TEXT.getExtension());
      }
      uploadComponent.update(uploadOpts);
    }
    
    private final DynamicUploadComponent uploadComponent = uploadComponentFactory.get(uploadOpts);

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
        selectItem.addChangedHandler(new ChangedHandler() {

          public void onChanged(ChangedEvent event) {
            databaseType = selectItem.getValueAsString();
            updateSourceType();
          }
          
        });
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

        updateSourceType();
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

  private ComponentFactory<UploadComponentOptions, DynamicUploadComponent> uploadComponentFactory;

  @Inject
  public void setUploadComponentFactory(final ComponentFactory<UploadComponentOptions, DynamicUploadComponent> uploadComponentFactory) {
    this.uploadComponentFactory = uploadComponentFactory;
  }

}