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
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateIdentificationAnalysisDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateTropixFileDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.UploadFileDescription;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.UploadComponent;
import edu.umn.msi.tropix.webgui.client.components.UploadComponent.CanUpload;
import edu.umn.msi.tropix.webgui.client.components.UploadComponentFactory.UploadComponentOptions;
import edu.umn.msi.tropix.webgui.client.components.newwizards.MetadataWizardPageFactory.MetadataWizardPageImpl;
import edu.umn.msi.tropix.webgui.client.components.newwizards.RunTreeComponentImpl.RunInputTypeEnum;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.constants.ComponentConstants;
import edu.umn.msi.tropix.webgui.client.constants.ConstantsInstances;
import edu.umn.msi.tropix.webgui.client.utils.Sets;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardCompletionHandler;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardFactoryImpl;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardOptions;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPage;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPageImpl;
import edu.umn.msi.tropix.webgui.services.jobs.JobSubmitService;

public class IdentificationAnalysisUploadCommandComponentFactoryImpl extends WizardCommandComponentFactoryImpl {
  private static final ComponentConstants CONSTANTS = ConstantsInstances.COMPONENT_INSTANCE;

  private class IdentificationAnalysisUploadWizardCommand extends WizardCommand {
    private SelectItem analysisTypeSelectItem;
    private MetadataWizardPageImpl metadataPage = getMetadataWizardPageFactory().get(getLocations(), CONSTANTS.idWizardAnalysisType());
    private IdentificationAnalysisInputsWizardPageImpl inputsPage = new IdentificationAnalysisInputsWizardPageImpl(getTreeComponentFactory(),
        getLocationFactory(), getLocations(), RunInputTypeEnum.RUN, true);

    private final UploadComponentOptions uploadOpts = new UploadComponentOptions(new AsyncCallbackImpl<LinkedHashMap<String, String>>() {
      @Override
      public void handleSuccess() {
        try {
          final JobDescription jobDescription = new JobDescription(CONSTANTS.idWizardJobDescriptionName()
              + metadataPage.getMetadataCanvasSupplier().getName());
          final UploadFileDescription uploadDescription = ActivityDescriptions.createUploadFileDescription(jobDescription, getResult().values()
              .iterator().next());
          final CreateTropixFileDescription createAnalysisFileDescription = ActivityDescriptions.createFileFromUpload(uploadDescription, false);
          final CreateIdentificationAnalysisDescription createIdentificationAnalysisDescription = new CreateIdentificationAnalysisDescription();
          createIdentificationAnalysisDescription.setCommitted(true);
          createIdentificationAnalysisDescription.setJobDescription(jobDescription);
          ActivityDescriptions.initCommonMetadata(createIdentificationAnalysisDescription, metadataPage.getMetadataCanvasSupplier());
          createIdentificationAnalysisDescription.setAnalysisType(analysisTypeSelectItem.getValue().toString());
          createIdentificationAnalysisDescription.setDatabaseId(inputsPage.getDatabaseId());
          createIdentificationAnalysisDescription.setRunId(inputsPage.getRunId());
          createIdentificationAnalysisDescription.addDependency(ActivityDependency.Builder.on(createAnalysisFileDescription).produces("objectId")
              .consumes("analysisFileId").build());
          final CommitObjectDescription commitDescription = ActivityDescriptions.createCommitDescription(createIdentificationAnalysisDescription);
          final Set<ActivityDescription> descriptions = Sets.newHashSet(uploadDescription, createAnalysisFileDescription,
              createIdentificationAnalysisDescription, commitDescription);
          JobSubmitService.Util.getInstance().submit(descriptions, new AsyncCallbackImpl<Void>());
        } finally {
          destroy();
        }
      }
    });
    private final UploadComponent uploadComponent = uploadComponentFactory.get(uploadOpts);

    private WizardPage uploadWizardPage = new WizardPageImpl<VLayout>() {
      @Override
      public boolean allowNext() {
        return super.isValid();
      }

      @Override
      public boolean isValid() {
        final CanUpload canUpload = uploadComponent.canUpload();
        setError(canUpload.getReason());
        return super.isValid() && canUpload.getCanUpload();
      }

      {
        this.setTitle(CONSTANTS.idWizardPrerunFileTitle());
        this.setDescription(CONSTANTS.idWizardPrerunFileDescription());
        this.setCanvas(new VLayout());
        final DynamicForm form = new DynamicForm();
        analysisTypeSelectItem = new SelectItem();
        analysisTypeSelectItem.setTitle(CONSTANTS.idWizardPrerunFileType());
        final LinkedHashMap<String, String> typeMap = new LinkedHashMap<String, String>(4);
        typeMap.put("Mascot", CONSTANTS.idWizardPrerunFileMascot());
        typeMap.put("SequestBean", CONSTANTS.idWizardPrerunFileSequest());
        analysisTypeSelectItem.setValueMap(typeMap);
        form.setItems(analysisTypeSelectItem);
        form.setHeight(25);
        analysisTypeSelectItem.addChangedHandler(new ChangedHandler() {
          public void onChanged(final ChangedEvent event) {
            setValid(analysisTypeSelectItem.getValue() != null);
          }
        });
        this.setValid(false);
        this.getCanvas().addMember(form);
        this.getCanvas().addMember(uploadComponent.get());
      }
    };

    IdentificationAnalysisUploadWizardCommand(final Collection<TreeItem> locations) {
      super(locations);
      final ArrayList<WizardPage> pages = new ArrayList<WizardPage>(3);
      pages.add(metadataPage);
      pages.add(inputsPage);
      pages.add(uploadWizardPage);
      final WizardOptions options = new WizardOptions();
      options.setTitle(CONSTANTS.idWizardPrerunTitle());
      setWidget(WizardFactoryImpl.getInstance().getWizard(pages, options, new WizardCompletionHandler() {
        public void onCompletion(final Window wizard) {
          uploadComponent.startUpload();
        }
      }));
    }

  }

  @Override
  public WizardCommand get(final Collection<TreeItem> locations) {
    return new IdentificationAnalysisUploadWizardCommand(locations);
  }

  private ComponentFactory<UploadComponentOptions, ? extends UploadComponent> uploadComponentFactory;

  @Inject
  public void setUploadComponentFactory(final ComponentFactory<UploadComponentOptions, ? extends UploadComponent> uploadComponentFactory) {
    this.uploadComponentFactory = uploadComponentFactory;
  }

}
