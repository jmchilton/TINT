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

import java.util.Iterator;
import java.util.Map;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.common.test.EasyMockUtils;

public class GridServiceSupplierImplTest {

  @SuppressWarnings("unchecked")
  @Test(groups = "unit")
  public void closures() {
    GridServiceSupplierImpl<QueueGridService> supplier = new GridServiceSupplierImpl<QueueGridService>();
    @SuppressWarnings("unchecked")
    Closure<QueueGridService> closure1 = EasyMock.createMock(Closure.class);
    supplier.setGridServiceClass(QueueGridService.class);
    supplier.setAddressesSupplier(Lists.newArrayList("http://service"));
    supplier.setGridServiceModifiers(Lists.<Closure<? super QueueGridService>>newArrayList(closure1));

    Map<String, Object> properties = Maps.newHashMap();
    properties.put("serviceAddress", "http://service");
    closure1.apply((QueueGridService) EasyMockUtils.isBeanWithProperties(properties));
    EasyMock.replay(closure1);
    Iterator<QueueGridService> services = supplier.get().iterator();
    QueueGridService service1 = services.next();
    assert service1.getServiceAddress().equals("http://service");
    assert !services.hasNext();
    EasyMock.verify(closure1);
  }

}
