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

package edu.umn.msi.tropix.client.metadata;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.xml.namespace.QName;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.QueueStatusBeanImpl;
import edu.umn.msi.tropix.common.jobqueue.queuestatus.QueueStatus;
import edu.umn.msi.tropix.common.test.FreshConfigTest;
import edu.umn.msi.tropix.models.proteomics.IdentificationType;
import edu.umn.msi.tropix.proteomics.cagrid.metadata.AvailableIdentificationType;
import gov.nih.nci.cagrid.metadata.ServiceMetadata;

@ContextConfiguration
public class LocalMetadataSpringTest extends FreshConfigTest {

  @Resource(name = "metadataResolver")
  private MetadataResolver resolver;

  @Inject
  private QueueStatusBeanImpl queueStatusBean;

  /**
   * Test the metadata resolved is delegating to the local metadata resolved on local:// URLs
   * and that thing deserialize properly.
   */
  @Test(groups = "spring")
  public void testMetadataResolver() {
    final QueueStatus status = new QueueStatus();
    status.setActive(true);
    status.setSize(134);
    queueStatusBean.set(status);
    final QueueStatus resolvedStatus = resolver.getMetadata("local://Omssa", QueueStatus.getTypeDesc().getXmlType(), QueueStatus.class);
    assert status.equals(resolvedStatus);

    final ServiceMetadata scaffoldMetadata = resolver.getMetadata("local://Scaffold", ServiceMetadata.getTypeDesc().getXmlType(),
        ServiceMetadata.class);
    assert scaffoldMetadata.getServiceDescription().getService().getName().equals("Local Scaffold");

    final AvailableIdentificationType idMetadata = resolver.getMetadata("local://XTandem",
        QName.valueOf("{http://msi.umn.edu/tropix/proteomics/cagrid/metadata}IdentificationMetadata"), AvailableIdentificationType.class);
    assert IdentificationType.fromParameterType(idMetadata.getParameterType().getValue()) == IdentificationType.XTANDEM;
    final String[] services = new String[] {"local://MyriMatch", "local://IdPicker"};
    for(final String service : services) {
      assert null != resolver.getMetadata(service, ServiceMetadata.getTypeDesc().getXmlType(), ServiceMetadata.class);
    }

  }
}
