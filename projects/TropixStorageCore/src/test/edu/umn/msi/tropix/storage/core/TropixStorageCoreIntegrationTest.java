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

package edu.umn.msi.tropix.storage.core;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.test.FreshConfigTest;

@ContextConfiguration(locations = "classpath:edu/umn/msi/tropix/storage/core/context.xml")
public class TropixStorageCoreIntegrationTest extends FreshConfigTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  
  @Inject
  private PersistentFileMapperService fileMapperService;
  
  @Inject
  private StorageManager manager;
  
  @Test(groups = "integration")
  public void storageService() {
    manager.upload(UUID.randomUUID().toString(), "cow").onUpload(new ByteArrayInputStream("Moo Cow".getBytes()));
    final File tempFile = FILE_UTILS.createTempFile();
    try {
      final String id = UUID.randomUUID().toString();
      fileMapperService.registerMapping(id, tempFile.getAbsolutePath());
      manager.upload(id, "cow").onUpload(new ByteArrayInputStream("Moo Cow2".getBytes()));
      assert FILE_UTILS.readFileToString(tempFile).equals("Moo Cow2");
    } finally {
      FILE_UTILS.deleteQuietly(tempFile);
    }
  }
}
