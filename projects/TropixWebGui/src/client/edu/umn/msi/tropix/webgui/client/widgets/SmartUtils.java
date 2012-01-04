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

package edu.umn.msi.tropix.webgui.client.widgets;

import javax.annotation.Nullable;

import com.google.common.base.Supplier;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Command;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.DataSourceField;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.FieldType;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.BaseWidget;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.HTMLPane;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.CloseClickHandler;
import com.smartgwt.client.widgets.events.CloseClientEvent;
import com.smartgwt.client.widgets.events.HasClickHandlers;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangeHandler;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.form.fields.events.HasChangedHandlers;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.HasSelectionChangedHandlers;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.menu.MenuItem;
import com.smartgwt.client.widgets.menu.MenuItemIfFunction;
import com.smartgwt.client.widgets.menu.events.MenuItemClickEvent;

import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.SelectionComponent;
import edu.umn.msi.tropix.webgui.client.smart.handlers.CommandClickHandlerImpl;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.Property;

public class SmartUtils {
  private static boolean useIcons = false;
  private static boolean autoWidthIcons = false;

  public static <T> HandlerRegistration bindProperty(final Property<T> property, final HasChangedHandlers hasChangedHandler) {
    return hasChangedHandler.addChangedHandler(new ChangedHandler() {
      public void onChanged(final ChangedEvent event) {
        @SuppressWarnings("unchecked")
        final T value = (T) event.getValue();
        property.set(value);
      }
    });
  }

  public static <T> HandlerRegistration addListener(final HasChangedHandlers hasChangedHandlers, final Listener<T> listener) {
    return hasChangedHandlers.addChangedHandler(new ChangedHandler() {
      public void onChanged(final ChangedEvent event) {
        @SuppressWarnings("unchecked")
        final T value = (T) event.getValue();
        listener.onEvent(value);
      }
    });
  }

  // TODO: Refactor all code to use this if possible
  public static void enabledWhenHasSelection(final Canvas conditionalWidget, final HasSelectionChangedHandlers selectionWidget) {
    enabledWhenHasSelection(conditionalWidget, selectionWidget, null);
  }

  public static void enabledWhenHasSelection(final Canvas conditionalWidget, final HasSelectionChangedHandlers selectionWidget,
      final Boolean selectedByDefault) {
    selectionWidget.addSelectionChangedHandler(new SelectionChangedHandler() {
      public void onSelectionChanged(final SelectionEvent event) {
        conditionalWidget.setDisabled(!event.getState());
      }
    });
    if(selectedByDefault != null) {
      conditionalWidget.setDisabled(!selectedByDefault);
    }
  }

  public static void enabledWhenValid(final Canvas conditionalWidget, final HasValidation form) {
    form.addValidationListener(new Listener<Boolean>() {
      public void onEvent(final Boolean isValid) {
        conditionalWidget.setDisabled(!isValid);
      }
    });
    conditionalWidget.setDisabled(!form.isValid());
  }

  public static Canvas getFiller() {
    final Canvas canvas = new Canvas();
    canvas.setSize("*", "*");
    return canvas;
  }

  public interface Cleanable {
    /**
     * Should remove any GUI modification corresponding to the object implementing this interface.
     */
    void close();
  }

  public static VLayout getFullVLayout() {
    final VLayout layout = new VLayout();
    setWidthAndHeight100(layout);
    return layout;
  }

  public static VLayout getFullVLayout(final Canvas... widgets) {
    final VLayout layout = getFullVLayout();
    for(Canvas widget : widgets) {
      layout.addMember(widget);
    }
    return layout;
  }

  public static <T extends Canvas> T setWidthAndHeight100(final T canvas) {
    canvas.setWidth100();
    canvas.setHeight100();
    return canvas;
  }

  public static Layout getLoadingCanvas() {
    final Layout layout = getFullVLayout();
    indicateLoading(layout);
    return layout;
  }

  public static Cleanable indicateLoading(final Layout canvas) {
    final HStack ihStack = new HStack();
    // ihStack.setWidth100();
    // ihStack.setHeight100();
    ihStack.setSize("140", "50");

    ihStack.setBackgroundColor("white");
    ihStack.setBorder("1px solid #AAA");
    ihStack.setLayoutAlign(Alignment.CENTER);

    final VStack imageOutline = new VStack();
    imageOutline.setWidth100();
    imageOutline.setHeight("40");

    imageOutline.setLayoutAlign(Alignment.CENTER);

    final Label loadingText = new Label("<span style=\"color: #444;\">L o a d i n g . . .</span>");
    loadingText.setOverflow(Overflow.VISIBLE);
    loadingText.setHeight(14);
    loadingText.setWidth(140);
    loadingText.setAlign(Alignment.CENTER);
    loadingText.setLayoutAlign(Alignment.CENTER);
    imageOutline.addMember(loadingText);

    final Img image = new Img();
    image.setSize("128", "15");
    image.setLayoutAlign(Alignment.CENTER);
    image.setSrc(GWT.getHostPageBaseURL() + "images/loading_bar.gif");
    // imageOutline.addMember(image);
    HTMLPane html = new HTMLPane();
    html.setSize("128", "15");
    html.setLayoutAlign(Alignment.CENTER);
    html.setContents("<img src=\"" + GWT.getHostPageBaseURL() + "images/loading_bar.gif\" />");
    imageOutline.addMember(html);
    ihStack.addMember(imageOutline);

    final HStack hStack = new HStack();
    hStack.setHeight100();
    hStack.setWidth100();
    final VStack vLayout = new VStack();
    vLayout.setLayoutAlign(Alignment.CENTER);

    vLayout.setWidth100();
    vLayout.setHeight(50);
    vLayout.addMember(ihStack);

    hStack.addMember(vLayout);
    for(Canvas child : canvas.getMembers()) {
      child.setOpacity(30);
    }
    for(Canvas child : canvas.getChildren()) {
      child.setOpacity(30);
    }
    canvas.addChild(hStack);
    return new Cleanable() {
      public void close() {
        canvas.removeChild(hStack);
        for(Canvas child : canvas.getMembers()) {
          child.setOpacity(100);
        }
        for(Canvas child : canvas.getChildren()) {
          child.setOpacity(100);
        }
      }
    };
  }

  public interface ConditionalWidgetSupplier extends Supplier<Layout> {
    boolean useSelection();
  }

  public static String getTitle(final String text) {
    return text.replace(" ", "_");
  }

  public static void markDisabled(final Canvas canvas) {
    canvas.setOpacity(30);
    canvas.disable();
  }

  public static void unmarkDisabled(final Canvas canvas) {
    canvas.setOpacity(100);
    canvas.enable();
  }

  private static class ConditionalWidgetSupplierImpl<S, T extends Canvas> extends WidgetSupplierImpl<Layout> implements ConditionalWidgetSupplier {
    private boolean useSelection;

    ConditionalWidgetSupplierImpl(final String text, final SelectionComponent<S, T> widget, final Listener<Boolean> validationListener,
        final boolean enabled) {
      final CheckboxItem selectItem = new CheckboxItem(getTitle(text), text);
      final Form form = new Form(selectItem);
      form.setNumCols(1);
      form.setAlign(Alignment.LEFT);
      form.setWidth100();

      final Canvas selectionCanvas = widget.get();
      if(enabled) {
        useSelection = true;
        selectItem.setValue(true);
      } else {
        useSelection = false;
        markDisabled(selectionCanvas);
      }
      selectItem.addChangeHandler(new ChangeHandler() {
        public void onChange(final ChangeEvent event) {
          if((Boolean) event.getValue()) {
            useSelection = true;
            unmarkDisabled(selectionCanvas);
            validationListener.onEvent(widget.getSelection() != null);
          } else {
            useSelection = false;
            markDisabled(selectionCanvas);
            validationListener.onEvent(true);
          }
        }
      });
      widget.addSelectionListener(new Listener<S>() {
        public void onEvent(final S event) {
          validationListener.onEvent(!useSelection || event != null);
        }
      });
      final VLayout vLayout = new VLayout();
      vLayout.addMember(form);
      vLayout.addMember(selectionCanvas);
      setWidget(vLayout);
    }

    public boolean useSelection() {
      return useSelection;
    }
  }

  public static <S, T extends Canvas> ConditionalWidgetSupplier getConditionalSelectionWidget(final String text,
      final SelectionComponent<S, T> widget, final Listener<Boolean> validationListener) {
    return getConditionalSelectionWidget(text, widget, validationListener, false);
  }

  public static <S, T extends Canvas> ConditionalWidgetSupplier getConditionalSelectionWidget(final String text,
      final SelectionComponent<S, T> widget, final Listener<Boolean> validationListener, final boolean enabled) {
    return new ConditionalWidgetSupplierImpl<S, T>(text, widget, validationListener, enabled);
  }

  /**
   * 
   * @param text
   *          Text of label
   * @return A label whose height is only large enough to display {@code text}.
   */
  public static Label smartParagraph(final String text) {
    final Label label = new Label(text);
    label.setWidth100();
    label.setOverflow(Overflow.VISIBLE);
    label.setHeight(1);
    return label;
  }

  public static Button getCancelButton() {
    return getButton("Cancel", Resources.CROSS, null, null);
  }

  public static <T extends Supplier<? extends Canvas>> Button getCancelButton(final T canvasToDestroy) {
    final Button cancelButton = getCancelButton();
    SmartUtils.destoryOnClick(canvasToDestroy, cancelButton);
    return cancelButton;
  }

  /**
   * Common button types.
   * 
   */
  public static enum ButtonType {
    CREATE("Create", Resources.ADD), SAVE("Save", Resources.SAVE), ADD("Add", Resources.ADD), EDIT("Edit", Resources.EDIT), REMOVE("Remove",
        Resources.CROSS), OK("Ok", Resources.OK);
    private final String title, icon;

    private ButtonType(final String title, final String icon) {
      this.title = title;
      this.icon = icon;
    }

  };

  public static Button getButton(final ButtonType buttonType, @Nullable final Command command) {
    return getButton(buttonType.title, buttonType.icon, command);
  }

  public static Button getButton(final String title, @Nullable final String icon) {
    return getButton(title, icon, null, null);
  }

  public static Button getButton(final String title) {
    return getButton(title, (Command) null);
  }

  public static Button getButton(final String title, @Nullable final Command command) {
    return getButton(title, (String) null, command);
  }

  public static Button getButton(final String title, @Nullable final String icon, @Nullable final Command command) {
    return getButton(title, icon, command, null);
  }

  public static Button getButton(final String title, @Nullable final String icon, @Nullable final Integer width) {
    return getButton(title, icon, null, width);
  }

  public static Button getButton(final String title, @Nullable final String icon, @Nullable final Command command, @Nullable final Integer width) {
    final Button button = new Button();
    button.setTitle(title + " ");
    if(useIcons && icon != null) {
      button.setIcon(icon);
      button.setShowDisabledIcon(false);
    }
    if(autoWidthIcons) {
      button.setAutoFit(true);
    } else if(width != null) {
      button.setWidth(width);
    }
    if(command != null) {
      button.addClickHandler(new CommandClickHandlerImpl(command));
    }
    return button;
  }

  public static void destoryOnClick(final Canvas canvas, final HasClickHandlers button) {
    button.addClickHandler(new ClickHandler() {
      public void onClick(final ClickEvent event) {
        canvas.destroy();
      }
    });
  }

  public static <T extends Supplier<? extends Canvas>> void destoryOnClick(final T canvasSupplier, final HasClickHandlers button) {
    button.addClickHandler(new ClickHandler() {
      public void onClick(final ClickEvent event) {
        canvasSupplier.get().destroy();
      }
    });
  }

  public static void destroyOnClose(final Window window) {
    window.addCloseClickHandler(new CloseClickHandler() {
      public void onCloseClick(final CloseClientEvent event) {
        window.markForDestroy();
      }
    });
  }

  public static void removeAllRecords(final ListGrid listGrid) {
    final ListGridRecord[] records = listGrid.getRecords();
    for(final ListGridRecord record : records) {
      listGrid.removeData(record);
    }
  }

  public static void removeAllMembers(final Layout layout) {
    final Canvas[] members = layout.getMembers();
    if(members != null && members.length > 0) {
      layout.removeMembers(members);
    }
  }

  public static void removeAndDestroyAllMembers(final Layout layout) {
    final Canvas[] members = layout.getMembers();
    if(members != null && members.length > 0) {
      layout.removeMembers(members);
      for(final Canvas canvas : members) {
        canvas.destroy();
      }
    }
  }

  public static void addRecords(final ListGrid listGrid, final Iterable<? extends Record> records) {
    for(final Record record : records) {
      listGrid.addData(record);
    }
  }

  public static <T> Listener<T> onEventDestroy(final Supplier<? extends BaseWidget> widgetSupplier) {
    return new Listener<T>() {
      public void onEvent(final T event) {
        widgetSupplier.get().destroy();
      }
    };
  }

  public static MenuItem getMenuItem(final String text, @Nullable final String icon, @Nullable final Command clickCommand) {
    return getMenuItem(text, icon, clickCommand, null);
  }

  public static MenuItem getMenuItem(final String text, @Nullable final String icon, @Nullable final Command clickCommand,
      @Nullable final MenuItemIfFunction menuItemIfFunction) {
    final MenuItem menuItem = new MenuItem(text);
    if(icon != null) {
      menuItem.setIcon(icon);
    }
    if(clickCommand != null) {
      menuItem.addClickHandler(new com.smartgwt.client.widgets.menu.events.ClickHandler() {
        public void onClick(final MenuItemClickEvent event) {
          clickCommand.execute();
        }
      });
    }
    if(menuItemIfFunction != null) {
      menuItem.setEnableIfCondition(menuItemIfFunction);
    }
    return menuItem;
  }

  public static DataSourceItemBuilder getFieldBuilder(final String name) {
    return new DataSourceItemBuilder(name, name);
  }

  public static DataSourceItemBuilder getFieldBuilder(final String name, final String title) {
    return new DataSourceItemBuilder(name, title);
  }

  public static DataSourceField getHiddenIdField() {
    return getFieldBuilder("id").hidden().primary().get();
  }

  public static DataSource newDataSourceWithFields(final DataSourceField... fields) {
    final DataSource dataSource = new DataSource();
    dataSource.setFields(fields);
    return dataSource;
  }

  public static class DataSourceItemBuilder implements Supplier<DataSourceField> {
    private String name;
    private String title;
    private boolean canEdit = false, primary = false, hidden = false;
    private FieldType type = FieldType.TEXT;
    private String width = null;

    public DataSourceItemBuilder ofType(final FieldType fieldType) {
      this.type = fieldType;
      return this;
    }

    public DataSourceItemBuilder list() {
      return ofType(FieldType.ENUM);
    }

    public DataSourceItemBuilder checkbox() {
      return ofType(FieldType.BOOLEAN);
    }

    public DataSourceItemBuilder image() {
      return ofType(FieldType.IMAGE);
    }

    public DataSourceItemBuilder editable() {
      this.canEdit = true;
      return this;
    }

    public DataSourceItemBuilder hidden() {
      this.hidden = true;
      return this;
    }

    public DataSourceItemBuilder primary() {
      this.primary = true;
      return this;
    }

    public DataSourceItemBuilder withTitle(final String title) {
      this.title = title;
      return this;
    }

    public DataSourceItemBuilder(final String name, final String title) {
      this.name = name;
      this.title = title;
    }

    public DataSourceField get() {
      DataSourceField field;
      if(title != null) {
        field = new DataSourceField(name, type, title);
      } else {
        field = new DataSourceField(name, type);
      }
      if(type == FieldType.IMAGE) {
        field.setAttribute("imageURLPrefix", GWT.getHostPageBaseURL());
        field.setAttribute("imageURLSuffix", "");
      }
      field.setCanEdit(canEdit);
      field.setPrimaryKey(primary);
      field.setHidden(hidden);
      if(width != null) {
        field.setAttribute("width", width);
      }
      return field;
    }

    public DataSourceItemBuilder withWidth(final int width) {
      this.width = Integer.toString(width);
      return this;
    }

    public DataSourceItemBuilder withWidth(final String width) {
      this.width = width;
      return this;
    }
  }

}
