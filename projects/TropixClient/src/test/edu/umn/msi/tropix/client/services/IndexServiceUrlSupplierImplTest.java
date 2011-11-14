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

package edu.umn.msi.tropix.client.services;

import edu.umn.msi.tropix.common.test.EasyMockUtils;
import gov.nih.nci.cagrid.discovery.client.DiscoveryClient;
import gov.nih.nci.cagrid.metadata.exceptions.ResourcePropertyRetrievalException;

import java.util.Iterator;

import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.base.Function;

public class IndexServiceUrlSupplierImplTest {

  @Test(groups = "unit")
  public void get() throws Exception {
    final IndexServiceUrlSupplierImpl supplier = new IndexServiceUrlSupplierImpl();
    final String service = "http://service";
    supplier.setIndexServiceUrl(service);
    Function<String, DiscoveryClient> discoveryClientFactory;
    discoveryClientFactory = EasyMockUtils.createMockFunction();
    supplier.setDiscoveryClientFunction(discoveryClientFactory);
    final DiscoveryClient client = EasyMock.createMock(DiscoveryClient.class);
    EasyMock.expect(discoveryClientFactory.apply(service)).andReturn(client);
    final EndpointReferenceType[] eprs = new EndpointReferenceType[10];
    for(int i = 0; i < 10; i++) {
      final EndpointReferenceType epr = new EndpointReferenceType();
      epr.setAddress(new AttributedURI(service + "/" + i));
      eprs[i] = epr;
    }
    EasyMock.expect(client.getAllServices(false)).andReturn(eprs);
    EasyMock.replay(discoveryClientFactory, client);
    final Iterator<String> strs = supplier.iterator();
    assert strs.next().equals(service + "/" + 0);
  }
  
  
  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void discoveryException() throws Exception {
    final IndexServiceUrlSupplierImpl supplier = new IndexServiceUrlSupplierImpl();
    final String service = "http://service";
    supplier.setIndexServiceUrl(service);
    Function<String, DiscoveryClient> discoveryClientFactory;
    discoveryClientFactory = EasyMockUtils.createMockFunction();
    supplier.setDiscoveryClientFunction(discoveryClientFactory);
    final DiscoveryClient client = EasyMock.createMock(DiscoveryClient.class);
    EasyMock.expect(discoveryClientFactory.apply(service)).andReturn(client);
    EasyMock.expect(client.getAllServices(false)).andThrow(new ResourcePropertyRetrievalException());
    EasyMock.replay(discoveryClientFactory, client);
    supplier.iterator();
  }  
}
