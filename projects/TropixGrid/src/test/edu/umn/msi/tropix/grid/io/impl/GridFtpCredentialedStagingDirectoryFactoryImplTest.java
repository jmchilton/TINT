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

package edu.umn.msi.tropix.grid.io.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.StagingDirectory;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.MockObjectCollection;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.gridftp.GridFtpClient;
import edu.umn.msi.tropix.grid.gridftp.GridFtpClientFactory;
import edu.umn.msi.tropix.grid.gridftp.GridFtpClient.GridFtpFile;

public class GridFtpCredentialedStagingDirectoryFactoryImplTest {
  private GridFtpCredentialedStagingDirectoryFactoryImpl factory;
  private GridFtpClientFactory gridFtpClientFactory;
  private Credential credential;
  private GridFtpClient gridFtpClient;
  private MockObjectCollection mockObjects;
  private String path;

  @BeforeMethod(groups = "unit")
  public void init() {
    credential = EasyMock.createMock(Credential.class);
    gridFtpClientFactory = EasyMock.createMock(GridFtpClientFactory.class);
    gridFtpClient = EasyMock.createMock(GridFtpClient.class);

    path = System.getProperty("java.io.tmpdir");

    mockObjects = MockObjectCollection.fromObjects(gridFtpClientFactory, gridFtpClient);

    factory = new GridFtpCredentialedStagingDirectoryFactoryImpl();
    factory.setGridFtpClientFactory(gridFtpClientFactory);
    factory.setTempDirectoryPath(path);

    EasyMock.expect(gridFtpClientFactory.getGridFtpClient(credential)).andReturn(gridFtpClient);
  }

  @Test(groups = "unit")
  public void getNew() {
    mockObjects.replay();
    factory.get(credential);
    mockObjects.verifyAndReset();
  }

  @Test(groups = "unit")
  public void getExisting() {
    mockObjects.replay();
    factory.get(credential, "/tmp");
    mockObjects.verifyAndReset();
  }

  @Test(groups = "unit")
  public void getSep() {
    mockObjects.replay();
    factory.setSep("moo");
    assert factory.get(credential).getSep().equals("moo");    
    mockObjects.verifyAndReset();
  }

  @Test(groups = "unit")
  public void setup() {
    final GridFtpClient client2 = EasyMock.createMock(GridFtpClient.class);
    final Credential credential2 = EasyMock.createMock(Credential.class);
    mockObjects.add(client2);
    EasyMock.expect(gridFtpClientFactory.getGridFtpClient(credential2)).andReturn(client2);
    final EasyMockUtils.Reference<String> path1 = EasyMockUtils.newReference(), path2 = EasyMockUtils.newReference();
    gridFtpClient.makeDir(EasyMockUtils.record(path1));
    client2.makeDir(EasyMockUtils.record(path2));
    mockObjects.replay();
    factory.get(credential).setup();
    factory.get(credential2).setup();
    mockObjects.verifyAndReset();
    assert path1.get().startsWith(path);
    assert path2.get().startsWith(path);
    assert !path1.get().equals(path2.get()) : "Path1 " + path1.get() + " Path2 " + path2.get();
  }

  @Test(groups = "unit")
  public void getAbsolutePath() {
    final EasyMockUtils.Reference<String> path = EasyMockUtils.newReference();
    gridFtpClient.makeDir(EasyMockUtils.record(path));
    mockObjects.replay();
    final StagingDirectory dir = factory.get(credential);
    dir.setup();
    assert dir.getAbsolutePath().equals(path.get());
    mockObjects.verifyAndReset();
  }

  @Test(groups = "unit")
  public void getAbsolutePathRecovered() {
    mockObjects.replay();
    final StagingDirectory dir = factory.get(credential, path + File.separator + "moo");
    assert dir.getAbsolutePath().equals(path + File.separator + "moo");
    mockObjects.verifyAndReset();
  }

  @Test(groups = "unit")
  public void makeDir() {
    gridFtpClient.makeDir(path + File.separator + "moo");
    mockObjects.replay();
    factory.get(credential, path).makeDirectory("moo");
    mockObjects.verifyAndReset();
  }
  
  @Test(groups = "unit")
  public void cleanUpDontDelete() {
    factory.setDeleteStagedFiles(false);
    mockObjects.replay();
    factory.get(credential).cleanUp();
    mockObjects.verifyAndReset();
  }

  @Test(groups = "unit")
  public void testInputContext() {
    gridFtpClient.get(EasyMock.eq(path + File.separator + "moo"), EasyMockUtils.copy(new ByteArrayInputStream("Hello World!".getBytes())));
    mockObjects.replay();
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    factory.get(credential, path).getInputContext("moo").get(outputStream);
    assert new String(outputStream.toByteArray()).equals("Hello World!");
  }
  
  @Test(groups = "unit")
  public void testOutputContext() {
    final ByteArrayOutputStream output = new ByteArrayOutputStream();
    gridFtpClient.put(EasyMock.eq(path + File.separator + "moo"), EasyMockUtils.copy(output));
    mockObjects.replay();
    factory.get(credential, path).getOutputContext("moo").put("Hello World!".getBytes());
    assert new String(output.toByteArray()).equals("Hello World!");
  }

  @Test(groups = "unit")
  public void testGetResourceNames() {
    //factory.setDeleteStagedFiles(true); // this is the default
    final GridFtpFile file1 = new GridFtpFile(path + File.separator + "moo", false);
    final GridFtpFile dir1 = new GridFtpFile(path + File.separator  + "cow", true);
    final GridFtpFile file2 = new GridFtpFile(path + File.separator + "moo2", false);    
    EasyMock.expect(gridFtpClient.list(path)).andReturn(Lists.newArrayList(file1, file2, dir1));
    mockObjects.replay();
    final List<String> resources = Lists.newArrayList(factory.get(credential, path).getResourceNames(null));
    assert resources.size() == 3;
    assert resources.containsAll(Lists.newArrayList("moo", "cow", "moo2"));
    mockObjects.verifyAndReset();
    
  }  
  @Test(groups = "unit")
  public void cleanUpDoDelete() {
    //factory.setDeleteStagedFiles(true); // this is the default
    final GridFtpFile file1 = new GridFtpFile(path + File.separator + "moo", false);
    final GridFtpFile dir1 = new GridFtpFile(path + File.separator  + "cow", true);
    final GridFtpFile file2 = new GridFtpFile(path + File.separator + "moo2", false);
    final GridFtpFile file3 = new GridFtpFile(path + File.separator + "cow" + File.separator + "moo3", false);
    
    EasyMock.expect(gridFtpClient.list(path)).andReturn(Lists.newArrayList(file1, file2, dir1));
    EasyMock.expect(gridFtpClient.list(path + File.separator + "cow")).andReturn(Lists.newArrayList(file3));
    gridFtpClient.deleteFile(path + File.separator + "moo");
    gridFtpClient.deleteFile(path + File.separator + "moo2");
    gridFtpClient.deleteFile(path + File.separator + "cow" + File.separator + "moo3");
    gridFtpClient.deleteDir(path + File.separator + "cow");
    gridFtpClient.deleteDir(path);
    mockObjects.replay();
    factory.get(credential, path).cleanUp();
    mockObjects.verifyAndReset();
  }
}
