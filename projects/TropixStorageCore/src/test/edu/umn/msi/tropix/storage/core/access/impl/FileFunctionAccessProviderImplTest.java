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

package edu.umn.msi.tropix.storage.core.access.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import org.easymock.classextension.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;

import edu.umn.msi.tropix.common.io.FileCoercible;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.TempDirectoryTest;

public class FileFunctionAccessProviderImplTest extends TempDirectoryTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private FileFunctionAccessProviderImpl provider = null;
  private Function<String, File> fileFunction = null;
  private String id;
  private File tempFile;

  @SuppressWarnings("unchecked")
  @BeforeMethod(groups = "unit")
  public void init() {
    provider = new FileFunctionAccessProviderImpl();
    fileFunction = EasyMock.createMock(Function.class);
    provider.setFileFunction(fileFunction);
    id = UUID.randomUUID().toString();
    tempFile = getFile("test");
  }

  private void expectMap() {
    expectMap(id, tempFile);
  }

  private void expectMap(final String id, final File file) {
    EasyMock.expect(fileFunction.apply(id)).andStubReturn(file);
    EasyMock.replay(fileFunction);
  }

  @Test(groups = "unit")
  public void putFile() {
    final File toFile = getFile("abc/def");
    expectMap(id, toFile);
    long length = provider.putFile(id, new ByteArrayInputStream("Hello World!".getBytes()));
    assert length == toFile.length();
    assert length == "Hello World!".getBytes().length;
    assert FILE_UTILS.readFileToString(toFile).equals("Hello World!");
  }

  @Test(groups = "unit")
  public void prepareUpload() throws IOException {
    final File toFile = getFile("abc/def");
    expectMap(id, toFile);
    final OutputStream outputStream = provider.getPutFileOutputStream(id);
    InputContexts.forString("Hello World!").get(outputStream);
    outputStream.close();
    assert "Hello World!".equals(FILE_UTILS.readFileToString(toFile));
  }

  @Test(groups = "unit")
  public void getFile() {
    expectMap();
    assert ((FileCoercible) provider.getFile(id)).asFile().equals(tempFile);
  }

  @Test(groups = "unit")
  public void getLength() {
    getFileUtils().writeStringToFile(tempFile, "test");
    expectMap();
    assert provider.getLength(id) == 4;
  }

  @Test(groups = "unit")
  public void getDateModified() {
    getFileUtils().writeStringToFile(tempFile, "test");
    expectMap();
    assert provider.getDateModified(id) == tempFile.lastModified();
  }

  @Test(groups = "unit")
  public void deleteFile() {
    touch();
    expectMap();
    provider.deleteFile(id);
    EasyMockUtils.verifyAndReset(fileFunction);
    assert !tempFile.exists();
  }

  @Test(groups = "unit")
  public void fileExists() {
    touch();
    expectMap();
    EasyMockUtils.verifyAndReset(fileFunction);
    FILE_UTILS.deleteQuietly(tempFile);
    expectMap(id, tempFile);
    assert !provider.fileExists(id);
  }

  private void touch() {
    getFileUtils().touch(tempFile);
  }

}
