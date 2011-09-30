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

package edu.umn.msi.tropix.webgui.client.identification;

class ValidationInfo {
  private String[] domain; // null if not validate against domain
  private String minValString;
  private String maxValString;
  private String regex;
  private String type;
  private boolean isList;
  
  public void setDomain(final String[] domain) {
    this.domain = domain;
  }
  public String[] getDomain() {
    return domain;
  }
  public void setMinValString(final String minValString) {
    this.minValString = minValString;
  }
  public String getMinValString() {
    return minValString;
  }
  public void setMaxValString(final String maxValString) {
    this.maxValString = maxValString;
  }
  public String getMaxValString() {
    return maxValString;
  }
  public void setRegex(final String regex) {
    this.regex = regex;
  }
  public String getRegex() {
    return regex;
  }
  public void setType(final String type) {
    this.type = type;
  }
  public String getType() {
    return type;
  }
  public void setList(final boolean isList) {
    this.isList = isList;
  }
  public boolean isList() {
    return isList;
  }
}

// Copied from the proteomics project (proteomics.jar). Should find a better
// way of sharing this code!

// Imports and dependencies kept to a minimum so GWT can make use of this class
public abstract class AbstractParameterValidator {
  public static final String TRUE_CONSTANT = "1";
  public static final String FALSE_CONSTANT = "0";

  abstract ValidationInfo getValidationInfo(String parameterName);

  private static boolean validateDomain(final ValidationInfo vInfo, final String value) {
    if(vInfo.getDomain() != null) {
      for(final String element : vInfo.getDomain()) {
        if(element.equals(value)) {
          return true;
        }
      }
      return false;
    } else {
      return true;
    }
  }

  private static boolean validateType(final ValidationInfo vInfo, final String value) {
    if(vInfo.getType() == null) {
      return true;
    }
    try {
      if(vInfo.getType().equals("float")) {
        Double.parseDouble(value);
      } else if(vInfo.getType().equals("integer")) {
        Integer.parseInt(value);
      }
    } catch(final NumberFormatException e) {
      return false;
    }

    if(vInfo.getType().equals("boolean")) {
      if(!value.equals(AbstractParameterValidator.FALSE_CONSTANT) && !value.equals(AbstractParameterValidator.TRUE_CONSTANT)) {
        return false;
      }
    }
    return true;
  }

  private static boolean validateRange(final ValidationInfo vInfo, final String value) {
    if(vInfo.getMinValString() == null && vInfo.getMaxValString() == null) {
      return true;
    }

    if(vInfo.getType().equals("integer")) {
      return AbstractParameterValidator.validateIntegerRange(vInfo, value);
    } else if(vInfo.getType().equals("float")) {
      return AbstractParameterValidator.validateFloatingPointRange(vInfo, value);
    } else {
      return true;
    }
  }

  private static boolean validateIntegerRange(final ValidationInfo vInfo, final String value) {
    boolean tooSmall = false;
    boolean tooLarge = false;
    if(vInfo.getMinValString() != null && !vInfo.getMinValString().trim().equals("")) {
      tooSmall = Integer.parseInt(value) < Integer.parseInt(vInfo.getMinValString());
    }
    if(vInfo.getMaxValString() != null && !vInfo.getMaxValString().trim().equals("")) {
      tooLarge = Integer.parseInt(value) > Integer.parseInt(vInfo.getMaxValString());
    }
    return !tooSmall && !tooLarge;
  }

  private static boolean validateFloatingPointRange(final ValidationInfo vInfo, final String value) {
    boolean tooSmall = false;
    boolean tooLarge = false;
    if(vInfo.getMinValString() != null && !vInfo.getMinValString().trim().equals("")) {
      tooSmall = Double.parseDouble(value) < Double.parseDouble(vInfo.getMinValString());
    }
    if(vInfo.getMaxValString() != null && !vInfo.getMaxValString().trim().equals("")) {
      tooLarge = Double.parseDouble(value) > Double.parseDouble(vInfo.getMaxValString());
    }
    return !tooSmall && !tooLarge;
  }

  private static boolean validateRegex(final ValidationInfo vInfo, final String value) {
    if(vInfo.getRegex() == null) {
      return true;
    } else {
      return value.matches(vInfo.getRegex());
    }
  }

  private boolean validateSingleValue(final ValidationInfo vInfo, final String value) {
    return AbstractParameterValidator.validateDomain(vInfo, value) && AbstractParameterValidator.validateType(vInfo, value) && AbstractParameterValidator.validateRange(vInfo, value) && AbstractParameterValidator.validateRegex(vInfo, value);
  }

  public boolean validate(final String name, final String value) {
    final ValidationInfo vInfo = this.getValidationInfo(name);
    if(!vInfo.isList()) {
      return this.validateSingleValue(vInfo, value);
    } else {
      final String[] values = value.split("(\\s)+,(\\s)+");
      // for(String curValue : values) {
      for(final String value2 : values) {
        final String curValue = value2;
        if(!this.validateSingleValue(vInfo, curValue)) {
          return false;
        }
      }
      return true;
    }
  }
}
