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

import edu.umn.msi.tropix.models.BowtieIndex;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.service.BowtieIndexService;
import edu.umn.msi.tropix.persistence.service.impl.MessageConstants;
import edu.umn.msi.tropix.persistence.service.impl.StockFileExtensionEnum;

public class BowtieIndexServiceTest extends ServiceTest {
  @Autowired
  private BowtieIndexService bowtieIndexService;

  @Test
  public void create() {
    final User user = createTempUser();
    for(final Destination destination : super.getTestDestinations(user)) {
      final BowtieIndex bowtieIndex = new BowtieIndex();
      bowtieIndex.setName("index5");

      final TropixFile tropixFile = saveNewCommitted(new TropixFile(), user);
      bowtieIndexService.createBowtieIndex(user.getCagridId(), destination.getId(), bowtieIndex, tropixFile.getId());

      final BowtieIndex loadedBowtieIndex = (BowtieIndex) destination.getContents().iterator().next();
      assert loadedBowtieIndex.getIndexesFile() != null;
      assert loadedBowtieIndex.getIndexesFile().getId() != null;
      assert loadedBowtieIndex.getName().equals("index5");

      destination.validate(new TropixObject[]{loadedBowtieIndex, loadedBowtieIndex.getIndexesFile()});
      assert loadedBowtieIndex.getPermissionChildren().iterator().next().getId().equals(loadedBowtieIndex.getIndexesFile().getId());

      assert loadedBowtieIndex.getIndexesFile().getName().equals(getMessageSource().getMessage(MessageConstants.BOWTIE_INDEX_NAME, "index5"));
      assert loadedBowtieIndex.getIndexesFile().getDescription().equals(getMessageSource().getMessage(MessageConstants.BOWTIE_INDEX_DESCRIPTION, "index5"));
      assert loadedBowtieIndex.getIndexesFile().getFileType().equals(getFileType(StockFileExtensionEnum.BOWTIE_INDEX));
    }
  }

}
