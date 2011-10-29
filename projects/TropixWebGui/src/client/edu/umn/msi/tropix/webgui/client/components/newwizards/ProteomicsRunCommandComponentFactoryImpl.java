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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Supplier;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.Window;

import edu.umn.msi.tropix.client.services.QueueGridService;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDependency;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescriptions;
import edu.umn.msi.tropix.jobs.activities.descriptions.CommitObjectDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CommonMetadataProvider;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateFolderDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateProteomicsRunDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateTropixFileDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.PollJobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitProteomicsConvertDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitThermofinniganRunJobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.UploadFileDescription;
import edu.umn.msi.tropix.models.utils.StockFileExtensionEnum;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.DynamicUploadComponent;
import edu.umn.msi.tropix.webgui.client.components.ServiceSelectionComponent;
import edu.umn.msi.tropix.webgui.client.components.UploadComponentFactory.UploadComponentOptions;
import edu.umn.msi.tropix.webgui.client.components.newwizards.MetadataWizardPageFactory.MetadataWizardPageImpl;
import edu.umn.msi.tropix.webgui.client.components.newwizards.ProteomicsRunSourceTypeWizardPageImpl.ProteomicsRunSource;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.constants.ComponentConstants;
import edu.umn.msi.tropix.webgui.client.constants.ConstantsInstances;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.Lists;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardCompletionHandler;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardFactoryImpl;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardOptions;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPage;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPageGroup;
import edu.umn.msi.tropix.webgui.services.jobs.JobSubmitService;

public class ProteomicsRunCommandComponentFactoryImpl extends WizardCommandComponentFactoryImpl {
  private static final ComponentConstants CONSTANTS = ConstantsInstances.COMPONENT_INSTANCE;

  ProteomicsRunCommandComponentFactoryImpl() {
  }

  private ComponentFactory<UploadComponentOptions, DynamicUploadComponent> uploadComponentFactory;

  @Inject
  public void setUploadComponentFactory(final ComponentFactory<UploadComponentOptions, DynamicUploadComponent> uploadComponentFactory) {
    this.uploadComponentFactory = uploadComponentFactory;
  }

  private Supplier<ServiceSelectionComponent<QueueGridService>> serviceSelectionComponentSupplier;

  @Inject
  public void setServiceSelectionComponentSupplier(final Supplier<ServiceSelectionComponent<QueueGridService>> serviceGridSupplier) {
    this.serviceSelectionComponentSupplier = serviceGridSupplier;
  }

  private class ProteomicsRunWizardCommand extends WizardCommand {
    private MetadataWizardPageImpl singleMetadataWizardPage;
    private MetadataWizardPageImpl batchMetadataWizardPage;
    private WizardPageGroup<MetadataWizardPageImpl> metadataWizardPages;

    private final ServiceWizardPageImpl<QueueGridService> thermoServicesSelectionPage;
    private final ServiceWizardPageImpl<QueueGridService> proteomicsConvertServicesSelectionPage;

    private final UploadComponentOptions uploadOpts = new UploadComponentOptions(new AsyncCallbackImpl<LinkedHashMap<String, String>>() {
      @Override
      public void handleSuccess() {
        try {
          final ProteomicsRunSource type = sourcePage.getProteomicsRunSourceProperty().get();
          final boolean batch = sourcePage.getBatchProperty().get();

          final HashSet<ActivityDescription> descriptions = new HashSet<ActivityDescription>();
          final Set<Map.Entry<String, String>> fileEntries = getResult().entrySet();
          final CommonMetadataProvider metadataProvider =
              metadataWizardPages.getEnabledWizardPage().getMetadataCanvasSupplier();

          final CreateFolderDescription createFolderDescription = new CreateFolderDescription();
          if(batch) {
            ActivityDescriptions.initCommonMetadata(createFolderDescription, metadataProvider);
            createFolderDescription.setCommitted(true);
            descriptions.add(createFolderDescription);
          }

          for(Map.Entry<String, String> entry : fileEntries) {
            String calculatedName = metadataProvider.getName();
            if(batch && type == ProteomicsRunSource.THERMO) {
              calculatedName = Utils.stripRawExtension(entry.getKey());
            } else if(batch && type == ProteomicsRunSource.MZXML) {
              calculatedName = Utils.stripMzxmlExtension(entry.getKey());
            }
            final JobDescription jobDescription = new JobDescription();
            jobDescription.setName(CONSTANTS.runWizardJobDescriptionName() + calculatedName);

            final UploadFileDescription uploadDescription = ActivityDescriptions.createUploadFileDescription(jobDescription, entry.getValue());

            final CreateProteomicsRunDescription createProteomicsRunDescription = new CreateProteomicsRunDescription();
            createProteomicsRunDescription.setJobDescription(jobDescription);
            if(batch) {
              createProteomicsRunDescription.addDependency(ActivityDependency.Builder.on(createFolderDescription).produces("objectId")
                  .consumes("destinationId").build());
              createProteomicsRunDescription.setName(calculatedName);
            } else {
              ActivityDescriptions.initCommonMetadata(createProteomicsRunDescription, metadataProvider);
            }

            if(type == ProteomicsRunSource.THERMO) {
              final String serviceAddress = thermoServicesSelectionPage.getGridService().getServiceAddress();
              final CreateTropixFileDescription createRawFileDescription = ActivityDescriptions.createFileFromUpload(uploadDescription, false);
              createRawFileDescription.setExtension(StockFileExtensionEnum.THERMO_RAW.getExtension());

              final SubmitThermofinniganRunJobDescription submitDescription = ActivityDescriptions.createSubmitThermo(createRawFileDescription,
                  serviceAddress, calculatedName);
              final PollJobDescription pollJobDescription = ActivityDescriptions.buildPollDescription(submitDescription);
              final CreateTropixFileDescription createMzxmlDescription = ActivityDescriptions.buildCreateResultFile(pollJobDescription);

              createProteomicsRunDescription.addDependency(ActivityDependency.Builder.on(createMzxmlDescription).produces("objectId")
                  .consumes("mzxmlFileId").build());
              createProteomicsRunDescription.addDependency(ActivityDependency.Builder.on(createRawFileDescription).produces("objectId")
                  .consumes("sourceId").build());

              descriptions.add(createRawFileDescription);
              descriptions.add(submitDescription);
              descriptions.add(pollJobDescription);
              descriptions.add(createMzxmlDescription);
            } else if(type == ProteomicsRunSource.MGF) {
              final CreateTropixFileDescription createSourceFileDescription = ActivityDescriptions.createFileFromUpload(uploadDescription, false);
              createSourceFileDescription.setExtension(StockFileExtensionEnum.MASCOT_GENERIC_FORMAT.getExtension());

              final SubmitProteomicsConvertDescription submitDescription = ActivityDescriptions.createSubmitProteomicsConvert(
                  createSourceFileDescription, proteomicsConvertServicesSelectionPage.getGridService().getServiceAddress(), calculatedName);
              submitDescription.setInputName(calculatedName);
              final PollJobDescription pollJobDescription = ActivityDescriptions.buildPollDescription(submitDescription);

              final CreateTropixFileDescription createMzxmlDescription = ActivityDescriptions.buildCreateResultFile(pollJobDescription);

              createProteomicsRunDescription.addDependency(ActivityDependency.Builder.on(createMzxmlDescription).produces("objectId")
                  .consumes("mzxmlFileId").build());
              createProteomicsRunDescription.addDependency(ActivityDependency.Builder.on(createSourceFileDescription).produces("objectId")
                  .consumes("sourceId").build());

              descriptions.add(createSourceFileDescription);
              descriptions.add(submitDescription);
              descriptions.add(pollJobDescription);
              descriptions.add(createMzxmlDescription);
            } else if(type == ProteomicsRunSource.MZXML) {
              final CreateTropixFileDescription createMzxmlFileDescription = ActivityDescriptions.createFileFromUpload(uploadDescription, false);
              createProteomicsRunDescription.addDependency(ActivityDependency.Builder.on(createMzxmlFileDescription).produces("objectId")
                  .consumes("mzxmlFileId").build());
              descriptions.add(createMzxmlFileDescription);
            }

            descriptions.add(uploadDescription);
            descriptions.add(createProteomicsRunDescription);
            final CommitObjectDescription commitDescription = ActivityDescriptions.createCommitDescription(createProteomicsRunDescription);
            descriptions.add(commitDescription);
          }
          JobSubmitService.Util.getInstance().submit(descriptions, new AsyncCallbackImpl<Void>());
        } finally {
          destroy();
        }
      }
    });

    private final DynamicUploadComponent uploadComponent = uploadComponentFactory.get(uploadOpts);
    // private ConditionalSampleWizardPageImpl sampleWizardPage = new ConditionalSampleWizardPageImpl(getLocationFactory(),
    // getTreeComponentFactory());
    private UploadWizardPageImpl uploadWizardPage = new UploadWizardPageImpl(uploadComponent, CONSTANTS.runWizardSourceTitle(),
        CONSTANTS.runWizardSourceDescription());
    private final ProteomicsRunSourceTypeWizardPageImpl sourcePage = new ProteomicsRunSourceTypeWizardPageImpl(false);

    private void intializeMetadataPages() {
      singleMetadataWizardPage = getMetadataWizardPageFactory().get(getLocations(), CONSTANTS.runWizardType());
      batchMetadataWizardPage = getMetadataWizardPageFactory().get(getLocations(), CONSTANTS.runWizardBatchType());
      metadataWizardPages = WizardPageGroup.getWizardPageGroupFor(singleMetadataWizardPage, batchMetadataWizardPage);
    }

    private final Listener<Object> sourceTypeListener = new Listener<Object>() {
      public void onEvent(final Object event) {
        updateSourceType();
      }
    };

    private void updateSourceType() {
      final ProteomicsRunSource type = sourcePage.getProteomicsRunSourceProperty().get();
      final boolean batch = sourcePage.getBatchProperty().get();

      if(batch) {
        metadataWizardPages.enableOnly(batchMetadataWizardPage);
      } else {
        metadataWizardPages.enableOnly(singleMetadataWizardPage);
      }

      if(type == ProteomicsRunSource.THERMO) {
        uploadOpts.setTypes("*.raw;*.RAW");
        uploadOpts.setTypesDescription("Thermo Finnigan RAW");
      } else if(type == ProteomicsRunSource.MZXML) {
        uploadOpts.setTypes("*.mzXML;*.mzxml;*.MZXML;*.MzXML");
        uploadOpts.setTypesDescription("MzXML");
      } else if(type == ProteomicsRunSource.MGF) {
        uploadOpts.setTypes("*.mgf;*.MGF;*.msm;*.MSM");
        uploadOpts.setTypesDescription("Mascot Generic Format");
      }
      uploadOpts.setAllowMultiple(batch);
      uploadComponent.update(uploadOpts);

      thermoServicesSelectionPage.setEnabled(type == ProteomicsRunSource.THERMO);
      proteomicsConvertServicesSelectionPage.setEnabled(type == ProteomicsRunSource.MGF);
    }

    private void setupSourceTypeListeners() {
      sourcePage.getBatchProperty().addListener(sourceTypeListener);
      sourcePage.getProteomicsRunSourceProperty().addListener(sourceTypeListener);
    }

    ProteomicsRunWizardCommand(final Collection<TreeItem> locations) {
      super(locations);
      final ArrayList<WizardPage> pages = Lists.newArrayListWithCapacity(8);
      intializeMetadataPages();
      pages.add(sourcePage);
      pages.add(singleMetadataWizardPage);
      pages.add(batchMetadataWizardPage);

      final ServiceSelectionComponent<QueueGridService> thermoSelectionComponent = serviceSelectionComponentSupplier.get();
      thermoSelectionComponent.setServicesType("rawExtract");
      thermoServicesSelectionPage = new ServiceWizardPageImpl<QueueGridService>(thermoSelectionComponent);

      final ServiceSelectionComponent<QueueGridService> proteomicsConvertSelectionComponent = serviceSelectionComponentSupplier.get();
      proteomicsConvertSelectionComponent.setServicesType("proteomicsConvert");
      proteomicsConvertServicesSelectionPage = new ServiceWizardPageImpl<QueueGridService>(proteomicsConvertSelectionComponent);

      pages.add(thermoServicesSelectionPage);
      pages.add(proteomicsConvertServicesSelectionPage);
      // pages.add(sampleWizardPage);
      pages.add(uploadWizardPage);
      final WizardOptions options = new WizardOptions();
      options.setTitle(CONSTANTS.runWizardTitle());
      setupSourceTypeListeners();
      updateSourceType();
      setWidget(WizardFactoryImpl.getInstance().getWizard(pages, options, new WizardCompletionHandler() {
        public void onCompletion(final Window wizard) {
          uploadComponent.startUpload();
        }
      }));
    }
  }

  @Override
  public WizardCommand get(final Collection<TreeItem> locations) {
    return new ProteomicsRunWizardCommand(locations);
  }
}
