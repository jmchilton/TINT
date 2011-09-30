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

package edu.umn.msi.tropix.webgui.client.components.galaxy;

/**
 * Galaxy represents file types as "formats" which do not start with a
 * period (txt instead of .txt), TINT generally deals with extensions that
 * include the period (.txt instead of txt). This class contains static helper
 * methods for converting between these two formats.
 * 
 * @author John Chilton
 *
 */
public class GalaxyFormatUtils {
  
  /**
   * 
   * @param extension Tint style file type description
   * @return This extension with a period removed (if needed).
   */
  public static String extensionToFormat(final String extension) {
    return extension.startsWith(".") ? extension.substring(1) : extension;
  }

  /**
   * 
   * @param format Galaxy style file type description
   * @return This format without the period prefixed (if needed).
   */
  public static String formatToExtension(final String format) {
    return !format.startsWith(".") ? ("." + format) : format;
  }  
  
}
