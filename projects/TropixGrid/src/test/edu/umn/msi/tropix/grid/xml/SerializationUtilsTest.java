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

package edu.umn.msi.tropix.grid.xml;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.xml.XMLException;
import gov.nih.nci.cagrid.metadata.common.ResearchCenter;

import java.io.File;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.io.output.NullWriter;
import org.testng.annotations.Test;

public class SerializationUtilsTest {
  private static final SerializationUtils UTILS = SerializationUtilsFactory.getInstance();
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();

  @Test(groups = "unit", expectedExceptions = XMLException.class, timeOut = 100)
  public void urlProblem() throws MalformedURLException {
    UTILS.deserialize(new URL("file://not.a.file"), ResearchCenter.class);    
  }
  
  @Test(groups = "unit", expectedExceptions = XMLException.class)
  public void deserializationProblem() {
    UTILS.deserialize(new StringReader("No actually XML."), ResearchCenter.class);    
  }
  
  @Test(groups = "unit", expectedExceptions = XMLException.class)
  public void serializationProblem() {
    final ResearchCenter inputCenter = new ResearchCenter();
    inputCenter.setDisplayName("Moo");
    UTILS.serialize(new NullWriter(), ResearchCenter.class, ResearchCenter.getTypeDesc().getXmlType());    
  }
  
  @Test(groups = "unit")
  public void serializeDeserialize() {
    final File tempFile = FILE_UTILS.createTempFile();
    try {
      final ResearchCenter inputCenter = new ResearchCenter();
      inputCenter.setDisplayName("Moo");
      UTILS.serialize(FILE_UTILS.getFileWriter(tempFile), inputCenter, ResearchCenter.getTypeDesc().getXmlType());
      final ResearchCenter outputCenter = UTILS.deserialize(tempFile, ResearchCenter.class);
      assert outputCenter.getDisplayName().equals("Moo");
    } finally {
      FILE_UTILS.deleteQuietly(tempFile);
    }
  }
}
