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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.events.ItemChangedEvent;
import com.smartgwt.client.widgets.form.events.ItemChangedHandler;
import com.smartgwt.client.widgets.form.fields.FormItem;

import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.ListenerList;
import edu.umn.msi.tropix.webgui.client.utils.ListenerLists;

/**
 * Extends Smart's Dynamic Form to provide a common look and feel and extended functionality including a validation concept the isn't as tied to Smart or DataSources and instead makes use of the {@code Listener} and {@code Predicate} interface.
 * 
 * @author John Chilton
 * 
 */
public class Form extends DynamicForm implements HasValidation {
  private final ListenerList<Boolean> validationListeners = ListenerLists.getInstance();
  private Boolean clientValid = false;
  private FormItem[] items;
  
  @Override
  public void setItems(final FormItem... items) {
    this.items = items;
    super.setItems(items);
  }
  
  /**
   * Resulting list is unmodifiable.
   * 
   * @return List of FormItems 
   */  
  public List<FormItem> getItems() {
    return Collections.unmodifiableList(Arrays.asList(items));
  }
  
  public Form() {
    super();
    init(null);
  }

  public Form(final FormItem... items) {
    super();
    init(items);
  }

  private void init(@Nullable final FormItem[] items) {
    setCellSpacing(5);
    if(items != null) {
      setItems(items);
    }
  }

  public void setValidationPredicate(final Predicate<Form> predicate) {
    clientValid = predicate.apply(this);
    this.addItemChangedHandler(new ItemChangedHandler() {
      public void onItemChanged(final ItemChangedEvent event) {
        final boolean clientValid = predicate.apply(Form.this);
        if(clientValid != Form.this.clientValid) {
          Form.this.clientValid = clientValid;
          validationListeners.onEvent(clientValid);
        }
      }
    });
  }

  public void addValidationListener(final Listener<Boolean> validationListener) {
    validationListeners.add(validationListener);
  }

  public boolean isValid() {
    return clientValid;
  }

}
