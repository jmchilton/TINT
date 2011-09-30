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

package edu.umn.msi.tropix.webgui.client.components.impl;

import java.util.Collection;

import com.google.common.base.Supplier;
import com.google.gwt.user.client.Command;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.Layout;

import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.MultiSelectionComponent;
import edu.umn.msi.tropix.webgui.client.components.MultiSelectionWindowComponent;
import edu.umn.msi.tropix.webgui.client.components.SelectionComponent;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;

public class SelectionWindowComponentSupplierImpl<S> implements Supplier<MultiSelectionWindowComponent<S, Window>> {
  private Supplier<? extends SelectionComponent<S, ? extends Canvas>> selectionComponentSupplier;
  private Supplier<Window> windowSupplier;
  private boolean nullable = true;

  class SelectionWindowComponentImpl extends WindowComponentImpl<Window> implements MultiSelectionWindowComponent<S, Window> {
    private final SelectionComponent<S, ? extends Canvas> selectionComponent;
    private Listener<S> selectionCallback;
    private Listener<Collection<S>> multiSelectionCallback;

    SelectionWindowComponentImpl() {
      final Button okButton = SmartUtils.getButton("Select", Resources.OK, new Command() {
        public void execute() {
          if(selectionCallback != null) {
            final S selection = selectionComponent.getSelection();
            selectionCallback.onEvent(selection);
          } 
          if(multiSelectionCallback != null) {
            final Collection<S> selection = ((MultiSelectionComponent<S, ? extends Canvas>) selectionComponent).getMultiSelection();
            multiSelectionCallback.onEvent(selection);           
          }
          get().destroy();
        }        
      });
      final Button cancelButton = SmartUtils.getCancelButton(this);      
      this.setWidget(windowSupplier.get());
      this.selectionComponent = selectionComponentSupplier.get();
      if(!nullable) {        
        this.selectionComponent.addSelectionListener(new Listener<S>() {
          public void onEvent(final S event) {
            okButton.setDisabled(selectionComponent.getSelection() == null);
          }
        });
      }
      @SuppressWarnings("unchecked")
      final Layout layout = new CanvasWithOpsLayout(selectionComponent.get(), okButton, cancelButton);
      this.get().addItem(layout);
    }

    public void setSelectionCallback(final Listener<S> selectionCallback) {
      this.selectionCallback = selectionCallback;
    }

    public void setMultiSelectionCallback(final Listener<Collection<S>> selectionCallback) {
      this.multiSelectionCallback = selectionCallback;
    }

  }

  public MultiSelectionWindowComponent<S, Window> get() {
    return new SelectionWindowComponentImpl();
  }

  public void setSelectionComponentSupplier(final Supplier<? extends SelectionComponent<S, ? extends Canvas>> selectionComponentSupplier) {
    this.selectionComponentSupplier = selectionComponentSupplier;
  }

  public void setWindowSupplier(final Supplier<Window> windowSupplier) {
    this.windowSupplier = windowSupplier;
  }

  public void setNullable(final boolean nullable) {
    this.nullable = nullable;
  }
}
