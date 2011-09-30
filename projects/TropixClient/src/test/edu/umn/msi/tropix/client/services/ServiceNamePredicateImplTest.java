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

package edu.umn.msi.tropix.client.services;

import java.util.Arrays;

import org.testng.annotations.Test;

public class ServiceNamePredicateImplTest {

  @Test(groups = "unit")
  public void setValidName() {
    final ServiceNamePredicateImpl predicate = new ServiceNamePredicateImpl();
    predicate.setValidName("Scaffold");
    assert predicate.apply("http://service/wsrf/cagrid/Scaffold");
    assert !predicate.apply("http://service/wsrf/cagrid/Scaffold/ScaffoldJobContext");
  }

  @Test(groups = "unit")
  public void setValidNames() {
    final ServiceNamePredicateImpl predicate = new ServiceNamePredicateImpl();
    predicate.setValidNames(Arrays.asList("Sequest", "XTandem"));

    assert predicate.apply("http://service/wsrf/cagrid/Sequest");
    assert predicate.apply("http://msi.umn.edu/wsrf/cagrid/XTandem");
    assert !predicate.apply("http://service/wsrf/cagrid/InvalidXTandem");
  }

}
