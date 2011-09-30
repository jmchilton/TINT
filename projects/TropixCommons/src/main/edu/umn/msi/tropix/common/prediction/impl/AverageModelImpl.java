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

import edu.umn.msi.tropix.common.prediction.Model;

/**
 * A model that makes predictions using a running average
 * of its input data.
 * 
 * @author John Chilton
 *
 */
public class AverageModelImpl implements Model {
  private double average = 0.0;
  private long count = 0;

  public void addData(final double[] xs, final double y) {
    if(this.count == 0) {
      this.average = y;
    } else {
      this.average = this.average + (y - this.average) / (this.count + 1);
    }
    this.count++;
  }

  public Double predict(final double[] x) {
    if(this.count == 0) {
      return null;
    } else {
      return this.average;
    }
  }

}
