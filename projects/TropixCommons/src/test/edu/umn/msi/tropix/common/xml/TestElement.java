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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {})
@XmlRootElement(name = "TestElement", namespace="http://test/namespace")
public class TestElement {

  @XmlAttribute
  private Float deltaCn;
  @XmlAttribute
  private String xCorrs;

  /**
   * Gets the value of the deltaCn property.
   * 
   * @return possible object is {@link Float }
   * 
   */
  public Float getDeltaCn() {
    return deltaCn;
  }

  /**
   * Sets the value of the deltaCn property.
   * 
   * @param value
   *          allowed object is {@link Float }
   * 
   */
  public void setDeltaCn(final Float value) {
    this.deltaCn = value;
  }

  /**
   * Gets the value of the xCorrs property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getXCorrs() {
    return xCorrs;
  }

  /**
   * Sets the value of the xCorrs property.
   * 
   * @param value
   *          allowed object is {@link String }
   * 
   */
  public void setXCorrs(final String value) {
    this.xCorrs = value;
  }

}
