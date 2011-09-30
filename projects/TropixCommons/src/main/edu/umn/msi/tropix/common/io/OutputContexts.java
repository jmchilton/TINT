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
import java.io.OutputStream;
import java.util.Iterator;

import com.google.common.base.Function;

/**
 * Utilities for dealing with and creating {@link OutputContext} objects.
 * 
 * @author John Chilton
 *
 */
public class OutputContexts {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();

  private static final Function<File, OutputContext> FOR_FILE_FUNCTION = new Function<File, OutputContext>() {
    public OutputContext apply(final File file) {
      return new FileContext(file);
    }
  };

  private static final Function<OutputStream, OutputContext> FOR_STREAM_FUNCTION = new Function<OutputStream, OutputContext>() {
    public OutputContext apply(final OutputStream outputStream) {
      return new StreamOutputContextImpl() {
        public void put(final InputStream inputStream) {
          OutputContexts.IO_UTILS.copyLarge(inputStream, outputStream);
        }
      };
    }
  };

  public static Function<OutputStream, OutputContext> getForStreamFunction() {
    return OutputContexts.FOR_STREAM_FUNCTION;
  }

  public static Function<File, OutputContext> getForFileFunction() {
    return OutputContexts.FOR_FILE_FUNCTION;
  }

  public static void put(final Iterable<OutputContext> contexts, final Iterable<File> files) {
    OutputContexts.put(contexts.iterator(), files.iterator());
  }

  public static void put(final Iterator<OutputContext> contexts, final Iterator<File> files) {
    while(contexts.hasNext()) {
      contexts.next().put(files.next());
    }
  }

  public static OutputContext forFile(final File file) {
    return OutputContexts.FOR_FILE_FUNCTION.apply(file);
  }

  public static OutputContext forOutputStream(final OutputStream stream) {
    return OutputContexts.FOR_STREAM_FUNCTION.apply(stream);
  }

}
