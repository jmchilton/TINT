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

package edu.umn.msi.tropix.common.test;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Reader;
import java.io.StringReader;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.LineIterator;

public class LineIteratorTest {

  @Test(groups = {"unit"})
  public void nonBlockingTest() {
    final String testStr1 = "hello world!\nhow are you world?\nglad to here it world\n\nsee ya\n";
    final LineIterator lineIterator = new LineIterator(new StringReader(testStr1));
    String line;
    assert lineIterator.hasNext();
    line = lineIterator.next();
    assert line.equals("hello world!\n");
    assert lineIterator.hasNext();
    line = lineIterator.next();
    assert line.equals("how are you world?\n");
    assert lineIterator.hasNext();
    line = lineIterator.next();
    assert line.equals("glad to here it world\n");
    assert lineIterator.hasNext();
    line = lineIterator.next();
    assert line.equals("\n");
    assert lineIterator.hasNext();
    line = lineIterator.next();
    assert line.equals("see ya\n");
  }

  @Test(groups = "unit", expectedExceptions = UnsupportedOperationException.class)
  public void remove() {
    final LineIterator lineIterator = new LineIterator(new StringReader("moo"));
    lineIterator.remove();
  }

  @Test(groups = "unit", expectedExceptions = IllegalStateException.class)
  public void invalidNext() {
    final LineIterator lineIterator = new LineIterator(new StringReader("moo"));
    lineIterator.next();
    lineIterator.next();
  }

  @Test(groups = "unit", expectedExceptions = IllegalStateException.class)
  public void readerException() throws IOException {
    final Reader reader = EasyMock.createMock(Reader.class);
    org.easymock.EasyMock.expect(reader.ready()).andThrow(new IOException());
    EasyMock.replay(reader);
    final LineIterator lineIterator = new LineIterator(reader);
    lineIterator.hasNext();
  }

  @Test(groups = {"unit"})
  public void blockingTest() throws IOException {
    final PipedWriter pipedWriter = new PipedWriter();
    final PipedReader pipedReader = new PipedReader(pipedWriter);
    final LineIterator lineIterator = new LineIterator(pipedReader);
    assert !lineIterator.hasNext();
    pipedWriter.write("hello");
    assert !lineIterator.hasNext();
    pipedWriter.write("\n");
    assert lineIterator.hasNext();
    assert lineIterator.next().equals("hello\n");
    pipedWriter.write("hello world\n!!!\n ekk ");
    assert lineIterator.hasNext();
    assert lineIterator.next().equals("hello world\n");
    assert lineIterator.hasNext();
    assert lineIterator.next().equals("!!!\n");
    assert !lineIterator.hasNext();
    assert !lineIterator.hasNext();
    pipedWriter.write(" the cat !!!\r");
    assert lineIterator.hasNext();
    assert lineIterator.next().equals(" ekk  the cat !!!\r");
  }

}
