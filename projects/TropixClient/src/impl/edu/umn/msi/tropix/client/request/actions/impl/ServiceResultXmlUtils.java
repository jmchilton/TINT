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

package edu.umn.msi.tropix.client.request.actions.impl;

import info.minnesotapartnership.tropix.request.models.ServiceResult;

import java.io.StringReader;
import java.io.StringWriter;

import edu.umn.msi.tropix.grid.xml.SerializationUtils;
import edu.umn.msi.tropix.grid.xml.SerializationUtilsFactory;

class ServiceResultXmlUtils {
  private static final SerializationUtils SERIALIZATION_UTILS = SerializationUtilsFactory.getInstance();

  static String toXml(final ServiceResult serviceResult) {
    final StringWriter stringWriter = new StringWriter();
    SERIALIZATION_UTILS.serialize(stringWriter, serviceResult, ServiceResult.getTypeDesc().getXmlType());
    return stringWriter.toString();
  }

  static ServiceResult fromXml(final String serviceResultXml) {
    final StringReader reader = new StringReader(serviceResultXml);
    return SERIALIZATION_UTILS.deserialize(reader, ServiceResult.class);
  }

}
