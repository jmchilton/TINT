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

package edu.umn.msi.tropix.persistence.service.test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;

public class MockTropixObjectLoaderServiceImplTest {

  @Test
  public void testLoads() {
    final MockTropixObjectLoaderServiceImpl service = new MockTropixObjectLoaderServiceImpl();
    final String userId = UUID.randomUUID().toString();

    final Folder folder = new Folder();
    assert folder.getId() == null;
    service.saveObject(folder);    
    assert folder.getId() != null;

    assert service.load(userId, folder.getId()).equals(folder);
    assert service.load(userId, folder.getId(), TropixObjectTypeEnum.FOLDER).equals(folder);
    assert Iterables.elementsEqual(Arrays.asList(service.load(userId, new String[] {folder.getId()}, TropixObjectTypeEnum.TROPIX_OBJECT)),
        Lists.newArrayList(folder));

  }

  class Listener implements Closure<TropixObject> {
    private List<TropixObject> savedObjects = Lists.newArrayList();

    public void apply(final TropixObject input) {
      savedObjects.add(input);
    }

  }

  @Test
  public void testRegisterListener() {
    final MockTropixObjectLoaderServiceImpl service = new MockTropixObjectLoaderServiceImpl();
    final Listener listener = new Listener();
    service.registerSaveListener(listener);

    final TropixObject object1 = new TropixObject(), object2 = new TropixObject();
    service.saveObjects(object1, object2);

    assert Iterables.elementsEqual(listener.savedObjects, Arrays.asList(object1, object2));   
  }


}
