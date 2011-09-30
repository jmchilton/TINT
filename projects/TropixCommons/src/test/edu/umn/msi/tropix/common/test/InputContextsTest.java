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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.InputContexts;

public class InputContextsTest {

  @Test(groups = "unit")
  public void iterableTest() {
    final List<File> files = new ArrayList<File>(10);
    final List<InputContext> contexts = new ArrayList<InputContext>(10);
    for(int i = 0; i < 10; i++) {
      final InputContext inputContext = EasyMock.createMock(InputContext.class);
      final File file = new File("moo" + i);
      inputContext.get(file);
      files.add(file);
      contexts.add(inputContext);
    }
    EasyMockUtils.replayAll(contexts);
    InputContexts.put(contexts, files);
    EasyMockUtils.verifyAll(contexts);
  }

}
