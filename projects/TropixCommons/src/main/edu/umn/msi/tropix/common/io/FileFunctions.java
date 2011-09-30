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
import java.io.InputStream;

import javax.annotation.concurrent.Immutable;

import com.google.common.base.Function;

/**
 * Utility class for creating various {@link Function} objects operating
 * on {@link File}s.
 * 
 * @author John Chilton
 *
 */
@Immutable
public class FileFunctions {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final Function<File, InputStream> INPUT_STREAM_FUNCTION = new Function<File, InputStream>() {
    public InputStream apply(final File file) {
      return FILE_UTILS.getFileInputStream(file);
    }
  };

  private static final Function<String, File> TO_FILE_FUNCTION = new Function<String, File>() {
    public File apply(final String path) {
      return new File(path);
    }
  };

  private static final Function<File, String> NAME_FUNCTION = new Function<File, String>() {
    public String apply(final File file) {
      return file.getName();
    }
  };

  private static final Function<String, File> PATH_TO_FILE_FUNCTION = new Function<String, File>() {
    public File apply(final String path) {
      return new File(path);
    }
  };

  public static Function<String, File> toFileFunction() {
    return TO_FILE_FUNCTION;
  }

  public static Function<File, InputStream> getInputStreamFunction() {
    return FileFunctions.INPUT_STREAM_FUNCTION;
  }

  public static Function<File, String> getNameFunction() {
    return FileFunctions.NAME_FUNCTION;
  }

  public static Function<String, File> getPathToFileFunction() {
    return PATH_TO_FILE_FUNCTION;
  }

}
