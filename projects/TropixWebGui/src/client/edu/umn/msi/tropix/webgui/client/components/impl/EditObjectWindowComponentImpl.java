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

import com.google.gwt.user.client.Command;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;

import edu.umn.msi.tropix.webgui.client.components.EditObjectComponent;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils.ButtonType;

public class EditObjectWindowComponentImpl<T> extends WindowComponentImpl<Window> {
  private Listener<T> callback;
  
  public EditObjectWindowComponentImpl(final EditObjectComponent<? extends Canvas, T> component, final PopOutWindowBuilder windowBuilder, final ButtonType buttonType) {
    final Button button = SmartUtils.getButton(buttonType, new Command() {
      public void execute() {
        callback.onEvent(component.getObject());
        destroy();
      }
    });
    SmartUtils.enabledWhenValid(button, component);
    setWidget(windowBuilder.withContents(new CanvasWithOpsLayout<Canvas>(component.get(), button)).get());
  }
  
  public void setCallback(final Listener<T> callback) {
    this.callback = callback;
  }
  
}
  