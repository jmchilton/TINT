package edu.umn.msi.tropix.webgui.client.components.impl;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.client.galaxy.GalaxyExportOptions;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.locations.Location;
import edu.umn.msi.tropix.models.locations.LocationPredicates;
import edu.umn.msi.tropix.models.locations.Locations;
import edu.umn.msi.tropix.models.utils.ModelUtils;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.components.newwizards.RunTreeComponentImpl;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions.SelectionType;
import edu.umn.msi.tropix.webgui.client.forms.ValidationListener;
import edu.umn.msi.tropix.webgui.client.smart.handlers.CommandClickHandlerImpl;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.Lists;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.Form;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.object.ExportService;

public class GalaxyExportComponentSupplierImpl implements Supplier<WindowComponent<Window>> {
  private final TreeComponentFactory treeComponentFactory;
  private final LocationFactory locationFactory;
  private final ExportType exportType;

  private enum ExportType {
    FILE, RAW, PEAK_LIST;
  }

  public static class GalaxyFileExportComponentSupplierImpl extends GalaxyExportComponentSupplierImpl {

    @Inject
    public GalaxyFileExportComponentSupplierImpl(final TreeComponentFactory treeComponentFactory,
        final LocationFactory locationFactory) {
      super(treeComponentFactory, locationFactory, ExportType.FILE);
    }

  }

  public static class GalaxyPeakListExportComponentSupplierImpl extends GalaxyExportComponentSupplierImpl {

    @Inject
    public GalaxyPeakListExportComponentSupplierImpl(final TreeComponentFactory treeComponentFactory,
        final LocationFactory locationFactory) {
      super(treeComponentFactory, locationFactory, ExportType.PEAK_LIST);
    }

  }

  public static class GalaxyRawExportComponentSupplierImpl extends GalaxyExportComponentSupplierImpl {

    @Inject
    public GalaxyRawExportComponentSupplierImpl(final TreeComponentFactory treeComponentFactory,
        final LocationFactory locationFactory) {
      super(treeComponentFactory, locationFactory, ExportType.RAW);
    }

  }

  protected GalaxyExportComponentSupplierImpl(final TreeComponentFactory treeComponentFactory,
      final LocationFactory locationFactory,
      final ExportType exportType) {
    this.treeComponentFactory = treeComponentFactory;
    this.locationFactory = locationFactory;
    this.exportType = exportType;
  }

  private class GalaxyPeakListExportComponentImpl extends GalaxyExportComponentImpl {
    private RunTreeComponentImpl runLocationComponent;

    protected Canvas initSelectionComponent() {
      runLocationComponent = new RunTreeComponentImpl(treeComponentFactory, locationFactory, Lists.<TreeItem>newArrayList(),
          false, this);
      return runLocationComponent.get();
    }

    protected Command getExportCallback() {
      return new Command() {
        public void execute() {
          runLocationComponent.getRuns(new AsyncCallbackImpl<Collection<ProteomicsRun>>() {
            protected void handleSuccess() {
              final GalaxyExportOptions options = getExportOptions();
              // options.getFileObjectIds().addAll(Locations.getIds(treeComponent.getMultiSelection()));
              ArrayList<String> arrayList = Lists.newArrayList();
              for(final String id : ModelUtils.getIds(getResult())) {
                arrayList.add(id);
              }
              if(exportType == ExportType.RAW) {
                ExportService.Util.getInstance().exportRawGalaxy(options, arrayList, exportCompleteCallback());
              } else if(exportType == ExportType.PEAK_LIST) {
                ExportService.Util.getInstance().exportMzXMLGalaxy(options, arrayList, exportCompleteCallback());
              }
              get().destroy();
            }
          });
        }
      };
    }
  }

  private class GalaxyFileExportComponentImpl extends GalaxyExportComponentImpl {
    private TreeComponent treeComponent;

    protected Canvas initSelectionComponent() {
      final TreeOptions treeOptions = new TreeOptions();
      treeOptions.setSelectionType(SelectionType.MULTIPlE);
      treeOptions.setInitialItems(locationFactory.getTropixObjectSourceRootItems(null));
      final Predicate<Location> locationPredicate = LocationPredicates.getTropixObjectTreeItemTypePredicate(TropixObjectTypeEnum.FILE, false);
      treeOptions.setSelectionPredicate(locationPredicate);
      treeComponent = treeComponentFactory.get(treeOptions);

      treeComponent.addMultiSelectionListener(new Listener<Collection<TreeItem>>() {
        public void onEvent(Collection<TreeItem> event) {
          onValidation(true);
        }
      });

      return treeComponent.get();
    }

    protected Command getExportCallback() {
      return new Command() {
        public void execute() {
          final GalaxyExportOptions options = getExportOptions();
          options.getFileObjectIds().addAll(Locations.getIds(treeComponent.getMultiSelection()));
          ExportService.Util.getInstance().exportGalaxy(options, exportCompleteCallback());
          get().destroy();
        }
      };
    }

  }

  private abstract class GalaxyExportComponentImpl extends WindowComponentImpl<Window> implements ValidationListener {
    private final Form form;
    private final TextItem nameItem;
    private final CheckboxItem checkboxItem;

    private final Button exportButton = SmartUtils.getButton("Export", Resources.SAVE);

    protected abstract Command getExportCallback();

    protected AsyncCallbackImpl<Void> exportCompleteCallback() {
      return new AsyncCallbackImpl<Void>() {
        protected void handleSuccess() {
          SC.say("Files exported successfully.");
        }
      };
    }

    protected GalaxyExportOptions getExportOptions() {
      final GalaxyExportOptions options = new GalaxyExportOptions();
      final String name = StringUtils.toString(nameItem.getValue());
      options.setName(name);
      options.setMakePrivate(checkboxItem.getValueAsBoolean());
      return options;
    }

    protected abstract Canvas initSelectionComponent();

    private boolean validSelection = false;

    private void validate() {
      exportButton.setDisabled(!form.isValid() || !validSelection);
    }

    public void onValidation(final boolean validSelection) {
      this.validSelection = validSelection;
      validate();
    }

    GalaxyExportComponentImpl() {
      nameItem = new TextItem("name", "Name");
      checkboxItem = new CheckboxItem("makePrivate", "Make uploaded data private?");
      form = new Form(nameItem, checkboxItem);
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
      final Canvas canvas = initSelectionComponent();
      exportButton.setDisabled(true);

      // getMultiSelectionComponent().addMultiSelectionListener(new Listener<Collection<TreeItem>>() {
      // public void onEvent(final Collection<TreeItem> event) {
      // validate();
      // }
      // });
      exportButton.addClickHandler(new CommandClickHandlerImpl(getExportCallback()));
      final VLayout layout = new VLayout();
      layout.setMembers(form, canvas);
      final CanvasWithOpsLayout<VLayout> windowLayout = new CanvasWithOpsLayout<VLayout>(layout, exportButton);
      windowLayout.setWidth("400px");
      windowLayout.setHeight("500px");
      setWidget(PopOutWindowBuilder.titled("Galaxy Export").autoSized().withContents(windowLayout).get());
    }
  }

  public WindowComponent<Window> get() {
    if(exportType == ExportType.FILE) {
      return new GalaxyFileExportComponentImpl();
    } else if(exportType == ExportType.PEAK_LIST || exportType == ExportType.RAW) {
      return new GalaxyPeakListExportComponentImpl();
    } else {
      throw new IllegalStateException("Unknown export type");
    }
  }

}
