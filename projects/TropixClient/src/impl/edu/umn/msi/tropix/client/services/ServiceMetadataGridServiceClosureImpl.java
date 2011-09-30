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

import edu.umn.msi.tropix.common.collect.Closure;
import gov.nih.nci.cagrid.metadata.ServiceMetadata;

public class ServiceMetadataGridServiceClosureImpl extends GridServiceModifierBase implements Closure<GridService> {
  public static final QName QNAME_SERVICE_METADTATA = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata", "ServiceMetadata");

  public void apply(final GridService gridService) {
    final ServiceMetadata serviceMetadata = getMetadataResolver().getMetadata(gridService.getServiceAddress(), QNAME_SERVICE_METADTATA, ServiceMetadata.class);
    final String serviceName = serviceMetadata.getServiceDescription().getService().getName();
    gridService.setServiceName(serviceName);
  }

}
