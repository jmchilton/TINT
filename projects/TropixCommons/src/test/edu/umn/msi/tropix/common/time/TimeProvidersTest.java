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

package edu.umn.msi.tropix.common.time;

import org.testng.annotations.Test;

import com.google.common.collect.Lists;

public class TimeProvidersTest {

  @Test(groups = "unit")
  public void testConstructor() {
    new TimeProviders();
  }
  
  @Test(groups = "unit") 
  public void testStaticTimeInstance() {
    final TimeProvider provider = TimeProviders.getFixedTimeProvider(Lists.newArrayList(1L, 5L, 8L));
    assert provider.currentTimeMillis() == 1L;
    assert provider.currentTimeMillis() == 5L;
    assert provider.currentTimeMillis() == 8L;
  }
  
  
  @Test(groups = "unit")
  public void testDefaultInstance() throws InterruptedException {
    final long before = System.currentTimeMillis();
    // Sleep a little in the middle to account for possible small changes in CPU clock
    Thread.sleep(10);
    final long during = TimeProviders.getInstance().currentTimeMillis();
    Thread.sleep(10);
    final long after = System.currentTimeMillis();
    
    assert before <= during && during <= after;
  }
}
