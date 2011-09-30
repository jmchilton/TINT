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

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.xml.XMLUtility;

public class XMLUtilityTest {

  @SuppressWarnings("unchecked")
  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void exception() {
    new XMLUtility(String.class);
  }

  /*
   * @Test(groups="unit",expectedExceptions=RuntimeException.class) public void serializationException() { XMLUtility<FileSet> util = new XMLUtility(FileSet.class); util.serialize(new FileSet(), new OutputStreamWriter(new ClosedOutputStream())); }
   */

  /*
   * @Test(groups="unit",expectedExceptions=RuntimeException.class) public void deserializationException() { XMLUtility<FileSet> util = new XMLUtility(FileSet.class); util.deserialize(new ClosedInputStream()); }
   */

  /*
   * @Test(groups="unit")
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * public void sb() { XMLUtility<FileSet> util = new XMLUtility(FileSet.class); StringBuffer sb = new StringBuffer(); util.serialize(new FileSet(), sb); util.deserialize(new StringReader(sb.toString())); }
   * 
   * @Test(groups="unit") public void serializePath() throws IOException { XMLUtility<FileSet> util = new XMLUtility(FileSet.class); File file = File.createTempFile("moo", "test"); util.serialize(new FileSet(), file.getAbsolutePath()); util.deserialize(file.getAbsolutePath()); }
   */

}
