package edu.umn.msi.tropix.webgui.client.components.impl;

import java.util.Collection;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.client.galaxy.GalaxyExportOptions;
import edu.umn.msi.tropix.models.locations.LocationPredicates;
import edu.umn.msi.tropix.models.locations.Locations;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions.SelectionType;
import edu.umn.msi.tropix.webgui.client.smart.handlers.CommandClickHandlerImpl;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.Form;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.object.ExportService;

public class GalaxyExportComponentSupplierImpl implements Supplier<WindowComponent<Window>> {
  private final TreeComponentFactory treeComponentFactory;
  private final LocationFactory locationFactory;

  @Inject
  public GalaxyExportComponentSupplierImpl(final TreeComponentFactory treeComponentFactory,
      final LocationFactory locationFactory) {
    this.treeComponentFactory = treeComponentFactory;
    this.locationFactory = locationFactory;
  }

  private class GalaxyExportComponentImpl extends WindowComponentImpl<Window> {
    private final Form form;
    private final TreeComponent treeComponent;
    private final Button exportButton = SmartUtils.getButton("Export", Resources.SAVE);

    private void validate() {
      exportButton.setDisabled(!form.isValid() || treeComponent.getMultiSelection().isEmpty());
    }

    GalaxyExportComponentImpl() {
      final TextItem nameItem = new TextItem("name", "Name");
      form = new Form(nameItem);
      form.setValidationPredicate(new Predicate<Form>() {
        public boolean apply(final Form form) {
          return StringUtils.hasText(form.getValueAsString("name"));
        }
      });
      form.addItemChangedHandler(new ItemChangedHandler() {
        public void onItemChanged(final ItemChangedEvent event) {
          validate();
        }
      });

      exportButton.setDisabled(true);

      final TreeOptions treeOptions = new TreeOptions();
      treeOptions.setSelectionType(SelectionType.MULTIPlE);
      treeOptions.setInitialItems(locationFactory.getTropixObjectSourceRootItems(null));
      treeOptions.setSelectionPredicate(LocationPredicates.getTropixObjectTreeItemTypePredicate(TropixObjectTypeEnum.FILE, false));
      treeComponent = treeComponentFactory.get(treeOptions);
      final Command exportCommand = new Command() {
        public void execute() {
          final GalaxyExportOptions options = new GalaxyExportOptions();
          options.setName(StringUtils.toString(nameItem.getValue()));
          options.getFileObjectIds().addAll(Locations.getIds(treeComponent.getMultiSelection()));
          ExportService.Util.getInstance().exportGalaxy(options,
              new AsyncCallbackImpl<Void>() {
                protected void handleSuccess() {
                  SC.say("Files exported successfully.");
                }
              });
          get().destroy();
        }
      };
      treeComponent.addMultiSelectionListener(new Listener<Collection<TreeItem>>() {
        public void onEvent(final Collection<TreeItem> event) {
          validate();
        }
      });
      exportButton.addClickHandler(new CommandClickHandlerImpl(exportCommand));
      final VLayout layout = new VLayout();
      layout.setMembers(form, treeComponent.get());
      final CanvasWithOpsLayout<VLayout> windowLayout = new CanvasWithOpsLayout<VLayout>(layout, exportButton);
      windowLayout.setWidth("400px");
      windowLayout.setHeight("500px");
      setWidget(PopOutWindowBuilder.titled("Galaxy Export").autoSized().withContents(windowLayout).get());
    }
  }

  public WindowComponent<Window> get() {
    return new GalaxyExportComponentImpl();
  }

}
