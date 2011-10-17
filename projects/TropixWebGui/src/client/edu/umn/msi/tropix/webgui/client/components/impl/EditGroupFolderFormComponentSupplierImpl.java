package edu.umn.msi.tropix.webgui.client.components.impl;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;
import com.smartgwt.client.widgets.form.fields.CanvasItem;
import com.smartgwt.client.widgets.form.fields.TextItem;

import edu.umn.msi.tropix.webgui.client.components.EditGroupFolderFormComponent;
import edu.umn.msi.tropix.webgui.client.components.GridUserItemComponent;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.Form;
import edu.umn.msi.tropix.webgui.client.widgets.WidgetSupplierImpl;

public class EditGroupFolderFormComponentSupplierImpl implements Supplier<EditGroupFolderFormComponent> {
  private Supplier<GridUserItemComponent> gridUserItemComponentSupplier;

  public EditGroupFolderFormComponent get() {
    return new EditGroupFolderFormComponentImpl(gridUserItemComponentSupplier.get());
  }

  @Inject
  public void setGridUserItemComponentSupplier(final Supplier<GridUserItemComponent> gridUserItemComponentSupplier) {
    this.gridUserItemComponentSupplier = gridUserItemComponentSupplier;
  }

  private static class EditGroupFolderFormComponentImpl extends WidgetSupplierImpl<Form> implements EditGroupFolderFormComponent {
    private TextItem nameItem;
    private CanvasItem ownerItem;
    private GridUserItemComponent gridUserItemComponent;
    private Form form;

    EditGroupFolderFormComponentImpl(final GridUserItemComponent gridUserItemComponent) {
      this.gridUserItemComponent = gridUserItemComponent;
      nameItem = new TextItem("name", "Name");
      ownerItem = gridUserItemComponent.get();
      setWidget(new Form(nameItem, ownerItem));
      get().setValidationPredicate(new Predicate<Form>() {
        public boolean apply(Form input) {
          return StringUtils.hasText(input.getValue("name"));
        }
      });
    }

    public boolean validate() {
      return StringUtils.hasText(nameItem.getValue());// && StringUtils.hasText(gridUserItemComponent.getSelectedUserId());
    }

    public String getName() {
      return StringUtils.toString(nameItem.getValue());
    }

    public String getUserId() {
      return gridUserItemComponent.getSelectedUserId();
    }

    public void addChangedListener(final Listener<EditGroupFolderFormComponent> listener) {
      final EditGroupFolderFormComponent component = this;
      form.addItemChangedHandler(new ItemChangedHandler() {
        public void onItemChanged(final ItemChangedEvent event) {
          listener.onEvent(component);
        }
      });
    }

  }
}
