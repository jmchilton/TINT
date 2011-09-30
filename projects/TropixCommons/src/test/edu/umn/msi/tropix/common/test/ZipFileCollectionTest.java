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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.easymock.classextension.EasyMock;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.io.IORuntimeException;
import edu.umn.msi.tropix.common.io.ZipFileCollection;

public class ZipFileCollectionTest {

  public void copyResourceToFile(final File file, final String resource) throws IOException {
    final InputStream inputStream = this.getClass().getResourceAsStream(resource);
    final FileOutputStream tempOutputStream = new FileOutputStream(file, false);
    IOUtils.copy(inputStream, tempOutputStream);
    tempOutputStream.close();
    inputStream.close();
  }

  @Test(groups = "linux", expectedExceptions = IORuntimeException.class)
  public void testException() throws IOException {
    final File tempFile = File.createTempFile("temp", "");
    tempFile.deleteOnExit();
    this.copyResourceToFile(tempFile, "hello.zip");
    final Supplier<File> supplier = EasyMockUtils.createMockSupplier();
    org.easymock.EasyMock.expect(supplier.get()).andReturn(new File("/moo"));
    EasyMock.replay(supplier);
    new ZipFileCollection(tempFile, supplier);
  }

  @Test(groups = {"unit"})
  public void testEmpty() throws IOException {
    final File tempFile = File.createTempFile("temp", "");

    ZipFileCollection collection;
    File[] files;

    // Java seems to choke the empty zip file!
    /*
     * copyResourceToFile(tempFile, "empty.zip"); collection = new ZipFileCollection(tempFile);
     * 
     * files = collection.getFiles(); assert files.length == 0; assert collection.deleteAll();
     */

    this.copyResourceToFile(tempFile, "empty-file.zip");
    collection = new ZipFileCollection(tempFile);
    files = collection.getFilesArray();
    assert files.length == 1;
    assert files[0].exists();
    assert FileUtils.readFileToByteArray(files[0]).length == 0;
    assert collection.deleteAll();
    assert !files[0].exists();

    assert collection.getZipEntryNames().length == 1;
    assert collection.getZipEntryNames()[0].equals("empty");

    this.copyResourceToFile(tempFile, "hello.zip");
    collection = new ZipFileCollection(tempFile);
    files = collection.getFilesArray();
    assert files.length == 2 : "Length of files is " + files.length;
    assert files[0].exists() && files[1].exists();
    this.assertHelloWorldContents(files[0]);
    this.assertHelloWorldContents(files[1]);
    assert collection.deleteAll();
    assert !files[0].exists() && !files[1].exists();

    assert collection.getZipEntryNames().length == 2;
    assert collection.getZipEntryNames()[0].startsWith("hello");
    assert collection.getZipEntryNames()[1].startsWith("hello");

  }

  private void assertHelloWorldContents(final File file) throws IOException {
    assert FileUtils.readFileToString(file).equals("Hello World!");
  }
}
