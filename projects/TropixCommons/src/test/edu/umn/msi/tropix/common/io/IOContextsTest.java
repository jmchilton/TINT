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
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

public class IOContextsTest {

  @Test(groups = "unit")
  public void byetArrayOutputContext() {
    final ByteArrayOutputContextImpl bc = new ByteArrayOutputContextImpl();
    bc.put("Hello World".getBytes());
    assert new String(bc.toByteArray()).equals("Hello World");
  }

  @Test(groups = "unit")
  public void outputForStreamFunction() {
    final ByteArrayOutputStream stream = new ByteArrayOutputStream();
    final OutputContext outputContext = OutputContexts.getForStreamFunction().apply(stream);
    outputContext.put(new byte[] {1, 2});
    assert stream.toByteArray().length == 2;
    assert stream.toByteArray()[0] == 1;
    assert stream.toByteArray()[1] == 2;
  }

  @Test(groups = "unit")
  public void inputForFileFunction() throws IOException {
    final File file1 = File.createTempFile("tpx", "tst"), file2 = File.createTempFile("tpx", "tst");
    try {
      FileUtils.writeStringToFile(file1, "Moo Cow");

      final InputContext inputContext = InputContexts.getForFileFunction().apply(file1);
      inputContext.get(file2);
      final String contents = FileUtils.readFileToString(file2);
      assert contents.equals("Moo Cow") : contents;
    } finally {
      FileUtils.deleteQuietly(file1);
      FileUtils.deleteQuietly(file2);
    }
  }

  @Test(groups = "unit")
  public void inputForFile() throws IOException {
    final File file1 = File.createTempFile("tpx", "tst"), file2 = File.createTempFile("tpx", "tst");
    try {
      FileUtils.writeStringToFile(file1, "Moo Cow");

      final InputContext inputContext = InputContexts.forFile(file1);
      inputContext.get(file2);
      assert FileUtils.readFileToString(file2).equals("Moo Cow");
    } finally {
      FileUtils.deleteQuietly(file1);
      FileUtils.deleteQuietly(file2);
    }
  }

  @Test(groups = "unit")
  public void inputForStream() throws IOException {
    final ByteArrayInputStream inputStream = new ByteArrayInputStream("Moo Cow".getBytes());

    final InputContext inputContext = InputContexts.forInputStream(inputStream);
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    inputContext.get(outputStream);
    assert Arrays.equals(outputStream.toByteArray(), "Moo Cow".getBytes());
  }

  @Test(groups = "unit")
  public void outputForFileFunction() throws IOException {
    final File file1 = File.createTempFile("tpx", "tst"), file2 = File.createTempFile("tpx", "tst");
    try {
      FileUtils.writeStringToFile(file1, "Moo Cow");

      final OutputContext outputContext = OutputContexts.getForFileFunction().apply(file2);
      outputContext.put(file1);
      assert FileUtils.readFileToString(file2).equals("Moo Cow");
    } finally {
      FileUtils.deleteQuietly(file1);
      FileUtils.deleteQuietly(file2);
    }

  }

  @Test(groups = "unit")
  public void outputForStream() {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    final OutputContext outputContext = OutputContexts.forOutputStream(outputStream);
    outputContext.put("Moo Cow".getBytes());

    assert Arrays.equals(outputStream.toByteArray(), "Moo Cow".getBytes());
  }

  @Test(groups = "unit")
  public void getAsByteArray() {
    final InputContext context = InputContexts.forString("Moo Cow");
    assert Arrays.equals("Moo Cow".getBytes(), InputContexts.getAsByteArray(context));
  }

  @Test(groups = "unit")
  public void getForByteArrayFunction() throws IOException {
    final InputContext context = InputContexts.getForByteArrayFunction().apply("Moo Cow".getBytes());
    final File file = File.createTempFile("tpxtst", "");
    try {
      context.get(file);
      FileUtils.readFileToString(file).equals("Moo Cow");
    } finally {
      FileUtils.deleteQuietly(file);
    }
  }

  @Test(groups = "unit")
  public void outputForFile() throws IOException {
    final File file1 = File.createTempFile("tpx", "tst"), file2 = File.createTempFile("tpx", "tst");
    try {
      FileUtils.writeStringToFile(file1, "Moo Cow");

      final OutputContext outputContext = OutputContexts.forFile(file2);
      outputContext.put(file1);
      assert FileUtils.readFileToString(file2).equals("Moo Cow");
    } finally {
      FileUtils.deleteQuietly(file1);
      FileUtils.deleteQuietly(file2);
    }
  }
}
