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

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.google.common.base.Supplier;
import com.google.inject.Inject;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.Autofit;
import com.smartgwt.client.types.ListGridFieldType;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.util.ValueCallback;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.DrawEvent;
import com.smartgwt.client.widgets.events.DrawHandler;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeNode;

import edu.umn.msi.tropix.galaxy.tool.Command;  
import edu.umn.msi.tropix.galaxy.tool.Conditional;
import edu.umn.msi.tropix.galaxy.tool.ConfigFile;
import edu.umn.msi.tropix.galaxy.tool.ConfigFiles;
import edu.umn.msi.tropix.galaxy.tool.Data;
import edu.umn.msi.tropix.galaxy.tool.InputType;
import edu.umn.msi.tropix.galaxy.tool.Inputs;
import edu.umn.msi.tropix.galaxy.tool.Outputs;
import edu.umn.msi.tropix.galaxy.tool.Param;
import edu.umn.msi.tropix.galaxy.tool.ParamType;
import edu.umn.msi.tropix.galaxy.tool.Repeat;
import edu.umn.msi.tropix.galaxy.tool.Tool;
import edu.umn.msi.tropix.galaxy.tool.WhenData;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.EditObjectComponent;
import edu.umn.msi.tropix.webgui.client.components.FileTypeFormItemComponent;
import edu.umn.msi.tropix.webgui.client.components.impl.EditObjectComponentImpl;
import edu.umn.msi.tropix.webgui.client.components.impl.EditObjectWindowComponentImpl;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.Lists;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.ClientListGrid;
import edu.umn.msi.tropix.webgui.client.widgets.Form;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils.ButtonType;

// TODO: Add selected to edit 
// TODO: Escape HTML throughout
public class EditGalaxyToolComponentFactoryImpl implements ComponentFactory<Tool, EditObjectComponent<Canvas, Tool>> {
  private static int lastId = 0;

  private final Supplier<FileTypeFormItemComponent> fileTypeFormItemComponentSupplier;

  @Inject
  public EditGalaxyToolComponentFactoryImpl(final Supplier<FileTypeFormItemComponent> fileTypeFormItemComponentSupplier) {
    this.fileTypeFormItemComponentSupplier = fileTypeFormItemComponentSupplier;
  }
  
  private static TreeNode getNewNode() {
      final TreeNode node = new TreeNode();
    node.setAttribute("position", 0);
    node.setAttribute("id", "" + ++lastId);   
    node.setAttribute("isRoot", false);
    return node;
  }
  

  public static class EditInputWindowComponentImpl extends EditObjectWindowComponentImpl<Param> {
    public EditInputWindowComponentImpl(final EditGalaxyParamComponentImpl component, final ButtonType buttonType, final Listener<Param> callback) {
      super(component, PopOutWindowBuilder.titled("Edit Galaxy Input Parameter").autoSized(), buttonType);
      setCallback(callback);
    }
  }

  public class EditOutputWindowComponentImpl extends EditObjectWindowComponentImpl<Data> {
    public EditOutputWindowComponentImpl(final Data data, final ButtonType buttonType, final Listener<Data> callback) {
      super(new EditGalaxyOutputComponentImpl(data, fileTypeFormItemComponentSupplier.get()), PopOutWindowBuilder.titled("Edit Galaxy Output").autoSized(), buttonType);
      setCallback(callback);
    }
  }

  public static class EditConfigFileWindowComponentImpl extends EditObjectWindowComponentImpl<ConfigFile> {

    public EditConfigFileWindowComponentImpl(final ConfigFile configFile, final ButtonType buttonType, final Listener<ConfigFile> callback) {
      super(new EditGalaxyConfigFileComponentImpl(configFile), PopOutWindowBuilder.titled("Edit Galaxy Config File"), buttonType);
      setCallback(callback);
    }
    
  }
  
  
  private static TreeNode paramToTreeNode(final Param param, final boolean fixedType) {
    final TreeNode newRecord = getNewNode();
    newRecord.setAttribute("name", param.getName());
    newRecord.setAttribute("folder", false);
    newRecord.setAttribute("object", param);
    newRecord.setAttribute("fixedType", fixedType);
    return newRecord;
  }

  private static void addAsSelectedsChild(final TreeGrid tree, final TreeNode newNode) {
    addAsChild(tree, newNode, (TreeNode) tree.getSelectedRecord());            
  }


  private static void addAsChild(final TreeGrid treeGrid, final TreeNode newNode, final TreeNode parent) {
    final Tree tree = treeGrid.getData();
    newNode.setAttribute("parent", parent.getAttribute("id"));
    final TreeNode[] siblings = tree.getChildren(parent);
    final int position = siblings == null ? 0 : siblings.length;
    newNode.setAttribute("position", position);
    treeGrid.getData().add(newNode, parent);
  }
  
  private enum Direction { UP, DOWN; }

  private static boolean canMove(final TreeGrid treeGrid, final Direction direction) {
    final Tree tree = treeGrid.getData();
    final TreeNode selectedNode = (TreeNode) treeGrid.getSelectedRecord();
    final TreeNode parentNode = tree.getParent(selectedNode);
    final TreeNode[] childNodes = tree.getChildren(parentNode);
    final int numChildren = childNodes == null ? 0 : childNodes.length;
    final int position = selectedNode.getAttributeAsInt("position");
    return numChildren > 1 
           && ((direction == Direction.UP && position > 0) 
               || (direction == Direction.DOWN && position < (numChildren - 1)));
  }
  
  private static void move(final TreeGrid treeGrid, final Direction direction) {
    if(!canMove(treeGrid, direction)) {
      return;
    }
    final Tree tree = treeGrid.getData();
    final TreeNode selectedNode = (TreeNode) treeGrid.getSelectedRecord();
    final TreeNode parentNode = tree.getParent(selectedNode);
    final TreeNode[] childNodes = tree.getChildren(parentNode);
    final int position = selectedNode.getAttributeAsInt("position");
    if(direction == Direction.UP && position > 0) {
      selectedNode.setAttribute("position", position - 1);
    } else if(direction == Direction.DOWN && position < childNodes.length - 1) {
      selectedNode.setAttribute("position", position + 1);
    }      
    for(TreeNode node : childNodes) {
      if(node == selectedNode) {
        continue;
      }
      final int thisPosition = node.getAttributeAsInt("position");
      if(direction == Direction.UP && thisPosition == (position - 1) 
         || direction == Direction.DOWN && thisPosition == (position + 1)) {
        node.setAttribute("position", position);
      }
    }
    // Only way I could find to resort the tree, update the fields, and trigger selection handler
    treeGrid.redraw();
    tree.closeAll();
    tree.openAll();
    treeGrid.deselectRecord(selectedNode);
    treeGrid.selectRecord(selectedNode);
  }  
  
  private class EditGalaxyToolFormComponentImpl extends EditObjectComponentImpl<Canvas, Tool> {
    private final TextItem nameItem = new TextItem("name", "Name");
    private final TextAreaItem descriptionItem = new TextAreaItem("description", "Description");
    private final TextItem interpreterItem = new TextItem("interpreter", "Interpreter (Optional)");
    private final TextAreaItem commandItem = new TextAreaItem("command", "Command");
    private final TextAreaItem helpItem = new TextAreaItem("help", "Help");
    private final Form form;    
    private final Tool inputTool;
    private Canvas inputsCanvas, outputsCanvas, configFilesCanvas;
    private final ListGrid outputsGrid = new ClientListGrid("id"), configFilesGrid = new ClientListGrid("id");
    private final TreeGrid inputsTreeGrid = new TreeGrid();
    
    EditGalaxyToolFormComponentImpl(final Tool tool) {
      this.inputTool = tool;
      form = new Form(nameItem, descriptionItem, interpreterItem, commandItem, helpItem);
      form.setWidth100();
      nameItem.setWidth("*");
      descriptionItem.setWidth("*");
      interpreterItem.setWidth("*");
      commandItem.setWidth("*");
      helpItem.setWidth("*");
      
      form.addItemChangedHandler(getItemChangedHandler());
      
      descriptionItem.setValue(StringUtils.toString(tool.getDescription()));
      nameItem.setValue(StringUtils.toString(tool.getName()));
      descriptionItem.setValue(StringUtils.toString(tool.getDescription()));      
      final Command command = tool.getCommand();
      if(command != null) {
        interpreterItem.setValue(StringUtils.toString(command.getInterpreter()));
        commandItem.setValue(StringUtils.toString(command.getValue()));
      }
      helpItem.setValue(StringUtils.toString(tool.getHelp()));
      
      initOutputsCanvas();
      initInputsCanvas();
      initConfigFilesCanvas();
      
      final VLayout layout = new VLayout();
      layout.addMember(form);
      layout.addMember(inputsCanvas);
      layout.addMember(outputsCanvas);
      layout.addMember(configFilesCanvas);
      this.setWidget(layout);
    }

    
    private void initInputsCanvas() {
      final ListGridField nameField = new ListGridField("name", "Input Name");
      nameField.setType(ListGridFieldType.TEXT);
      nameField.setWidth("*");

      final Tree tree = new Tree();
      tree.setIdField("id");
      tree.setModelType(TreeModelType.PARENT);
      tree.setParentIdField("parent");
      tree.setIsFolderProperty("folder");

      final TreeNode rootNode = getNewNode();
      rootNode.setAttribute("name", "*Top Level Inputs*");
      rootNode.setAttribute("isRoot", true);
      rootNode.setAttribute("folder", true);
      rootNode.setAttribute("parent", "1");
      rootNode.setAttribute("object", new Object());
      tree.setRoot(rootNode);

      inputsTreeGrid.setFields(nameField);
      inputsTreeGrid.setData(tree);
      inputsTreeGrid.setShowRoot(true);
      inputsTreeGrid.setShowConnectors(true);
      //treeGrid.setCanSort(false);
      inputsTreeGrid.setMinHeight(200);
      inputsTreeGrid.setSortField("position");
      inputsTreeGrid.setHeight(200);

      inputsTreeGrid.addDrawHandler(new DrawHandler() {
        public void onDraw(final DrawEvent event) {
          tree.openAll();
        }
      });

      final Button addParamButton = SmartUtils.getButton("Add Param", Resources.ADD, new com.google.gwt.user.client.Command() {
        public void execute() {
          new EditInputWindowComponentImpl(new EditGalaxyParamComponentImpl(new Param(), false, fileTypeFormItemComponentSupplier.get()), ButtonType.ADD, new Listener<Param>() {
            public void onEvent(final Param param) {
              addAsSelectedsChild(inputsTreeGrid, paramToTreeNode(param, false));
            }
          }).execute();
        }
      });

      final Button addConditionalButton = SmartUtils.getButton("Add Conditional", Resources.ADD, new com.google.gwt.user.client.Command() {
        public void execute() {
          SC.askforValue("Name of conditional", new ValueCallback() {
            public void execute(final String value) {
              try {
                final Conditional cond = new Conditional();
                cond.setName(value);

                final TreeNode node = getNewNode();
                node.setAttribute("name", "Conditional[" + value + "]");
                node.setAttribute("folder", true);
                node.setAttribute("object", cond);
                
                addAsSelectedsChild(inputsTreeGrid, node);
                final Param param = new Param();
                param.setName("picknewname");
                param.setType(ParamType.SELECT);

                final TreeNode inputNode = paramToTreeNode(param, true);              
                addAsChild(inputsTreeGrid, inputNode, node);
              } catch(Exception e) {
                e.printStackTrace();
              }
            }
          });
        }      
      });

      final Button addRepeatButton = SmartUtils.getButton("Add Repeat", Resources.ADD, new com.google.gwt.user.client.Command() {
        public void execute() {
          SC.say("Not yet implemented.");
        }      
      });

      final Button moveUpButton = SmartUtils.getButton("Move Up", new com.google.gwt.user.client.Command() {
        public void execute() {
          move(inputsTreeGrid, Direction.UP);
        }
      });

      final Button moveDownButton = SmartUtils.getButton("Move Down", new com.google.gwt.user.client.Command() {
        public void execute() {
          move(inputsTreeGrid, Direction.DOWN);
        }      
      });

      final Button removeButton = SmartUtils.getButton(ButtonType.REMOVE, new com.google.gwt.user.client.Command() {
        public void execute() {
          inputsTreeGrid.removeSelectedData();
        }      
      });

      final Button editButton = SmartUtils.getButton(ButtonType.EDIT, new com.google.gwt.user.client.Command() {
        public void execute() {
          final TreeNode selectedNode = (TreeNode) inputsTreeGrid.getSelectedRecord();
          final Object selectedObject = selectedNode.getAttributeAsObject("object");
          if(selectedObject instanceof Param) {
            new EditInputWindowComponentImpl(new EditGalaxyParamComponentImpl((Param) selectedObject, selectedNode.getAttributeAsBoolean("fixedType"), fileTypeFormItemComponentSupplier.get()), ButtonType.OK, new Listener<Param>() {
              public void onEvent(final Param param) {
                selectedNode.setAttribute("name", param.getName());
                selectedNode.setAttribute("object", param);
              }
            }).execute();
          }
        }
      });

      final com.google.gwt.user.client.Command disableAllButtons = new com.google.gwt.user.client.Command() {
        public void execute() {
          editButton.setDisabled(true);
          addParamButton.setDisabled(true);
          addRepeatButton.setDisabled(true);
          addConditionalButton.setDisabled(true);
          moveDownButton.setDisabled(true);
          moveUpButton.setDisabled(true);
          removeButton.setDisabled(true);          
        }
      };
      disableAllButtons.execute();
      inputsTreeGrid.addSelectionChangedHandler(new SelectionChangedHandler() {
        public void onSelectionChanged(final SelectionEvent event) {
          final ListGridRecord record = inputsTreeGrid.getSelectedRecord();
          if(record == null) {
            disableAllButtons.execute();
          } else {
            final boolean isRoot = record.getAttributeAsBoolean("isRoot");
            final Object selectedObject = record.getAttributeAsObject("object");
            final boolean canAddInput = isRoot || (selectedObject instanceof WhenData || selectedObject instanceof Repeat);
            addParamButton.setDisabled(!canAddInput);
            addConditionalButton.setDisabled(!canAddInput);
            addRepeatButton.setDisabled(!canAddInput);
            editButton.setDisabled(isRoot || selectedObject instanceof WhenData);
            removeButton.setDisabled(isRoot || selectedObject instanceof WhenData);          
            moveUpButton.setDisabled(!canMove(inputsTreeGrid, Direction.UP));
            moveDownButton.setDisabled(!canMove(inputsTreeGrid, Direction.DOWN));
          }
        }
      });
      final HLayout layout = new HLayout();
      final VLayout leftLayout = new VLayout();
      leftLayout.addMember(inputsTreeGrid);
      final VLayout rightLayout = new VLayout();
      rightLayout.setMembersMargin(5);
      layout.setPadding(10);
      layout.setMembersMargin(10);
      rightLayout.addMember(addParamButton);
      rightLayout.addMember(addConditionalButton);
      rightLayout.addMember(addRepeatButton);
      rightLayout.addMember(editButton);
      rightLayout.addMember(moveUpButton);
      rightLayout.addMember(moveDownButton);
      rightLayout.addMember(removeButton);
      rightLayout.setWidth("120px");
      for(Canvas canvas : rightLayout.getMembers()) {
        canvas.setWidth("100%");
      }
      layout.setMembers(leftLayout, rightLayout);
      layout.setGroupTitle("Inputs");
      layout.setIsGroup(true);
      this.inputsCanvas = layout;
      
      // Populate tree with input tool's inputs
      final Inputs inputs = inputTool.getInputs();
      if(inputs != null) {
        addInputsChildren(rootNode, inputs.getInputElement());
      }      
    }
    
    private void addInputsChildren(final TreeNode node, final List<InputType> inputs) {
      for(InputType input : inputs) {
        if(input instanceof Param) {
          addAsChild(inputsTreeGrid, paramToTreeNode((Param) input, false), node);
        }
      }
    }

    private void initConfigFilesCanvas() {
      final ListGridField nameField = new ListGridField("name", "File Name");
      nameField.setType(ListGridFieldType.TEXT);
      nameField.setWidth("*");

      configFilesGrid.setMinHeight(100);
      configFilesGrid.setAutoFitMaxRecords(5);
      configFilesGrid.setAutoFitData(Autofit.VERTICAL);
      configFilesGrid.setFields(nameField);
      configFilesGrid.setEmptyMessage("No files to show");

      final Button addButton = SmartUtils.getButton("Add", Resources.ADD, new com.google.gwt.user.client.Command() {
        public void execute() {
          new EditConfigFileWindowComponentImpl(new ConfigFile(), ButtonType.ADD, new Listener<ConfigFile>() {
            public void onEvent(final ConfigFile configFile) {
              configFilesGrid.addData(getRecordForConfigFile(configFile));
            }
          }).execute();
        }
      });

      final Button editButton = SmartUtils.getButton(ButtonType.EDIT, new com.google.gwt.user.client.Command() {
        public void execute() {
          final ListGridRecord record = configFilesGrid.getSelectedRecord();
          new EditConfigFileWindowComponentImpl((ConfigFile) record.getAttributeAsObject("object"), ButtonType.OK, new Listener<ConfigFile>() {
            public void onEvent(final ConfigFile configFile) {
              record.setAttribute("name", configFile.getName());
              record.setAttribute("object", configFile);
            }
          }).execute();        
        }
      });

      final Button removeButton = SmartUtils.getButton(ButtonType.REMOVE, new com.google.gwt.user.client.Command() {
        public void execute() {
          configFilesGrid.removeSelectedData();
        }
      });

      SmartUtils.enabledWhenHasSelection(editButton, configFilesGrid, false);
      SmartUtils.enabledWhenHasSelection(removeButton, configFilesGrid, false);
      final Layout canvas = new CanvasWithOpsLayout<ListGrid>(configFilesGrid, addButton, editButton, removeButton);
      canvas.setPadding(10);
      canvas.setGroupTitle("Files");
      canvas.setIsGroup(true);
      configFilesCanvas = canvas;
      
      // Populate this grid
      final ConfigFiles configFiles = inputTool.getConfigfiles();
      if(configFiles != null) {
        for(final ConfigFile configFile : configFiles.getConfigfile()) {
          configFilesGrid.addData(getRecordForConfigFile(configFile));
        }
      }
    }
    
    private ListGridRecord getRecordForData(final Data data) {
      final ListGridRecord record = new ListGridRecord();
      record.setAttribute("name", data.getName());
      record.setAttribute("id", ++lastId);
      record.setAttribute("object", data);
      return record;
    }
    
    private ListGridRecord getRecordForConfigFile(final ConfigFile configFile) {
      final ListGridRecord record = new ListGridRecord();
      record.setAttribute("name", configFile.getName());
      record.setAttribute("id", ++lastId);
      record.setAttribute("object", configFile);
      return record;
    }

    
    private void initOutputsCanvas() {
      final ListGridField nameField = new ListGridField("name", "Output Name");
      nameField.setType(ListGridFieldType.TEXT);
      nameField.setWidth("*");

      outputsGrid.setMinHeight(100);
      outputsGrid.setAutoFitMaxRecords(5);
      outputsGrid.setAutoFitData(Autofit.VERTICAL);
      outputsGrid.setFields(nameField);
      outputsGrid.setEmptyMessage("No outputs to show");

      final Button addButton = SmartUtils.getButton("Add", Resources.ADD, new com.google.gwt.user.client.Command() {
        public void execute() {
          new EditOutputWindowComponentImpl(new Data(), ButtonType.ADD, new Listener<Data>() {
            public void onEvent(final Data data) {
              outputsGrid.addData(getRecordForData(data));
            }
          }).execute();
        }
      });

      final Button editButton = SmartUtils.getButton(ButtonType.EDIT, new com.google.gwt.user.client.Command() {
        public void execute() {
          final ListGridRecord record = outputsGrid.getSelectedRecord();
          new EditOutputWindowComponentImpl((Data) record.getAttributeAsObject("object"), ButtonType.OK, new Listener<Data>() {
            public void onEvent(final Data data) {
              record.setAttribute("name", data.getName());
              record.setAttribute("object", data);
            }
          }).execute();        
        }
      });

      final Button removeButton = SmartUtils.getButton(ButtonType.REMOVE, new com.google.gwt.user.client.Command() {
        public void execute() {
          outputsGrid.removeSelectedData();
        }
      });


      SmartUtils.enabledWhenHasSelection(editButton, outputsGrid);
      SmartUtils.enabledWhenHasSelection(removeButton, outputsGrid);
      final Layout canvas = new CanvasWithOpsLayout<ListGrid>(outputsGrid, addButton, editButton, removeButton);
      canvas.setPadding(10);
      canvas.setGroupTitle("Outputs");
      canvas.setIsGroup(true);
      this.outputsCanvas = canvas;
      
      // Populate this grid
      final Outputs outputs = inputTool.getOutputs();
      if(outputs != null) {
        for(Data data : outputs.getData()) {
          outputsGrid.addData(getRecordForData(data));
        }
      }
    }
    
    final List<InputType> getInputs(final Tree tree, final TreeNode node) {
      final List<InputType> results = Lists.newArrayList();
      final TreeNode[] children = tree.getChildren(node);
      if(children != null) {
        // Sort children by position attribute in case smart doesn't return them
        // in expected order.
        Arrays.sort(children, new Comparator<TreeNode>() {
          public int compare(final TreeNode node1, final TreeNode node2) {
            final int pos1 = node1.getAttributeAsInt("position"); 
            final int pos2 = node2.getAttributeAsInt("position");
            return pos1 < pos2 ? -1 : (pos1 == pos2 ? 0 : 1);
          }        
        });
        for(TreeNode child : children) {
          final Object object = child.getAttributeAsObject("object");
          if(object instanceof Param) {
            results.add((Param) object);
          } 
          // TODO: Implement remaining types.
        }
      }
      return results;
    }

    public Tool getObject() {
      try {
      final Tool tool = new Tool();
      final Command command = new Command(); 
      final String interpreterStr = form.getValueAsString("interpreter");
      if(StringUtils.hasText(interpreterStr)) {
        command.setInterpreter(interpreterStr);
      } else {
        command.setInterpreter(null);
      }
      command.setValue(StringUtils.toString(commandItem.getValue()));
      tool.setDescription(StringUtils.toString(descriptionItem.getValue()));
      tool.setCommand(command);
      tool.setName(StringUtils.toString(nameItem.getValue()));
      tool.setHelp(StringUtils.toString(helpItem.getValue()));
      
      final Inputs inputs = new Inputs();
      final Tree inputsTree = inputsTreeGrid.getData();      
      final TreeNode inputsRootNode = inputsTree.getRoot();
      inputs.getInputElement().addAll(getInputs(inputsTree, inputsRootNode));

      final Outputs outputs = new Outputs();
      final RecordList outputsRecordList = outputsGrid.getDataAsRecordList();
      for(int i = 0; i < outputsRecordList.getLength(); i++) {
        final Record record = outputsRecordList.get(i);
        outputs.getData().add((Data) record.getAttributeAsObject("object"));
      }
      
      final ConfigFiles configFiles = new ConfigFiles();
      final RecordList configFilesRecordList = configFilesGrid.getDataAsRecordList();
      for(int i = 0; i < configFilesRecordList.getLength(); i++) {
        final Record record = configFilesRecordList.get(i);
        configFiles.getConfigfile().add((ConfigFile) record.getAttributeAsObject("object"));
      }
      
      tool.setInputs(inputs);
      tool.setOutputs(outputs);
      tool.setConfigfiles(configFiles);
      return tool;
      } catch(RuntimeException e) {
        e.printStackTrace();
        throw e;
      }
    }

    public boolean isValid() {
      return StringUtils.hasText(form.getValueAsString("name"))
      && StringUtils.hasText(form.getValueAsString("command"));
    }

  }

  public EditObjectComponent<Canvas, Tool> get(final Tool tool) {
    return new EditGalaxyToolFormComponentImpl(tool);
  }

}
