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

package edu.umn.msi.tropix.storage.core.access.fs;

import java.io.File;
import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.storage.core.PersistentFileMapperService;
import edu.umn.msi.tropix.storage.core.access.fs.PersistentFileMapperFileFunctionImpl;

public class PersistentFileMapperFileFunctionImplTest {

  @Test(groups = "unit")
  public void testApply() {
    final PersistentFileMapperService fileMapper = EasyMock.createMock(PersistentFileMapperService.class);
    final PersistentFileMapperFileFunctionImpl function = new PersistentFileMapperFileFunctionImpl(fileMapper);
    final String id = UUID.randomUUID().toString();
    EasyMock.expect(fileMapper.getPath(id)).andReturn("/tmp/moo");
    EasyMock.replay(fileMapper);
    assert function.apply(id).equals(new File("/tmp/moo"));
  }
  
}
