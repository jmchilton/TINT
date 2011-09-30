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

package edu.umn.msi.tropix.grid.gridftp.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.annotation.Resource;

import org.globus.gsi.GlobusCredential;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.grid.gridftp.GridFtpClient;
import edu.umn.msi.tropix.grid.gridftp.GridFtpClientFactory;

@ContextConfiguration(locations = {"classpath:edu/umn/msi/tropix/grid/io/impl/testGramStagingDirectory.xml"})
public class GridFtpClientFactoryImplIntegrationTest extends AbstractTestNGSpringContextTests {

  @Resource
  private GridFtpClientFactory gridFtpClientFactory;

  @Resource
  private Supplier<GlobusCredential> hostProxySupplier;

  @Test(groups = "integration")
  public void ftpOps() {
    final GridFtpClient gridFtpClient = gridFtpClientFactory.getGridFtpClient(Credentials.get(hostProxySupplier.get()));
    final String randomDir = System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID().toString();
    final String mooFile = randomDir + File.separator + "moo";
    try {
      assert !gridFtpClient.exists(randomDir);
      gridFtpClient.makeDir(randomDir);
      assert gridFtpClient.exists(randomDir);

      assert Iterables.isEmpty(gridFtpClient.list(randomDir));

      assert !gridFtpClient.exists(mooFile);
      gridFtpClient.put(mooFile, new ByteArrayInputStream("Hello World".getBytes()));
      assert gridFtpClient.exists(mooFile);

      assert Iterables.getOnlyElement(gridFtpClient.list(randomDir)).getPath().equals(mooFile);
      assert !Iterables.getOnlyElement(gridFtpClient.list(randomDir)).isDirectory();

      final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      gridFtpClient.get(mooFile, outputStream);
      assert new String(outputStream.toByteArray()).equals("Hello World");

      assert gridFtpClient.exists(mooFile);
      gridFtpClient.deleteFile(mooFile);
      assert !gridFtpClient.exists(mooFile);

      assert Iterables.isEmpty(gridFtpClient.list(randomDir));

      assert gridFtpClient.exists(randomDir);
      gridFtpClient.deleteDir(randomDir);
      assert !gridFtpClient.exists(randomDir);

    } finally {
      deleteFileQuitely(gridFtpClient, mooFile);
      deleteDirectoryQuitely(gridFtpClient, randomDir);
    }

  }

  static void deleteFileQuitely(@Nullable final GridFtpClient client, @Nullable final String filename) {
    try {
      if(client != null) {
        client.deleteFile(filename);
      }
    } catch(final RuntimeException e) {
      return; // Ignore error
    }
  }

  static void deleteDirectoryQuitely(@Nullable final GridFtpClient client, @Nullable final String directory) {
    try {
      if(client != null) {
        client.deleteDir(directory);
      }
    } catch(final RuntimeException e) {
      return; // Ignore
    }
  }

  
  
}
