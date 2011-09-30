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

import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.Analysis;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.Run;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;

public class TropixObjectServiceLoadRecentTest extends ServiceTest {
  @Autowired
  private TropixObjectService tropixObjectService;

  @Test
  public void loadRecentNoTypes() throws InterruptedException {
    final TropixObject object1 = new TropixObject(), object2 = new TropixObject(), object3 = new TropixObject();

    final User user = createTempUser();
    object1.setCommitted(true);
    object2.setCommitted(true);
    object3.setCommitted(true);
    super.saveNewTropixObject(object1, user);
    Thread.sleep(2);
    super.saveNewTropixObject(object2, user);
    Thread.sleep(2);
    super.saveNewTropixObject(object3, user);
    Thread.sleep(2);

    final TropixObject[] objects = tropixObjectService.loadRecent(user.getCagridId(), 2, true, null, false);
    assert objects.length == 2 : objects.length;
    assert objects[1].getId().equals(object2.getId());
    assert objects[0].getId().equals(object3.getId()) : objects[0].getId();
  }

  @Test
  public void loadRecentRequireParent() throws InterruptedException {
    final TropixObject object1 = new TropixObject(), object2 = new TropixObject(), object3 = new TropixObject();

    final Folder folder1 = new Folder();
    folder1.setCommitted(true);
    folder1.setContents(new HashSet<TropixObject>());

    final User user = createTempUser();
    saveNewTropixObject(folder1, user);
    object1.setCommitted(true);
    object2.setCommitted(true);
    object3.setCommitted(true);
    super.saveNewTropixObject(object1, user);
    Thread.sleep(2);
    super.saveNewTropixObject(object2, user);
    Thread.sleep(2);
    super.saveNewTropixObject(object3, user);
    Thread.sleep(2);

    TropixObject[] objects = tropixObjectService.loadRecent(user.getCagridId(), 2, false, null, true);
    assert objects.length == 0 : objects.length;

    getTropixObjectDao().addToFolder(folder1.getId(), object1.getId());
    objects = tropixObjectService.loadRecent(user.getCagridId(), 2, false, null, true);
    assert objects.length == 1 : objects.length;
    assert objects[0].getId().equals(object1.getId());

    getTropixObjectDao().addToFolder(folder1.getId(), object3.getId());
    objects = tropixObjectService.loadRecent(user.getCagridId(), 2, false, null, true);
    assert objects.length == 2 : objects.length;
    assert objects[0].getId().equals(object3.getId());
    assert objects[1].getId().equals(object1.getId());
  }

  @Test
  public void loadRecentTypes() throws InterruptedException {
    final TropixObject object1 = new Run(), object2 = new ProteomicsRun(), object3 = new Folder(), object4 = new Analysis();

    final User user = createTempUser();
    object1.setCommitted(true);
    object2.setCommitted(true);
    object3.setCommitted(true);
    super.saveNewTropixObject(object1, user);
    Thread.sleep(2);
    super.saveNewTropixObject(object2, user);
    Thread.sleep(2);
    super.saveNewTropixObject(object3, user);
    Thread.sleep(2);
    super.saveNewTropixObject(object4, user);
    TropixObject[] objects = tropixObjectService.loadRecent(user.getCagridId(), 2, true, new TropixObjectTypeEnum[] {TropixObjectTypeEnum.FOLDER, TropixObjectTypeEnum.PROTEOMICS_RUN}, false);
    assert objects.length == 2 : objects.length;
    assert objects[0].getId().equals(object3.getId()) : objects[0].getId();
    assert objects[1].getId().equals(object2.getId());

    objects = tropixObjectService.loadRecent(user.getCagridId(), 2, true, new TropixObjectTypeEnum[] {TropixObjectTypeEnum.FOLDER, TropixObjectTypeEnum.RUN}, false);
    assert objects.length == 2 : objects.length;
    assert objects[0].getId().equals(object3.getId()) : objects[0].getId();
    assert objects[1].getId().equals(object2.getId()) : objects[1].getId();

    objects = tropixObjectService.loadRecent(user.getCagridId(), 3, true, new TropixObjectTypeEnum[] {TropixObjectTypeEnum.FOLDER, TropixObjectTypeEnum.RUN}, false);
    assert objects.length == 3 : objects.length;
    assert objects[0].getId().equals(object3.getId()) : objects[0].getId();
    assert objects[1].getId().equals(object2.getId()) : objects[1].getId();
    assert objects[2].getId().equals(object1.getId()) : objects[2].getId();

    objects = tropixObjectService.loadRecent(user.getCagridId(), 5, true, new TropixObjectTypeEnum[] {TropixObjectTypeEnum.RUN}, false);
    assert objects.length == 2 : objects.length;
    assert objects[0].getId().equals(object2.getId()) : objects[0].getId();
    assert objects[1].getId().equals(object1.getId()) : objects[1].getId();

    objects = tropixObjectService.loadRecent(user.getCagridId(), 5, true, new TropixObjectTypeEnum[] {TropixObjectTypeEnum.RUN}, false);
    assert objects.length == 2 : objects.length;
    assert objects[0].getId().equals(object2.getId()) : objects[0].getId();
    assert objects[1].getId().equals(object1.getId()) : objects[1].getId();

    objects = tropixObjectService.loadRecent(user.getCagridId(), 2, true, new TropixObjectTypeEnum[] {TropixObjectTypeEnum.FILE}, false);
    assert objects.length == 0 : objects.length;
  }

  @Test
  public void loadRecentExcludeFolders() throws InterruptedException {
    final TropixObject object1 = new Run(), object2 = new ProteomicsRun(), object3 = new Folder(), object4 = new Analysis();
    final TropixObject object5 = new VirtualFolder();

    final User user = createTempUser();
    object1.setCommitted(true);
    object2.setCommitted(true);
    object3.setCommitted(true);
    object4.setCommitted(true);
    object5.setCommitted(true);

    super.saveNewTropixObject(object1, user);

    Thread.sleep(2);
    super.saveNewTropixObject(object2, user);
    Thread.sleep(2);
    super.saveNewTropixObject(object3, user);
    Thread.sleep(2);
    super.saveNewTropixObject(object4, user);
    Thread.sleep(2);
    super.saveNewTropixObject(object5, user);

    TropixObject[] objects = tropixObjectService.loadRecent(user.getCagridId(), 5, false, null, false);
    assert objects.length == 3 : objects.length;
    assert objects[0].getId().equals(object4.getId()) : objects[0].getId();
    assert objects[1].getId().equals(object2.getId()) : objects[1].getId();
    assert objects[2].getId().equals(object1.getId());

    objects = tropixObjectService.loadRecent(user.getCagridId(), 10, false, new TropixObjectTypeEnum[] {TropixObjectTypeEnum.FOLDER, TropixObjectTypeEnum.RUN, TropixObjectTypeEnum.ANALYSIS, TropixObjectTypeEnum.VIRTUAL_FOLDER}, false);
    System.out.println(objects[0]);
    assert objects.length == 3 : objects.length;
    assert objects[0].getId().equals(object4.getId());
    assert objects[1].getId().equals(object2.getId());
    assert objects[2].getId().equals(object1.getId());

    objects = tropixObjectService.loadRecent(user.getCagridId(), 10, false, new TropixObjectTypeEnum[] {TropixObjectTypeEnum.FILE}, false);
    assert objects.length == 0 : objects.length;
  }

}
