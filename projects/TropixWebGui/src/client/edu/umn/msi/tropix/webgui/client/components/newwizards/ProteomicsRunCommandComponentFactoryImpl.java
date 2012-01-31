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

import com.google.common.base.Supplier;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.Window;

import edu.umn.msi.tropix.client.services.QueueGridService;
import edu.umn.msi.tropix.models.utils.StockFileExtensionEnum;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.DynamicUploadComponent;
import edu.umn.msi.tropix.webgui.client.components.ServiceSelectionComponent;
import edu.umn.msi.tropix.webgui.client.components.UploadComponentFactory.FileSource;
import edu.umn.msi.tropix.webgui.client.components.UploadComponentFactory.UploadComponentOptions;
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
    private MetadataWizardPageGroup metadataWizardPages;

    private final ServiceWizardPageImpl<QueueGridService> thermoServicesSelectionPage;
    private final ServiceWizardPageImpl<QueueGridService> proteomicsConvertServicesSelectionPage;

    private final UploadComponentOptions uploadOpts = new UploadComponentOptions(true, new AsyncCallbackImpl<List<FileSource>>() {
      @Override
      public void handleSuccess() {
        try {
          final PeakListWorkflowBuilder builder = new PeakListWorkflowBuilder(CONSTANTS);
          builder.setFileSources(getResult());
          builder.setCommonMetadataProvider(metadataWizardPages.getEnabledWizardPage().getMetadataCanvasSupplier());
          builder.setSourcePage(sourcePage);
          builder.setThermoServicesSelectionPage(thermoServicesSelectionPage);
          builder.setProteomicsConvertServicesSelectionPage(proteomicsConvertServicesSelectionPage);
          JobSubmitService.Util.getInstance().submit(builder.build(), new AsyncCallbackImpl<Void>());
        } finally {
          destroy();
        }
      }
    });

    private final DynamicUploadComponent uploadComponent = uploadComponentFactory.get(uploadOpts);
    private UploadWizardPageImpl uploadWizardPage = new UploadWizardPageImpl(uploadComponent, CONSTANTS.runWizardSourceTitle(),
        CONSTANTS.runWizardSourceDescription());
    private final ProteomicsRunSourceTypeWizardPageImpl sourcePage = new ProteomicsRunSourceTypeWizardPageImpl(false);

    private void intializeMetadataPages() {
      metadataWizardPages = new MetadataWizardPageGroup(getMetadataWizardPageFactory(), getLocations(), CONSTANTS.runWizardType(),
          CONSTANTS.runWizardBatchType());
    }

    private final Listener<Object> sourceTypeListener = new Listener<Object>() {
      public void onEvent(final Object event) {
        updateSourceType();
      }
    };

    private void updateSourceType() {
      final ProteomicsRunSource type = sourcePage.getProteomicsRunSourceProperty().get();
      final boolean batch = sourcePage.getBatchProperty().get();
      metadataWizardPages.setBatch(batch);

      if(type == ProteomicsRunSource.THERMO) {
        uploadOpts.setTypes("*.raw;*.RAW");
        uploadOpts.setTypesDescription("Thermo Finnigan RAW");
        uploadOpts.setExtension(StockFileExtensionEnum.THERMO_RAW.getExtension());
      } else if(type == ProteomicsRunSource.MZXML) {
        uploadOpts.setTypes("*.mzXML;*.mzxml;*.MZXML;*.MzXML");
        uploadOpts.setTypesDescription("MzXML");
        uploadOpts.setExtension(null);
      } else if(type == ProteomicsRunSource.MGF) {
        uploadOpts.setTypes("*.mgf;*.MGF;*.msm;*.MSM");
        uploadOpts.setTypesDescription("Mascot Generic Format");
        uploadOpts.setExtension(null);
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
      pages.addAll(metadataWizardPages.asList());

      final ServiceSelectionComponent<QueueGridService> thermoSelectionComponent = serviceSelectionComponentSupplier.get();
      thermoSelectionComponent.setServicesType("rawExtract");
      thermoServicesSelectionPage = new ServiceWizardPageImpl<QueueGridService>(thermoSelectionComponent);

      final ServiceSelectionComponent<QueueGridService> proteomicsConvertSelectionComponent = serviceSelectionComponentSupplier.get();
      proteomicsConvertSelectionComponent.setServicesType("proteomicsConvert");
      proteomicsConvertServicesSelectionPage = new ServiceWizardPageImpl<QueueGridService>(proteomicsConvertSelectionComponent);

      pages.add(thermoServicesSelectionPage);
      pages.add(proteomicsConvertServicesSelectionPage);
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
