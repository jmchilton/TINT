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

import javax.annotation.Resource;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.io.OutputContexts;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.gridftp.GridFtpClient;
import edu.umn.msi.tropix.grid.gridftp.GridFtpClientFactory;
import edu.umn.msi.tropix.grid.gridftp.GridFtpClientUtils;

@ContextConfiguration(locations = {"classpath:edu/umn/msi/tropix/grid/io/impl/testGramStagingDirectory.xml"})
public class GridFtpClientUtilsIntegrationTest extends AbstractTestNGSpringContextTests {

  @Resource
  private GridFtpClientFactory gridFtpClientFactory;

  @Resource
  private Supplier<Credential> hostProxySupplier;

  @Test(groups = "integration")
  public void contexts() {
    final GridFtpClient gridFtpClient = gridFtpClientFactory.getGridFtpClient(hostProxySupplier.get());
    final String randomDir = System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID().toString();
    try {
      gridFtpClient.makeDir(randomDir);
      final OutputContext outputContext = GridFtpClientUtils.getOutputContext(gridFtpClient, randomDir + File.separator + "moo");
      InputContexts.forString("Hello World").get(outputContext);
      final InputContext inputContext = GridFtpClientUtils.getInputContext(gridFtpClient, randomDir + File.separator + "moo");
      final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
      inputContext.get(OutputContexts.forOutputStream(byteStream));
      assert "Hello World".equals(new String(byteStream.toByteArray()));
    } finally {
      GridFtpClientUtils.deleteDirectoryRecursively(gridFtpClient, randomDir);
    }
  }

  @Test(groups = "integration")
  public void deleteRecursively() {
    final GridFtpClient gridFtpClient = gridFtpClientFactory.getGridFtpClient(hostProxySupplier.get());
    final String randomDir = System.getProperty("java.io.tmpdir") + File.separator + UUID.randomUUID().toString();
    gridFtpClient.makeDir(randomDir);
    final ByteArrayInputStream inputStream = new ByteArrayInputStream("Hello World".getBytes());
    gridFtpClient.put(randomDir + File.separator + "moo1", inputStream);
    gridFtpClient.put(randomDir + File.separator + "moo3", inputStream);
    gridFtpClient.put(randomDir + File.separator + "moo2", inputStream);
    gridFtpClient.put(randomDir + File.separator + "moo4", inputStream);

    gridFtpClient.makeDir(randomDir + File.separator + "mooDir");
    gridFtpClient.makeDir(randomDir + File.separator + "mooDir" + File.separator + "mooSubDir");
    gridFtpClient.put(randomDir + File.separator + "mooDir" + File.separator + "mooSubDir" + File.separator + "moo5", inputStream);
    GridFtpClientUtils.deleteDirectoryRecursively(gridFtpClient, randomDir);
    assert !gridFtpClient.exists(randomDir);
  }
}
