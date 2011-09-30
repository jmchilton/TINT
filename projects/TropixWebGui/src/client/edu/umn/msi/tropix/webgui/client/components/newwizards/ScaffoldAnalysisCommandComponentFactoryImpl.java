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
import com.smartgwt.client.widgets.Window;

import edu.umn.msi.tropix.client.services.ScaffoldGridService;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescriptions;
import edu.umn.msi.tropix.jobs.activities.descriptions.CommitObjectDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateScaffoldAnalysisDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateScaffoldDriverDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateTropixFileDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.PollJobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.ScaffoldSample;
import edu.umn.msi.tropix.jobs.activities.descriptions.StringParameterSet;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitScaffoldAnalysisDescription;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.components.MetadataInputComponent;
import edu.umn.msi.tropix.webgui.client.components.ServiceSelectionComponent;
import edu.umn.msi.tropix.webgui.client.components.newwizards.MetadataWizardPageFactory.MetadataWizardPageImpl;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.constants.ComponentConstants;
import edu.umn.msi.tropix.webgui.client.constants.ConstantsInstances;
import edu.umn.msi.tropix.webgui.client.utils.Sets;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardCompletionHandler;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardFactoryImpl;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardOptions;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPage;
import edu.umn.msi.tropix.webgui.services.jobs.JobSubmitService;

public class ScaffoldAnalysisCommandComponentFactoryImpl extends WizardCommandComponentFactoryImpl {
  private static final ComponentConstants CONSTANTS = ConstantsInstances.COMPONENT_INSTANCE;
  private final boolean enableQuant;

  public ScaffoldAnalysisCommandComponentFactoryImpl() {
    this(false);
  }

  public ScaffoldAnalysisCommandComponentFactoryImpl(final boolean enableQuant) {
    this.enableQuant = enableQuant;
  }

  private class ScaffoldWizardCommand extends WizardCommand {

    ScaffoldWizardCommand(final Collection<TreeItem> locations) {
      super(locations);

      final ServiceSelectionComponent<ScaffoldGridService> selectionComponent = serviceSelectionComponentSupplier.get();
      selectionComponent.setServicesType("scaffold");

      final MetadataWizardPageImpl metadataPage = getMetadataWizardPageFactory().get(getLocations(), CONSTANTS.scaffoldWizardType());
      final ServiceWizardPageImpl<ScaffoldGridService> servicesSelectionPageImpl = new ServiceWizardPageImpl<ScaffoldGridService>(selectionComponent);
      final SampleSelectionWizardPageImpl sampleSelectionPageImpl = new SampleSelectionWizardPageImpl(getLocationFactory(), getTreeComponentFactory());
      final ScaffoldParametersPageImpl parametersPageImpl = new ScaffoldParametersPageImpl();
      final ScaffoldQuantSamplesWizardPageImpl quantSamplesPage = new ScaffoldQuantSamplesWizardPageImpl(sampleSelectionPageImpl);
      final ScaffoldQuantCategoriesWizardPageImpl quantCategoriesPage = new ScaffoldQuantCategoriesWizardPageImpl(
          quantSamplesPage.getCategoryUpdateListener());

      final ArrayList<WizardPage> pages = new ArrayList<WizardPage>(4);
      pages.add(metadataPage);
      pages.add(servicesSelectionPageImpl);
      pages.add(sampleSelectionPageImpl);
      if(enableQuant) {
        pages.add(quantCategoriesPage);
        pages.add(quantSamplesPage);
      }
      pages.add(parametersPageImpl);

      final WizardOptions options = new WizardOptions();
      options.setTitle(CONSTANTS.scaffoldWizardTitle());
      setWidget(WizardFactoryImpl.getInstance().getWizard(pages, options, new WizardCompletionHandler() {
        public void onCompletion(final Window wizard) {
          final ScaffoldGridService gridService = servicesSelectionPageImpl.getGridService();
          final MetadataInputComponent metadataProvider = metadataPage.getMetadataCanvasSupplier();

          final JobDescription jobDescription = new JobDescription(CONSTANTS.scaffoldWizardJobDescriptionName() + metadataProvider.getName());
          final CreateScaffoldDriverDescription createDriver = new CreateScaffoldDriverDescription();
          createDriver.setScaffoldVersion(gridService.getScaffoldVersion());
          createDriver.setJobDescription(jobDescription);
          final List<ScaffoldSample> scaffoldSamples = sampleSelectionPageImpl.getScaffoldSamples();
          if(enableQuant) {
            quantSamplesPage.augmentScaffoldSamples(scaffoldSamples);
          }
          createDriver.setScaffoldSamples(scaffoldSamples);
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

          final Set<ActivityDescription> activityDescriptions = Sets.newHashSet(createDriver, createDriverFile, submitDescription,
              pollJobDescription, createAnalysisFileDescription, createAnalysis, commitDescription);
          JobSubmitService.Util.getInstance().submit(activityDescriptions, new AsyncCallbackImpl<Void>());
          destroy();
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
