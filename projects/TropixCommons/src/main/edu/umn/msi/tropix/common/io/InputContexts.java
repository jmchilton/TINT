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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import com.google.common.base.Function;

/**
 * Utilities for dealing with and creating {@link InputContext} objects.
 * 
 * @author John Chilton
 * 
 */
public class InputContexts {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();

  private static final Function<File, FileStreamInputContextImpl> FOR_FILE_FUNCTION = new Function<File, FileStreamInputContextImpl>() {
    public FileStreamInputContextImpl apply(final File file) {
      return new FileStreamInputContextImpl(file);
    }
  };

  public static void get(final OutputStream outputStream, final InputStreamCoercible hasInputStream) {
    final InputStream inputStream = hasInputStream.asInputStream();
    try {
      IO_UTILS.copy(inputStream, outputStream);
    } finally {
      IO_UTILS.closeQuietly(inputStream);
    }    
  }
  
  public abstract static class HasStreamInputContextImpl extends StreamInputContextImpl implements HasStreamInputContext {

    public void get(final OutputStream outputStream) {
      InputContexts.get(outputStream, this);
    }

  }

  // Not needed but kept for compatibility
  public static class FileStreamInputContextImpl extends FileContext {

    public FileStreamInputContextImpl(final File file) {
      super(file);
    }

  }

  public static class InputStreamInputContextImpl extends HasStreamInputContextImpl {
    private final InputStream inputStream;

    public InputStreamInputContextImpl(final InputStream inputStream) {
      this.inputStream = inputStream;
    }

    public InputStream asInputStream() {
      return inputStream;
    }

  }

  private static final Function<InputStream, InputStreamInputContextImpl> FOR_STREAM_FUNCTION = new Function<InputStream, InputStreamInputContextImpl>() {
    public InputStreamInputContextImpl apply(final InputStream inputStream) {
      return new InputStreamInputContextImpl(inputStream);
    }
  };

  private static final Function<byte[], InputStreamInputContextImpl> FOR_BYTE_ARRAY_FUNCTION = new Function<byte[], InputStreamInputContextImpl>() {
    public InputStreamInputContextImpl apply(final byte[] bytes) {
      return new InputStreamInputContextImpl(new ByteArrayInputStream(bytes));
    }
  };

  private static final Function<URL, InputContext> FOR_URL_FUNCTION = new Function<URL, InputContext>() {
    public InputContext apply(final URL url) {
      return new InvertedInputContextImpl() {
        @Override
        public void get(final OutputContext outputContext) {
          outputContext.put(url);
        }
      };
    }
  };

  public static Function<InputStream, InputStreamInputContextImpl> getForStreamFunction() {
    return InputContexts.FOR_STREAM_FUNCTION;
  }

  public static Function<File, FileStreamInputContextImpl> getForFileFunction() {
    return InputContexts.FOR_FILE_FUNCTION;
  }

  public static Function<byte[], InputStreamInputContextImpl> getForByteArrayFunction() {
    return InputContexts.FOR_BYTE_ARRAY_FUNCTION;
  };

  public static void putIntoOutputContexts(final Iterable<InputContext> contexts, final Iterable<OutputContext> files) {
    InputContexts.putIntoOutputContexts(contexts.iterator(), files.iterator());
  }

  public static void putIntoOutputContexts(final Iterator<InputContext> contexts, final Iterator<OutputContext> files) {
    while(contexts.hasNext()) {
      contexts.next().get(files.next());
    }
  }

  public static void put(final Iterable<InputContext> contexts, final Iterable<File> files) {
    InputContexts.put(contexts.iterator(), files.iterator());
  }

  public static void put(final Iterator<InputContext> contexts, final Iterator<File> files) {
    while(contexts.hasNext()) {
      contexts.next().get(files.next());
    }
  }

  public static FileStreamInputContextImpl forFile(final File file) {
    return InputContexts.FOR_FILE_FUNCTION.apply(file);
  }

  public static InputStreamInputContextImpl forInputStream(final InputStream stream) {
    return InputContexts.FOR_STREAM_FUNCTION.apply(stream);
  }

  public static InputStreamInputContextImpl forByteArray(final byte[] bytes) {
    return InputContexts.FOR_BYTE_ARRAY_FUNCTION.apply(bytes);
  }

  public static InputStreamInputContextImpl forString(final String str) {
    return InputContexts.forByteArray(str.getBytes());
  }

  public static InputContext forUrl(final String url) {
    try {
      return forUrl(new URL(url));
    } catch(MalformedURLException e) {
      throw new RuntimeException(e);
    }
  }

  public static byte[] getAsByteArray(final InputContext inputContext) {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    try {
      inputContext.get(outputStream);
      InputContexts.IO_UTILS.flush(outputStream);
      return outputStream.toByteArray();
    } finally {
      InputContexts.IO_UTILS.closeQuietly(outputStream);
    }
  }

  public static String toString(final InputContext inputContext) {
    return new String(getAsByteArray(inputContext));
  }

  public static InputContext forUrl(final URL resource) {
    return FOR_URL_FUNCTION.apply(resource);
  }

}
