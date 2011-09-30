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

package edu.umn.msi.tropix.client.search.impl;

import info.minnesotapartnership.tropix.search.TropixSearchService;
import info.minnesotapartnership.tropix.search.models.Data;
import info.minnesotapartnership.tropix.search.models.File;
import info.minnesotapartnership.tropix.search.models.Study;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import org.easymock.classextension.EasyMock;
import org.testng.annotations.Test;

import com.google.common.base.Objects;

import edu.umn.msi.tropix.client.directory.GridUser;
import edu.umn.msi.tropix.client.search.models.GridData;
import edu.umn.msi.tropix.client.search.models.GridFile;
import edu.umn.msi.tropix.grid.GridServiceFactory;

public class TropixSearchClientImplTest {
  private final GridUser user1 = new GridUser();

  private void compare(final Data data, final GridData gridData) {
    assert Objects.equal(data.getDataId(), gridData.getId());
    assert Objects.equal(data.getDataCreationDate(), gridData.getCreationDate());
    assert Objects.equal(data.getDataDescription(), gridData.getDescription());
    assert Objects.equal(data.getDataName(), gridData.getName());
    assert Objects.equal(data.isDataHasChildren(), gridData.isDataHasChildren());
    assert Objects.equal(data.getDataOwnerId(), gridData.getOwnerId());
    if(Objects.equal(data.getDataOwnerId(), user1.getGridId())) {
      assert gridData.getUserName().equals(user1.toString());
    }
    if(data instanceof File) {
      assert gridData instanceof GridFile;
      final GridFile gridFile = (GridFile) gridData;
      final File file = (File) data;
      assert Objects.equal(file.getFileIdentifier(), gridFile.getFileIdentifier());
      assert Objects.equal(file.getFileType(), gridFile.getType());
      assert Objects.equal(file.getFileTypeDescription(), gridFile.getTypeDescription());
    }
  }

  public void verifyResults(final Data[] input, final List<GridData> outputs, final String serviceUrl) {
    int i = 0;
    for(final GridData output : outputs) {
      compare(input[i++], output);
      assert output.getServiceUrl().equals(serviceUrl);
    }
    assert i == outputs.size();
  }

  private Data[] getData() {
    final Data data1 = new Data();
    data1.setDataId("1234");
    data1.setDataName("Moo");
    data1.setDataHasChildren(true);
    data1.setDataDescription("this is a description");
    data1.setDataOwnerId("john123");

    final File file1 = new File();
    file1.setDataId("12345");
    file1.setFileIdentifier("345423452");
    file1.setFileType("The Type");
    file1.setFileTypeDescription("A description of the type");
    file1.setDataName("moocow");

    final Study study1 = new Study();
    study1.setDataId("123456");
    study1.setDataName("the study");

    final Data[] data = new Data[] {data1, file1, study1};
    return data;
  }

  enum Op {
    TOP_LEVEL, CHILDREN
  };

  @Test(groups = "unit")
  public void getTopLevelItems() throws RemoteException {
    test(Op.TOP_LEVEL, getData());
    test(Op.TOP_LEVEL, new Data[0]);
    test(Op.TOP_LEVEL, null);
  }

  @Test(groups = "unit")
  public void getChildItems() throws RemoteException {
    test(Op.CHILDREN, getData());
    test(Op.CHILDREN, new Data[0]);
    test(Op.CHILDREN, null);
  }

  private void test(final Op op, final Data[] data) throws RemoteException {
    final TropixSearchClientImpl client = new TropixSearchClientImpl();
    @SuppressWarnings("unchecked")
    final GridServiceFactory<TropixSearchService> sFactory = EasyMock.createMock(GridServiceFactory.class);
    client.setTropixSearchServiceFactory(sFactory);
    final String address = "http://Moo";
    final String inputId = "chilton";
    final TropixSearchService service = EasyMock.createMock(TropixSearchService.class);

    user1.setFirstName("John");
    user1.setGridId("john123");
    final GridUser user2 = new GridUser();
    user2.setFirstName("Bob");
    user2.setGridId("bob321");
    client.setGridUserIterable(Arrays.asList(user1, user2));

    EasyMock.expect(sFactory.getService(address, null)).andReturn(service);
    if(op.equals(Op.TOP_LEVEL)) {
      EasyMock.expect(service.getUsersTopLevelData(inputId)).andReturn(data);
    } else {
      EasyMock.expect(service.getChildren(inputId)).andReturn(data);
    }

    EasyMock.replay(sFactory, service);
    List<GridData> list;
    if(op.equals(Op.TOP_LEVEL)) {
      list = client.getTopLevelItems(address, null, inputId);
    } else {
      list = client.getChildItems(address, null, inputId);
    }
    verifyResults(data, list, address);
    EasyMock.verify(sFactory, service);
  }
}
