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

import com.google.common.base.Supplier;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;

import edu.umn.msi.tropix.webgui.client.components.CanvasComponent;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;

public class CompositeWindowComponentFactoryImpl<S, T extends Window> implements ComponentFactory<S, WindowComponent<T>> {
  private Supplier<? extends T> windowSupplier;
  private ComponentFactory<S, ? extends CanvasComponent<? extends Canvas>> baseComponentFactory;

  private class CompositeWindowComponentImpl extends WindowComponentImpl<T> {
    CompositeWindowComponentImpl(final S input) {
      this.setWidget(CompositeWindowComponentFactoryImpl.this.windowSupplier.get());
      this.get().addItem(CompositeWindowComponentFactoryImpl.this.baseComponentFactory.get(input).get());
    }
  }

  public WindowComponent<T> get(final S input) {
    return new CompositeWindowComponentImpl(input);
  }

  public void setWindowSupplier(final Supplier<? extends T> windowSupplier) {
    this.windowSupplier = windowSupplier;
  }

  public void setBaseComponentFactory(final ComponentFactory<S, ? extends CanvasComponent<? extends Canvas>> baseComponentFactory) {
    this.baseComponentFactory = baseComponentFactory;
  }

}
