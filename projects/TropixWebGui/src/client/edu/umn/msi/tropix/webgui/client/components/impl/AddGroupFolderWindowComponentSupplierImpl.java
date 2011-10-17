package edu.umn.msi.tropix.webgui.client.components.impl;

import com.allen_sauer.gwt.log.client.Log;
import com.google.common.base.Supplier;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.EditGroupFolderFormComponent;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.Form;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.object.FolderService;

// TODO: Remove code duplication between this and EditCatalogProviderFormComponentSupplierImpl
public class AddGroupFolderWindowComponentSupplierImpl implements Supplier<WindowComponent<Window>> {
  private Supplier<EditGroupFolderFormComponent> editGroupFolderFormComponentSupplier;
  private static LocationUpdateMediator locationUpdateMediator = LocationUpdateMediator.getInstance();

  // private Log log = Log.getLogger();

  @Inject
  public void setEditCatalogProviderFormComponentSupplier(final Supplier<EditGroupFolderFormComponent> editGroupFolderFormComponentSupplier) {
    this.editGroupFolderFormComponentSupplier = editGroupFolderFormComponentSupplier;
  }

  public WindowComponent<Window> get() {
    return new AddGroupFolderWindowComponent(editGroupFolderFormComponentSupplier.get());
  }

  private static class AddGroupFolderWindowComponent extends WindowComponentImpl<Window> {
    AddGroupFolderWindowComponent(final EditGroupFolderFormComponent editGroupFolderFormComponent) {
      final Form form = editGroupFolderFormComponent.get();
      final Button createButton = SmartUtils.getButton("Create", Resources.ADD);
      createButton.setDisabled(true);
      createButton.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          // Log.info("In onClick");
          final Folder folder = new Folder();
          final String name = editGroupFolderFormComponent.getName();
          folder.setName(name);
          folder.setCommitted(true);
          Log.info("Have name " + name);
          final String userId = editGroupFolderFormComponent.getUserId();
          Log.info("Have userId " + userId);
          FolderService.Util.getInstance().createGroupFolder(folder, userId, new AsyncCallbackImpl<Folder>(get()));
          locationUpdateMediator.onEvent(new LocationUpdateMediator.AddUpdateEvent("groupFolder", null, ""));
        }
      });
      SmartUtils.enabledWhenValid(createButton, form);
      this.setWidget(PopOutWindowBuilder.titled("Create New Group Folder").sized(320, 240)
          .withContents(new CanvasWithOpsLayout<Form>(form, createButton)).get());
    }
  }

}
