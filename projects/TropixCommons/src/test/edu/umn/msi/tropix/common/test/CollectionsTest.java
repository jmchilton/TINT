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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.collect.Collections;

public class CollectionsTest {

  @Test(groups = "unit")
  public void transform() {
    final List<Integer> integers = Arrays.asList(1, 2, 3, 4);
    final Function<Integer, Integer> doubleF = new Function<Integer, Integer>() {
      public Integer apply(final Integer input) {
        return input * 2;
      }
    };
    Collection<Integer> output = Collections.transform(integers, doubleF);
    final Iterator<Integer> outputIter = output.iterator();
    assert outputIter.next().equals(2);
    assert outputIter.next().equals(4);
    assert outputIter.next().equals(6);
    assert outputIter.next().equals(8);
    assert !outputIter.hasNext();
    output = Lists.newLinkedList();
    Collections.transform(integers, doubleF, output);
    assert output.equals(Arrays.asList(2, 4, 6, 8));

    assert Collections.transform(Arrays.<Integer>asList(), doubleF).isEmpty();

  }

}
