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

package edu.umn.msi.tropix.grid.io.transfer.impl;

import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.easymock.classextension.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.grid.io.transfer.TransferContextFactory;
import edu.umn.msi.tropix.transfer.http.client.HttpTransferClient;
import edu.umn.msi.tropix.transfer.types.CaGridTransferResource;
import edu.umn.msi.tropix.transfer.types.HttpTransferResource;
import edu.umn.msi.tropix.transfer.types.NamedTransferResource;
import edu.umn.msi.tropix.transfer.types.TransferResource;

public class TransferResourceContextFactoryImplTest {
  private TransferResourceContextFactoryImpl factory;
  private TransferContextFactory<Credential> transferContextFactory;
  private HttpTransferClient httpTransferClient;
  private Credential credential;
  
  @BeforeMethod(groups = "unit")
  public void init() {
    transferContextFactory = EasyMock.createMock(TransferContextFactory.class);
    httpTransferClient = EasyMock.createMock(HttpTransferClient.class);
    factory = new TransferResourceContextFactoryImpl(transferContextFactory, httpTransferClient);
    credential = Credentials.getMock();
  }
  
  
  @Test(groups = "unit", expectedExceptions = IllegalArgumentException.class)
  public void unknownSubclassDownload() {
    factory.getDownloadContext(new TransferResource(), credential);
  }

  @Test(groups = "unit", expectedExceptions = IllegalArgumentException.class)
  public void unknownSubclassUpload() {
    factory.getUploadContext(new TransferResource(), credential);
  }
  
  @Test(groups = "unit")
  public void testDefaultConstructor() {
    new TransferResourceContextFactoryImpl();
  }
  
  @Test(groups = "unit")
  public void caGridTransfer() {
    final CaGridTransferResource resource = new CaGridTransferResource();
    final TransferServiceContextReference transferServiceContextReference = new TransferServiceContextReference();
    resource.setTransferServiceContextReference(transferServiceContextReference);

    final OutputContext context = EasyMock.createMock(OutputContext.class);
    EasyMock.expect(transferContextFactory.getUploadContext(EasyMock.same(transferServiceContextReference), EasyMock.eq(credential))).andStubReturn(context);
    EasyMock.replay(transferContextFactory);
    assert factory.getUploadContext(resource, credential) == context;
    EasyMockUtils.verifyAndReset(transferContextFactory);
    
    final InputContext inputContext = EasyMock.createMock(InputContext.class);
    EasyMock.expect(transferContextFactory.getDownloadContext(EasyMock.same(transferServiceContextReference), EasyMock.eq(credential))).andStubReturn(inputContext);
    EasyMock.replay(transferContextFactory);
    assert factory.getDownloadContext(resource, credential) == inputContext;
    EasyMockUtils.verifyAndReset(transferContextFactory);
    

  }
    
  @Test(groups = "unit")
  public void http() {
    final HttpTransferResource baseResource = new HttpTransferResource();
    baseResource.setUrl("http://moo");
    final NamedTransferResource resource = new NamedTransferResource("test", baseResource);
    
    final OutputContext context = EasyMock.createMock(OutputContext.class);
    EasyMock.expect(httpTransferClient.getOutputContext("http://moo")).andStubReturn(context);
    EasyMock.replay(httpTransferClient);
    assert factory.getUploadContext(resource, credential) == context;
    EasyMockUtils.verifyAndReset(httpTransferClient);
    
    final InputContext inputContext = EasyMock.createMock(InputContext.class);
    EasyMock.expect(httpTransferClient.getInputContext("http://moo")).andStubReturn(inputContext);
    EasyMock.replay(httpTransferClient);
    assert factory.getDownloadContext(resource, credential) == inputContext;
    EasyMockUtils.verifyAndReset(httpTransferClient);
    
  }
  
}
