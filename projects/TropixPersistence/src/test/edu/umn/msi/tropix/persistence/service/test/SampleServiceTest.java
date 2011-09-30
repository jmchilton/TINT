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

import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.TissueSample;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.persistence.service.SampleService;

public class SampleServiceTest extends ServiceTest {

  @Autowired
  private SampleService sampleService;

  @Test
  public void createTissue() {
    final User newUser = createTempUser();
    for(final Destination destination : super.getTestDestinations(newUser)) {
      final TissueSample tissueSample = new TissueSample();
      tissueSample.setName("Tissue Sample");
      tissueSample.setSpecies("Cow");
      tissueSample.setCommitted(true);
      sampleService.createTissueSample(newUser.getCagridId(), destination.getId(), tissueSample);

      final TissueSample loadedSample = (TissueSample) destination.getContents().iterator().next();
      assert loadedSample.getName().equals("Tissue Sample");
      assert loadedSample.getSpecies().equals("Cow");
      destination.validate(new TropixObject[]{loadedSample});
      assert loadedSample.getCommitted();
      assert loadedSample.getPermissionChildren().isEmpty();
    }
  }

  @Test(expectedExceptions = RuntimeException.class)
  public void createInvalidFolder() {
    final User newUser1 = createTempUser(), newUser2 = createTempUser();

    final TissueSample tissueSample = new TissueSample();
    final String id = newId();
    tissueSample.setName("Tissue Sample");
    tissueSample.setId(id);
    tissueSample.setSpecies("Cow");
    sampleService.createTissueSample(newUser1.getCagridId(), newUser2.getHomeFolder().getId(), tissueSample);
  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void createWithNullFolder() {
    final User newUser1 = createTempUser();
    final TissueSample tissueSample = new TissueSample();
    final String id = newId();
    tissueSample.setName("Tissue Sample");
    tissueSample.setId(id);
    tissueSample.setSpecies("Cow");
    sampleService.createTissueSample(newUser1.getCagridId(), null, tissueSample);
  }

  @Test
  public void proteomicsRunIds() {
    final User user = createTempUser(), otherUser = createTempUser();

    final ProteomicsRun run1 = new ProteomicsRun();
    run1.setCommitted(true);
    final ProteomicsRun run2 = new ProteomicsRun();
    run2.setCommitted(true);
    final ProteomicsRun run3 = new ProteomicsRun();
    run3.setCommitted(false);
    final ProteomicsRun run4 = new ProteomicsRun();
    run4.setCommitted(true);
    run4.setDeletedTime("1234556789");
    final ProteomicsRun run5 = new ProteomicsRun();
    run5.setCommitted(true);

    saveNewTropixObject(run1, user);
    saveNewTropixObject(run2, user);
    saveNewTropixObject(run3, user);
    saveNewTropixObject(run4, user);
    saveNewTropixObject(run5, otherUser);

    final TissueSample sample1 = new TissueSample();
    final HashSet<ProteomicsRun> runs = new HashSet<ProteomicsRun>();
    runs.add(run1);
    runs.add(run3);
    runs.add(run2);
    runs.add(run4);
    runs.add(run5);

    sample1.setProteomicsRuns(runs);
    saveNewTropixObject(sample1);

    getTropixObjectDao().setOwner(sample1.getId(), user);
    ProteomicsRun[] returnedRuns = sampleService.getProteomicsRuns(user.getCagridId(), sample1.getId());

    assert !run1.getId().equals(run2.getId());
    assert returnedRuns.length == 2;
    assert returnedRuns[0] != null && returnedRuns[1] != null;
    assert returnedRuns[0].getId() != null && returnedRuns[1].getId() != null;
    assert run1.getId().equals(returnedRuns[0].getId()) || run1.getId().equals(returnedRuns[1].getId());
    assert run2.getId().equals(returnedRuns[0].getId()) || run2.getId().equals(returnedRuns[1].getId());

    final TissueSample emptySample = new TissueSample();
    saveNewTropixObject(emptySample);
    getTropixObjectDao().setOwner(emptySample.getId(), user);
    returnedRuns = sampleService.getProteomicsRuns(user.getCagridId(), emptySample.getId());
    assert returnedRuns.length == 0;

  }
}
