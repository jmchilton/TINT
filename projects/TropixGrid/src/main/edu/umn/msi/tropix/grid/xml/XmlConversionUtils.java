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

import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.QName;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.common.xml.XMLUtility;
import gov.nih.nci.cagrid.common.Utils;

public class XmlConversionUtils {
  private static final SerializationUtils SERIALIZATION_UTILS = SerializationUtilsFactory.getInstance();

  @SuppressWarnings("unchecked")
  public static Object jaxbToCaGrid(final Object input, final XMLUtility wrapper, final Class c) {
    try {
      final String inputXml = wrapper.toString(input);
      return Utils.deserializeObject(new StringReader(inputXml), c);
    } catch(final Exception e) {
      throw new IllegalArgumentException("Failed to convert xml based object.", e);
    }
  }

  @SuppressWarnings("unchecked")
  public static Object caGridToJaxb(final Object input, final XMLUtility wrapper, final QName qname) {
    Preconditions.checkNotNull(input);
    final StringWriter xmlWriter = new StringWriter();
    SERIALIZATION_UTILS.serialize(xmlWriter, input, qname);
    final String xml = xmlWriter.toString();
    return wrapper.fromString(xml);
  }
}
