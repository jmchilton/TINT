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

import com.google.common.base.Supplier;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;

import edu.umn.msi.tropix.client.services.ScaffoldGridService;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDependency;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescriptions;
import edu.umn.msi.tropix.jobs.activities.descriptions.CommitObjectDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateFolderDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateScaffoldAnalysisDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateScaffoldDriverDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateTropixFileDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.MergeScaffoldSamplesDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.PollJobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.StringParameterSet;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitScaffoldAnalysisDescription;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.components.MetadataInputComponent;
import edu.umn.msi.tropix.webgui.client.components.MetadataInputComponentFactory.MetadataOptions.DestinationType;
import edu.umn.msi.tropix.webgui.client.components.ServiceSelectionComponent;
import edu.umn.msi.tropix.webgui.client.components.newwizards.MetadataWizardPageFactory.MetadataWizardPageImpl;
import edu.umn.msi.tropix.webgui.client.components.newwizards.ScaffoldSampleTypeWizardPageImpl.ScaffoldSampleType;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItem;
import edu.umn.msi.tropix.webgui.client.constants.ComponentConstants;
import edu.umn.msi.tropix.webgui.client.constants.ConstantsInstances;
import edu.umn.msi.tropix.webgui.client.forms.ValidationListener;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.Lists;
import edu.umn.msi.tropix.webgui.client.utils.Sets;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardCompletionHandler;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardFactoryImpl;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardOptions;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPage;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPageGroup;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPageImpl;
import edu.umn.msi.tropix.webgui.services.jobs.JobSubmitService;

public class BatchScaffoldAnalysisCommandComponentFactoryImpl extends WizardCommandComponentFactoryImpl {
  private static final ComponentConstants CONSTANTS = ConstantsInstances.COMPONENT_INSTANCE;

  private class ScaffoldWizardCommand extends WizardCommand {

    private MetadataWizardPageImpl singleMetadataWizardPage;
    private MetadataWizardPageImpl batchMetadataWizardPage;
    private WizardPageGroup<MetadataWizardPageImpl> metadataWizardPages;

    private ScaffoldParametersPageImpl parametersPageImpl;
    private IdentificationWizardPageImpl identificationWizardPageImpl;
    private SampleSelectionWizardPageImpl sampleSelectionPageImpl;
    private ScaffoldSampleTypeWizardPageImpl typePageImpl;
    // private MetadataWizardPageImpl metadataPage;
    private ServiceSelectionComponent<ScaffoldGridService> selectionComponent;
    private ServiceWizardPageImpl<ScaffoldGridService> servicesSelectionPageImpl;

    private class IdentificationWizardPageImpl extends WizardPageImpl<Canvas> {
      private IdentificationTreeComponentImpl identificationTreeComponent = new IdentificationTreeComponentImpl(getTreeComponentFactory(),
          getLocationFactory(), getLocations(),
          ScaffoldConstants.validForSampleSelection(ScaffoldConstants.VALID_SCAFFOLD_IDENTIFICATION_TYPES), new ValidationListener() {
            public void onValidation(final boolean isValid) {
              setValid(isValid);
            }
          });

      IdentificationWizardPageImpl() {
        setTitle(CONSTANTS.scaffoldWizardSelectIdentificationTitle());
        setDescription(CONSTANTS.scaffoldWizardSelectIdentificationDescription());
        setCanvas(identificationTreeComponent.get());
        setEnabled(false);
      }

    }

    private String getAnalysisJobDescriptionName(final String analysisName) {
      return CONSTANTS.scaffoldWizardJobDescriptionName() + analysisName;
    }

    private void onComplete() {
      final Set<ActivityDescription> descriptions = Sets.newHashSet();
      final MetadataInputComponent metadataProvider = metadataWizardPages.getEnabledWizardPage().getMetadataCanvasSupplier();
      final String enteredName = metadataProvider.getName();
      Collection<TreeItem> treeItems = identificationWizardPageImpl.identificationTreeComponent.getSelectedItems();
      Collection<MergeScaffoldSamplesDescription> mergeScaffoldSamples = Lists.newArrayList();
      boolean analyzeAsMudpit = typePageImpl.getAnalyzeAsMudpit();
      final ScaffoldSampleType scaffoldType = typePageImpl.getScaffoldType();

      if(!scaffoldType.equals(ScaffoldSampleType.CUSTOM)) {
        CreateFolderDescription scaffoldFolder = null;
        if(scaffoldType.equals(ScaffoldSampleType.MANY_ANALYSIS)) {
          scaffoldFolder = new CreateFolderDescription();
          ActivityDescriptions.initCommonMetadata(scaffoldFolder, metadataProvider);
          descriptions.add(scaffoldFolder);

          for(TreeItem treeItem : treeItems) {
            final TropixObjectTreeItem identificationAnalysisItem = (TropixObjectTreeItem) treeItem;
            final String name = identificationAnalysisItem.getObject().getName();
            final JobDescription jobDescription = new JobDescription(getAnalysisJobDescriptionName(name));
            final MergeScaffoldSamplesDescription mergeSamples = new MergeScaffoldSamplesDescription();
            mergeSamples.setJobDescription(jobDescription);
            mergeSamples.addName(name);
            mergeSamples.addIdentificationId(identificationAnalysisItem.getId());
            mergeSamples.setMudpit(typePageImpl.getAnalyzeAsMudpit());
            mergeScaffoldSamples.add(mergeSamples);
          }
        } else {
          final JobDescription jobDescription = new JobDescription(getAnalysisJobDescriptionName(enteredName));
          final MergeScaffoldSamplesDescription mergeSamples = new MergeScaffoldSamplesDescription();
          mergeSamples.setJobDescription(jobDescription);
          mergeSamples.setMudpit(analyzeAsMudpit);
          if(scaffoldType == ScaffoldSampleType.ONE_SAMPLE) {
            mergeSamples.addName(enteredName);
            mergeSamples.setProduceMultipleSamples(false);
          } else {
            mergeSamples.setProduceMultipleSamples(true);
          }
          for(TreeItem treeItem : treeItems) {
            final TropixObjectTreeItem identificationAnalysisItem = (TropixObjectTreeItem) treeItem;
            mergeSamples.addIdentificationId(identificationAnalysisItem.getId());
            if(scaffoldType == ScaffoldSampleType.MANY_SAMPLE) {
              mergeSamples.addName(identificationAnalysisItem.getObject().getName());
            }
          }
          mergeScaffoldSamples.add(mergeSamples);
        }
        for(final MergeScaffoldSamplesDescription mergeSamples : mergeScaffoldSamples) {
          final CreateScaffoldDriverDescription createDriver = new CreateScaffoldDriverDescription();
          ScaffoldGridService scaffoldGridService = (ScaffoldGridService) servicesSelectionPageImpl.getGridService();
          createDriver.setJobDescription(mergeSamples.getJobDescription());
          createDriver.setScaffoldVersion(scaffoldGridService.getScaffoldVersion());
          createDriver.setParameterSet(StringParameterSet.fromMap(parametersPageImpl.getFormPanelSupplier().getParametersMap()));
          createDriver.addDependency(ActivityDependency.Builder.on(mergeSamples).produces("scaffoldSamples").consumes("scaffoldSamples").build());

          final CreateTropixFileDescription createDriverFile = ActivityDescriptions.createFileForScaffoldDriver(createDriver);
          final SubmitScaffoldAnalysisDescription submitDescription = ActivityDescriptions.createSubmitScaffold(createDriver, createDriverFile,
              scaffoldGridService.getServiceAddress());
          final PollJobDescription pollJobDescription = ActivityDescriptions.buildPollDescription(submitDescription);
          final CreateTropixFileDescription createAnalysisFileDescription = ActivityDescriptions.buildCreateResultFile(pollJobDescription);
          final CreateScaffoldAnalysisDescription createAnalysis = ActivityDescriptions.createScaffoldAnalysis(createDriver, createDriverFile,
              createAnalysisFileDescription);
          if(scaffoldFolder != null) {
            final List<String> sampleNames = mergeSamples.getNames().toList();
            createAnalysis.setName(sampleNames.get(0));
            createAnalysis.addDependency(ActivityDependency.Builder.on(scaffoldFolder).produces("objectId").consumes("destinationId").build());
          } else {
            ActivityDescriptions.initCommonMetadata(createAnalysis, metadataProvider);
          }
          createAnalysis.setScaffoldVersion(scaffoldGridService.getScaffoldVersion());

          final CommitObjectDescription commitScaffoldDescription = ActivityDescriptions.createCommitDescription(createAnalysis);

          descriptions.add(mergeSamples);
          descriptions.add(createDriver);
          descriptions.add(createDriverFile);
          descriptions.add(submitDescription);
          descriptions.add(pollJobDescription);
          descriptions.add(createAnalysisFileDescription);
          descriptions.add(createAnalysis);
          descriptions.add(commitScaffoldDescription);
        }
      } else {
        final ScaffoldGridService gridService = servicesSelectionPageImpl.getGridService();

        final JobDescription jobDescription = new JobDescription(getAnalysisJobDescriptionName(enteredName));
        final CreateScaffoldDriverDescription createDriver = new CreateScaffoldDriverDescription();
        createDriver.setScaffoldVersion(gridService.getScaffoldVersion());
        createDriver.setJobDescription(jobDescription);
        createDriver.setScaffoldSamples(sampleSelectionPageImpl.getScaffoldSamples());
        createDriver.setParameterSet(StringParameterSet.fromMap(parametersPageImpl.getFormPanelSupplier().getParametersMap()));

        final CreateTropixFileDescription createDriverFile = ActivityDescriptions.createFileForScaffoldDriver(createDriver);
        final SubmitScaffoldAnalysisDescription submitDescription = ActivityDescriptions.createSubmitScaffold(createDriver, createDriverFile,
            gridService.getServiceAddress());
        final PollJobDescription pollJobDescription = ActivityDescriptions.buildPollDescription(submitDescription);
        final CreateTropixFileDescription createAnalysisFileDescription = ActivityDescriptions.buildCreateResultFile(pollJobDescription);
        final CreateScaffoldAnalysisDescription createAnalysis = ActivityDescriptions.createScaffoldAnalysis(createDriver, createDriverFile,
            createAnalysisFileDescription);
        createAnalysis.setScaffoldVersion(gridService.getScaffoldVersion());
        ActivityDescriptions.initCommonMetadata(createAnalysis, metadataProvider);
        final CommitObjectDescription commitDescription = ActivityDescriptions.createCommitDescription(createAnalysis);

        descriptions.addAll(Sets.newHashSet(createDriver, createDriverFile, submitDescription,
            pollJobDescription, createAnalysisFileDescription, createAnalysis, commitDescription));
      }
      JobSubmitService.Util.getInstance().submit(descriptions, new AsyncCallbackImpl<Void>());
      destroy();
    }

    private void intializeMetadataPages() {
      singleMetadataWizardPage = getMetadataWizardPageFactory().get(getLocations(), CONSTANTS.scaffoldWizardAnalysisType(), DestinationType.HOME);
      batchMetadataWizardPage = getMetadataWizardPageFactory().get(getLocations(), CONSTANTS.scaffoldWizardAnalysisBatchType());
      metadataWizardPages = WizardPageGroup.getWizardPageGroupFor(singleMetadataWizardPage, batchMetadataWizardPage);
    }

    ScaffoldWizardCommand(final Collection<TreeItem> locations) {
      super(locations);

      selectionComponent = serviceSelectionComponentSupplier.get();
      selectionComponent.setServicesType("scaffold");
      servicesSelectionPageImpl = new ServiceWizardPageImpl<ScaffoldGridService>(selectionComponent);

      parametersPageImpl = new ScaffoldParametersPageImpl();
      identificationWizardPageImpl = new IdentificationWizardPageImpl();
      sampleSelectionPageImpl = new SampleSelectionWizardPageImpl(getLocationFactory(), getTreeComponentFactory());
      typePageImpl = new ScaffoldSampleTypeWizardPageImpl(true);

      typePageImpl.registerScaffoldSampleTypeChangeListener(new Listener<ScaffoldSampleType>() {
        public void onEvent(final ScaffoldSampleType event) {
          sampleSelectionPageImpl.setEnabled(event == ScaffoldSampleType.CUSTOM);
          identificationWizardPageImpl.setEnabled(event != ScaffoldSampleType.CUSTOM);
          boolean batch = event == ScaffoldSampleType.MANY_ANALYSIS;
          if(batch) {
            metadataWizardPages.enableOnly(batchMetadataWizardPage);
          } else {
            metadataWizardPages.enableOnly(singleMetadataWizardPage);
          }
        }
      });

      intializeMetadataPages();
      // metadataPage = getMetadataWizardPageFactory().get(getLocations(), "scaffold analysis");
      final ArrayList<WizardPage> pages = new ArrayList<WizardPage>(6);
      pages.add(typePageImpl);
      pages.add(singleMetadataWizardPage);
      pages.add(batchMetadataWizardPage);
      pages.add(servicesSelectionPageImpl);
      pages.add(identificationWizardPageImpl);
      pages.add(sampleSelectionPageImpl);
      pages.add(parametersPageImpl);
      final WizardOptions options = new WizardOptions();
      options.setTitle("New Scaffold Analysis");
      setWidget(WizardFactoryImpl.getInstance().getWizard(pages, options, new WizardCompletionHandler() {
        public void onCompletion(final Window wizard) {
          onComplete();
        }
      }));
    }

  }

  @Override
  public WizardCommand get(final Collection<TreeItem> locations) {
    return new ScaffoldWizardCommand(locations);
  }

  private Supplier<ServiceSelectionComponent<ScaffoldGridService>> serviceSelectionComponentSupplier;

  @Inject
  public void setServiceSelectionComponentSupplier(final Supplier<ServiceSelectionComponent<ScaffoldGridService>> serviceGridSupplier) {
    this.serviceSelectionComponentSupplier = serviceGridSupplier;
  }

}
