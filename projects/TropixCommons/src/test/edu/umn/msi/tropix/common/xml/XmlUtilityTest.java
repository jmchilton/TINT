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

package edu.umn.msi.tropix.common.xml;

import java.io.ByteArrayOutputStream;
import java.io.File;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.io.OutputContexts;

public class XmlUtilityTest {
  private static final String DEFAULT_NAMESPACE = "xmlns=\"http://test/namespace\"";
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();

  private static String fillInNamespace(final String template) {
    return String.format(template, DEFAULT_NAMESPACE);
  }
  
  @Test(groups = "unit")
  public void deserializeWithNamespaceFilter() {
    final XMLUtility<TestElement> util = new XMLUtility<TestElement>(TestElement.class);
    util.setNamespaceFilter("http://test/namespace");
    util.deserialize("<TestElement deltaCn=\"4.4\" />".getBytes());
  }
  
  @Test(groups = "unit")
  public void deserializeInMemory() {
    final XMLUtility<TestElement> util = new XMLUtility<TestElement>(TestElement.class);
    TestElement testElement = util.deserialize(InputContexts.forString(fillInNamespace("<TestElement %s deltaCn=\"4.4\" />")));
    assert util.serialize(testElement).contains("deltaCn=\"4.4\"");

    testElement = util.fromString(fillInNamespace("<TestElement %s deltaCn=\"4.4\" />"));
    assert util.toString(testElement).contains("deltaCn=\"4.4\"");

    testElement = util.deserialize(fillInNamespace("<TestElement %s deltaCn=\"4.4\" />").getBytes());
    assert util.serialize(testElement).contains("deltaCn=\"4.4\"");
    final StringBuffer buffer = new StringBuffer();
    util.serialize(testElement, buffer);
    assert buffer.toString().contains("deltaCn=\"4.4\"");
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    util.serialize(testElement, outputStream);
    assert new String(outputStream.toByteArray()).contains("deltaCn=\"4.4\"");
    
    outputStream = new ByteArrayOutputStream();
    util.serialize(testElement, OutputContexts.forOutputStream(outputStream));
    assert new String(outputStream.toByteArray()).contains("deltaCn=\"4.4\"");
  }
  
  @Test(groups = "unit")
  public void deserializePath() {
    File inputFile = null, outFile = null;
    try {
      inputFile = FILE_UTILS.createTempFile();
      outFile = FILE_UTILS.createTempFile();
      final FormattedXmlUtility<TestElement> util = new FormattedXmlUtility<TestElement>(TestElement.class);
      FILE_UTILS.writeStringToFile(inputFile, fillInNamespace("<TestElement %s deltaCn=\"4.4\" />"));
      final TestElement testElement = util.deserialize(inputFile.getAbsolutePath());
      util.serialize(testElement, outFile.getAbsolutePath());
      assert FILE_UTILS.readFileToString(outFile).contains("deltaCn=\"4.4\"");
    } finally {
      FILE_UTILS.deleteQuietly(inputFile);
      FILE_UTILS.deleteQuietly(outFile);
    }
  }

}
