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

package edu.umn.msi.tropix.webgui.client.components.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import com.google.inject.Inject;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;

import edu.umn.msi.tropix.client.request.RequestBean;
import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.catalog.beans.Attribute;
import edu.umn.msi.tropix.webgui.client.catalog.beans.FieldValue;
import edu.umn.msi.tropix.webgui.client.catalog.beans.ServiceBean;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItemExpanders;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.Form;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils.Cleanable;
import edu.umn.msi.tropix.webgui.services.tropix.RequestService;

public class RequestServiceWindowComponentFactoryImpl implements ComponentFactory<ServiceBean, WindowComponent<Window>> {
  private TreeComponentFactory treeComponentFactory;
  private LocationFactory locationFactory;

  @Inject
  public void setLocationFactory(final LocationFactory locationFactory) {
    this.locationFactory = locationFactory;
  }

  @Inject
  public void setTreeComponentFactory(final TreeComponentFactory treeComponentFactory) {
    this.treeComponentFactory = treeComponentFactory;
  }

  public WindowComponent<Window> get(final ServiceBean input) {
    return new RequestServiceWindowComponentImpl(input, treeComponentFactory, locationFactory);
  }

  private static class RequestServiceWindowComponentImpl extends WindowComponentImpl<Window> {
    private final DynamicForm form;
    private final TreeComponent treeComponent;

    private boolean validate() {
      return StringUtils.hasText(form.getValueAsString("requestName")) && StringUtils.hasText(form.getValueAsString("requestDescription")) && StringUtils.hasText(form.getValueAsString("name")) && StringUtils.hasText(form.getValueAsString("phone"))
          && StringUtils.hasText(form.getValueAsString("email")) && treeComponent.getSelection() != null;
    }

    RequestServiceWindowComponentImpl(final ServiceBean service, final TreeComponentFactory treeComponentFactory, final LocationFactory locationFactory) {
      final List<FormItem> items = new LinkedList<FormItem>();
      final List<String> attributeIds = new LinkedList<String>();

      final TextItem requestNameItem = new TextItem();
      requestNameItem.setWidth("*");
      requestNameItem.setName("requestName");
      requestNameItem.setTitle("Request Name");
      items.add(requestNameItem);

      final TextAreaItem requestDescriptionItem = new TextAreaItem();
      requestDescriptionItem.setWidth("*");
      requestDescriptionItem.setName("requestDescription");
      requestDescriptionItem.setTitle("Request Description");
      items.add(requestDescriptionItem);

      final TextItem nameItem = new TextItem();
      nameItem.setWidth("*");
      nameItem.setName("name");
      nameItem.setTitle("Your Name");
      items.add(nameItem);

      final TextItem phoneItem = new TextItem();
      phoneItem.setWidth("*");
      phoneItem.setName("phone");
      phoneItem.setTitle("Your Phone");
      items.add(phoneItem);

      final TextItem emailItem = new TextItem();
      emailItem.setWidth("*");
      emailItem.setName("email");
      emailItem.setTitle("Your E-Mail");
      items.add(emailItem);

      for(final Attribute attribute : service.getAttributes()) {
        if(attribute.getName().startsWith("TROPIX")) {
          continue;
        }
        final String attributeName = attribute.getName();
        final String attributeId = attribute.getId();
        attributeIds.add(attributeId);
        final List<FieldValue> values = attribute.getValues();

        FormItem inputItem;
        // If there is one value with id -1, its a free text field, otherwise an enum
        if(values.size() == 1 && values.get(0).getId().equals("-1")) {
          inputItem = new TextItem();
        } else {
          final SelectItem selectItem = new SelectItem();
          final LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
          for(final FieldValue value : values) {
            map.put(value.getId(), value.getValue());
          }
          selectItem.setValueMap(map);
          if(!values.isEmpty()) {
            selectItem.setValue(values.get(0).getId());
          }
          inputItem = selectItem;
        }
        inputItem.setWidth("*");
        inputItem.setName("#" + attributeId);
        inputItem.setTitle(attributeName);
        items.add(inputItem);
      }

      this.form = new Form();
      this.form.setItems(items.toArray(new FormItem[items.size()]));
      this.form.setNumCols(2);
      this.form.setWidth100();
      this.form.setColWidths("35%", "65%");

      final Button submitButton = SmartUtils.getButton("Submit", Resources.OK);
      /*
      submitButton.setTitle("Submit Request");
      submitButton.setIcon(Resources.OK);
      submitButton.setShowDisabledIcon(false);
      submitButton.setAutoFit(true);
      submitButton.setDisabled(true);
      */
      this.form.addItemChangedHandler(new ItemChangedHandler() {
        public void onItemChanged(final ItemChangedEvent event) {
          submitButton.setDisabled(!validate());
        }
      });

      final TreeOptions treeOptions = new TreeOptions();
      treeOptions.setInitialItems(Arrays.asList(locationFactory.getHomeRootItem(TropixObjectTreeItemExpanders.get(new TropixObjectType[] {TropixObjectTypeEnum.FOLDER}))));
      treeComponent = treeComponentFactory.get(treeOptions);

      this.treeComponent.addSelectionListener(new Listener<TreeItem>() {
        public void onEvent(final TreeItem event) {
          submitButton.setDisabled(!validate());
        }
      });

      final Label label = new Label("Select destination for request files:");
      label.setWidth100();
      label.setHeight(12);

      final TreeGrid treeGrid = treeComponent.get();
      treeGrid.setWidth("100%");
      treeGrid.setHeight("250");
      final VLayout layout = new VLayout();
      layout.addMember(form);
      layout.addMember(label);
      layout.addMember(treeGrid);

     
      
      submitButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          final Cleanable cleanable = SmartUtils.indicateLoading(get());
          final RequestBean requestBean = new RequestBean();
          requestBean.setRequestName(form.getValueAsString("requestName"));
          requestBean.setRequestDescription(form.getValueAsString("requestDescription"));
          requestBean.setName(form.getValueAsString("name"));
          requestBean.setPhone(form.getValueAsString("phone"));
          requestBean.setEmail(form.getValueAsString("email"));
          requestBean.setDestinationId(treeComponent.getSelection().getId());
          requestBean.setCatalogId(service.getCatalogId());
          requestBean.setServiceId(service.getId());
          final HashMap<String, String> inputs = new HashMap<String, String>(attributeIds.size());
          for(final String attributeId : attributeIds) {
            final FormItem formItem = form.getItem("#" + attributeId);
            final String name = formItem.getTitle();
            final String displayValue = formItem.getDisplayValue();
            inputs.put(name, displayValue);
          }
          requestBean.setInputs(inputs);
          
          RequestService.Util.getInstance().request(requestBean, new AsyncCallbackImpl<Void>(cleanable) {
            @Override
            public void onSuccess(final Void ignored) {
              cleanable.close();
              get().destroy();
              SC.say("Request sent.");
            }
          });
        }
      });

      final Layout windowLayout = new CanvasWithOpsLayout<VLayout>(layout, submitButton);
      windowLayout.setWidth100();
      this.setWidget(PopOutWindowBuilder.titled("Request Service").withContents(windowLayout).get());
    }
  }
}
