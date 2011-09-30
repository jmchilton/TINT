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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Supplier;
import com.google.gwt.user.client.Command;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.catalog.CatalogGlobals;
import edu.umn.msi.tropix.webgui.client.catalog.CatalogUtils;
import edu.umn.msi.tropix.webgui.client.catalog.beans.Attribute;
import edu.umn.msi.tropix.webgui.client.catalog.beans.Category;
import edu.umn.msi.tropix.webgui.client.catalog.beans.Field;
import edu.umn.msi.tropix.webgui.client.catalog.beans.FieldValue;
import edu.umn.msi.tropix.webgui.client.catalog.beans.ServiceBean;
import edu.umn.msi.tropix.webgui.client.components.EditCatalogServiceFormComponent;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.ClientListGrid;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.WidgetSupplierImpl;
import edu.umn.msi.tropix.webgui.services.tropix.CatalogServiceDefinition;

// TODO: Clean this up in many ways...
public class EditCatalogServiceFormComponentSupplierImpl implements Supplier<EditCatalogServiceFormComponent> {
  public EditCatalogServiceFormComponent get() {
    return new EditCatalogServiceFormComponentImpl();
  }

  private static class EditCatalogServiceFormComponentImpl extends WidgetSupplierImpl<Layout> implements EditCatalogServiceFormComponent {
    private final SelectItem categoryItem;
    private final DynamicForm form;
    private final ClientListGrid inputsListGrid;
    private List<Field> fieldList;

    private void showEnumeratedValues(final Field field) {
      final Window window = PopOutWindowBuilder.titled("Select Valid Values").sized(300, 200).get();
      final DynamicForm dynamicForm = new DynamicForm();
      dynamicForm.setWidth100();
      dynamicForm.setHeight("*");

      final SelectItem selectItem = new SelectItem("Possible Values");
      selectItem.setWidth("*");
      selectItem.setHeight("*");
      selectItem.setMultiple(true);
      final LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
      for(final FieldValue value : field.getFieldValues()) {
        map.put(value.getId(), value.getValue());
      }

      final Button okButton = SmartUtils.getButton("Ok", Resources.OK, new Command() {
        public void execute() {
          field.setSelectedValues(new ArrayList<String>());
          for(final String selectedValue : selectItem.getValues()) {
            field.getSelectedValues().add(selectedValue);
          }
          if(!fieldList.contains(field)) {
            fieldList.add(field);
          }
          reloadAttributes();
          window.destroy();
        }
      });

      dynamicForm.addItemChangedHandler(new ItemChangedHandler() {
        public void onItemChanged(final ItemChangedEvent event) {
          okButton.setDisabled(dynamicForm.getValues().isEmpty());
        }
      });

      selectItem.setValueMap(map);
      if(!field.getSelectedValues().isEmpty()) {
        selectItem.setDefaultValues(field.getSelectedValues().toArray(new String[field.getSelectedValues().size()]));
      } else {
        final Set<String> keySet = map.keySet();
        selectItem.setDefaultValues(keySet.toArray(new String[keySet.size()]));
      }
      dynamicForm.setFields(selectItem);
      final Layout layout = new CanvasWithOpsLayout<DynamicForm>(dynamicForm, okButton);
      layout.setWidth100();
      window.addItem(layout);
      window.show();
    }

    private void showAttributes(final HashMap<String, Field> map) {
      final Window window = PopOutWindowBuilder.titled("Add Service Input").get();
      final ClientListGrid listGrid = new ClientListGrid();
      listGrid.setSelectionType(SelectionStyle.SINGLE);
      final ListGridField nameField = new ListGridField("name", "Name");
      final ListGridField typeField = new ListGridField("type", "Type");
      listGrid.setFields(nameField, typeField);
      for(final Map.Entry<String, Field> fieldEntry : map.entrySet()) {
        final Field field = fieldEntry.getValue();
        final ListGridRecord record = new ListGridRecord();
        record.setAttribute("id", field.getId());
        record.setAttribute("name", field.getName());
        record.setAttribute("type", field.getType());
        record.setAttribute("field", field);
        listGrid.addData(record);
      }
      final Button addAttributeButton = SmartUtils.getButton("Add Attribute", Resources.ADD, new Command() {
        public void execute() {
          final Field addedField = new Field(((Field) listGrid.getSelectedRecord().getAttributeAsObject("field")));
          if(addedField.getType().equals("UNSTRUCTURED")) {
            addedField.setCurrentValue("<Enter Value>");
            fieldList.add(addedField);
            reloadAttributes();
          } else {
            showEnumeratedValues(addedField);
          }
          window.destroy();
        }
      });
      final CanvasWithOpsLayout<ClientListGrid> layout = new CanvasWithOpsLayout<ClientListGrid>(listGrid, addAttributeButton);
      window.addItem(layout);
      window.show();

    }

    private Button getAddFieldButton() {
      return SmartUtils.getButton("Add Attribute", Resources.ADD, new Command() {
        public void execute() {
          CatalogServiceDefinition.Util.getInstance().getFields(null, new AsyncCallbackImpl<HashMap<String, Field>>() {
            @Override
            public void onSuccess(final HashMap<String, Field> map) {
              showAttributes(map);
            }
          });
        }
      });
    }

    EditCatalogServiceFormComponentImpl() {
      this.form = new DynamicForm();
      this.form.setWrapItemTitles(false);
      this.form.setWidth("*");
      final TextItem nameItem = new TextItem("name", "Name");
      final TextItem publishItem = new TextItem("publisher", "Publisher");
      final TextAreaItem descriptionItem = new TextAreaItem("description", "Description");
      final SelectItem statusItem = new SelectItem("status", "Status");
      statusItem.setValueMap("ACTIVE", "INACTIVE");
      statusItem.setValue("ACTIVE");

      this.categoryItem = new SelectItem("category", "Category");
      this.populateCategoryDropDown();
      this.form.setItems(nameItem, publishItem, descriptionItem, statusItem, this.categoryItem);

      this.inputsListGrid = new ClientListGrid();
      final ListGridField inputNameItem = new ListGridField("name", "Input Name");
      final ListGridField inputDefaultValueItem = new ListGridField("value", "Possible Values");
      this.inputsListGrid.setFields(inputNameItem, inputDefaultValueItem);

      this.setWidget(new VLayout());
      this.get().setWidth100();
      this.get().addMember(this.form);

      final Button removeInputButton = SmartUtils.getButton("Remove Input", Resources.CROSS, new Command() {
        public void execute() {
          final ListGridRecord record = inputsListGrid.getSelectedRecord();
          fieldList.remove(record.getAttributeAsObject("object"));
          inputsListGrid.removeData(record);
        }
      });
      removeInputButton.setDisabled(true);
      SmartUtils.enabledWhenHasSelection(removeInputButton, inputsListGrid);

      final Button modifyEnumeratedButton = SmartUtils.getButton("Edit Values", Resources.EDIT, new Command() {
        public void execute() {
          showEnumeratedValues((Field) inputsListGrid.getSelectedRecord().getAttributeAsObject("object"));
        }
      });
      modifyEnumeratedButton.setDisabled(true);

      this.inputsListGrid.addSelectionChangedHandler(new SelectionChangedHandler() {
        public void onSelectionChanged(final SelectionEvent event) {
          modifyEnumeratedButton.setDisabled(!event.getState() || !event.getRecord().getAttributeAsString("type").equals("ENUMERATED"));
        }
      });

      final CanvasWithOpsLayout<ClientListGrid> inputsLayout = new CanvasWithOpsLayout<ClientListGrid>(this.inputsListGrid, this.getAddFieldButton(), removeInputButton, modifyEnumeratedButton);
      inputsLayout.setWidth100();
      this.get().addMember(inputsLayout);
    }

    public ServiceBean getServiceBean() {
      final ServiceBean service = new ServiceBean();
      service.setName(StringUtils.toString(this.form.getValueAsString("name")));
      service.setPublisher(StringUtils.toString(this.form.getValueAsString("publisher")));
      service.setDescription(StringUtils.toString(this.form.getValueAsString("description")));
      service.setStatus(StringUtils.toString(this.form.getValueAsString("status")));
      service.setCategoryID(StringUtils.toString(this.form.getValueAsString("category")));
      service.setCategory(this.form.getValueAsString("category"));
      service.setAttributes(new LinkedList<Attribute>());
      service.setFieldList(fieldList);
      return service;
    }

    public boolean validate() {
      return StringUtils.hasText(this.form.getValueAsString("name"));
    }

    private void populateCategoryDropDown() {
      final Collection<Category> categoryCollection = CatalogGlobals.getCategories().values();
      final Category[] categoryArray = categoryCollection.toArray(new Category[categoryCollection.size()]);
      final LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
      for(final Category category : categoryArray) {
        valueMap.put(category.getId(), category.getName());
      }
      this.categoryItem.setAutoFetchData(false);
      this.categoryItem.setValueMap(valueMap);
      this.categoryItem.setValue(categoryArray[0].getId());
    }

    public void reloadAttributes() {
      SmartUtils.removeAllRecords(inputsListGrid);
      for(final Field field : fieldList) {
        final String type = field.getType();
        final ListGridRecord record = new ListGridRecord();
        record.setAttribute("id", field.getId());
        record.setAttribute("name", StringUtils.sanitize(field.getName()));
        record.setAttribute("type", type);
        if(type.equals("ENUMERATED")) {
          final List<String> selectedValues = field.getSelectedValues();
          final HashMap<String, FieldValue> valueMap = new HashMap<String, FieldValue>();
          for(final FieldValue fieldValue : field.getFieldValues()) {
            valueMap.put(fieldValue.getId(), fieldValue);
          }
          final FieldValue[] fieldValues = new FieldValue[selectedValues.size()];
          int i = 0;
          for(final String selectedValue : selectedValues) {
            fieldValues[i++] = valueMap.get(selectedValue);
          }
          record.setAttribute("value", StringUtils.sanitize(CatalogUtils.getValuesString(fieldValues)));
        } else {
          record.setAttribute("value", StringUtils.sanitize("<Free Text Box>"));
          field.setCurrentValue("<Enter Value>");
        }
        record.setAttribute("object", field);
        inputsListGrid.addData(record);
      }
    }

    public void setServiceBean(final ServiceBean service) {
      this.form.setValue("description", service.getDescription());
      this.form.setValue("name", service.getName());
      this.form.setValue("status", service.getStatus());
      this.form.setValue("category", service.getCategoryID());
      this.form.setValue("publisher", service.getPublisher());
      this.fieldList = new LinkedList<Field>();
      final Map<String, Attribute> fieldIds = new HashMap<String, Attribute>(service.getAttributes().size());
      for(final Attribute attribute : service.getAttributes()) {
        fieldIds.put(attribute.getFieldID(), attribute);
      }
      CatalogServiceDefinition.Util.getInstance().getFields(new ArrayList<String>(fieldIds.keySet()), new AsyncCallbackImpl<HashMap<String, Field>>() {
        @Override
        public void onSuccess(final HashMap<String, Field> fieldMap) {
          for(final Field field : fieldMap.values()) {
            final String id = field.getId();
            final Attribute attribute = fieldIds.get(id);
            field.setAttribute(attribute);
            field.setSelectedValues(new ArrayList<String>());
            for(final FieldValue fieldValue : attribute.getValues()) {
              field.getSelectedValues().add(fieldValue.getId());
            }
            fieldList.add(field);
          }
          reloadAttributes();
        }
      });
    }

    public void addChangedListener(final Listener<EditCatalogServiceFormComponent> listener) {
      final EditCatalogServiceFormComponent component = this;
      this.form.addItemChangedHandler(new ItemChangedHandler() {
        public void onItemChanged(final ItemChangedEvent event) {
          listener.onEvent(component);
        }
      });
    }

  }
}
