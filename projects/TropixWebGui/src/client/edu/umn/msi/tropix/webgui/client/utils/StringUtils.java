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

package edu.umn.msi.tropix.webgui.client.utils;

import javax.annotation.Nullable;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

public class StringUtils {
  public static boolean hasText(@Nullable final Object value) {
    return value != null && value.toString().length() > 0;
  }

  public static String toString(@Nullable final Object object) {
    return object == null ? "" : object.toString();
  }

  public static String join(@Nullable final Iterable<String> strings) {
    return StringUtils.join(", ", strings);
  }

  public static String join(final String delim, @Nullable final Iterable<String> strings) {
    final StringBuilder sb = new StringBuilder();
    if(strings != null) {
      boolean first = true;
      for(final String string : strings) {
        if(first) {
          first = false;
        } else {
          sb.append(delim);
        }
        sb.append(string);
      }
    }
    return sb.toString();
  }

  /**
   * Based on an idea found at http://www.gwtapps.com/?p=21 .
   * 
   * @param html
   *          Text that possibly contains unescaped HTML fragments.
   * @return Escaped version of the text safe for display.
   */
  public static String escapeHtml(final String html) {
    final Element tempDiv = DOM.createDiv();
    DOM.setInnerText(tempDiv, html);
    return DOM.getInnerHTML(tempDiv);
  }

  /**
   * Converts the object to text, handles possible null values, and escapes HTML fragments.
   * 
   * @param object
   * @return Sanitized textual description of object.
   */
  public static String sanitize(@Nullable final Object object) {
    return escapeHtml(toString(object));
  }

}
