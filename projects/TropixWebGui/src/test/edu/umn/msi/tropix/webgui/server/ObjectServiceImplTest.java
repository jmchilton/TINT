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

package edu.umn.msi.tropix.webgui.server;

import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;

public class ObjectServiceImplTest extends BaseGwtServiceTest {
  private TropixObjectService tropixObjectService;
  private ObjectServiceImpl gwtObjectService;
  
  @BeforeMethod(groups = "unit")
  public void init() {
    super.init();
    tropixObjectService = EasyMock.createMock(TropixObjectService.class);
    gwtObjectService = new ObjectServiceImpl();
    gwtObjectService.setBeanSanitizer(getSanitizer());
    gwtObjectService.setTropixObjectService(tropixObjectService);
    gwtObjectService.setUserSession(getUserSession());
  }
  
  @Test(groups = "unit", dataProvider = "bool1", dataProviderClass=TestNGDataProviders.class)
  public void getChildren(final boolean withTypes) {
    final String objectId = UUID.randomUUID().toString();
    final TropixObjectTypeEnum[] types = withTypes ? new TropixObjectTypeEnum[] {TropixObjectTypeEnum.FILE} : null;
    final TropixObject object1 = createTropixObject(TropixObject.class);
    final TropixObject object2 = createTropixObject(TropixObject.class);
    final TropixObject[] objects = new TropixObject[] {object1, object2};
    if(withTypes) {
      EasyMock.expect(tropixObjectService.getChildren(getUserId(), objectId, types));      
    } else {
      EasyMock.expect(tropixObjectService.getChildren(getUserId(), objectId));
    }
    EasyMock.expectLastCall().andReturn(objects);
    EasyMock.replay(tropixObjectService);
    assert Iterables.elementsEqual(gwtObjectService.getChildren(objectId, types), Lists.newArrayList(object1, object2));
    assert getSanitizer().wasSanitized(object1);
    assert getSanitizer().wasSanitized(object2);    
    EasyMock.verify(tropixObjectService);
  }
}
