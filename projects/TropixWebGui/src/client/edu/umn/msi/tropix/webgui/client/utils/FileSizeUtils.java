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

import com.google.gwt.i18n.client.NumberFormat;

public class FileSizeUtils {
  private static final long BYTES_PER_KILOBYTE = 1024;
  private static final long BYTES_PER_MEGABYTE = 1024 * 1024;
  private static final long BYTES_PER_GIGABYTE = 1024 * 1024 * 1024;
  
  private static String divide(final long numerator, final long denominator) {
    final NumberFormat formatted = NumberFormat.getFormat("#######.00");
    return formatted.format((1.0 * numerator) / denominator); 
  }
  
  /**
   * 
   * @param fileSize File size in bytes.
   * @return A human readable string representing the file size.
   */
  public static String formatFileSize(final long fileSize) {
    String fileSizeStr = fileSize + " bytes";
    if(fileSize >= BYTES_PER_GIGABYTE) {
      fileSizeStr = divide(fileSize, BYTES_PER_GIGABYTE) + " gigabytes";
    } else if(fileSize >= BYTES_PER_MEGABYTE) {
      fileSizeStr = divide(fileSize, BYTES_PER_MEGABYTE) + " megabytes";
    } else if(fileSize >= BYTES_PER_KILOBYTE) {
      fileSizeStr = divide(fileSize, BYTES_PER_KILOBYTE) + " kilobytes";
    }
    return fileSizeStr;
  }
  
}
