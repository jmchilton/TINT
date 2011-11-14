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

import javax.xml.namespace.QName;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.client.metadata.MetadataResolver;
import edu.umn.msi.tropix.common.jobqueue.queuestatus.QueueStatus;
import edu.umn.msi.tropix.proteomics.cagrid.metadata.AvailableIdentificationType;
import edu.umn.msi.tropix.proteomics.cagrid.metadata.ParameterType;
import gov.nih.nci.cagrid.metadata.ServiceMetadata;
import gov.nih.nci.cagrid.metadata.ServiceMetadataServiceDescription;
import gov.nih.nci.cagrid.metadata.service.Service;

public class GridServiceClosuresTest {
  private static final QName QNAME_AVAILABLE_IDENTIFICATION_TYPE = new QName("http://msi.umn.edu/tropix/proteomics/cagrid/metadata", "IdentificationMetadata");
  private static final QName QNAME_QUEUE_STATUS = new QName("http://msi.umn.edu/tropix/common/jobqueue/queueStatus", "QueueStatus");
  private static final QName QNAME_SERVICE_METADTATA = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata", "ServiceMetadata");  
  private static final String SERVICE = "http://SERVICE:9021/Test";
  
  @Test(groups = "unit")
  public void idApplyClosure() {
    final MetadataResolver resolver = EasyMock.createMock(MetadataResolver.class);
    IdentificationGridServiceClosureImpl closure = new IdentificationGridServiceClosureImpl();
    closure.setMetadataResolver(resolver);
    AvailableIdentificationType type = new AvailableIdentificationType();
    type.setApplicationName("sequest");
    type.setParameterType(ParameterType.OmssaXml);
    EasyMock.expect(resolver.getMetadata(SERVICE, QNAME_AVAILABLE_IDENTIFICATION_TYPE , AvailableIdentificationType.class)).andReturn(type);
    
    EasyMock.replay(resolver);
    IdentificationGridService service = new IdentificationGridService();
    service.setServiceAddress(SERVICE);
    closure.apply(service);
    
    assert service.getApplicationName().equals("sequest");
    assert service.getParameterType().equals("OmssaXml");
  }
  
  @Test(groups = "unit")
  public void queueServiceClosure() {
    final MetadataResolver resolver = EasyMock.createMock(MetadataResolver.class);
    QueueGridServiceClosureImpl closure = new QueueGridServiceClosureImpl();
    closure.setMetadataResolver(resolver);
    QueueStatus type = new QueueStatus();
    type.setActive(true);
    type.setSize(10);
    
    EasyMock.expect(resolver.getMetadata(SERVICE, QNAME_QUEUE_STATUS , QueueStatus.class)).andReturn(type);
    
    EasyMock.replay(resolver);
    QueueGridService service = new QueueGridService();
    service.setServiceAddress(SERVICE);
    closure.apply(service);
    
    assert service.getJobsPending() == 10;
    assert service.getActive();
  }
  
  @Test(groups = "unit")
  public void serviceMetadataClosure() {
    final MetadataResolver resolver = EasyMock.createMock(MetadataResolver.class);
    ServiceMetadataGridServiceClosureImpl closure = new ServiceMetadataGridServiceClosureImpl();
    closure.setMetadataResolver(resolver);
    ServiceMetadata metadata = new ServiceMetadata();
    ServiceMetadataServiceDescription serviceDescription = new ServiceMetadataServiceDescription();
    Service serviceObject = new Service();
    serviceObject.setName("Test Service");
    serviceDescription.setService(serviceObject);
    metadata.setServiceDescription(serviceDescription);
    EasyMock.expect(resolver.getMetadata(SERVICE, QNAME_SERVICE_METADTATA , ServiceMetadata.class)).andReturn(metadata);
    
    EasyMock.replay(resolver);
    GridService service = new GridService();
    service.setServiceAddress(SERVICE);
    closure.apply(service);
    
    assert service.getServiceName().equals("Test Service");
  }  
  
}
