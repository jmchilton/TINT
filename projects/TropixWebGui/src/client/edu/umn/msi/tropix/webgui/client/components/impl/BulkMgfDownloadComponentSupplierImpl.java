package edu.umn.msi.tropix.webgui.client.components.impl;

import java.util.Collection;

import com.google.common.base.Supplier;
import com.google.gwt.user.client.Command;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.Layout;

import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.utils.ModelUtils;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.components.newwizards.RunTreeComponentImpl;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.constants.ConstantsInstances;
import edu.umn.msi.tropix.webgui.client.forms.ValidationListener;
import edu.umn.msi.tropix.webgui.client.smart.handlers.CommandClickHandlerImpl;
import edu.umn.msi.tropix.webgui.client.utils.Lists;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.GWTDownloadFormPanel;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;

public class BulkMgfDownloadComponentSupplierImpl implements Supplier<WindowComponent<Window>> {
  private final TreeComponentFactory treeComponentFactory;
  private final LocationFactory locationFactory;
  private final String mgfStyle;
  private final boolean filterITraq;

  protected BulkMgfDownloadComponentSupplierImpl(final TreeComponentFactory treeComponentFactory,
      final LocationFactory locationFactory,
      final String mgfStyle,
      final boolean filterITraq) {
    this.treeComponentFactory = treeComponentFactory;
    this.locationFactory = locationFactory;
    this.mgfStyle = mgfStyle;
    this.filterITraq = filterITraq;
  }

  private class BulkMgfDownloadComponentImpl extends WindowComponentImpl<Window> implements ValidationListener {
    private final GWTDownloadFormPanel smartDownloadFormPanel;
    private final Button downloadButton;

    private final AsyncCallbackImpl<Collection<ProteomicsRun>> getRunsCallback = new AsyncCallbackImpl<Collection<ProteomicsRun>>() {
      @Override
      protected void handleSuccess() {
        final Iterable<String> idIterable = ModelUtils.getIds(getResult());
        smartDownloadFormPanel.setFilename(ConstantsInstances.COMPONENT_INSTANCE.bulkMgfDownloadName());
        smartDownloadFormPanel.setParameter("mgfStyle", mgfStyle);
        smartDownloadFormPanel.setParameter("filterITraq", Boolean.toString(filterITraq));
        final String ids = StringUtils.join(idIterable);
        smartDownloadFormPanel.setId(ids);
        smartDownloadFormPanel.execute();
      }
    };

    // private SelectItem mgfStyleItem;

    BulkMgfDownloadComponentImpl() {
      smartDownloadFormPanel = new GWTDownloadFormPanel("mgfStyle", "filterITraq");
      smartDownloadFormPanel.setType("bulkMgf");
      downloadButton = SmartUtils.getButton(ConstantsInstances.COMPONENT_INSTANCE.bulkMgfDownload(), Resources.DOWNLOAD);
      downloadButton.setID("BulkMgfDownload_Button_Download");
      final RunTreeComponentImpl runTreeComponent = new RunTreeComponentImpl(treeComponentFactory, locationFactory, Lists.<TreeItem>newArrayList(),
          false, this) {
        @Override
        public String getTreeId() {
          return "BulkMgfDownload";
        }
      };

      final Command downloadCommand = new Command() {
        public void execute() {
          runTreeComponent.getRuns(getRunsCallback);
        }
      };
      downloadButton.setDisabled(true);
      downloadButton.addClickHandler(new CommandClickHandlerImpl(downloadCommand));

      /*
       * mgfStyleItem = new SelectItem("mgfStyle", "MGF Style");
       * final LinkedHashMap<String, String> styleMap = Maps.newLinkedHashMap();
       * styleMap.put("DEFAULT", "Standard");
       * styleMap.put("MSM", "MSM style");
       * styleMap.put("PROTEIN_PILOT", "ProtinPilot style");
       * mgfStyleItem.setValueMap(styleMap);
       * mgfStyleItem.setValue("DEFAULT");
       */
      // final Form form = new Form("BulkMgfDownload");
      // form.setWidth100();

      final Layout formLayout = SmartUtils.getFullVLayout(runTreeComponent.get());

      final CanvasWithOpsLayout<Canvas> layout = new CanvasWithOpsLayout<Canvas>(formLayout, downloadButton);
      // layout.setWidth("400px");
      // layout.setHeight("500px");
      // layout
      layout.addChild(smartDownloadFormPanel);
      this.setWidget(PopOutWindowBuilder.titled("Bulk MGF Download").sized(600, 600).withContents(layout).get());
    }

    public void onValidation(final boolean isValid) {
      downloadButton.setDisabled(!isValid);
    }
  }

  public WindowComponent<Window> get() {
    return new BulkMgfDownloadComponentImpl();
  }

}
