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

package edu.umn.msi.tropix.models.utils;

import java.util.Date;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.TropixObject;

public class ModelPredicatesTest {

  @Test(groups = "unit")
  public void testConstructor() {
    new ModelPredicates();
  }

  @Test(groups = "unit")
  public void testTypePredicate() {
    assert ModelPredicates.getTypePredicate(TropixObjectTypeEnum.ANALYSIS).apply(new IdentificationAnalysis());
    assert !ModelPredicates.getTypePredicate(TropixObjectTypeEnum.ANALYSIS).apply(new TropixObject());
    assert !ModelPredicates.getTypePredicate(TropixObjectTypeEnum.ANALYSIS).apply(new Folder());
  }

  @Test(groups = "unit")
  public void testDeletedObjectsInvalid() {
    final TropixObject object = new TropixObject();
    object.setDeletedTime(new Date().getTime() + "");
    assert !ModelPredicates.isValidObjectPredicate().apply(object);
  }

  @Test(groups = "unit")
  public void testUncommittedObjectsInvalid() {
    final TropixObject object = new TropixObject();
    object.setCommitted(false);
    assert !ModelPredicates.isValidObjectPredicate().apply(object);
    object.setCommitted(null);
    assert !ModelPredicates.isValidObjectPredicate().apply(object);
  }

  @Test(groups = "unit")
  public void testCommittedObjectsIsValid() {
    final TropixObject object = new TropixObject();
    object.setCommitted(true);
    assert ModelPredicates.isValidObjectPredicate().apply(object);
  }

}
