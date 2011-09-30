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

package edu.umn.msi.tropix.common.collect.spring;

import java.util.Arrays;

import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

public class JmxStringIterableTest {

  @Test(groups = "unit")
  public void setString() {
    final JmxStringIterable impl = new JmxStringIterable();
    impl.setContents("http://service/, http://service2");
    assert Iterables.elementsEqual(impl, Arrays.asList("http://service/", "http://service2"));
    assert impl.getContents().contains("http://service/");
    assert impl.getContents().contains("http://service2");
  }

  @Test(groups = "unit")
  public void addRemoveElement() {
    final JmxStringIterable impl = new JmxStringIterable();
    assert !Iterables.contains(impl, "moo");
    impl.addElement("moo");
    assert Iterables.contains(impl, "moo");
    impl.removeElement("moo");
    assert !Iterables.contains(impl, "moo");
  }

  @Test(groups = "unit")
  public void setStringOne() {
    final JmxStringIterable impl = new JmxStringIterable();
    impl.setContents("http://service/");
    assert Iterables.elementsEqual(impl, Arrays.asList("http://service/"));
    assert impl.getContents().equals("http://service/");
  }

  @Test(groups = "unit")
  public void setStringEmpty() {
    final JmxStringIterable impl = new JmxStringIterable();
    impl.setContents("");
    assert Iterables.elementsEqual(impl, Arrays.asList());
    assert impl.getContents().equals("");
  }

}
