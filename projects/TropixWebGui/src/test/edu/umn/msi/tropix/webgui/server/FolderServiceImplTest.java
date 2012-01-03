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

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.VirtualFolder;
import edu.umn.msi.tropix.models.utils.TropixObjectContext;
import edu.umn.msi.tropix.models.utils.TropixObjectContexts;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.persistence.service.FolderService;
import edu.umn.msi.tropix.persistence.service.ProviderService;

public class FolderServiceImplTest extends BaseGwtServiceTest {
  private FolderService folderService;
  private ProviderService providerService;
  private String folderId;
  private FolderServiceImpl gwtFolderService;

  @BeforeMethod(groups = "unit")
  public void init() {
    super.init();
    folderId = UUID.randomUUID().toString();
    folderService = EasyMock.createMock(FolderService.class);
    providerService = EasyMock.createMock(ProviderService.class);
    gwtFolderService = new FolderServiceImpl(folderService, getUserSession(), getSanitizer(), providerService);
  }

  @Test(groups = "unit")
  public void getAllGroupFolders() {
    final Folder folder1 = createTropixObject(Folder.class);
    EasyMock.expect(folderService.getAllGroupFolders(getUserId())).andReturn(new Folder[] {folder1});
    replay();
    verifyOneObjectReturned(gwtFolderService.getAllGroupFolders(), folder1);
  }

  @Test(groups = "unit")
  public void getGroupFolders() {
    final Folder folder1 = createTropixObject(Folder.class);
    EasyMock.expect(folderService.getGroupFolders(getUserId())).andReturn(new Folder[] {folder1});
    replay();
    verifyOneObjectReturned(gwtFolderService.getGroupFolders(), folder1);
  }

  @Test(groups = "unit")
  public void getGroupSharedFolders() {
    final String groupId = UUID.randomUUID().toString();
    final VirtualFolder folder1 = createTropixObject(VirtualFolder.class);
    final TropixObjectContext<VirtualFolder> context = new TropixObjectContext<VirtualFolder>(TropixObjectContexts.getOwnerContext(), folder1);
    EasyMock.expect(folderService.getGroupSharedFolders(getUserId(), groupId)).andReturn(Lists.newArrayList(context));
    replay();
    verifyOneContextReturned(gwtFolderService.getGroupSharedFolders(groupId), folder1);
  }

  @Test(groups = "unit", dataProvider = "bool1", dataProviderClass = TestNGDataProviders.class)
  public void getFolderContents(final boolean withTypes) {
    final TropixObjectTypeEnum[] types = withTypes ? new TropixObjectTypeEnum[] {TropixObjectTypeEnum.FILE} : null;
    final TropixObject object1 = createTropixObject(TropixObject.class);
    final TropixObject object2 = createTropixObject(TropixObject.class);
    final TropixObject[] objects = new TropixObject[] {object1, object2};
    if(withTypes) {
      EasyMock.expect(folderService.getFolderContents(getUserId(), folderId, types));
    } else {
      EasyMock.expect(folderService.getFolderContents(getUserId(), folderId));
    }
    EasyMock.expectLastCall().andReturn(objects);
    replay();
    assert Iterables.elementsEqual(gwtFolderService.getFolderContents(folderId, types), Lists.newArrayList(object1, object2));
    assert getSanitizer().wasSanitized(object1);
    assert getSanitizer().wasSanitized(object2);
    EasyMock.verify(folderService);
  }

  private void replay() {
    EasyMock.replay(folderService);
  }

  private <T extends TropixObject> void verifyOneContextReturned(final Iterable<? extends TropixObjectContext<T>> results, final T expectedFolder) {
    verifyOneObjectReturned(Iterables.transform(results, new Function<TropixObjectContext<? extends T>, T>() {
      public T apply(final TropixObjectContext<? extends T> input) {
        return input.getTropixObject();
      }
    }), expectedFolder);
  }

  private <T> void verifyOneObjectReturned(final Iterable<T> results, final T expectedFolder) {
    assert Iterables.elementsEqual(results, Lists.<T>newArrayList(expectedFolder));
    assert getSanitizer().wasSanitized(expectedFolder);
    EasyMock.verify(folderService);
  }

}
