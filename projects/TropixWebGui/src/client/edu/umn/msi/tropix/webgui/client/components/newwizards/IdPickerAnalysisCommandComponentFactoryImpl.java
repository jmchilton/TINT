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
import java.util.Set;

import com.google.common.base.Supplier;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.client.services.GridService;
import edu.umn.msi.tropix.client.services.QueueGridService;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescriptions;
import edu.umn.msi.tropix.jobs.activities.descriptions.CommitObjectDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateIdPickerParametersDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateTropixFileDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.PollJobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.StringParameterSet;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitIdPickerAnalysisDescription;
import edu.umn.msi.tropix.models.proteomics.IdentificationType;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.components.MetadataInputComponent;
import edu.umn.msi.tropix.webgui.client.components.ServiceSelectionComponent;
import edu.umn.msi.tropix.webgui.client.components.newwizards.MetadataWizardPageFactory.MetadataWizardPageImpl;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.forms.FormPanelFactory;
import edu.umn.msi.tropix.webgui.client.forms.FormPanelSupplier;
import edu.umn.msi.tropix.webgui.client.forms.ValidationListener;
import edu.umn.msi.tropix.webgui.client.utils.Lists;
import edu.umn.msi.tropix.webgui.client.utils.Sets;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardCompletionHandler;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardFactoryImpl;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardOptions;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPage;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPageImpl;
import edu.umn.msi.tropix.webgui.services.jobs.JobSubmitService;

public class IdPickerAnalysisCommandComponentFactoryImpl extends WizardCommandComponentFactoryImpl {

  private class IdPickerWizardCommand extends WizardCommand {

    IdPickerWizardCommand(final Collection<TreeItem> locations) {
      super(locations);

      final ServiceSelectionComponent<QueueGridService> selectionComponent = serviceSelectionComponentSupplier.get();
      selectionComponent.setServicesType("idPicker");
      final ServiceWizardPageImpl<QueueGridService> servicesSelectionPageImpl = new ServiceWizardPageImpl<QueueGridService>(selectionComponent);

      final ParametersPageImpl parametersPageImpl = new ParametersPageImpl();
      final SampleSelectionWizardPageImpl sampleSelectionPageImpl = new SampleSelectionWizardPageImpl(getLocationFactory(),
          getTreeComponentFactory(), false, Lists.newArrayList(IdentificationType.MYRIMATCH, IdentificationType.TAGRECON));
      final MetadataWizardPageImpl metadataPage = getMetadataWizardPageFactory().get(getLocations(), "IDPicker analysis");
      final ArrayList<WizardPage> pages = new ArrayList<WizardPage>(4);
      pages.add(metadataPage);
      pages.add(servicesSelectionPageImpl);
      pages.add(sampleSelectionPageImpl);
      pages.add(parametersPageImpl);
      final WizardOptions options = new WizardOptions();
      options.setTitle("New IDPicker Analysis");
      setWidget(WizardFactoryImpl.getInstance().getWizard(pages, options, new WizardCompletionHandler() {
        public void onCompletion(final Window wizard) {
          final GridService gridService = servicesSelectionPageImpl.getGridService();
          final MetadataInputComponent metadataProvider = metadataPage.getMetadataCanvasSupplier();
          final String name = metadataProvider.getName();
          String destinationId = metadataProvider.getDestinationId();

          final JobDescription jobDescription = new JobDescription("Create IDPicker analysis " + name);
          final CreateIdPickerParametersDescription createDriver = new CreateIdPickerParametersDescription();
          createDriver.setJobDescription(jobDescription);
          createDriver.setScaffoldSamples(sampleSelectionPageImpl.getScaffoldSamples());
          createDriver.setParameterSet(StringParameterSet.fromMap(parametersPageImpl.formPanelSupplier.getParametersMap()));

          final CreateTropixFileDescription createDriverFile = ActivityDescriptions.createFileForScaffoldDriver(createDriver);
          createDriverFile.setName(name + " parameters.xml");
          createDriverFile.setExtension(".xml");
          createDriverFile.setDestinationId(destinationId);

          final SubmitIdPickerAnalysisDescription submitDescription = ActivityDescriptions.createSubmitIdPicker(createDriver, createDriverFile,
              gridService.getServiceAddress());
          final PollJobDescription pollJobDescription = ActivityDescriptions.buildPollDescription(submitDescription);
          final CreateTropixFileDescription createResultXmlFileDescription = ActivityDescriptions.buildCreateResultFile(pollJobDescription, 0);
          createResultXmlFileDescription.setName(name + ".xml");
          createResultXmlFileDescription.setExtension(".xml");
          createResultXmlFileDescription.setDescription(metadataProvider.getDescription());
          createResultXmlFileDescription.setDestinationId(destinationId);

          final CreateTropixFileDescription createResultZippedHtmlFileDescription = ActivityDescriptions.buildCreateResultFile(pollJobDescription, 1);
          createResultZippedHtmlFileDescription.setName(name + ".zip");
          createResultZippedHtmlFileDescription.setExtension(".zip");
          createResultZippedHtmlFileDescription.setDestinationId(destinationId);

          final CommitObjectDescription commitXmlDescription = ActivityDescriptions.createCommitDescription(createResultXmlFileDescription);
          final CommitObjectDescription commitZippedHtmlDescription = ActivityDescriptions
              .createCommitDescription(createResultZippedHtmlFileDescription);
          final CommitObjectDescription commitParametersDescription = ActivityDescriptions.createCommitDescription(createDriverFile);

          final Set<ActivityDescription> activityDescriptions = Sets.newHashSet(createDriver, createDriverFile, submitDescription,
              pollJobDescription, createResultXmlFileDescription, createResultZippedHtmlFileDescription, commitXmlDescription,
              commitZippedHtmlDescription, commitParametersDescription);
          JobSubmitService.Util.getInstance().submit(activityDescriptions, new AsyncCallbackImpl<Void>());
          destroy();
        }

      }));
    }

    class ParametersPageImpl extends WizardPageImpl<VLayout> {
      private FormPanelSupplier formPanelSupplier;
      {
        setTitle("Parameters");
        setDescription("Specify parameters for IdPicker anlaysis.");
        setCanvas(new VLayout());

        final AsyncCallback<FormPanelSupplier> callback = new AsyncCallbackImpl<FormPanelSupplier>() {
          public void onSuccess(final FormPanelSupplier formPanelSupplierInput) {
            formPanelSupplier = formPanelSupplierInput;
            formPanelSupplier.addValidationListener(new ValidationListener() {
              public void onValidation(final boolean isValid) {
                setValid(isValid);
              }
            });
            final Widget widget = formPanelSupplier.get();
            widget.setHeight(getCanvas().getHeightAsString());
            getCanvas().addMember(widget);
          }
        };
        FormPanelFactory.createParametersPanel("idPicker", callback);
      }
    }

  }

  @Override
  public WizardCommand get(final Collection<TreeItem> locations) {
    return new IdPickerWizardCommand(locations);
  }

  private Supplier<ServiceSelectionComponent<QueueGridService>> serviceSelectionComponentSupplier;

  @Inject
  public void setServiceSelectionComponentSupplier(final Supplier<ServiceSelectionComponent<QueueGridService>> serviceGridSupplier) {
    this.serviceSelectionComponentSupplier = serviceGridSupplier;
  }

}
