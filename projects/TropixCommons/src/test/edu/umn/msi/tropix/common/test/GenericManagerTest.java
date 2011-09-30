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

package edu.umn.msi.tropix.common.test;

import java.util.Map;
import java.util.Set;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.manager.GenericManager;

public class GenericManagerTest {

  static <T, U extends T> U createMock(final Class<T> clazz) {
    return null;
  };

  @Test(groups = "unit")
  public void delegation() {
    final GenericManager<Object, Object> genericManager = new GenericManager<Object, Object>();
    final Map<Object, Object> map = EasyMock.createMock(Map.class);
    genericManager.setMap(map);

    map.clear();
    EasyMock.replay(map);
    genericManager.clear();
    EasyMockUtils.verifyAndReset(map);

    EasyMock.expect(map.containsKey("moo")).andReturn(true);
    EasyMock.replay(map);
    assert genericManager.containsKey("moo");
    EasyMockUtils.verifyAndReset(map);

    EasyMock.expect(map.containsValue("moo")).andReturn(false);
    EasyMock.replay(map);
    assert !genericManager.containsValue("moo");
    EasyMockUtils.verifyAndReset(map);

    EasyMock.expect(map.get("moo")).andReturn("cow");
    EasyMock.replay(map);
    "cow".equals(genericManager.get("moo"));
    EasyMockUtils.verifyAndReset(map);

    EasyMock.expect(map.get("moo")).andReturn("cow");
    EasyMock.replay(map);
    "cow".equals(genericManager.apply("moo"));
    EasyMockUtils.verifyAndReset(map);

    EasyMock.expect(map.isEmpty()).andReturn(true);
    EasyMock.replay(map);
    assert genericManager.isEmpty();
    EasyMockUtils.verifyAndReset(map);

    EasyMock.expect(map.size()).andReturn(8);
    EasyMock.replay(map);
    assert genericManager.size() == 8;
    EasyMockUtils.verifyAndReset(map);

    final Set<Object> set = EasyMock.createMock(Set.class);
    EasyMock.expect(map.keySet()).andReturn(set);
    EasyMock.replay(map);
    assert set == genericManager.keySet();
    EasyMockUtils.verifyAndReset(map);

    EasyMock.expect(map.put("moo", "cow")).andReturn("oldcow");
    EasyMock.replay(map);
    assert "oldcow".equals(genericManager.put("moo", "cow"));
    EasyMockUtils.verifyAndReset(map);

    final Map<Object, Object> addMap = EasyMock.createMock(Map.class);
    map.putAll(addMap);
    EasyMock.replay(map);
    genericManager.putAll(addMap);
    EasyMockUtils.verifyAndReset(map);

    EasyMock.expect(map.remove("moo")).andReturn("cow");
    EasyMock.replay(map);
    "cow".equals(genericManager.remove("moo"));
    EasyMockUtils.verifyAndReset(map);

  }
}
