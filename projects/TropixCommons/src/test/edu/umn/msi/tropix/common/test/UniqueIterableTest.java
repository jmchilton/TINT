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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.collect.UniqueIterable;

// TODO: Fix SuppressWarnings
public class UniqueIterableTest {

  @Test(groups = "unit", expectedExceptions = UnsupportedOperationException.class)
  public void remove() {
    @SuppressWarnings("unchecked")
    final UniqueIterable<String> iterable = new UniqueIterable<String>(Arrays.<String>asList("moo"));
    iterable.iterator().remove();
  }

  @Test(groups = "unit", expectedExceptions = NoSuchElementException.class)
  public void noSuchElement() {
    @SuppressWarnings("unchecked")
    final UniqueIterable<String> iterable = new UniqueIterable<String>(Arrays.asList("moo"));
    final Iterator<String> iter = iterable.iterator();
    assert "moo".equals(iter.next());
    iter.next();
  }

  @Test(groups = "unit")
  public void emptyOne() {
    @SuppressWarnings("unchecked")
    final UniqueIterable<String> iterable = new UniqueIterable<String>(Lists.<String>newArrayList());
    final Iterator<String> iter = iterable.iterator();
    assert !iter.hasNext();
  }

  @Test(groups = "unit")
  public void emptyZero() {
    @SuppressWarnings("unchecked")
    final UniqueIterable<String> iterable = new UniqueIterable<String>();
    final Iterator<String> iter = iterable.iterator();
    assert !iter.hasNext();
  }

  @SuppressWarnings("unchecked")
  @Test(groups = "unit")
  public void single() {
    final List<List<String>> str = Arrays.<List<String>>asList(Arrays.<String>asList(), Arrays.asList("moo"), Arrays.asList("moo", "moo"));
    UniqueIterable<String> iterable = new UniqueIterable<String>(str.get(1));
    Iterator<String> iter = iterable.iterator();
    assert iter.next().equals("moo");
    assert !iter.hasNext();

    iterable = new UniqueIterable<String>(str.get(0), str.get(1));
    iter = iterable.iterator();
    assert iter.next().equals("moo");
    assert !iter.hasNext();

    iterable = new UniqueIterable<String>(str.get(0), str.get(1), str.get(0), str.get(2));
    iter = iterable.iterator();
    assert iter.next().equals("moo");
    assert !iter.hasNext();

    iterable = new UniqueIterable<String>(str);
    iter = iterable.iterator();
    assert iter.next().equals("moo");
    assert !iter.hasNext();

  }

}
