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

package edu.umn.msi.tropix.common.prediction.impl;

import org.testng.annotations.Test;

public class AverageModelImplTest {
  private void assertAlmostEquals(final double x1, final double x2) {
    assert Math.abs(x1 - x2) < .001;
  }

  @Test(groups = "unit")
  public void runningAverage() {
    final AverageModelImpl model = new AverageModelImpl();
    assert model.predict(null) == null;
    assert model.predict(new double[] {1.0, 2.0}) == null;
    model.addData(null, 1.0);
    this.assertAlmostEquals(model.predict(null), 1.0);
    model.addData(null, 1.0);
    this.assertAlmostEquals(model.predict(null), 1.0);
    model.addData(null, 2.0);
    model.addData(null, 2.0);
    this.assertAlmostEquals(model.predict(null), 1.5);
    model.addData(null, 0.0);
    model.addData(null, 0.0);
    model.addData(null, 0.0);
    model.addData(null, 0.0);
    this.assertAlmostEquals(model.predict(null), 0.75);
  }
}
