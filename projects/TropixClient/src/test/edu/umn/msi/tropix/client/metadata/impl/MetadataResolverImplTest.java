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

package edu.umn.msi.tropix.client.metadata.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.utils.XMLUtils;
import org.apache.commons.io.IOUtils;
import org.easymock.Capture;
import org.easymock.classextension.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import edu.umn.msi.tropix.client.test.ClientTests;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.proteomics.cagrid.metadata.AvailableIdentificationType;
import gov.nih.nci.cagrid.metadata.ServiceMetadata;

public class MetadataResolverImplTest {
  private static final QName ID_QNAME = new QName("http://msi.umn.edu/tropix/proteomics/cagrid/metadata", "IdentificationMetadata");
  private static final QName SERVICE_METADATA_QNAME = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata", "ServiceMetadata");
  
  private MetadataResolverImpl resolver = new MetadataResolverImpl();
  private MetadataElementResolver metadataElementResolver;
  
  @BeforeMethod(groups = "unit")
  public void init() {
    resolver = new MetadataResolverImpl();
    metadataElementResolver = EasyMock.createMock(MetadataElementResolver.class);
    resolver.setMetadataElementResolver(metadataElementResolver);
  }
  
  @Test(groups = "unit")
  public void resolve() throws IOException, SAXException, ParserConfigurationException {
    final String str = IOUtils.toString(ClientTests.class.getResourceAsStream("Sequest_identificationMetadata.xml"));
    final Document document = XMLUtils.getDocumentBuilder().parse(new ByteArrayInputStream(str.getBytes()));
    final Element element = document.getDocumentElement();
    final Capture<EndpointReferenceType> eprCapture = EasyMockUtils.newCapture();
    EasyMock.expect(metadataElementResolver.getMetadataElement(EasyMock.capture(eprCapture), EasyMock.eq(ID_QNAME))).andReturn(element);
    EasyMock.replay(metadataElementResolver);
    final AvailableIdentificationType metadata = resolver.getMetadata("http://service/moo/cow/Host", ID_QNAME, AvailableIdentificationType.class);
    EasyMock.verify(metadataElementResolver);
    assert metadata.getApplicationName().equals("MSI Sequest");
    assert eprCapture.getValue().getAddress().getHost().equals("service");
    assert eprCapture.getValue().getAddress().getPath().equals("/moo/cow/Host") : eprCapture.getValue().getAddress().getPath();
    assert eprCapture.getValue().getAddress().getScheme().equals("http") : eprCapture.getValue().getAddress().getScheme();
  }
  
  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void invalidType() throws IOException, SAXException, ParserConfigurationException {
    final String str = IOUtils.toString(ClientTests.class.getResourceAsStream("Sequest_identificationMetadata.xml"));
    final Document document = XMLUtils.getDocumentBuilder().parse(new ByteArrayInputStream(str.getBytes()));
    final Element element = document.getDocumentElement();
    final Capture<EndpointReferenceType> eprCapture = EasyMockUtils.newCapture();
    EasyMock.expect(metadataElementResolver.getMetadataElement(EasyMock.capture(eprCapture), EasyMock.eq(SERVICE_METADATA_QNAME))).andReturn(element);
    EasyMock.replay(metadataElementResolver);
    resolver.getMetadata("http://service/moo/cow/Host", SERVICE_METADATA_QNAME, ServiceMetadata.class);
  }

}
