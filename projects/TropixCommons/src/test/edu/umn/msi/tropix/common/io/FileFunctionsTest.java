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
import java.io.IOException;
import java.io.InputStream;

import org.testng.annotations.Test;

public class FileFunctionsTest {

  @Test(groups = "unit")
  public void getInputStreamFunction() throws IOException {
    final File file = File.createTempFile("tpxtst", "");
    try {
      org.apache.commons.io.FileUtils.writeStringToFile(file, "Moo Cow");
      final InputStream stream = FileFunctions.getInputStreamFunction().apply(file);
      assert org.apache.commons.io.IOUtils.toString(stream).equals("Moo Cow");
    } finally {
      file.delete();
    }
  }

  @Test(groups = "unit")
  public void pathToFile() {
    assert new File("moo").equals(FileFunctions.getPathToFileFunction().apply("moo"));
  }
}
