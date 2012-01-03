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
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Supplier;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;

import edu.umn.msi.tropix.client.services.QueueGridService;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDependency;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescriptions;
import edu.umn.msi.tropix.jobs.activities.descriptions.CommitObjectDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateBowtieAnalysisDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateTropixFileDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.IdList;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.PollJobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.StringParameterSet;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitBowtieAnalysisDescription;
import edu.umn.msi.tropix.models.BowtieIndex;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.locations.TropixObjectLocation;
import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.components.MetadataInputComponent;
import edu.umn.msi.tropix.webgui.client.components.ServiceSelectionComponent;
import edu.umn.msi.tropix.webgui.client.components.newwizards.MetadataWizardPageFactory.MetadataWizardPageImpl;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItemPredicates;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItemExpanders;
import edu.umn.msi.tropix.webgui.client.forms.FormPanelFactory;
import edu.umn.msi.tropix.webgui.client.forms.FormPanelSupplier;
import edu.umn.msi.tropix.webgui.client.forms.ValidationListener;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.Sets;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardCompletionHandler;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardFactoryImpl;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardOptions;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPage;
import edu.umn.msi.tropix.webgui.client.widgets.wizards.WizardPageImpl;
import edu.umn.msi.tropix.webgui.services.jobs.JobSubmitService;

public class BowtieAnalysisCommandComponentFactoryImpl extends WizardCommandComponentFactoryImpl {

  static class ParametersPageImpl extends WizardPageImpl<VLayout> {
    private FormPanelSupplier formPanelSupplier;

    ParametersPageImpl() {
      this.setTitle("Parameters");
      this.setDescription("Specify parameters for Bowtie anlaysis.");
      this.setCanvas(new VLayout());

      final AsyncCallback<FormPanelSupplier> callback = new AsyncCallbackImpl<FormPanelSupplier>() {
        public void onSuccess(final FormPanelSupplier formPanelSupplierInput) {
          formPanelSupplier = formPanelSupplierInput;
          formPanelSupplier.addValidationListener(new ValidationListener() {
            public void onValidation(final boolean isValid) {
              setValid(isValid);
            }
          });
          getCanvas().addMember(formPanelSupplier.get());
        }
      };
      FormPanelFactory.createParametersPanel("bowtie", callback);
    }

  }

  private class DatabaseInputPageImpl extends WizardPageImpl<VLayout> {
    private final VLayout inputsLayout = new VLayout();
    private final List<String> databaseIds = new LinkedList<String>();
    private final DynamicForm optionsForm = getOptionsForm();

    DatabaseInputPageImpl() {
      this.setTitle("Sequences");
      this.setDescription("Specify databases contain input sequences for Bowtie analysis.");
      this.setCanvas(new VLayout());

      getCanvas().addMember(optionsForm);
      inputsLayout.setWidth100();
      inputsLayout.setHeight100();
      getCanvas().addMember(inputsLayout);
      getCanvas().setMembersMargin(10);

      optionsForm.addItemChangedHandler(new ItemChangedHandler() {
        public void onItemChanged(final ItemChangedEvent event) {
          resetInputs();
        }
      });

      resetInputs();
    }

    private void resetInputs() {
      databaseIds.clear();
      SmartUtils.removeAndDestroyAllMembers(inputsLayout);
      final String inputType = optionsForm.getValueAsString("type");
      final String format = optionsForm.getValueAsString("format");

      if(!inputType.equals("PAIRED")) {
        final TreeComponent treeComponent = getTreeComponent(format);
        inputsLayout.addMember(treeComponent.get());
      }
    }

    private TreeComponent getTreeComponent(final String format) {
      final TreeOptions treeOptions = new TreeOptions();
      final TropixObjectType[] inputTypes = new TropixObjectType[] {TropixObjectTypeEnum.FOLDER, TropixObjectTypeEnum.VIRTUAL_FOLDER, TropixObjectTypeEnum.DATABASE};

      treeOptions.setInitialItems(getLocationFactory().getTropixObjectSourceRootItems(TropixObjectTreeItemExpanders.get(inputTypes)));
      treeOptions.setShowPredicate(TreeItemPredicates.showDatabaseTypePredicate(format));
      treeOptions.setSelectionPredicate(TreeItemPredicates.selectDatabaseTypePredicate(format));

      final TreeComponent tree = getTreeComponentFactory().get(treeOptions);
      tree.addMultiSelectionListener(new Listener<Collection<TreeItem>>() {
        public void onEvent(final Collection<TreeItem> treeItems) {
          databaseIds.clear();
          if(treeItems != null) {
            for(final TreeItem treeItem : treeItems) {
              databaseIds.add(treeItem.getId());
            }
          }
          checkValid();
        }
      });
      return tree;
    }

    private void checkValid() {
      setValid(!databaseIds.isEmpty());
    }

    private DynamicForm getOptionsForm() {
      final DynamicForm form = new DynamicForm();
      form.setWidth100();
      form.setHeight(30);
      form.setNumCols(4); // title input title input

      final SelectItem formatItem = new SelectItem();
      formatItem.setWidth(100);
      formatItem.setName("format");
      formatItem.setTitle("File Format");
      formatItem.setValueMap("FASTA", "FASTQ", "RAW");

      final SelectItem typeItem = new SelectItem();
      typeItem.setWidth(100);
      typeItem.setName("type");
      typeItem.setTitle("Analysis Type");

      final LinkedHashMap<String, String> typeValueMap = new LinkedHashMap<String, String>();
      typeValueMap.put("PAIRED", "Paired");
      typeValueMap.put("UNPAIRED", "Unpaired");
      typeValueMap.put("MIXED", "Mixed");
      typeItem.setValueMap(typeValueMap);

      form.setItems(formatItem, typeItem);
      form.setValue("format", "FASTA");
      form.setValue("type", "UNPAIRED");

      return form;
    }

  }

  class IndexPageImpl extends WizardPageImpl<VLayout> {
    private String indexId = null;

    IndexPageImpl() {
      this.setTitle("Index");
      this.setDescription("Specify a Bowtie Index for analysis");
      this.setCanvas(new VLayout());

      final TreeOptions databaseTreeOptions = new TreeOptions();
      final TropixObjectType[] dbTypes = new TropixObjectType[] {TropixObjectTypeEnum.FOLDER, TropixObjectTypeEnum.VIRTUAL_FOLDER, TropixObjectTypeEnum.BOWTIE_INDEX};
      databaseTreeOptions.setInitialItems(getLocationFactory().getTropixObjectSourceRootItems(TropixObjectTreeItemExpanders.get(dbTypes)));
      databaseTreeOptions.setShowPredicate(TreeItemPredicates.getTropixObjectTreeItemTypePredicate(dbTypes, true));
      databaseTreeOptions.setSelectionPredicate(TreeItemPredicates.getTropixObjectNotFolderPredicate());

      final TreeComponent databaseTree = getTreeComponentFactory().get(databaseTreeOptions);
      databaseTree.addSelectionListener(new Listener<TreeItem>() {
        public void onEvent(final TreeItem treeItem) {
          indexId = null;
          if(treeItem instanceof TropixObjectTreeItem) {
            final TropixObject object = ((TropixObjectLocation) treeItem).getObject();
            indexId = (treeItem == null || !(object instanceof BowtieIndex)) ? null : object.getId();
          }
          setValid(indexId != null);
        }
      });
      final TreeGrid databaseTreeGrid = databaseTree.get();
      getCanvas().addMember(databaseTreeGrid);

    }
  }

  private class BowtieAnalysisWizardCommand extends WizardCommand {
    private final MetadataWizardPageImpl metadataWizardPage;
    private final ServiceWizardPageImpl<QueueGridService> servicesSelectionPageImpl;
    private final ParametersPageImpl parametersPage;
    private final DatabaseInputPageImpl databaseInputPage;
    private final IndexPageImpl indexPage;

    BowtieAnalysisWizardCommand(final Collection<TreeItem> locations) {
      super(locations);
      final ArrayList<WizardPage> pages = new ArrayList<WizardPage>(4);
      metadataWizardPage = getMetadataWizardPageFactory().get(getLocations(), "Bowtie alignment analysis");
      final ServiceSelectionComponent<QueueGridService> selectionComponent = serviceSelectionComponentSupplier.get();
      selectionComponent.setServicesType("bowtie");
      servicesSelectionPageImpl = new ServiceWizardPageImpl<QueueGridService>(selectionComponent);
      parametersPage = new ParametersPageImpl();
      databaseInputPage = new DatabaseInputPageImpl();
      indexPage = new IndexPageImpl();
      pages.add(metadataWizardPage);
      pages.add(servicesSelectionPageImpl);
      pages.add(indexPage);
      pages.add(databaseInputPage);
      pages.add(parametersPage);

      final WizardOptions options = new WizardOptions();
      options.setTitle("New Bowtie Alignment Analysis");

      this.setWidget(WizardFactoryImpl.getInstance().getWizard(pages, options, new WizardCompletionHandler() {

        public void onCompletion(final Window wizard) {
          final MetadataInputComponent metadata = metadataWizardPage.getMetadataCanvasSupplier();
          final JobDescription jobDescription = new JobDescription("Create Bowtie analysis " + metadata.getName());

          final SubmitBowtieAnalysisDescription submitDescription = new SubmitBowtieAnalysisDescription();
          final IdList databaseIds = IdList.forIterable(databaseInputPage.databaseIds);
          submitDescription.setDatabaseIds(databaseIds);
          submitDescription.setIndexId(indexPage.indexId);
          submitDescription.setInputFormat(databaseInputPage.optionsForm.getValueAsString("format"));
          submitDescription.setInputType(databaseInputPage.optionsForm.getValueAsString("type"));
          submitDescription.setJobDescription(jobDescription);
          submitDescription.setParameterSet(StringParameterSet.fromMap(parametersPage.formPanelSupplier.getParametersMap()));
          submitDescription.setServiceUrl(servicesSelectionPageImpl.getGridService().getServiceAddress());

          final PollJobDescription pollJobDescription = ActivityDescriptions.buildPollDescription(submitDescription);
          //final SingleResultFetchDescription resultDescription = ActivityDescriptions.buildResultFetchDescription(pollJobDescription);
          final CreateTropixFileDescription createOutputDescription = ActivityDescriptions.buildCreateResultFile(pollJobDescription);

          final CreateBowtieAnalysisDescription createBowtieAnalysisDescription = new CreateBowtieAnalysisDescription();
          ActivityDescriptions.initCommonMetadata(createBowtieAnalysisDescription, metadata);
          createBowtieAnalysisDescription.setJobDescription(jobDescription);
          createBowtieAnalysisDescription.setBowtieIndexId(indexPage.indexId);
          createBowtieAnalysisDescription.setDatabaseIds(databaseIds);
          createBowtieAnalysisDescription.addDependency(ActivityDependency.Builder.on(createOutputDescription).produces("objectId").consumes("outputFileId").build());
          final CommitObjectDescription commitDescription = ActivityDescriptions.createCommitDescription(createBowtieAnalysisDescription);

          JobSubmitService.Util.getInstance().submit(Sets.newHashSet(submitDescription, pollJobDescription, createOutputDescription, createBowtieAnalysisDescription, commitDescription), new AsyncCallbackImpl<Void>());
          destroy();
        }
      }));

    }

  }

  public WizardCommand get(final Collection<TreeItem> locations) {
    return new BowtieAnalysisWizardCommand(locations);
  }

  private Supplier<ServiceSelectionComponent<QueueGridService>> serviceSelectionComponentSupplier;

  @Inject
  public void setServiceSelectionComponentSupplier(final Supplier<ServiceSelectionComponent<QueueGridService>> serviceGridSupplier) {
    this.serviceSelectionComponentSupplier = serviceGridSupplier;
  }

}
