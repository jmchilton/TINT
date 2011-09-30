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

package edu.umn.msi.tropix.common.test;

import org.testng.annotations.DataProvider;

/**
 * This class contains generic TestNG DataProvider 
 * methods for general use.
 * 
 * @author John Chilton
 *
 */
public class TestNGDataProviders {

  /**
   * 
   * @return true and false as parameters for a test method that takes in one boolean
   * parameter.
   */
  @DataProvider
  public static Object[][] bool1() {
    return new Object[][] {new Object[]{true}, new Object[]{false}};
  }

  /**
   * 
   * @return true and false permutations as parameters for a test method that takes in two boolean
   * parameters.
   */
  @DataProvider
  public static Object[][] bool2() {
    return new Object[][] {new Object[]{true, true}, new Object[]{false, true},
                           new Object[]{true, false}, new Object[]{false, false}};
  }
  
}
