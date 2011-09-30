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

import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.models.utils.TropixObjectTypeEnum;
import edu.umn.msi.tropix.persistence.service.TropixObjectService;

public class TropixObjectServiceGetChildrenTest extends ServiceTest {
  @Autowired
  private TropixObjectService tropixObjectService;

  @Test
  public void testGetChildren() {
    final User user = createTempUser();
    final String userId = user.getCagridId();
    final ProteomicsRun run = saveNewCommitted(new ProteomicsRun(), user);
    
    assert tropixObjectService.getChildren(userId, run.getId()).length == 0;
    assert tropixObjectService.getChildren(userId, run.getId(), new TropixObjectTypeEnum[] {TropixObjectTypeEnum.FILE}).length == 0;

    final TropixFile mzxmlFile = saveNewCommitted(new TropixFile(), user);
    run.setMzxml(mzxmlFile);
    getTropixObjectDao().saveOrUpdateTropixObject(mzxmlFile);
    getTropixObjectDao().saveOrUpdateTropixObject(run);
    
    assert tropixObjectService.getChildren(userId, run.getId()).length == 1;
    assert tropixObjectService.getChildren(userId, run.getId(), new TropixObjectTypeEnum[] {TropixObjectTypeEnum.FILE}).length == 1;
    assert tropixObjectService.getChildren(userId, run.getId(), new TropixObjectTypeEnum[] {TropixObjectTypeEnum.PROTEOMICS_RUN}).length == 0;
  }
  
}
