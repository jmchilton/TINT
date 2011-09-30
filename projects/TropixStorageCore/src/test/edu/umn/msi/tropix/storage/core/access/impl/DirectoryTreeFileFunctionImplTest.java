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

package edu.umn.msi.tropix.storage.core.access.impl;

import java.io.File;
import java.util.UUID;

import org.testng.annotations.Test;

public class DirectoryTreeFileFunctionImplTest {

  @Test(groups = "unit", expectedExceptions = IllegalArgumentException.class)
  public void invalidDepthTooSmall() {
    final DirectoryTreeFileFunctionImpl function = new DirectoryTreeFileFunctionImpl();
    function.setTreeDepth(-1);
  }

  @Test(groups = "unit", expectedExceptions = IllegalArgumentException.class)
  public void invalidWidthTooSmall() {
    final DirectoryTreeFileFunctionImpl function = new DirectoryTreeFileFunctionImpl();
    function.setTreeWidth(0);
  }

  @Test(groups = "unit", expectedExceptions = IllegalArgumentException.class)
  public void invalidWidthTooLarge() {
    final DirectoryTreeFileFunctionImpl function = new DirectoryTreeFileFunctionImpl();
    function.setTreeWidth(10000000);
  }

  @Test(groups = "unit")
  public void apply() {
    final DirectoryTreeFileFunctionImpl function = new DirectoryTreeFileFunctionImpl();
    function.setTreeWidth(1);
    function.setTreeDepth(1);
    function.setDirectory("moo");
    final String id = UUID.randomUUID().toString();
    File file = function.apply(id);
    // Verify depth of 1
    assert file.getParentFile().getParentFile().equals(new File("moo"));
    // Verify width of 1
    assert file.getParentFile().getName().length() == 1;

    function.setTreeWidth(2);
    function.setTreeDepth(2);
    file = function.apply(id);
    // Verify depth of 2
    assert file.getParentFile().getParentFile().getParentFile().equals(new File("moo"));
    // Verify width of 2
    assert file.getParentFile().getName().length() == 2;
    assert file.getParentFile().getParentFile().getName().length() == 2;
    assert file.getName().equals(id);
  }

}
