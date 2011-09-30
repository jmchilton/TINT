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
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;

import edu.umn.msi.tropix.client.services.QueueGridService;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDependency;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescriptions;
import edu.umn.msi.tropix.jobs.activities.descriptions.CommitObjectDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CommonMetadataProvider;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateITraqQuantitationAnalysisDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateITraqQuantitationTrainingDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateTropixFileDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.IdList;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.PollJobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.StringParameter;
import edu.umn.msi.tropix.jobs.activities.descriptions.StringParameterSet;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitITraqQuantitationAnalysisDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitITraqQuantitationTrainingDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitJobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.TropixObjectDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.UploadFileDescription;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.utils.ModelFunctions;
import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.ServiceSelectionComponent;
import edu.umn.msi.tropix.webgui.client.components.UploadComponent;
import edu.umn.msi.tropix.webgui.client.components.UploadComponentFactory.UploadComponentOptions;
import edu.umn.msi.tropix.webgui.client.components.newwizards.MetadataWizardPageFactory.MetadataWizardPageImpl;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItemPredicates;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItemExpander;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItemExpanders;
import edu.umn.msi.tropix.webgui.client.constants.ComponentConstants;
import edu.umn.msi.tropix.webgui.client.constants.ConstantsInstances;
import edu.umn.msi.tropix.webgui.client.utils.Iterables;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.Lists;
import edu.umn.msi.tropix.webgui.client.utils.Sets;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.Form;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils.ConditionalWidgetSupplier;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardCompletionHandler;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardFactoryImpl;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardOptions;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPage;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPageImpl;
import edu.umn.msi.tropix.webgui.services.jobs.JobSubmitService;
import edu.umn.msi.tropix.webgui.services.protip.IdentificationJob;

public class ITraqQuantitationCommandComponentFactoryImpl extends WizardCommandComponentFactoryImpl {
  private static final ComponentConstants CONSTANTS = ConstantsInstances.COMPONENT_INSTANCE;
  private boolean training;

  public ITraqQuantitationCommandComponentFactoryImpl(final boolean training) {
    this.training = training;
  }

  private class ITraqQuantitationWizardCommand extends WizardCommand {
    private final MetadataWizardPageImpl metadataWizardPage;
    private final ServiceWizardPageImpl<QueueGridService> servicesPage;
    private final Form typeForm = new Form();
    private Form trainingForm;
    private String scaffoldAnalysisId = null;
    private ConditionalWidgetSupplier conditionalTrainingComponent;
    private TreeComponent trainingTree;
    private Collection<ProteomicsRun> runs;

    private String getQuantificationType() {
      return typeForm.getValueAsString("quantificationType");
    }

    private void submit(final String fileId) {
      final CommonMetadataProvider metadataProvider = metadataWizardPage.getMetadataCanvasSupplier();
      final String analysisName = metadataProvider.getName();
      final String descriptionName;
      if(training) {
        descriptionName = CONSTANTS.ltqiquantTrainingWizardJobDescriptionName() + analysisName;
      } else {
        descriptionName = CONSTANTS.ltqiquantWizardJobDescriptionName() + analysisName;
      }

      final JobDescription jobDescription = new JobDescription(descriptionName);
      final UploadFileDescription upload = ActivityDescriptions.createUploadFileDescription(jobDescription, fileId);
      final CreateTropixFileDescription createReportFile = ActivityDescriptions.createFileFromUpload(upload, false);
      final String quantificationType = getQuantificationType();
      final IdList runIds = IdList.forIterable(Iterables.transform(runs, ModelFunctions.getIdFunction()));
      final SubmitJobDescription submitDescription;
      final String trainingId = conditionalTrainingComponent.useSelection() ? trainingTree.getSelection().getId() : null;
      if(training) {
        final SubmitITraqQuantitationTrainingDescription submit = new SubmitITraqQuantitationTrainingDescription();
        submit.setRunIdList(runIds);
        submit.setQuantificationType(quantificationType);
        final StringParameterSet parameters = new StringParameterSet();
        @SuppressWarnings("unchecked")
        final Map<String, Object> valueMap = trainingForm.getValues();
        for(final Map.Entry<String, Object> entry : valueMap.entrySet()) {
          final StringParameter stringParameter = new StringParameter();
          stringParameter.setKey(entry.getKey());
          stringParameter.setValue(entry.getValue().toString());
          parameters.addParameter(stringParameter);
        }
        submit.setParameterSet(parameters);
        submitDescription = submit;
      } else {
        final SubmitITraqQuantitationAnalysisDescription submit = new SubmitITraqQuantitationAnalysisDescription();
        submit.setRunIdList(runIds);
        submit.setQuantificationType(quantificationType);
        submit.setTrainingId(trainingId);
        submitDescription = submit;
      }
      submitDescription.setJobDescription(jobDescription);
      submitDescription.setServiceUrl(servicesPage.getGridService().getServiceAddress());
      submitDescription.addDependency(ActivityDependency.Builder.on(createReportFile).produces("objectId").consumes("reportFileId").build());

      final PollJobDescription pollJobDescription = ActivityDescriptions.buildPollDescription(submitDescription);
      final CreateTropixFileDescription createResultFileDescription = ActivityDescriptions.buildCreateResultFile(pollJobDescription);

      final TropixObjectDescription objectDescription;
      if(training) {
        final CreateITraqQuantitationTrainingDescription createTraining = new CreateITraqQuantitationTrainingDescription();
        createTraining.setRunIdList(runIds);
        objectDescription = createTraining;
      } else {
        final CreateITraqQuantitationAnalysisDescription createAnalysis = new CreateITraqQuantitationAnalysisDescription();
        createAnalysis.setRunIdList(runIds);
        createAnalysis.setTrainingId(trainingId);

        objectDescription = createAnalysis;
      }
      ActivityDescriptions.initCommonMetadata(objectDescription, metadataProvider);
      objectDescription.setJobDescription(jobDescription);
      objectDescription.addDependency(ActivityDependency.Builder.on(createResultFileDescription).produces("objectId").consumes("outputFileId")
          .build());
      objectDescription.addDependency(ActivityDependency.Builder.on(createReportFile).produces("objectId").consumes("reportFileId").build());
      final CommitObjectDescription commitDescription = ActivityDescriptions.createCommitDescription(objectDescription);
      JobSubmitService.Util.getInstance().submit(
          Sets.newHashSet(upload, createReportFile, submitDescription, pollJobDescription, createResultFileDescription, objectDescription,
              commitDescription), new AsyncCallbackImpl<Void>());
      destroy();
    }

    private final WizardPageImpl<VLayout> parametersPage = new WizardPageImpl<VLayout>() {

      private void resetTrainingForm(final Listener<Boolean> validationListener) {
        if(trainingForm != null) {
          getCanvas().removeMember(trainingForm);
          trainingForm.destroy();
        }

        trainingForm = new Form();
        trainingForm.addValidationListener(validationListener);
        validationListener.onEvent(false); // Form isn't initially valid
        trainingForm.setHeight("*");
        final List<TextItem> items = new LinkedList<TextItem>();
        final TextItem i114Item = new TextItem("i114Proportion", "i114 Proportion");
        final TextItem i115Item = new TextItem("i115Proportion", "i115 Proportion");
        final TextItem i116Item = new TextItem("i116Proportion", "i116 Proportion");
        final TextItem i117Item = new TextItem("i117Proportion", "i117 Proportion");
        if(getQuantificationType().equals("FOUR_PLEX")) {
          trainingForm.setNumCols(2);
          items.addAll(Arrays.asList(i114Item, i115Item, i116Item, i117Item));
        } else {
          trainingForm.setNumCols(4);
          final TextItem i113Item = new TextItem("i113Proportion", "i113 Proportion");
          final TextItem i118Item = new TextItem("i118Proportion", "i118 Proportion");
          final TextItem i119Item = new TextItem("i119Proportion", "i119 Proportion");
          final TextItem i121Item = new TextItem("i121Proportion", "i121 Proportion");
          items.addAll(Arrays.asList(i113Item, i114Item, i115Item, i116Item, i117Item, i118Item, i119Item, i121Item));
        }
        final TextItem numBins = new TextItem("numBins", "Number of Bins");
        numBins.setValue(100);
        items.add(numBins);
        for(FormItem item : items) {
          item.setWidth(100);
        }
        trainingForm.setItems(items.toArray(new FormItem[0]));
        trainingForm.setValidationPredicate(new Predicate<Form>() {
          public boolean apply(final Form form) {
            for(final TextItem item : items) {
              final String itemValue = form.getValueAsString(item.getName());
              if(!StringUtils.hasText(itemValue)) {
                return false;
              }
              try {
                Double.parseDouble(itemValue);
              } catch(final RuntimeException e) {
                return false;
              }
            }
            return true;
          }
        });
        getCanvas().addMember(trainingForm);
      }

      {
        setCanvas(new VLayout());
        setTitle(CONSTANTS.ltqiquantWizardParametersTitle());
        setDescription(CONSTANTS.ltqiquantWizardParametersDescription());
        setValid(true);

        final SelectItem selectionItem = new SelectItem("quantificationType", "iTraq Label Type");
        final LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
        valueMap.put("FOUR_PLEX", "4-Plex");
        valueMap.put("EIGHT_PLEX", "8-Plex");
        selectionItem.setValueMap(valueMap);
        selectionItem.setValue("FOUR_PLEX");
        typeForm.setItems(selectionItem);
        typeForm.setWidth100();
        typeForm.setColWidths("160", "*");
        getCanvas().addMember(typeForm);
        if(training) {
          selectionItem.addChangedHandler(new ChangedHandler() {
            public void onChanged(final ChangedEvent event) {
              resetTrainingForm(getValidationListener());
            }
          });
          resetTrainingForm(getValidationListener());
        }
      }
    };

    private final UploadComponentOptions uploadOptions = new UploadComponentOptions(new AsyncCallbackImpl<LinkedHashMap<String, String>>() {
      @Override
      public void handleSuccess() {
        submit(getResult().values().iterator().next());
      }
    });

    private final UploadWizardPageImpl uploadPage = new UploadWizardPageImpl(uploadComponentFactory.get(uploadOptions),
        CONSTANTS.ltqiquantWizardFileTitle(),
        CONSTANTS.ltqiquantWizardFileDescription());

    private WizardPageImpl<VLayout> selectWizardPage = new WizardPageImpl<VLayout>() {
      {
        this.setCanvas(new VLayout());
        this.setTitle(CONSTANTS.ltqiquantWizardAnalysisTitle());
        this.setDescription(CONSTANTS.ltqiquantWizardAnalysisDescription());

        final TropixObjectType[] types = new TropixObjectType[] {TropixObjectTypeEnum.VIRTUAL_FOLDER, TropixObjectTypeEnum.FOLDER,
            TropixObjectTypeEnum.SCAFFOLD_ANALYSIS};
        final TropixObjectTreeItemExpander expander = TropixObjectTreeItemExpanders.get(types);
        final TreeOptions treeOptions = new TreeOptions();
        treeOptions.setInitialItems(getLocationFactory().getTropixObjectSourceRootItems(expander));
        treeOptions.setShowPredicate(TreeItemPredicates.getTropixObjectTreeItemTypePredicate(types, true));
        treeOptions.setSelectionPredicate(TreeItemPredicates.getTropixObjectNotFolderPredicate());
        final TreeComponent tree = getTreeComponentFactory().get(treeOptions);

        tree.addSelectionListener(new Listener<TreeItem>() {
          public void onEvent(final TreeItem treeItem) {
            scaffoldAnalysisId = treeItem == null ? null : treeItem.getId();
            setValid(treeItem != null);
          }
        });

        final TreeGrid treeGrid = tree.get();
        treeGrid.setWidth("100%");
        treeGrid.setHeight("100%");
        this.getCanvas().addMember(treeGrid);
        this.setValid(false);
      }
    };

    private WizardPageImpl<Layout> trainingWizardPage = new WizardPageImpl<Layout>() {
      {
        this.setTitle(CONSTANTS.ltqiquantWizardTrainingTitle());
        this.setDescription(CONSTANTS.ltqiquantWizardTrainingDescription());

        final TropixObjectType[] types = new TropixObjectType[] {TropixObjectTypeEnum.VIRTUAL_FOLDER, TropixObjectTypeEnum.FOLDER,
            TropixObjectTypeEnum.ITRAQ_QUANTITATION_TRAINING};
        final TropixObjectTreeItemExpander expander = TropixObjectTreeItemExpanders.get(types);
        final TreeOptions treeOptions = new TreeOptions();
        treeOptions.setInitialItems(getLocationFactory().getTropixObjectSourceRootItems(expander));
        treeOptions.setShowPredicate(TreeItemPredicates.getTropixObjectTreeItemTypePredicate(types, true));
        treeOptions.setSelectionPredicate(TreeItemPredicates.getTropixObjectNotFolderPredicate());
        trainingTree = getTreeComponentFactory().get(treeOptions);

        conditionalTrainingComponent = SmartUtils.getConditionalSelectionWidget(CONSTANTS.ltqiquantWizardUseTraining(), trainingTree,
            getValidationListener());
        setCanvas(conditionalTrainingComponent.get());
        this.setValid(true);
      }
    };

    ITraqQuantitationWizardCommand(final Collection<TreeItem> locations) {
      super(locations);
      final ArrayList<WizardPage> pages = new ArrayList<WizardPage>(4);
      final String objectType;
      if(training) {
        objectType = CONSTANTS.ltqiquantWizardTrainingType();
      } else {
        objectType = CONSTANTS.ltqiquantWizardAnalysisType();
      }
      metadataWizardPage = getMetadataWizardPageFactory().get(locations, objectType);

      final ServiceSelectionComponent<QueueGridService> selectionComponent = serviceSelectionComponentSupplier.get();
      selectionComponent.setServicesType("iTraqQuantitation");
      servicesPage = new ServiceWizardPageImpl<QueueGridService>(selectionComponent);
      pages.add(metadataWizardPage);
      pages.add(servicesPage);
      pages.add(selectWizardPage);
      if(!training) {
        pages.add(trainingWizardPage);
      }
      pages.add(parametersPage);
      pages.add(uploadPage);

      final WizardOptions options = new WizardOptions();
      final String title;
      if(training) {
        title = CONSTANTS.ltqiquantTrainingWizardTitle();
      } else {
        title = CONSTANTS.ltqiquantWizardTitle();
      }
      options.setTitle(title);
      setWidget(WizardFactoryImpl.getInstance().getWizard(pages, options, new WizardCompletionHandler() {
        public void onCompletion(final Window wizard) {
          IdentificationJob.Util.getInstance().getRuns(Lists.newArrayList(scaffoldAnalysisId), new AsyncCallbackImpl<Collection<ProteomicsRun>>() {
            @Override
            protected void handleSuccess() {
              runs = getResult();
              uploadPage.startUpload();
            }
          });
        }
      }));
    }
  }

  public WizardCommand get(final Collection<TreeItem> locations) {
    return new ITraqQuantitationWizardCommand(locations);
  }

  private Supplier<ServiceSelectionComponent<QueueGridService>> serviceSelectionComponentSupplier;

  @Inject
  public void setServiceSelectionComponentSupplier(final Supplier<ServiceSelectionComponent<QueueGridService>> serviceGridSupplier) {
    this.serviceSelectionComponentSupplier = serviceGridSupplier;
  }

  private ComponentFactory<UploadComponentOptions, ? extends UploadComponent> uploadComponentFactory;

  @Inject
  public void setUploadComponentFactory(final ComponentFactory<UploadComponentOptions, ? extends UploadComponent> uploadComponentFactory) {
    this.uploadComponentFactory = uploadComponentFactory;
  }

}
