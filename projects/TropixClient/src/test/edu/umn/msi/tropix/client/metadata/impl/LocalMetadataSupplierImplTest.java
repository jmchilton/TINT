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

import java.io.StringReader;
import java.io.StringWriter;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.queuestatus.QueueStatus;
import edu.umn.msi.tropix.grid.xml.SerializationUtils;
import edu.umn.msi.tropix.grid.xml.SerializationUtilsFactory;

public class LocalMetadataSupplierImplTest {
  private static final SerializationUtils SERIALIZATION_UTILS = SerializationUtilsFactory.getInstance();
  
  @Test(groups = "unit")
  public void testObjectReader() {
    final LocalMetadataSupplierImpl<QueueStatus> supplier = new LocalMetadataSupplierImpl<QueueStatus>(QueueStatus.class);
    supplier.setQname(QueueStatus.getTypeDesc().getXmlType());
    final QueueStatus queueStatus = new QueueStatus();
    queueStatus.setSize(103);
    queueStatus.setActive(false);

    final StringWriter writer = new StringWriter();
    SERIALIZATION_UTILS.serialize(writer, queueStatus, QueueStatus.getTypeDesc().getXmlType());    
    supplier.setObjectReader(new StringReader(writer.toString()));
    assert supplier.get().equals(queueStatus);
  }
  
}
