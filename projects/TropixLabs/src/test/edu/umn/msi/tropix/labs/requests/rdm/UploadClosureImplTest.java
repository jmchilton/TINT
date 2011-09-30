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

package edu.umn.msi.tropix.labs.requests.rdm;

import java.io.File;
import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;

import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.MockObjectCollection;
import edu.umn.msi.tropix.common.test.EasyMockUtils.Reference;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.persistence.service.RequestService;
import edu.umn.msi.tropix.storage.client.ModelStorageData;

public class UploadClosureImplTest {

  @Test(groups = "unit")
  public void executeNormally() {
    // Setup participants
    final OutputContext outputContext = EasyMock.createMock(OutputContext.class);
    final ModelStorageData mockStorageData = EasyMock.createMock(ModelStorageData.class);
    final Supplier<ModelStorageData> supplier = Suppliers.ofInstance(mockStorageData);
    final RequestService requestService = EasyMock.createMock(RequestService.class);
    final MockObjectCollection mockObjects = MockObjectCollection.fromObjects(mockStorageData, requestService, outputContext);
    final String requestId = UUID.randomUUID().toString();
    final File requestDir = new File(System.getProperty("java.io.tmpdir"), requestId);
    assert requestDir.mkdirs();
    final File newFile = new File(requestDir, "moo.txt");
    final Reference<TropixFile> tropixFileReference = EasyMockUtils.newReference();

    // Setup uploadClosure
    final UploadClosureImpl uploadClosure = new UploadClosureImpl();
    uploadClosure.setModelStorageDataSupplier(supplier);
    uploadClosure.setRequestService(requestService);

    // Record
    EasyMock.expect(mockStorageData.getUploadContext()).andReturn(outputContext);
    EasyMock.expect(mockStorageData.getTropixFile()).andReturn(new TropixFile());
    outputContext.put(newFile);
    requestService.setupRequestFile(EasyMock.eq(requestId), EasyMockUtils.record(tropixFileReference));
    mockObjects.replay();

    // Test
    uploadClosure.apply(newFile);

    // Verify
    mockObjects.verifyAndReset();
    assert tropixFileReference != null;
    final TropixFile tropixFile = tropixFileReference.get();
    assert tropixFile.getName().equals("moo.txt");
    assert !tropixFile.getCommitted();
  }
}
