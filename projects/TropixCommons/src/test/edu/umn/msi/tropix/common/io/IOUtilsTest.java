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
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.io.output.ClosedOutputStream;
import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.CommonTests;

public class IOUtilsTest {
  private static final Random RANDOM = new Random();
  private IOUtils ioUtils = IOUtilsFactory.getInstance();

  @Test(groups = "unit")
  public void flush() throws IOException {
    final Flushable flushable = EasyMock.createMock(Flushable.class);
    flushable.flush();
    EasyMock.replay(flushable);
    ioUtils.flush(flushable);
    EasyMock.verify(flushable);
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void flushException() throws IOException {
    final Flushable flushable = EasyMock.createMock(Flushable.class);
    flushable.flush();
    EasyMock.expectLastCall().andThrow(new IOException());
    EasyMock.replay(flushable);
    ioUtils.flush(flushable);
  }

  @Test(groups = "unit")
  public void flushQuietlyException() throws IOException {
    final Flushable flushable = EasyMock.createMock(Flushable.class);
    flushable.flush();
    EasyMock.expectLastCall().andThrow(new IOException());
    EasyMock.replay(flushable);
    ioUtils.flushQuietly(flushable);
    ioUtils.flushQuietly(null);
  }

  @Test(groups = "unit")
  public void flushQuietly() throws IOException {
    final Flushable flushable = EasyMock.createMock(Flushable.class);
    flushable.flush();
    EasyMock.replay(flushable);
    ioUtils.flushQuietly(flushable);
    EasyMock.verify(flushable);
  }

  @Test(groups = "unit")
  public void readerToString() {
    final StringReader reader = new StringReader("Moo Cow");
    assert ioUtils.toString(reader).equals("Moo Cow");
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void readerToStringException() {
    final Reader reader = new ClosedReader();
    ioUtils.toString(reader);
  }

  @Test(groups = "unit")
  public void lineReader() {
    final LineNumberReader reader = new LineNumberReader(new StringReader("Moo Cow\nMoo Cow 2"));
    assert ioUtils.readLine(reader).equals("Moo Cow");
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void lineReaderException() {
    final LineNumberReader reader = new LineNumberReader(new ClosedReader());
    ioUtils.readLine(reader);
  }

  @Test(groups = "unit")
  public void append() {
    final StringWriter writer = new StringWriter();
    ioUtils.append(writer, "Moo Cow");
    assert writer.toString().equals("Moo Cow");
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void appendException() {
    final ClosedWriter writer = new ClosedWriter();
    ioUtils.append(writer, "Moo Cow");
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void copyStreamError() {
    this.ioUtils.copy(new ByteArrayInputStream("Hello".getBytes()), new ClosedOutputStream());
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void copyLargeStreamError() {
    this.ioUtils.copyLarge(new ByteArrayInputStream("Hello".getBytes()), new ClosedOutputStream());
  }

  static class ClosedReader extends Reader {
    public void close() throws IOException {
    }

    public int read(final char[] arg0, final int arg1, final int arg2) throws IOException {
      throw new IOException();
    }
  }

  static class ClosedWriter extends Writer {
    public void close() throws IOException {
    }

    public void flush() throws IOException {
    }

    public void write(final char[] arg0, final int arg1, final int arg2) throws IOException {
      throw new IOException();
    }
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void copyReaderError() {
    this.ioUtils.copy(new StringReader("Hello\nmoo"), new ClosedWriter());
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void copyLargeReaderError() {
    this.ioUtils.copyLarge(new StringReader("Hello\nmoo"), new ClosedWriter());
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void toStringReaderError() {
    this.ioUtils.toString(new ClosedReader());
  }

  @Test(groups = "unit", expectedExceptions = IORuntimeException.class)
  public void toStringStreamError() {
    this.ioUtils.toString(new InputStream() {
      public int read() throws IOException {
        throw new IOException();
      }
    });
  }

  @Test(groups = "unit")
  public void close() throws IOException {
    final Closeable closeable = EasyMock.createMock(Closeable.class);
    closeable.close();
    EasyMock.replay(closeable);
    this.ioUtils.closeQuietly(closeable);
    EasyMock.verify(closeable);
  }

  @Test(groups = "unit")
  public void closesQuietly() throws IOException {
    final Closeable closeable = EasyMock.createMock(Closeable.class);
    closeable.close();
    EasyMock.expectLastCall().andThrow(new IOException());
    EasyMock.replay(closeable);
    this.ioUtils.closeQuietly(closeable);
    EasyMock.verify(closeable);
  }

  @Test(groups = "unit")
  public void copy() {
    this.copy(false, false);
  }

  @Test(groups = "unit")
  public void copyLarge() {
    this.copy(true, false);
  }

  @Test(groups = "unit")
  public void copyReader() {
    this.copy(false, true);
  }

  @Test(groups = "unit")
  public void copyReaderLarge() {
    this.copy(true, true);
  }

  private void copy(final boolean large, final boolean asReader) {
    final byte[] bytes = new byte[100];
    RANDOM.nextBytes(bytes);
    final ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    if(asReader) {
      if(!large) {
        this.ioUtils.copy(new InputStreamReader(bis), new OutputStreamWriter(bos));
      } else {
        this.ioUtils.copyLarge(new InputStreamReader(bis), new OutputStreamWriter(bos));
      }

    } else {
      if(!large) {
        this.ioUtils.copy(bis, bos);
      } else {
        this.ioUtils.copyLarge(bis, bos);
      }
    }
    Arrays.equals(bytes, bos.toByteArray());
  }

  @Test(groups = "unit")
  public void isZipped() {
    assert this.ioUtils.isZippedStream(CommonTests.class.getResourceAsStream("hello.zip"));
    assert this.ioUtils.isZippedStream(CommonTests.class.getResourceAsStream("empty.zip"));
    assert this.ioUtils.isZippedStream(CommonTests.class.getResourceAsStream("empty-file.zip"));
    assert !this.ioUtils.isZippedStream(CommonTests.class.getResourceAsStream("moo.txt"));
  }
}
