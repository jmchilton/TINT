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

package edu.umn.msi.tropix.webgui.server.models;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import edu.umn.msi.tropix.models.FileType;
import edu.umn.msi.tropix.models.Folder;
import edu.umn.msi.tropix.models.TropixFile;

public class BeanSanitizerImplTest {
  
  public static class TropixFile2 extends TropixFile {
    public String getFoo(final int x) { // Make sure it doesn't choke on get method that takes in args
      return "" + x;
    }
    
    public String getFoo2() { // Make sure it doesn't choke on a get method without corresponding setter
      return "foo2";
    }
    
    public String getFoo3() { // Make sure it doesn't choke when getter throws exception
      throw new IllegalStateException("Simulated lazy initialization failure");
    }
    
    public void setFoo3(final String foo3) {
    }
  }
  
  public static class TropixFileInstrumentedjavassist extends TropixFile2 {
  }
  public static class FileTypeInstrumentedjavassist extends FileType {
  }
  
  @Test(groups = "unit", dataProvider = "bool1", dataProviderClass=TestNGDataProviders.class)
  public void testSanitize(final boolean instrumented) {
    final BeanSanitizerImpl beanSanitizer = new BeanSanitizerImpl();
    final TropixFile2 tropixFile = instrumented ? new TropixFileInstrumentedjavassist() : new TropixFile2();
    tropixFile.setName("test_name");
    tropixFile.setId("test_id");
    tropixFile.setParentFolder(new Folder());
    final FileType fileType = instrumented ? new FileTypeInstrumentedjavassist() : new FileType();
    fileType.setExtension("moo");
    fileType.setShortName("moo cow");
    fileType.setId("type_id");
    tropixFile.setFileType(fileType);
    final TropixFile2 sanitizedFile = beanSanitizer.sanitize(tropixFile);
    assert !(sanitizedFile instanceof TropixFileInstrumentedjavassist);
    assert sanitizedFile.getName().equals("test_name");
    assert sanitizedFile.getId().equals("test_id");
    
    // Verify it doesn't try to copy composite types that explictly told too
    assert sanitizedFile.getParentFolder() == null;
    
    // Verify explict mappings are copied
    final FileType sanitizedFileType = sanitizedFile.getFileType();
    assert sanitizedFileType != null;
    assert sanitizedFileType.getId().equals("type_id");
    assert sanitizedFileType.getShortName().equals("moo cow");
    assert sanitizedFileType.getExtension().equals("moo");
  }
  
}
