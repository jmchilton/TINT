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

package edu.umn.msi.tropix.common.io;

import java.io.File;
import java.util.Arrays;

/**
 * Utility methods for dealing with {@link Directory} objects.
 * 
 * @author John Chilton
 *
 */
public class Directories {

  public static String buildAbsolutePath(final Directory directory, final String... resources) {
    return Directories.buildAbsoluatePath(directory, Arrays.asList(resources));
  }

  private static String buildAbsoluatePath(final Directory directory, final Iterable<String> resources) {
    final StringBuilder builder = new StringBuilder(directory.getAbsolutePath());
    for(final String resource : resources) {
      builder.append(directory.getSep() + resource);
    }
    return builder.toString();
  }

  /**
   * Builds a {@link Directory} object for a given file.
   * 
   * @param file Java {@link File} object describing a physical directory to build a {@link Directory}
   *   instance for.
   * @return A {@link Directory} object wrapping the physical directory {@link file}.
   */
  public static Directory fromFile(final File file) {
    return StagingDirectories.getDefaultFactory().get(file.getAbsolutePath());
  }

}
