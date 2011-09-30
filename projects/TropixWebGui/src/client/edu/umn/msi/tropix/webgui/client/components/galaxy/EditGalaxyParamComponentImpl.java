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

package edu.umn.msi.tropix.webgui.client.components.galaxy;

import java.util.LinkedHashMap;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.galaxy.tool.Param;
import edu.umn.msi.tropix.galaxy.tool.ParamOption;
import edu.umn.msi.tropix.galaxy.tool.ParamType;
import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.FileTypeFormItemComponent;
import edu.umn.msi.tropix.webgui.client.components.impl.EditObjectComponentImpl;
import edu.umn.msi.tropix.webgui.client.utils.Maps;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.ClientListGrid;
import edu.umn.msi.tropix.webgui.client.widgets.Form;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;

public class EditGalaxyParamComponentImpl extends EditObjectComponentImpl<Canvas, Param> {
  private final FileTypeFormItemComponent fileTypeFormItemComponent;
  private final FormItem formatItem;
  private final TextItem nameItem = new TextItem("name", "Name");
  private final TextItem valueItem = new TextItem("value", "Default");
  private final TextItem labelItem = new TextItem("label", "Label");
  private final SelectItem typeItem = new SelectItem("type", "Type");
  private final TextAreaItem helpItem = new TextAreaItem("help", "Help");
  private final ListGrid optionsGrid = new ClientListGrid("id");
  private final Canvas optionsCanvas;
  private final Form formatForm;
  private final TextItem trueValueItem = new TextItem("trueValue", "True Value");
  private final TextItem falseValueItem = new TextItem("falseValue", "False Value");
  
  private final VLayout trueFalseCanvas = new VLayout();
  private final VLayout typeSpecificLayout = new VLayout();
  
  private void updateType() {
    for(final Canvas canvas : typeSpecificLayout.getChildren()) {
      canvas.hide();
    }
    final Object typeItemValue = typeItem.getValue();
    if(typeItemValue == null) {
      return;
    }
    final ParamType type = ParamType.fromValue(typeItemValue.toString());
    if(type == ParamType.SELECT) {
      optionsCanvas.show();
    } 
    if(type == ParamType.BOOLEAN) {
      trueFalseCanvas.show();
    }
    if(type == ParamType.DATA) {
      formatForm.show();
    }
    get().redraw();
  }
  
  public EditGalaxyParamComponentImpl(final Param param, final boolean fixedType, final FileTypeFormItemComponent fileTypeFormItemComponent) {
    this.fileTypeFormItemComponent = fileTypeFormItemComponent;
    final ListGridField nameField = new ListGridField("name", "Name");
    nameField.setType(ListGridFieldType.TEXT);
    nameField.setWidth("*");

    final ListGridField labelField = new ListGridField("label", "Label");
    nameField.setType(ListGridFieldType.TEXT);
    nameField.setWidth("*");
    
    optionsGrid.setCanEdit(true);
    optionsGrid.setMinHeight(100);
    optionsGrid.setAutoFitMaxRecords(5);
    optionsGrid.setAutoFitData(Autofit.VERTICAL);
    optionsGrid.setFields(nameField, labelField);
    optionsGrid.setEmptyMessage("No options to show");

    final Button addButton = SmartUtils.getButton("New Option", Resources.ADD, new com.google.gwt.user.client.Command() {
      public void execute() {
        optionsGrid.startEditingNew();
      }
    });

    final Button removeButton = SmartUtils.getButton(SmartUtils.ButtonType.REMOVE, new com.google.gwt.user.client.Command() {
      public void execute() {
        optionsGrid.removeSelectedData();
      }
    });

    SmartUtils.enabledWhenHasSelection(removeButton, optionsGrid);
    final LinkedHashMap<String, String> paramTypesMap = Maps.newLinkedHashMap();
    paramTypesMap.put(ParamType.INTEGER.value(), "Integer");
    paramTypesMap.put(ParamType.FLOAT.value(), "Floating-point Number");
    paramTypesMap.put(ParamType.TEXT.value(), "Text");
    paramTypesMap.put(ParamType.DATA.value(), "Existing File");
    paramTypesMap.put(ParamType.SELECT.value(), "Option List");
    paramTypesMap.put(ParamType.BOOLEAN.value(), "Check Box");

    helpItem.setWidth("*");
    
    typeItem.setValueMap(paramTypesMap);
    typeItem.addChangedHandler(new ChangedHandler() {
      public void onChanged(final ChangedEvent event) {
        updateType();
      }      
    });
    final Form form = new Form(nameItem, valueItem, labelItem, typeItem, helpItem);
    form.setColWidths("100px", "*");
    form.setMinWidth(300);
    form.addItemChangedHandler(getItemChangedHandler());

    trueFalseCanvas.addMember(new Form(trueValueItem, falseValueItem));
    trueFalseCanvas.hide();
    
    optionsCanvas = new CanvasWithOpsLayout<ListGrid>("Outputs", optionsGrid, addButton, removeButton);
    optionsCanvas.hide();
    
    formatItem = fileTypeFormItemComponent.get();
    formatItem.setTitle("File Type");
    formatForm = new Form(formatItem);
    formatForm.setWrapItemTitles(false);
    formatForm.hide();
    
    typeSpecificLayout.addChild(formatForm);
    typeSpecificLayout.addChild(optionsCanvas);
    typeSpecificLayout.addChild(trueFalseCanvas);
        
    final VLayout layout = new VLayout();
    layout.addMember(form);
    layout.addMember(typeSpecificLayout);
    setWidget(layout);

    nameItem.setValue(StringUtils.toString(param.getName()));
    valueItem.setValue(StringUtils.toString(param.getValue()));
    labelItem.setValue(StringUtils.toString(param.getLabel()));
    helpItem.setValue(StringUtils.toString(param.getHelp()));
    trueValueItem.setValue(StringUtils.toString(param.getTruevalue()));
    falseValueItem.setValue(StringUtils.toString(param.getFalsevalue()));
    nameItem.setWidth("150px");
    valueItem.setWidth("150px");
    labelItem.setWidth("150px");
    helpItem.setWidth("150px");
    
    if(StringUtils.hasText(param.getFormat())) {
      fileTypeFormItemComponent.setSelection(GalaxyFormatUtils.formatToExtension(param.getFormat()));      
    }
    if(param.getType() != null) {
      final String type = param.getType().value();
      typeItem.setValue(type);
      updateType();
    }
    for(ParamOption option : param.getOption()) {
      final ListGridRecord optionRecord = new ListGridRecord();
      optionRecord.setAttribute("name", option.getValueAttribute());
      optionRecord.setAttribute("label", option.getValue());
      optionsGrid.addData(optionRecord);
    }
  }

  public Param getObject() {
    try {
      final Param param = new Param();
      param.setValue(StringUtils.toString(valueItem.getValue()));
      param.setName(StringUtils.toString(nameItem.getValue()));
      param.setHelp(StringUtils.toString(helpItem.getValue()));
      param.setLabel(StringUtils.toString(labelItem.getValue()));
      param.setType(ParamType.fromValue(StringUtils.toString(typeItem.getValue())));
      param.setTruevalue(StringUtils.toString(trueValueItem.getValue()));
      param.setFalsevalue(StringUtils.toString(falseValueItem.getValue()));
      final FileType selectedFileType = fileTypeFormItemComponent.getSelection();
      if(selectedFileType != null) {
        param.setFormat(GalaxyFormatUtils.extensionToFormat(selectedFileType.getExtension()));        
      }
      final RecordList recordList = optionsGrid.getRecordList();
      for(int i = 0; i < recordList.getLength(); i++) {
        final Record optionRecord = recordList.get(i);
        final ParamOption option = new ParamOption();
        option.setValue(optionRecord.getAttributeAsString("label"));
        option.setValueAttribute(optionRecord.getAttributeAsString("name"));
        param.getOption().add(option);
      }
      return param;
    } catch(RuntimeException e) {
      e.printStackTrace();
      throw e;
    }
  }

  public boolean isValid() {
    return StringUtils.hasText(nameItem.getValue());
  }

}