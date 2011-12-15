package edu.umn.msi.tropix.webgui.client.components.impl;

import com.google.common.base.Supplier;
import com.google.gwt.user.client.Command;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.layout.SectionStack;
import com.smartgwt.client.widgets.layout.SectionStackSection;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.components.GridUserSelectionComponent;
import edu.umn.msi.tropix.webgui.client.components.SelectionWindowComponent;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.session.GroupService;

public class UserAdminWindowComponentSupplierImpl implements Supplier<WindowComponent<Window>> {
  private GridUserSelectionComponentSupplierImpl gridUserSelectionComponentSupplier;
  private Supplier<? extends SelectionWindowComponent<Group, ? extends Window>> groupSelectionWindowComponentSupplier;

  @Inject
  public void setGroupSelectionWindowComponentSupplier(
      final Supplier<? extends SelectionWindowComponent<Group, ? extends Window>> groupSelectionWindowComponentSupplier) {
    this.groupSelectionWindowComponentSupplier = groupSelectionWindowComponentSupplier;
  }

  @Inject
  public UserAdminWindowComponentSupplierImpl(final GridUserSelectionComponentSupplierImpl component) {
    this.gridUserSelectionComponentSupplier = component;
  }

  public WindowComponent<Window> get() {
    return new UserAdminWindowComponentImpl();
  }

  class UserAdminWindowComponentImpl extends WindowComponentImpl<Window> {

    UserAdminWindowComponentImpl() {
      this.setWidget(PopOutWindowBuilder.titled("User Admin").sized(600, 500).withContents(getContents()).get());
    }

    private Canvas getContents() {
      final SectionStack sectionStack = new SectionStack();
      sectionStack.addSection(getEditUserSection());
      return sectionStack;
    }

    private SectionStackSection getEditUserSection() {
      final SectionStackSection section = new SectionStackSection("Edit Users");
      final VLayout vLayout = SmartUtils.getFullVLayout();
      final GridUserSelectionComponent gridUserSelectionComponent = gridUserSelectionComponentSupplier.get();
      ListGrid selectionGrid = gridUserSelectionComponent.get();
      final Button setGroupButton = SmartUtils.getButton("Set Primary Group", new Command() {
        public void execute() {
          final SelectionWindowComponent<Group, ? extends Window> groupSelectionComponent = groupSelectionWindowComponentSupplier.get();
          groupSelectionComponent.setSelectionCallback(new Listener<Group>() {
            public void onEvent(final Group group) {
              GroupService.Util.getInstance().setPrimaryGroup(gridUserSelectionComponent.getSelection().getGridId(), group.getId(),
                  new AsyncCallbackImpl<Void>());
            }
          });
          groupSelectionComponent.execute();
        }
      });
      SmartUtils.enabledWhenHasSelection(setGroupButton, selectionGrid);
      vLayout.addMember(new CanvasWithOpsLayout<Canvas>(selectionGrid, setGroupButton));
      section.addItem(vLayout);
      return section;
    }

  }

}
