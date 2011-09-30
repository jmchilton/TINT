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

package edu.umn.msi.tropix.common.collect;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.testng.annotations.Test;

public class ClosuresTest {

  @Test(groups = "unit")
  public void nullClosure() {
    Closures.nullClosure().apply("Moo");
  }

  @Test(groups = "unit")
  public void forEach() {
    final List<Object> objects = new LinkedList<Object>();
    final List<String> strings = Arrays.asList("moo", "cow");
    Closures.forEach(strings, new Closure<Object>() {
      public void apply(final Object input) {
        objects.add(input);
      }
    });
    assert objects.contains("moo");
    assert objects.contains("cow");
    assert objects.size() == 2;
  }

  static class IncClosure implements Closure<Object> {
    private int count = 0;

    public void apply(final Object input) {
      count++;
    }
  }

  @SuppressWarnings("unchecked")
  @Test(groups = "unit")
  public void compose() {
    final IncClosure closure = new IncClosure();
    Closures.compose(closure, closure, closure).apply(4);
    assert closure.count == 3;
  }
}
