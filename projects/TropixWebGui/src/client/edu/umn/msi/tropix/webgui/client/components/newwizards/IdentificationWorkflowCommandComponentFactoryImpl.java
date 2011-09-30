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
import java.util.Map;
import java.util.Set;

import com.google.common.base.Supplier;
import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;

import edu.umn.msi.tropix.client.services.IdentificationGridService;
import edu.umn.msi.tropix.client.services.QueueGridService;
import edu.umn.msi.tropix.client.services.ScaffoldGridService;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.MetadataInputComponentFactory.MetadataOptions.DestinationType;
import edu.umn.msi.tropix.webgui.client.components.ServiceSelectionComponent;
import edu.umn.msi.tropix.webgui.client.components.UploadComponent;
import edu.umn.msi.tropix.webgui.client.components.UploadComponentFactory.UploadComponentOptions;
import edu.umn.msi.tropix.webgui.client.components.newwizards.MetadataWizardPageFactory.MetadataWizardPageImpl;
import edu.umn.msi.tropix.webgui.client.components.newwizards.ScaffoldSampleTypeWizardPageImpl.ScaffoldSampleType;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItem;
import edu.umn.msi.tropix.webgui.client.constants.ComponentConstants;
import edu.umn.msi.tropix.webgui.client.constants.ConstantsInstances;
import edu.umn.msi.tropix.webgui.client.forms.ValidationListener;
import edu.umn.msi.tropix.webgui.client.identification.ParametersPanelFactory;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.widgets.Form;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils.ConditionalWidgetSupplier;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardCompletionHandler;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardFactoryImpl;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardOptions;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPage;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPageImpl;
import edu.umn.msi.tropix.webgui.services.jobs.JobSubmitService;

public class IdentificationWorkflowCommandComponentFactoryImpl extends WizardCommandComponentFactoryImpl {
  private static final ComponentConstants CONSTANTS = ConstantsInstances.COMPONENT_INSTANCE;
  private Supplier<ServiceSelectionComponent<QueueGridService>> serviceSelectionComponentSupplier;
  private ParametersPanelFactory parametersPanelFactory;
  private ComponentFactory<UploadComponentOptions, ? extends UploadComponent> uploadComponentFactory;

  @Inject
  public void setServiceSelectionComponentSupplier(final Supplier<ServiceSelectionComponent<QueueGridService>> serviceGridSupplier) {
    this.serviceSelectionComponentSupplier = serviceGridSupplier;
  }

  @Inject
  public void setParametersPanelFactory(final ParametersPanelFactory parametersPanelFactory) {
    this.parametersPanelFactory = parametersPanelFactory;
  }

  @Inject
  public void setUploadComponentFactory(final ComponentFactory<UploadComponentOptions, ? extends UploadComponent> uploadComponentFactory) {
    this.uploadComponentFactory = uploadComponentFactory;
  }

  class IdentificationWorkflowWizardCommand extends WizardCommand {
    private boolean useExistingRuns;
    private boolean createSubfolders;
    private boolean multipleRuns = false;
    private boolean useScaffold = true;
    // private Boolean analyzeAsMudpit = false;
    private Map<String, String> uploadedRuns;
    private Collection<ProteomicsRun> selectedRuns;
    private ScaffoldSampleType scaffoldType = ScaffoldSampleType.MANY_ANALYSIS;

    private void finish() {
      final IdentificationWorkflowBuilder builder = new IdentificationWorkflowBuilder(CONSTANTS);
      builder.setCreateSubfolders(createSubfolders);
      builder.setUseExistingRuns(useExistingRuns);
      builder.setUseScaffold(useScaffold);
      builder.setScaffoldType(scaffoldType);
      builder.setCommonMetadataProvider(metadataWizardPage.getMetadataCanvasSupplier());
      builder.setParameterMap(parametersPage.getCurrentPanel().getParametersMap());
      builder.setIdService((IdentificationGridService) identificationSelectionPage.getGridService());
      builder.setDatabaseId(databasePage.getDatabase().getId());
      builder.setUploadedRuns(uploadedRuns);
      builder.setSelectedRuns(selectedRuns);
      builder.setRawExtractGridService(rawExtractSelectionPage.getGridService());
      if(useScaffold) {
        builder.setAnalyzeScaffoldAsMudpit(scaffoldOptionsPage.getAnalyzeAsMudpit());
        builder.setScaffoldGridService((ScaffoldGridService) scaffoldSelectionPage.scaffoldSelectionComponent.getSelection());
        builder.setScaffoldParameterMap(scaffoldParametersPage.getFormPanelSupplier().getParametersMap());
      }
      final Set<ActivityDescription> descriptions = builder.build();

      JobSubmitService.Util.getInstance().submit(descriptions, new AsyncCallbackImpl<Void>());
      destroy();
    }

    private void setMultipleRuns(final boolean multipleRuns) {
      this.multipleRuns = multipleRuns;
      scaffoldOptionsPage.setShowSampleType(multipleRuns);
    }

    private void setUseExistingRuns(final boolean useExistingRuns) {
      this.useExistingRuns = useExistingRuns;
      runsPage.setEnabled(useExistingRuns);
      uploadPage.setEnabled(!useExistingRuns);
      rawExtractSelectionPage.setEnabled(!useExistingRuns);
    }

    private void setUseScaffold(final boolean useScaffold) {
      this.useScaffold = useScaffold;
      scaffoldOptionsPage.setEnabled(useScaffold);
      scaffoldParametersPage.setEnabled(useScaffold);
    }

    private void setCreateSubfolders(final Boolean createSubfolders) {
      this.createSubfolders = createSubfolders;
    }

    private final UploadComponentOptions uploadOpts = new UploadComponentOptions(true, new AsyncCallbackImpl<LinkedHashMap<String, String>>() {
      @Override
      public void onSuccess(final LinkedHashMap<String, String> params) {
        uploadedRuns = params;
        finish();
      }
    });
    private UploadComponent uploadComponent = uploadComponentFactory.get(uploadOpts);
    private UploadWizardPageImpl uploadPage = new UploadWizardPageImpl(uploadComponent, CONSTANTS.runWizardThermoUploadTitle(),
        CONSTANTS.runWizardThermoUploadDescription()) {
      @Override
      public boolean isValid() {
        setMultipleRuns(uploadComponent.getNumSelectedFiles() > 1 || uploadComponent.isZip());
        return super.isValid();
      }
    };

    private class RunsWizardPageImpl extends WizardPageImpl<Canvas> {
      private RunTreeComponentImpl runTreeComponent = new RunTreeComponentImpl(getTreeComponentFactory(), getLocationFactory(), getLocations(),
          false, new ValidationListener() {
            public void onValidation(final boolean isValid) {
              if(runTreeComponent != null) {
                final boolean mayHaveMultipleRuns = runTreeComponent.mayHaveMultipleRuns();
                setMultipleRuns(mayHaveMultipleRuns);
              }
              setValid(isValid);
            }
          });

      RunsWizardPageImpl() {
        setTitle(CONSTANTS.idWorkflowRunTitle());
        setDescription(CONSTANTS.idWorkflowRunDescription());
        setCanvas(runTreeComponent.get());
        setEnabled(false);
      }

    }

    private final class DatabaseWizardPageImpl extends WizardPageImpl<TreeGrid> {
      private Database database = null;

      private DatabaseWizardPageImpl() {
        setTitle(CONSTANTS.idWorkflowDatabaseTitle());
        setDescription(CONSTANTS.idWorkflowDatabaseDescription());
        final TreeComponent databaseTree = Utils.getDatabaseTreeComponent(getLocationFactory(), getTreeComponentFactory(), getLocations());
        databaseTree.addSelectionListener(new Listener<TreeItem>() {
          public void onEvent(final TreeItem treeItem) {
            database = null;
            if(treeItem instanceof TropixObjectTreeItem) {
              final TropixObject object = ((TropixObjectTreeItem) treeItem).getObject();
              database = (Database) ((treeItem == null || !(object instanceof Database)) ? null : object);
            }
            setValid(database != null);
          }
        });
        final TreeGrid databaseTreeGrid = databaseTree.get();
        databaseTreeGrid.setWidth("100%");
        databaseTreeGrid.setHeight("40%");
        this.setCanvas(databaseTreeGrid);
      }

      private Database getDatabase() {
        return database;
      }
    }

    private DatabaseWizardPageImpl databasePage = new DatabaseWizardPageImpl();
    // private ConditionalSampleWizardPageImpl sampleWizardPage = new ConditionalSampleWizardPageImpl(getLocationFactory(),
    // getTreeComponentFactory());

    private WizardPageImpl<Canvas> workflowOptionsPage = new WizardPageImpl<Canvas>() {
      public void setTitle(final String title) {
        super.setTitle(title);
      }

      {
        this.setTitle(CONSTANTS.idWorkflowOptionsTitle());
        this.setDescription(CONSTANTS.idWorkflowOptionsDescription());
        this.setValid(true);

        final Layout layout = new VLayout();
        layout.setWidth100();
        layout.setHeight100();

        final Form form = new Form();

        final CheckboxItem useExistingRunsItem = new CheckboxItem("useExistingRuns", CONSTANTS.idWorkflowOptionsPeakLists());
        useExistingRunsItem.addChangedHandler(new ChangedHandler() {
          public void onChanged(final ChangedEvent event) {
            setUseExistingRuns((Boolean) useExistingRunsItem.getValue()); // AsBoolean());
          }
        });

        final CheckboxItem createSubfoldersItem = new CheckboxItem("createSubfolders", CONSTANTS.idWorkflowOptionsFolders());
        createSubfoldersItem.addChangedHandler(new ChangedHandler() {
          public void onChanged(final ChangedEvent event) {
            setCreateSubfolders((Boolean) createSubfoldersItem.getValue()); // AsBoolean());
          }
        });

        form.setItems(useExistingRunsItem, createSubfoldersItem);
        layout.addMember(form);
        this.setCanvas(layout);
      }
    };
    private ScaffoldSampleTypeWizardPageImpl scaffoldOptionsPage = new ScaffoldSampleTypeWizardPageImpl(false);

    private final MetadataWizardPageImpl metadataWizardPage = getMetadataWizardPageFactory().get(getLocations(), CONSTANTS.idWorkflowResultType(),
        DestinationType.HOME);
    private final ParametersWizardPage parametersPage;
    private final ConditionalScaffoldSelectionWizardPageImpl scaffoldSelectionPage;

    private final ServiceWizardPageImpl<QueueGridService> identificationSelectionPage;
    private final ServiceWizardPageImpl<QueueGridService> rawExtractSelectionPage;
    private final ScaffoldParametersPageImpl scaffoldParametersPage = new ScaffoldParametersPageImpl();
    private final RunsWizardPageImpl runsPage;

    class ConditionalScaffoldSelectionWizardPageImpl extends WizardPageImpl<Canvas> {
      private final ConditionalWidgetSupplier conditionalServiceComponent;
      private final ServiceSelectionComponent<QueueGridService> scaffoldSelectionComponent;

      ConditionalScaffoldSelectionWizardPageImpl(final ServiceSelectionComponent<QueueGridService> scaffoldSelectionComponent) {
        setTitle(CONSTANTS.scaffoldWizardServiceTitle());
        setDescription(CONSTANTS.scaffoldWizardServiceDescription());
        this.scaffoldSelectionComponent = scaffoldSelectionComponent;
        scaffoldSelectionComponent.addSelectionListener(new Listener<QueueGridService>() {
          public void onEvent(final QueueGridService event) {
          }
        });
        scaffoldSelectionComponent.fetchServices();
        conditionalServiceComponent = SmartUtils.getConditionalSelectionWidget(CONSTANTS.idWorkflowUseScaffold(), scaffoldSelectionComponent,
            new Listener<Boolean>() {
              public void onEvent(final Boolean valid) {
                setValid(valid);
                setUseScaffold(conditionalServiceComponent.useSelection());
              }
            }, true);
        setCanvas(conditionalServiceComponent.get());
        setValid(false);
      }
    }

    IdentificationWorkflowWizardCommand(final Collection<TreeItem> locations) {
      super(locations);

      final ServiceSelectionComponent<QueueGridService> scaffoldSelectionComponent = serviceSelectionComponentSupplier.get();
      scaffoldSelectionComponent.setServicesType("scaffold");

      final ServiceSelectionComponent<QueueGridService> identificationSelectionComponent = serviceSelectionComponentSupplier.get();
      identificationSelectionComponent.setServicesType("proteinIdentification");

      final ServiceSelectionComponent<QueueGridService> rawExtractSelectionComponent = serviceSelectionComponentSupplier.get();
      rawExtractSelectionComponent.setServicesType("rawExtract");

      scaffoldSelectionPage = new ConditionalScaffoldSelectionWizardPageImpl(scaffoldSelectionComponent);
      identificationSelectionPage = new ServiceWizardPageImpl<QueueGridService>(identificationSelectionComponent, CONSTANTS.idWizardServiceTitle(),
          CONSTANTS.idWizardServiceDescription());
      scaffoldOptionsPage.registerScaffoldSampleTypeChangeListener(new Listener<ScaffoldSampleType>() {
        public void onEvent(ScaffoldSampleType event) {
          scaffoldType = event;
        }
      });
      identificationSelectionComponent.addSelectionListener(new Listener<QueueGridService>() {
        public void onEvent(final QueueGridService gridService) {
          IdentificationGridService idService = (IdentificationGridService) gridService;
          parametersPage.setParametersType(idService.getParameterType());
        }
      });
      rawExtractSelectionPage = new ServiceWizardPageImpl<QueueGridService>(rawExtractSelectionComponent,
          CONSTANTS.runWizardRawExtractServiceTitle(),
          CONSTANTS.runWizardRawExtractServiceDescription());
      parametersPage = new ParametersWizardPage(locations, parametersPanelFactory);

      runsPage = new RunsWizardPageImpl(); // Must be constructed after rawExtractSelectionPage

      final ArrayList<WizardPage> pages = new ArrayList<WizardPage>(10);
      pages.add(workflowOptionsPage);
      pages.add(uploadPage);
      pages.add(runsPage);
      pages.add(metadataWizardPage);
      pages.add(rawExtractSelectionPage);
      pages.add(identificationSelectionPage);
      pages.add(databasePage);
      pages.add(parametersPage);
      pages.add(scaffoldSelectionPage);
      pages.add(scaffoldOptionsPage);
      pages.add(scaffoldParametersPage);

      final WizardOptions options = new WizardOptions();
      options.setTitle(CONSTANTS.idWorkflowTitle());
      setWidget(WizardFactoryImpl.getInstance().getWizard(pages, options, new WizardCompletionHandler() {
        public void onCompletion(final Window wizard) {
          hide();
          if(useExistingRuns) {
            runsPage.runTreeComponent.getRuns(new AsyncCallbackImpl<Collection<ProteomicsRun>>() {
              @Override
              protected void handleSuccess() {
                selectedRuns = getResult();
                finish();
              }
            });
          } else {
            uploadComponent.startUpload();
          }
        }
      }));
    }

  }

  public Command get(final Collection<TreeItem> treeItems) {
    return new IdentificationWorkflowWizardCommand(treeItems);
  }

}