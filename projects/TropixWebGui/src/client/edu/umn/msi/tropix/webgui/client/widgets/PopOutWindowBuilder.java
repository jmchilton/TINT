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

import com.google.common.base.Supplier;
import com.google.gwt.user.client.Command;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;

/**
 * A typical pop out window that can be dragged, resized, maximized, and minimized.
 * 
 * @author John Chilton (chilton at msi dot umn dot edu)
 * 
 */
public final class PopOutWindowBuilder implements Supplier<Window> {
  private static class Options {
    private boolean modal = false;
    private boolean destroyOnClose = true;
    private boolean autoSize = false;
    private String width = "400px", height = "400px";
    private String title = "";
    private Canvas[] contents = null;
    private String icon = null;
    private Overflow overflow = null;
  }
  
  private final Options options = new Options();
 
  private PopOutWindowBuilder(final String title) {
    options.title = title;
  }
  
  public PopOutWindowBuilder withOverflow(final Overflow overflow) {
    options.overflow = overflow;
    return this;
  }
  
    
  /**
   * Sets the size of the pop out window to be built.
   * 
   * @param width Width of window in pixels
   * @param height Height of window in pixels
   * @return The PopOutWindowBuilder instance
   */
  public PopOutWindowBuilder sized(final int width, final int height) {
    return sized(width + "px", height + "px");
  }
  
  /**
   * Sets the size of the pop out window to be built.
   * 
   * @param width Width of window
   * @param height Height of window
   * @return The PopOutWindowBuilder instance
   */
  public PopOutWindowBuilder sized(final String width, final String height) {
    options.width = width;
    options.height = height;
    return this;
  }
  
  /**
   * Sets the contents of the window to build.
   * 
   * @param contents {@link Canvas} instances to add the pop window's body 
   * @return The PopOutWindowBuilder instance
   */
  public PopOutWindowBuilder withContents(final Canvas... contents) {
    options.contents = contents;
    return this;
  }

  public PopOutWindowBuilder withIcon(final String icon) {
    options.icon = icon;
    return this;
  }

  
  /**
   * By default the resulting window will be destoryed on close, invoking
   * this method of the builder disables this behavior.
   * 
   * @return The PopOutWindowBuilder instance
   */
  public PopOutWindowBuilder hideOnClose() {
    options.destroyOnClose = false;
    return this;
  }
  
  /**
   * Built window will be auto sized.
   * 
   * @return The PopOutWindowBuilder instance
   */
  public PopOutWindowBuilder autoSized() {
    return autoSized(true);
  }

  public PopOutWindowBuilder modal() {
    options.modal = true;
    return this;
  }
  
  /**
   * Built window will or will not be auto sized based on input param.
   * 
   * @param autoSize Whether of not to auto size the window
   * 
   * @return The PopOutWindowBuilder instance
   */
  public PopOutWindowBuilder autoSized(final boolean autoSize) {
    options.autoSize = autoSize;
    return this;
  }
  
  /**
   * Instantiates a PopOutWindowBuilder with the specified title.
   * 
   * @param title Title of the ultimate PopOutWindow to build.
   * @return The PopOutWindowBuilder instance
   */
  public static PopOutWindowBuilder titled(final String title) {
    return new PopOutWindowBuilder(title);
  }
  
  public Command asCommand() {
    return new Command() {
      public void execute() {
        get().show();
      }      
    };
  }

  /**
   * Builds and returns a pop out window with the specified options.
   * 
   * @return A pop out window.
   */
  public Window get() {
    final Window window = new Window();
    if(options.overflow != null) {
      window.setOverflow(options.overflow);
    }
    window.setTitle(options.title);
    window.setCanDragReposition(true);
    window.setCanDragResize(true);
    window.setShowMaximizeButton(true);
    window.setShowMinimizeButton(true);
    
    if(options.icon != null) {
      window.setHeaderIcon(options.icon);
    }
    if(!options.autoSize) {
      window.setSize(options.width, options.height);
    } else {
      window.setAutoSize(true);
    }
    window.centerInPage();
    if(options.destroyOnClose) {
      SmartUtils.destroyOnClose(window);
    }
    if(options.contents != null) {
      for(Canvas canvas : options.contents) {
        window.addItem(canvas);
      }      
    }    
    window.setIsModal(options.modal);
    return window;
  }
  
}
