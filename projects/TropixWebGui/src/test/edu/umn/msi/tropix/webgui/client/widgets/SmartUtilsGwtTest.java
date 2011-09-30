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

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.fields.CheckboxItem;
import com.smartgwt.client.widgets.grid.events.HasSelectionChangedHandlers;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.webgui.client.SmartTestUtils;
import edu.umn.msi.tropix.webgui.client.TropixGwtTestCase;
import edu.umn.msi.tropix.webgui.client.components.SelectionComponent;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.ListenerList;
import edu.umn.msi.tropix.webgui.client.utils.ListenerLists;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils.Cleanable;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils.ConditionalWidgetSupplier;

public class SmartUtilsGwtTest extends TropixGwtTestCase {

  private class HandlerRegistrationImpl implements HandlerRegistration {
    private boolean removed = false;
    public void removeHandler() {   
      removed = true;
    }    
  }
  
  private class HasSelectionChangedHandlersImpl implements HasSelectionChangedHandlers {
    private SelectionChangedHandler selectionChangedHandler;
    private HandlerRegistrationImpl registration = new HandlerRegistrationImpl();
    
    public HandlerRegistration addSelectionChangedHandler(final SelectionChangedHandler handler) {
      selectionChangedHandler = handler;
      return registration;
    }

    public void fireEvent(final GwtEvent<?> event) {  
    }
    
  }
  
  public void testEnabledWhenHasSelection() {
    final VLayout layout = new VLayout();
    final HasSelectionChangedHandlersImpl changedHandler = new HasSelectionChangedHandlersImpl();

    SmartUtils.enabledWhenHasSelection(layout, changedHandler, false);
    assert layout.isDisabled();
    final JavaScriptObject object = JavaScriptObject.createObject();
    JSOHelper.setAttribute(object, "state", true);
    changedHandler.selectionChangedHandler.onSelectionChanged(new SelectionEvent(object));
    assert !layout.isDisabled();
  }
  
  public void testIndicateLoading() {
    final VLayout layout = new VLayout();
    final Label label = new Label("Label");

    layout.addMember(label);
    assert label.getOpacity() == null;
    final Cleanable cleanable = SmartUtils.indicateLoading(layout);
    assert label.getOpacity() < 100;
    cleanable.close();
    assert label.getOpacity() == null;
  }
  
  class SelectionComponentImpl implements SelectionComponent<String, VLayout> {
    private ListenerList<String> list = ListenerLists.getInstance();
    private final VLayout layout = new VLayout();
    private String selection = "default";
    
    public void addSelectionListener(final Listener<String> listener) {
      list.add(listener);
    }

    public String getSelection() {
      return selection;
    }

    public void removeSelectionListener(final Listener<String> listener) {
      list.remove(listener);
    }

    public VLayout get() {
      return layout;
    }
    
  }
  
  class ValidationListenerImpl implements Listener<Boolean> {
    private Boolean isValid;
    
    public void onEvent(final Boolean event) {
      this.isValid = event;
    }
    
  }
  
  final Form findForm(final Canvas[] children) {
    if(children != null) {
      for(Canvas child : children) {
        if(child instanceof Form) {
          return (Form) child;
        } else {
          final Form childForm = findForm(child);
          if(childForm != null) {
            return childForm;
          }
        }
      }
    }
    return null;
  }
  
  final Form findForm(final Canvas canvas) {
    final Form childForm = findForm(canvas.getChildren());
    if(childForm != null) {
      return childForm;
    }
    if(canvas instanceof Layout) {
      final Form memberForm = findForm(((Layout) canvas).getMembers());
      if(memberForm != null) {
        return memberForm;
      }
    }
    return null;
  }
  
  public void testConditionalWidgetSupplier() {
    final String text = "Use Widget";
    final SelectionComponentImpl selectionComponent = new SelectionComponentImpl();
    selectionComponent.selection = null;
    final ValidationListenerImpl validationListener = new ValidationListenerImpl();
    final ConditionalWidgetSupplier cWidgetSupplier = SmartUtils.getConditionalSelectionWidget(text, selectionComponent, validationListener, false);    
    assert selectionComponent.layout.isDisabled();
    final Form form = findForm(cWidgetSupplier.get());
    assert form != null;
    final CheckboxItem item = (CheckboxItem) form.getItems().get(0);
    SmartTestUtils.changeValue(item, true);
    assert !selectionComponent.layout.isDisabled();
    assert cWidgetSupplier.useSelection();
    assert !validationListener.isValid; // Selection is null
    
    SmartTestUtils.changeValue(item, false);
    assert selectionComponent.layout.isDisabled();
    assert !cWidgetSupplier.useSelection();
    assert validationListener.isValid; // Selection is null, but no selection is needed
    
    selectionComponent.selection = "Default";
    SmartTestUtils.changeValue(item, true);
    assert !selectionComponent.layout.isDisabled();
    assert cWidgetSupplier.useSelection();
    assert validationListener.isValid; // Selection is no longer null

  }

}
