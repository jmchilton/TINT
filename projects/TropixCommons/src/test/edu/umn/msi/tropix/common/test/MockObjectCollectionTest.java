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

import org.easymock.Capture;
import org.easymock.classextension.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.collect.Closure;

public class MockObjectCollectionTest {

  @Test(groups = "unit")
  public void replayAndReset() {
    for(final boolean add : new boolean[] {true, false}) {

      @SuppressWarnings("unchecked")
      final Closure<Object> closure = EasyMock.createMock(Closure.class);

      MockObjectCollection collection;
      if(add) {
        collection = new MockObjectCollection();
        collection.add(closure);
      } else {
        collection = MockObjectCollection.fromObjects(closure);
      }
      final Capture<Object> objectCapture = new Capture<Object>();
      closure.apply(EasyMock.capture(objectCapture));
      collection.replay();
      closure.apply("Moo");
      collection.verifyAndReset();
      assert objectCapture.getValue().equals("Moo");
    }
  }
}
