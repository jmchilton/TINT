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

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.io.IOUtils;


class IOUtilsImpl implements edu.umn.msi.tropix.common.io.IOUtils {

  public void closeQuietly(@Nullable final Closeable closeable) {
    try {
      if(closeable != null) {
        closeable.close();
      }
    } catch(final Exception e) {
      // Ignore IOException and NullPointerException
      return;
    }
  }

  public long copy(final InputStream input, final OutputStream output) {
    return this.copyLarge(input, output);
  }

  public long copy(final Reader input, final Writer output) {
    return this.copyLarge(input, output);
  }

  public long copyLarge(final InputStream input, final OutputStream output) {
    try {
      return IOUtils.copyLarge(input, output);
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public long copyLarge(final Reader input, final Writer output) {
    try {
      return IOUtils.copyLarge(input, output);
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public String toString(final InputStream input) {
    try {
      return IOUtils.toString(input);
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public String toString(final Reader input) {
    try {
      return IOUtils.toString(input);
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public boolean isZippedStream(final InputStream inputStream) {
    final byte[] firstBytes = new byte[2];
    try {
      final int numBytes = inputStream.read(firstBytes);
      if(numBytes < 2) {
        return false;
      }
      // I hex dumped a bunch of zip files and they all started with 4b50.
      // This is purely based on my observations. If at some point this stops
      // working, it will need to be fixed. This approach will yield some false
      // positives, so it shouldn't be considered a general use procedure.
      final String hexString = new String(Hex.encodeHex(firstBytes));
      // String hexString = new String(HexUtils.encode(firstBytes));
      return hexString.equals("504b");
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public void flush(final Flushable flushable) {
    try {
      flushable.flush();
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public void flushQuietly(@Nullable final Flushable flushable) {
    try {
      if(flushable != null) {
        flushable.flush();
      }
    } catch(final Exception e) {
      // Ignore.
      return;
    }
  }

  public String readLine(@Nonnull final LineNumberReader lineNumberReader) {
    try {
      return lineNumberReader.readLine();
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public Writer append(@Nonnull final Writer writer, @Nonnull final CharSequence charSequence) {
    try {
      return writer.append(charSequence);
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public byte[] toByteArray(final InputStream inputStream) {
    try {
      return IOUtils.toByteArray(inputStream);
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
  }

  public int read(final InputStream inputStream, final byte[] bytes) {
    try {
      return inputStream.read(bytes);
    } catch(final IOException e) {
      throw new IORuntimeException(e);
    }
  }

}
