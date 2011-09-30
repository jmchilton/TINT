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

package edu.umn.msi.tropix.common.io;

import java.io.File;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

public class DirectoriesTest {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();

  @Test(groups = "unit")
  public void buildAbsolutePathUnixStyle() {
    final Directory directory = EasyMock.createMock(Directory.class);
    EasyMock.expect(directory.getAbsolutePath()).andStubReturn("/home/moo");
    EasyMock.expect(directory.getSep()).andStubReturn("/");
    EasyMock.replay(directory);
    assert Directories.buildAbsolutePath(directory, "Hello", "World").equals("/home/moo/Hello/World");
    assert Directories.buildAbsolutePath(directory).equals("/home/moo");
  }

  @Test(groups = "unit")
  public void buildAbsolutePathWindowstyle() {
    final Directory directory = EasyMock.createMock(Directory.class);
    EasyMock.expect(directory.getAbsolutePath()).andStubReturn("C:\\moo");
    EasyMock.expect(directory.getSep()).andStubReturn("\\");
    EasyMock.replay(directory);
    assert Directories.buildAbsolutePath(directory, "Hello", "World").equals("C:\\moo\\Hello\\World");
  }

  @Test(groups = "unit")
  public void fromFile() {
    final File tempDirectory = FILE_UTILS.createTempDirectory();
    final Directory directory = Directories.fromFile(tempDirectory);
    assert directory.getAbsolutePath().equals(tempDirectory.getAbsolutePath());
  }
}
