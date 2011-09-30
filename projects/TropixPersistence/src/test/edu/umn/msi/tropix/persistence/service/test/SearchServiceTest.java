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
import java.util.Collection;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.models.Analysis;
import edu.umn.msi.tropix.models.Group;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.Run;
import edu.umn.msi.tropix.models.ScaffoldAnalysis;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.models.VirtualPermission;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.persistence.dao.Dao;
import edu.umn.msi.tropix.persistence.service.SearchService;
import edu.umn.msi.tropix.persistence.service.security.SecurityProvider;

// TODO: Test virtual folders for getChildren and getTopLevelItems
public class SearchServiceTest extends ServiceTest {
  @Autowired
  private SearchService searchService;

  @Autowired
  private SecurityProvider securityProvider;

  @Test
  public void getChildrenCombos() {
    final User user1 = createTempUser();

    final TropixFile file = new TropixFile();
    file.setName("File");
    file.setCommitted(true);
    saveNewTropixObject(file, user1);

    final ProteomicsRun thermoRun = new ProteomicsRun();
    thermoRun.setName("Run");
    thermoRun.setCommitted(true);
    thermoRun.setSource(file);
    saveNewTropixObject(thermoRun, user1);

    Collection<TropixObject> objects;
    objects = searchService.getChildren(user1.getCagridId(), thermoRun.getId());
    assert objects.iterator().next().equals(file);

    final ProteomicsRun proteomicsRun = new ProteomicsRun();
    proteomicsRun.setMzxml(file);
    proteomicsRun.setCommitted(true);
    saveNewTropixObject(proteomicsRun, user1);

    objects = searchService.getChildren(user1.getCagridId(), proteomicsRun.getId());
    assert objects.iterator().next().equals(file);

    final ScaffoldAnalysis scaffoldInputAnalysis = new ScaffoldAnalysis();
    scaffoldInputAnalysis.setCommitted(true);
    scaffoldInputAnalysis.setInput(file);
    saveNewTropixObject(scaffoldInputAnalysis, user1);

    objects = searchService.getChildren(user1.getCagridId(), scaffoldInputAnalysis.getId());
    assert objects.iterator().next().equals(file);

    final ScaffoldAnalysis scaffoldOutputAnalysis = new ScaffoldAnalysis();
    scaffoldOutputAnalysis.setCommitted(true);
    scaffoldOutputAnalysis.setOutputs(file);
    saveNewTropixObject(scaffoldOutputAnalysis, user1);

    objects = searchService.getChildren(user1.getCagridId(), scaffoldOutputAnalysis.getId());
    assert objects.iterator().next().equals(file);

    final IdentificationAnalysis idAnalysis = new IdentificationAnalysis();
    idAnalysis.setCommitted(true);
    idAnalysis.setOutput(file);
    saveNewTropixObject(idAnalysis, user1);
    objects = searchService.getChildren(user1.getCagridId(), idAnalysis.getId());
    assert objects.iterator().next().equals(file);
  }

  @Test
  public void getChildren() {
    final User user1 = createTempUser();

    final TropixFile runFile = new TropixFile();
    runFile.setName("File");
    runFile.setCommitted(true);
    saveNewTropixObject(runFile);
    getTropixObjectDao().setOwner(runFile.getId(), user1);

    final ProteomicsRun run = new ProteomicsRun();
    run.setName("Run");
    run.setCommitted(true);
    run.setSource(runFile);
    saveNewTropixObject(run);
    getTropixObjectDao().setOwner(run.getId(), user1);

    Collection<TropixObject> objects;
    objects = searchService.getChildren(user1.getCagridId(), run.getId());
    assert objects.size() == 1;

    runFile.setCommitted(false);
    getTropixObjectDao().saveOrUpdateTropixObject(runFile);
    objects = searchService.getChildren(user1.getCagridId(), run.getId());
    assert objects.size() == 0;

    runFile.setCommitted(true);
    getTropixObjectDao().saveOrUpdateTropixObject(runFile);
    objects = searchService.getChildren(user1.getCagridId(), run.getId());
    assert objects.size() == 1;

    runFile.setDeletedTime("" + System.currentTimeMillis());
    getTropixObjectDao().saveOrUpdateTropixObject(runFile);
    objects = searchService.getChildren(user1.getCagridId(), run.getId());
    assert objects.size() == 0;
  }

  @Test
  public void localAutoOwnerSearch() {
    final User user1 = createTempUser();
    final String user2Id = newId();

    TropixObject[] objects;
    final TropixObject object = new TropixObject();
    object.setName("moo xxx");
    object.setDescription("cow");
    object.setCommitted(true);
    this.saveNewTropixObject(object);
    getTropixObjectDao().setOwner(object.getId(), user1);

    objects = searchService.searchObjects(user1.getCagridId(), "moo xxx", null, user2Id, null);
    assert objects.length == 0;
  }

  @Test
  public void virtualFoldersIgnored() {
    final User user1 = createTempUser();

    final VirtualFolder virtualFolder = new VirtualFolder();
    virtualFolder.setName("moo");
    virtualFolder.setCommitted(true);
    virtualFolder.setDescription("cow");
    this.saveNewTropixObject(virtualFolder);
    getTropixObjectDao().setOwner(virtualFolder.getId(), user1);

    final Run run = new Run();
    run.setName("moo");
    run.setDescription("cow");
    run.setCommitted(true);
    this.saveNewTropixObject(run);
    getTropixObjectDao().setOwner(run.getId(), user1);

    TropixObject[] objects;

    objects = searchService.searchObjects(user1.getCagridId(), "moo", null, null, null);

    assert Arrays.asList(objects).contains(run);
    assert !Arrays.asList(objects).contains(virtualFolder);

    objects = searchService.quickSearchObjects(user1.getCagridId(), "moo");

    assert Arrays.asList(objects).contains(run);
    assert !Arrays.asList(objects).contains(virtualFolder);

  }

  @Test
  public void localSearchType() {
    final User user1 = createTempUser();

    TropixObject[] objects;

    final Run run = new Run();
    run.setName("moo");
    run.setDescription("cow");
    run.setCommitted(true);
    this.saveNewTropixObject(run);
    getTropixObjectDao().setOwner(run.getId(), user1);

    final Analysis analysis = new Analysis();
    analysis.setName("moo");
    analysis.setDescription("cow");
    analysis.setCommitted(true);
    this.saveNewTropixObject(analysis);
    getTropixObjectDao().setOwner(analysis.getId(), user1);

    objects = searchService.searchObjects(user1.getCagridId(), null, null, null, TropixObjectTypeEnum.RUN);
    assert Arrays.asList(objects).contains(run);
    assert !Arrays.asList(objects).contains(analysis);

    objects = searchService.searchObjects(user1.getCagridId(), null, null, null, TropixObjectTypeEnum.ANALYSIS);
    assert !Arrays.asList(objects).contains(run);
    assert Arrays.asList(objects).contains(analysis);

    objects = searchService.searchObjects(user1.getCagridId(), null, null, null, TropixObjectTypeEnum.TROPIX_OBJECT);
    assert Arrays.asList(objects).contains(run);
    assert Arrays.asList(objects).contains(analysis);

    objects = searchService.searchObjects(user1.getCagridId(), null, null, null, null);
    assert Arrays.asList(objects).contains(run);
    assert Arrays.asList(objects).contains(analysis);
  }

  @Test
  public void localSearch() {
    final User user1 = createTempUser();

    TropixObject[] objects;
    final TropixObject object = new TropixObject();
    object.setName("moo xxx");
    object.setCommitted(true);
    object.setDescription("cow");
    this.saveNewTropixObject(object);
    getTropixObjectDao().setOwner(object.getId(), user1);

    objects = searchService.searchObjects(user1.getCagridId(), "moo xxx", null, null, null);
    assert objects.length == 1 : objects.length;

    object.setDeletedTime("" + System.currentTimeMillis());
    getTropixObjectDao().saveOrUpdateTropixObject(object);
    objects = searchService.searchObjects(user1.getCagridId(), "moo xxx", null, null, null);
    assert objects.length == 0 : objects.length;

    object.setDeletedTime(null);
    getTropixObjectDao().saveOrUpdateTropixObject(object);
    objects = searchService.searchObjects(user1.getCagridId(), "moo xxx", null, null, null);
    assert objects.length == 1 : objects.length;

    object.setCommitted(false);
    getTropixObjectDao().saveOrUpdateTropixObject(object);
    objects = searchService.searchObjects(user1.getCagridId(), "moo xxx", null, null, null);
    assert objects.length == 0 : objects.length;

  }

  @Test
  public void quickSearch() {
    final User user1 = createTempUser(), user2 = createTempUser();

    final TropixObject object1 = new TropixObject();
    object1.setName("moo1");
    object1.setCommitted(true);
    object1.setDescription("cow");
    this.saveNewTropixObject(object1);
    getTropixObjectDao().setOwner(object1.getId(), user1);

    final TropixObject object2 = new TropixObject();
    object2.setName("moo2");
    object2.setCommitted(true);
    object2.setDescription("a cow and stuff");
    this.saveNewTropixObject(object2);
    getTropixObjectDao().setOwner(object2.getId(), user1);

    final TropixObject notOwnedObject = new TropixObject();
    notOwnedObject.setName("moo1");
    notOwnedObject.setCommitted(true);
    notOwnedObject.setDescription("cow");
    this.saveNewTropixObject(notOwnedObject);
    getTropixObjectDao().setOwner(notOwnedObject.getId(), user2);
    TropixObject[] objects;

    objects = searchService.quickSearchObjects(user1.getCagridId(), "moo1");
    assert objects.length == 1 : objects.length;
    objects = searchService.quickSearchObjects(user1.getCagridId(), "moo2");
    assert objects.length == 1 : objects.length;
    objects = searchService.quickSearchObjects(user1.getCagridId(), "cow");
    assert objects.length == 2 : objects.length;

    object1.setCommitted(false);
    getTropixObjectDao().saveOrUpdateTropixObject(object1);
    objects = searchService.quickSearchObjects(user1.getCagridId(), "moo1");
    assert objects.length == 0 : objects.length;

    object1.setCommitted(true);
    getTropixObjectDao().saveOrUpdateTropixObject(object1);
    objects = searchService.quickSearchObjects(user1.getCagridId(), "moo1");
    assert objects.length == 1 : objects.length;

    object1.setDeletedTime("" + System.currentTimeMillis());
    getTropixObjectDao().saveOrUpdateTropixObject(object1);
    objects = searchService.quickSearchObjects(user1.getCagridId(), "moo1");
    assert objects.length == 0 : objects.length;
  }

  /*
   * @Test public void getTopLevelObjects() { User user1 = createTempUser(), user2 = createTempUser(), user3 = createTempUser();
   * 
   * TropixObject object = new TropixObject(); object.setParentFolder(user1.getHomeFolder()); object.setCommitted(true); saveNewTropixObject(object, user1); tropixObjectDao.addPermissionParent(object.getId(), object.getParentFolder().getId());
   * tropixObjectDao.addRole(object.getId(), "read", user2);
   * 
   * Collection<TropixObject> results; results = searchService.getTopLevelObjects(user2.getCagridId(), user1.getCagridId()); assert results.size() == 1 : results.size(); results = searchService.getTopLevelObjects(user2.getCagridId(), null); assert results.size() == 2 :
   * results.size(); results = searchService.getTopLevelObjects(user3.getCagridId(), user1.getCagridId()); assert results.size() == 0; results = searchService.getTopLevelObjects(user3.getCagridId(), null); assert results.size() == 1;
   * 
   * object.setCommitted(false); tropixObjectDao.saveOrUpdateTropixObject(object); results = searchService.getTopLevelObjects(user2.getCagridId(), user1.getCagridId()); assert results.size() == 0 : results.size();
   * 
   * object.setCommitted(true); tropixObjectDao.saveOrUpdateTropixObject(object); results = searchService.getTopLevelObjects(user2.getCagridId(), user1.getCagridId()); assert results.size() == 1 : results.size();
   * 
   * object.setDeletedTime("" + System.currentTimeMillis()); tropixObjectDao.saveOrUpdateTropixObject(object); results = searchService.getTopLevelObjects(user2.getCagridId(), user1.getCagridId()); assert results.size() == 0 : results.size(); }
   */

  @Test
  public void getTopLevelObjectsVirtualGroup() {
    final User user3 = createTempUser();

    final VirtualFolder folder = new VirtualFolder();
    folder.setRoot(true);
    folder.setCommitted(true);
    getTropixObjectDao().saveOrUpdateTropixObject(folder);
    getTropixObjectDao().createVirtualPermission(folder.getId(), "read");
    // tropixObjectDao.addVirtualPermissionUser(folder.getId(), "read", user1.getCagridId());

    final Group group = super.createTempGroup();
    group.getUsers().add(user3);
    user3.getGroups().add(group);
    getDaoFactory().getDao(Group.class).saveObject(group);
    getUserDao().saveOrUpdateUser(user3);
    // assert ! securityProvider.canRead(folder.getId(), user3.getCagridId());
    // assert ! searchService.getTopLevelObjects(user3.getCagridId(), null).contains(folder);
    getTropixObjectDao().addVirtualPermissionGroup(folder.getId(), "read", group.getId());
    final Dao<VirtualPermission> vpDao = getDaoFactory().getDao(VirtualPermission.class);
    int count = 0;
    for(final VirtualPermission vp : vpDao.findAll()) {
      if(vp.getObjects().contains(folder)) {
        assert vp.getGroups().contains(group);
        count++;
      }
    }
    assert count == 1;
    assert securityProvider.canRead(folder.getId(), user3.getCagridId());
    assert searchService.getTopLevelObjects(user3.getCagridId(), null).contains(folder);

  }

  @Test
  public void getTopLevelObjectsVirtualUnique() {
    final User user1 = createTempUser();
    final Group group = createTempGroup(user1);
    final VirtualFolder folder = super.createTempRootVirtualFolder();
    getTropixObjectDao().addVirtualPermissionUser(folder.getId(), "read", user1.getCagridId());

    assert Iterables.frequency(searchService.getTopLevelObjects(user1.getCagridId(), null), folder) == 1;
    getTropixObjectDao().addVirtualPermissionGroup(folder.getId(), "read", group.getId());
    assert Iterables.frequency(searchService.getTopLevelObjects(user1.getCagridId(), null), folder) == 1;

    getTropixObjectDao().addVirtualPermissionUser(folder.getId(), "write", user1.getCagridId());
    assert Iterables.frequency(searchService.getTopLevelObjects(user1.getCagridId(), null), folder) == 1;

  }

  @Test
  public void getTopLevelObjectsVirtual() {
    final User user1 = createTempUser(), user2 = createTempUser(), user3 = createTempUser();

    final VirtualFolder folder = new VirtualFolder();
    folder.setRoot(true);
    folder.setCommitted(true);
    getTropixObjectDao().saveOrUpdateTropixObject(folder);
    getTropixObjectDao().createVirtualPermission(folder.getId(), "read");
    getTropixObjectDao().addVirtualPermissionUser(folder.getId(), "read", user1.getCagridId());

    final Group group = new Group();
    group.setUsers(new HashSet<User>());
    group.setName("Moo");

    final Dao<Group> groupDao = getDaoFactory().getDao(Group.class);
    groupDao.saveObject(group);

    group.getUsers().add(user3);
    user3.getGroups().add(group);
    groupDao.saveObject(group);
    getUserDao().saveOrUpdateUser(user3);

    assert searchService.getTopLevelObjects(user1.getCagridId(), null).contains(folder);
    assert !searchService.getTopLevelObjects(user2.getCagridId(), null).contains(folder);
    assert !searchService.getTopLevelObjects(user3.getCagridId(), null).contains(folder);

    assert searchService.getTopLevelObjects(user1.getCagridId(), user1.getCagridId()).contains(folder);
    assert !searchService.getTopLevelObjects(user2.getCagridId(), user1.getCagridId()).contains(folder);
    assert !searchService.getTopLevelObjects(user3.getCagridId(), user1.getCagridId()).contains(folder);

    getTropixObjectDao().setOwner(folder.getId(), user1);

    assert searchService.getTopLevelObjects(user1.getCagridId(), user1.getCagridId()).contains(folder);
    assert !searchService.getTopLevelObjects(user2.getCagridId(), user1.getCagridId()).contains(folder);
    assert !searchService.getTopLevelObjects(user3.getCagridId(), user1.getCagridId()).contains(folder);

    getTropixObjectDao().addVirtualPermissionUser(folder.getId(), "read", user2.getCagridId());

    assert searchService.getTopLevelObjects(user1.getCagridId(), user1.getCagridId()).contains(folder);
    assert searchService.getTopLevelObjects(user2.getCagridId(), user1.getCagridId()).contains(folder);
    assert !searchService.getTopLevelObjects(user3.getCagridId(), user1.getCagridId()).contains(folder);

    getTropixObjectDao().removeVirtualPermissionUser(folder.getId(), "read", user2.getCagridId());

    assert searchService.getTopLevelObjects(user1.getCagridId(), user1.getCagridId()).contains(folder);
    assert !searchService.getTopLevelObjects(user2.getCagridId(), user1.getCagridId()).contains(folder);

  }

}
