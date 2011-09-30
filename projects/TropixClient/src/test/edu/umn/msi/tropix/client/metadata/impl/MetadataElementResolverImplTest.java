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

import javax.xml.namespace.QName;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.testng.annotations.Test;

public class MetadataElementResolverImplTest {
  private static final String GME_URL = "http://mms.training.cagrid.org:8080/wsrf/services/cagrid/MetadataModelService";
  @Test(groups = "unit")
  public void constructor() {
    new MetadataElementResolvers();
  }
  
  @Test(groups = "unit")
  public void getMetadataElement() throws MalformedURIException {
    MetadataElementResolverImpl resolver = new MetadataElementResolverImpl();
    resolver.getMetadataElement(new EndpointReferenceType(new Address(GME_URL)), new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata", "ServiceMetadata"));
  }
  
  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void getInvalidMetadataElement() throws MalformedURIException {
    MetadataElementResolverImpl resolver = new MetadataElementResolverImpl();
    resolver.getMetadataElement(new EndpointReferenceType(new Address(GME_URL)), new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata", "ServiceMetadataMoo"));    
  }
}
