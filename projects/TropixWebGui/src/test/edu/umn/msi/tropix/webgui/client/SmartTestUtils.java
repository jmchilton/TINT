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

package edu.umn.msi.tropix.webgui.client;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.util.JSOHelper;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.events.ChangeEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;

/**
 * Utilities for mocking out GUI event, etc...
 * 
 * @author John Chilton
 *
 */
public class SmartTestUtils {  
  
  public static void changeValue(final FormItem item, final boolean value) {
    item.setValue(value);
    fireChangeAndChangedEvents(item, objectWithValue(value));
  }

  public static void changeValue(final FormItem item, final String value) {
    item.setValue(value);
    fireChangeAndChangedEvents(item, objectWithValue(value));
  }

  private static JavaScriptObject objectWithValue(final String value) {
    final JavaScriptObject object = JavaScriptObject.createObject();
    JSOHelper.setAttribute(object, "value", value);
    return object;
  }

  private static JavaScriptObject objectWithValue(final boolean value) {
    final JavaScriptObject object = JavaScriptObject.createObject();
    JSOHelper.setAttribute(object, "value", value);
    return object;
  }

  private static void fireChangeAndChangedEvents(final FormItem item, final JavaScriptObject object) {
    item.fireEvent(new ChangedEvent(object));
    item.fireEvent(new ChangeEvent(object));    
  }
  
}
