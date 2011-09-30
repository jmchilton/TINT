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
import java.util.LinkedList;

import org.easymock.EasyMock;

/**
 * A collection class designed to hold EasyMock mocks. This class
 * contains convince methods for replaying, verifying, etc... all
 * of the mocks at once.
 * 
 * @author John Chilton
 *
 */
public class MockObjectCollection extends LinkedList<Object> {
  private static final long serialVersionUID = 1L;

  public static MockObjectCollection fromObjects(final Object... mocks) {
    final MockObjectCollection collection = new MockObjectCollection();
    collection.addAll(Arrays.asList(mocks));
    return collection;
  }

  public void replay() {
    EasyMockUtils.replayAll(this.toArray());
  }

  public void verifyAndReset() {
    EasyMockUtils.verifyAndResetAll(this.toArray());
  }

  public void add(final Object... mocks) {
    this.addAll(Arrays.asList(mocks));
  }

  /**
   * This is a convince method for creating a mock and adding
   * it to the collection on mocks with one command.
   * 
   * The two lines:
   * <pre>
   * Foo foo = EasyMock.createMock(Foo.class);
   * collection.add(foo);
   * </pre>
   * can be replaced with the one line
   * <pre>
   * Foo foo = collection.createMock(Foo.class);
   * </pre>
   * 
   * 
   */
  public <T> T createMock(final Class<T> clazz) {
    final T object = EasyMock.createMock(clazz);
    this.add(object);
    return object;
  }

}
