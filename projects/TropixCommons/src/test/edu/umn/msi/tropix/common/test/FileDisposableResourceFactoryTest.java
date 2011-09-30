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
import java.io.IOException;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.io.FileUtils;
import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.io.DisposableResource;
import edu.umn.msi.tropix.common.io.impl.FileDisposableResourceFactory;

public class FileDisposableResourceFactoryTest {
  private static final Random RANDOM = new Random();
  
  @Test(groups = "unit", timeOut=1000)
  public void fromFile() throws IOException {
    final File tempFile = new File("Test");
    final DisposableResource resource = FileDisposableResourceFactory.getFileFunction().apply(tempFile);
    assert resource.getFile().equals(tempFile);
  }

  @Test(groups = "unit", timeOut=1000)
  public void fromByte() throws IOException {
    final byte[] bytes = new byte[10];
    RANDOM.nextBytes(bytes);

    final DisposableResource resource = FileDisposableResourceFactory.getByteFunciton().apply(bytes);
    final File file = resource.getFile();
    assert file.exists();
    final byte[] fileBytes = FileUtils.readFileToByteArray(file);
    assert Arrays.equals(bytes, fileBytes);
    resource.dispose();
    assert !file.exists();
  }

  @SuppressWarnings("unchecked")
  @Test(groups = "unit", timeOut=1000)
  public void fromByteWithTempFileSupplier() throws IOException {
    final Supplier<File> mockSupplier = EasyMock.createMock(Supplier.class);
    final File file = File.createTempFile("tpxtest", "");
    EasyMock.expect(mockSupplier.get()).andReturn(file);
    EasyMock.replay(mockSupplier);
    final byte[] bytes = new byte[10];
    RANDOM.nextBytes(bytes);
    final DisposableResource resource = FileDisposableResourceFactory.getByteFunction(mockSupplier).apply(bytes);
    EasyMock.verify(mockSupplier);
    assert resource.getFile().equals(file);
    assert file.exists();
    assert Arrays.equals(bytes, FileUtils.readFileToByteArray(file));
    resource.dispose();
    assert !file.exists();
  }

}
