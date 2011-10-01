package edu.umn.msi.tropix.webgui.client.components.newwizards;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;

import com.google.common.base.Predicate;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;

import edu.umn.msi.tropix.models.utils.TropixObjectType;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItemPredicates;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions.SelectionType;
import edu.umn.msi.tropix.webgui.client.components.tree.TropixObjectTreeItemExpanders;
import edu.umn.msi.tropix.webgui.client.forms.ValidationListener;
import edu.umn.msi.tropix.webgui.client.utils.Iterables;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.WidgetSupplierImpl;

public class LocationSelectionComponentImpl extends WidgetSupplierImpl<Layout> {

  public interface InputType {
    TropixObjectType getInputType();

    boolean getAllowMultiple();

    Predicate<TreeItem> getSelectionPredicate();

    boolean isBatch();

    String getSelectionLabel();

    Predicate<TreeItem> getShowPredicate();

  }

  public static class InputTypeImpl implements InputType {
    private final String name;
    private final TropixObjectType inputType;
    private final boolean allowMultiple;
    private final Predicate<TreeItem> selectionPredicate;
    private final Predicate<TreeItem> showPredicate;
    private final String selectionLabel;
    private final boolean isBatch;

    public InputTypeImpl(final String name,
        final TropixObjectType inputType,
        final boolean allowMultiple,
        final boolean isBatch,
        final Predicate<TreeItem> selectionPredicate,
        final String selectionLabel) {
      this(name,
           inputType,
           allowMultiple,
           isBatch,
           selectionPredicate,
           TreeItemPredicates.getTropixObjectTreeItemTypePredicate(
               new TropixObjectType[] {
                   TropixObjectTypeEnum.FOLDER,
                   TropixObjectTypeEnum.VIRTUAL_FOLDER, inputType},
               true),
           selectionLabel);
    }

    public InputTypeImpl(final String name,
                         final TropixObjectType inputType,
                         final boolean allowMultiple,
                         final boolean isBatch,
                         final Predicate<TreeItem> selectionPredicate,
                         final Predicate<TreeItem> showPredicate,
                         final String selectionLabel) {
      this.name = name;
      this.inputType = inputType;
      this.allowMultiple = allowMultiple;
      this.selectionPredicate = selectionPredicate;
      this.showPredicate = showPredicate;
      this.selectionLabel = selectionLabel;
      this.isBatch = isBatch;
    }

    public String getSelectionLabel() {
      return selectionLabel;
    }

    public boolean isBatch() {
      return isBatch;
    }

    public TropixObjectType getInputType() {
      return inputType;
    }

    public boolean getAllowMultiple() {
      return allowMultiple;
    }

    public Predicate<TreeItem> getShowPredicate() {
      return showPredicate;
    }

    public Predicate<TreeItem> getSelectionPredicate() {
      return selectionPredicate;
    }

    public String toString() {
      return name;
    }

  }

  protected Collection<TreeItem> selectedTreeItems;
  protected final Collection<TreeItem> initialTreeItems;
  private ArrayList<String> ids = new ArrayList<String>();
  protected final TreeComponentFactory treeComponentFactory;
  protected final LocationFactory locationFactory;
  protected final ValidationListener validationListener;
  protected InputType inputType;
  protected Iterable<InputType> validInputTypes;
  private TreeGrid inputTree;
  protected boolean optionalRun;
  protected boolean expand = false;
  protected Layout treeLayout = new VLayout();
  private TreeComponent tree;

  LocationSelectionComponentImpl(final TreeComponentFactory treeComponentFactory, final LocationFactory locationFactory,
      final Collection<TreeItem> treeItems,
      final boolean optionalRun, final Iterable<InputType> validInputTypes, final ValidationListener validationListener) {
    this.treeComponentFactory = treeComponentFactory;
    this.locationFactory = locationFactory;
    this.initialTreeItems = treeItems;
    this.validInputTypes = validInputTypes;
    this.validationListener = validationListener;
    this.optionalRun = optionalRun;
    // Preconditions.checkNotNull(validInputTypes);
    if(validInputTypes == null) {
      throw new NullPointerException("Valid input types should not be null");
    }
    initInputType(validInputTypes);

    setWidget(SmartUtils.getFullVLayout());
    final String headerLabel = "Select input" + (this.inputType.isBatch() ? "s " : " ") + (this.optionalRun ? "(optional)" : "") + ":";
    final HLayout headerLayout = buildHeader(headerLabel);
    get().addMember(headerLayout);
    get().addMember(treeLayout);
  }

  private void initInputType(final Iterable<InputType> validInputTypes) {
    this.inputType = Iterables.size(validInputTypes) > 1 ? null : Iterables.getOnlyElement(validInputTypes);
    if(this.inputType == null) { // Attempt to infer from selection
      for(InputType validInputType : validInputTypes) {
        if(validInputType == null) {
          throw new NullPointerException("Input type should not be null");
        }
        if(initialTreeItems != null &&
             Iterables.any(this.initialTreeItems, validInputType.getSelectionPredicate())) {
          this.inputType = validInputType;
          break;
        }
      }
      expand = true;
    }
    if(this.inputType == null) {
      this.inputType = validInputTypes.iterator().next();
    }
  }

  protected HLayout buildHeader(final String headerLabel) {
    final Label runLabel = SmartUtils.smartParagraph(headerLabel);
    runLabel.setAutoWidth();
    runLabel.setWrap(false);
    runLabel.setLayoutAlign(VerticalAlignment.CENTER);

    final HLayout headerLayout = new HLayout();
    headerLayout.addMember(runLabel);

    if(Iterables.size(validInputTypes) > 1) {
      final DynamicForm selectionForm = buildTypeSelectionForm();
      headerLayout.addMember(selectionForm);
    }

    headerLayout.setHeight(20);
    headerLayout.setWidth100();
    return headerLayout;
  }

  private DynamicForm buildTypeSelectionForm() {
    final SelectItem selectionItem = new SelectItem("inputType");
    selectionItem.setWidth("*");
    selectionItem.setShowTitle(false);
    selectionItem.addChangedHandler(new ChangedHandler() {
      public void onChanged(final ChangedEvent event) {
        for(InputType validInputType : validInputTypes) {
          if(validInputType.toString().equals(selectionItem.getValue().toString())) {
            setInputType(validInputType);
            break;
          }
        }
      }
    });

    final LinkedHashMap<String, String> valueMap = new LinkedHashMap<String, String>();
    for(InputType inputType : validInputTypes) {
      valueMap.put(inputType.toString(), inputType.getSelectionLabel());
    }
    selectionItem.setValueMap(valueMap);
    selectionItem.setValue(this.inputType.toString());

    final DynamicForm selectionForm = new DynamicForm();
    selectionForm.setNumCols(1);
    selectionForm.setItems(selectionItem);
    selectionForm.setHeight(20);
    selectionForm.setWidth100();
    return selectionForm;
  }

  protected void initWidget() {
    setInputType(inputType);
  }

  void setInputType(final InputType inputType) {
    this.inputType = inputType;
    this.ids = new ArrayList<String>();

    final TropixObjectType[] inputTypes = new TropixObjectType[] {
        TropixObjectTypeEnum.FOLDER,
        TropixObjectTypeEnum.VIRTUAL_FOLDER, inputType.getInputType()};

    final TreeOptions treeOptions = new TreeOptions();
    treeOptions.setInitialItems(locationFactory.getTropixObjectSourceRootItems(TropixObjectTreeItemExpanders.get(inputTypes)));
    treeOptions.setShowPredicate(inputType.getShowPredicate());
    treeOptions.setSelectionPredicate(inputType.getSelectionPredicate());
    if(inputType.getAllowMultiple()) {
      treeOptions.setSelectionType(SelectionType.MULTIPlE);
    }
    if(expand) {
      expand = false;
      treeOptions.expandAndSelectValidItems(this.initialTreeItems);
    }

    tree = this.treeComponentFactory.get(treeOptions);
    tree.addMultiSelectionListener(new Listener<Collection<TreeItem>>() {
      public void onEvent(final Collection<TreeItem> treeItems) {
        updateRunIds(treeItems);
        checkValid();
      }
    });

    final TreeGrid treeGrid = tree.get();
    SmartUtils.setWidthAndHeight100(treeGrid);
    updateTreeWidget(treeGrid);
    checkValid();
  }

  private void updateRunIds(final Collection<TreeItem> treeItems) {
    ids.clear();
    if(treeItems != null) {
      selectedTreeItems = treeItems;
      for(final TreeItem treeItem : treeItems) {
        ids.add(treeItem.getId());
      }
    } else {
      selectedTreeItems = Collections.emptyList();
    }
  }

  private void updateTreeWidget(final TreeGrid treeGrid) {
    if(this.inputTree != null) {
      treeLayout.removeMember(this.inputTree);
    }
    this.inputTree = treeGrid;
    treeLayout.addMember(this.inputTree);
  }

  protected InputType getInputType() {
    return inputType;
  }

  protected Collection<TreeItem> getSelectedItems() {
    return selectedTreeItems;
  }

  protected List<String> getIds() {
    return ids;
  }

  protected boolean isValid() {
    return optionalRun || !ids.isEmpty();
  }

  private void checkValid() {
    validationListener.onValidation(isValid());
  }

}