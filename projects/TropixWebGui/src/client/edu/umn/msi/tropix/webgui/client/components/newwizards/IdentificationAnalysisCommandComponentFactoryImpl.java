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
import java.util.HashSet;
import java.util.Map;

import com.google.common.base.Supplier;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.Window;

import edu.umn.msi.tropix.client.services.IdentificationGridService;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDependency;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescriptions;
import edu.umn.msi.tropix.jobs.activities.descriptions.CommitObjectDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateFolderDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateIdentificationAnalysisDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateIdentificationParametersDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateTropixFileDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.PollJobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.StringParameterSet;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitIdentificationAnalysisDescription;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.components.MetadataInputComponent;
import edu.umn.msi.tropix.webgui.client.components.ServiceSelectionComponent;
import edu.umn.msi.tropix.webgui.client.components.newwizards.LocationSelectionComponentImpl.InputType;
import edu.umn.msi.tropix.webgui.client.components.newwizards.MetadataWizardPageFactory.MetadataWizardPageImpl;
import edu.umn.msi.tropix.webgui.client.components.newwizards.RunTreeComponentImpl.RunInputTypeEnum;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.constants.ComponentConstants;
import edu.umn.msi.tropix.webgui.client.constants.ConstantsInstances;
import edu.umn.msi.tropix.webgui.client.identification.ParametersPanelFactory;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardCompletionHandler;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardFactoryImpl;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardOptions;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPage;
import edu.umn.msi.tropix.webgui.services.jobs.JobSubmitService;
import edu.umn.msi.tropix.webgui.services.protip.IdentificationJob;

public class IdentificationAnalysisCommandComponentFactoryImpl extends WizardCommandComponentFactoryImpl {
  private static final ComponentConstants CONSTANTS = ConstantsInstances.COMPONENT_INSTANCE;

  private String jobDescriptionName(final String analysisName) {
    return CONSTANTS.idWizardJobDescriptionName() + analysisName;
  }

  private class IdentificationAnalysisWizardCommand extends WizardCommand {
    IdentificationAnalysisWizardCommand(final Collection<TreeItem> locations) {
      super(locations);
      final ServiceSelectionComponent<IdentificationGridService> selectionComponent = serviceSelectionComponentSupplier.get();
      selectionComponent.setServicesType("proteinIdentification");
      final ServiceWizardPageImpl<IdentificationGridService> servicesSelectionPage = new ServiceWizardPageImpl<IdentificationGridService>(
          selectionComponent);

      final ParametersWizardPage parametersPage = new ParametersWizardPage(locations, parametersPanelFactory);
      final InputType inputType = batch ? null : RunInputTypeEnum.RUN;
      final IdentificationAnalysisInputsWizardPageImpl inputPage = new IdentificationAnalysisInputsWizardPageImpl(getTreeComponentFactory(),
          getLocationFactory(), getLocations(), inputType, false);
      final MetadataWizardPageImpl metadataWizardPage = getMetadataWizardPageFactory().get(getLocations(), CONSTANTS.idWizardAnalysisType());
      final MetadataInputComponent metadataCanvasSupplier = metadataWizardPage.getMetadataCanvasSupplier();
      final AsyncCallback<Void> callback = new AsyncCallbackImpl<Void>();

      servicesSelectionPage.addGridServiceChangedListener(new Listener<IdentificationGridService>() {
        public void onEvent(final IdentificationGridService event) {
          parametersPage.setParametersType(event.getParameterType());
        }
      });
      final ArrayList<WizardPage> pages = new ArrayList<WizardPage>(4);
      pages.add(metadataWizardPage);
      pages.add(servicesSelectionPage);
      pages.add(inputPage);
      pages.add(parametersPage);
      final WizardOptions options = new WizardOptions();
      options.setTitle(CONSTANTS.idWizardTitle());

      setWidget(WizardFactoryImpl.getInstance().getWizard(pages, options, new WizardCompletionHandler() {
        public void onCompletion(final Window wizard) {
          final IdentificationGridService gridService = servicesSelectionPage.getGridService();
          final Map<String, String> parameterMap = parametersPage.getCurrentPanel().getParametersMap();
          final String appName = gridService.getServiceName();
          final String databaseId = inputPage.getDatabaseId();
          if(batch) {
            IdentificationJob.Util.getInstance().getRuns(inputPage.getRunIds(), new AsyncCallbackImpl<Collection<ProteomicsRun>>() {
              protected void handleSuccess() {
                final HashSet<ActivityDescription> descriptions = new HashSet<ActivityDescription>();
                final CreateFolderDescription createFolder = new CreateFolderDescription();
                ActivityDescriptions.initCommonMetadata(createFolder, metadataCanvasSupplier);
                createFolder.setCommitted(true);
                descriptions.add(createFolder);

                final CreateIdentificationParametersDescription parametersDescription = new CreateIdentificationParametersDescription();
                parametersDescription.setCommitted(true);
                parametersDescription.setParameterType(gridService.getParameterType());
                parametersDescription.setParameters(StringParameterSet.fromMap(parameterMap));
                descriptions.add(parametersDescription);

                for(ProteomicsRun run : getResult()) {
                  final String analysisBase = run.getName();
                  final String analysisName = analysisBase + " (" + appName + ")";
                  final JobDescription jobDescription = new JobDescription(jobDescriptionName(analysisName));

                  final String runId = run.getId();
                  final SubmitIdentificationAnalysisDescription submitDescription = ActivityDescriptions.createSubmitIdentification(
                      parametersDescription, databaseId, runId, servicesSelectionPage.getGridService().getServiceAddress());
                  submitDescription.setJobDescription(jobDescription);
                  final PollJobDescription pollJobDescription = ActivityDescriptions.buildPollDescription(submitDescription);
                  final CreateTropixFileDescription createAnalysisFileDescription = ActivityDescriptions.buildCreateResultFile(pollJobDescription);

                  final CreateIdentificationAnalysisDescription createIdentificationAnalysisDescription = new CreateIdentificationAnalysisDescription();
                  createIdentificationAnalysisDescription.setName(analysisName);
                  createIdentificationAnalysisDescription.setDescription("");
                  createIdentificationAnalysisDescription.setJobDescription(jobDescription);
                  createIdentificationAnalysisDescription.setAnalysisType(gridService.getParameterType());
                  createIdentificationAnalysisDescription.setDatabaseId(databaseId);
                  createIdentificationAnalysisDescription.setRunId(runId);
                  createIdentificationAnalysisDescription.addDependency(ActivityDependency.Builder.on(createFolder).produces("objectId")
                      .consumes("destinationId").build());
                  createIdentificationAnalysisDescription.addDependency(ActivityDependency.Builder.on(createAnalysisFileDescription)
                      .produces("objectId").consumes("analysisFileId").build());
                  createIdentificationAnalysisDescription.addDependency(ActivityDependency.Builder.on(parametersDescription).produces("objectId")
                      .consumes("parametersId").build());

                  final CommitObjectDescription commitDescription = ActivityDescriptions
                      .createCommitDescription(createIdentificationAnalysisDescription);

                  descriptions.add(submitDescription);
                  descriptions.add(pollJobDescription);
                  descriptions.add(createAnalysisFileDescription);
                  descriptions.add(createIdentificationAnalysisDescription);
                  descriptions.add(commitDescription);
                }
                JobSubmitService.Util.getInstance().submit(descriptions, callback);
                destroy();
              }
            });
          } else {
            final String analysisBase = metadataCanvasSupplier.getName();
            final String analysisName = analysisBase + " (" + appName + ")";
            final HashSet<ActivityDescription> descriptions = new HashSet<ActivityDescription>();
            final JobDescription jobDescription = new JobDescription(jobDescriptionName(analysisName));

            final String runId = inputPage.getRunId();

            final CreateIdentificationParametersDescription parametersDescription = new CreateIdentificationParametersDescription();
            parametersDescription.setParameterType(gridService.getParameterType());
            parametersDescription.setParameters(StringParameterSet.fromMap(parameterMap));
            parametersDescription.setJobDescription(jobDescription);

            final SubmitIdentificationAnalysisDescription submitDescription = ActivityDescriptions.createSubmitIdentification(parametersDescription,
                databaseId, runId, servicesSelectionPage.getGridService().getServiceAddress());
            submitDescription.setJobDescription(jobDescription);
            submitDescription.addDependency(ActivityDependency.Builder.on(parametersDescription).produces("parametersId").consumes("parametersId")
                .build());

            final PollJobDescription pollJobDescription = ActivityDescriptions.buildPollDescription(submitDescription);
            final CreateTropixFileDescription createAnalysisFileDescription = ActivityDescriptions.buildCreateResultFile(pollJobDescription);

            final CreateIdentificationAnalysisDescription createIdentificationAnalysisDescription = new CreateIdentificationAnalysisDescription();
            ActivityDescriptions.initCommonMetadata(createIdentificationAnalysisDescription, metadataCanvasSupplier);
            createIdentificationAnalysisDescription.setName(analysisName);
            createIdentificationAnalysisDescription.setJobDescription(jobDescription);
            createIdentificationAnalysisDescription.setAnalysisType(gridService.getParameterType());
            createIdentificationAnalysisDescription.setDatabaseId(databaseId);
            createIdentificationAnalysisDescription.setRunId(runId);
            createIdentificationAnalysisDescription.addDependency(ActivityDependency.Builder.on(createAnalysisFileDescription).produces("objectId")
                .consumes("analysisFileId").build());
            createIdentificationAnalysisDescription.addDependency(ActivityDependency.Builder.on(parametersDescription).produces("objectId")
                .consumes("parametersId").build());

            final CommitObjectDescription commitDescription = ActivityDescriptions.createCommitDescription(createIdentificationAnalysisDescription);

            descriptions.add(parametersDescription);
            descriptions.add(submitDescription);
            descriptions.add(pollJobDescription);
            descriptions.add(createAnalysisFileDescription);
            descriptions.add(createIdentificationAnalysisDescription);
            descriptions.add(commitDescription);
            JobSubmitService.Util.getInstance().submit(descriptions, callback);
            destroy();
          }
        }
      }));
    }
  }

  @Override
  public WizardCommand get(final Collection<TreeItem> locations) {
    return new IdentificationAnalysisWizardCommand(locations);
  }

  private boolean batch = false;
  private Supplier<ServiceSelectionComponent<IdentificationGridService>> serviceSelectionComponentSupplier;
  private ParametersPanelFactory parametersPanelFactory;

  public void setBatch(final boolean batch) {
    this.batch = batch;
  }

  @Inject
  public void setServiceSelectionComponentSupplier(final Supplier<ServiceSelectionComponent<IdentificationGridService>> serviceGridSupplier) {
    this.serviceSelectionComponentSupplier = serviceGridSupplier;
  }

  @Inject
  public void setParametersPanelFactory(final ParametersPanelFactory parametersPanelFactory) {
    this.parametersPanelFactory = parametersPanelFactory;
  }
}