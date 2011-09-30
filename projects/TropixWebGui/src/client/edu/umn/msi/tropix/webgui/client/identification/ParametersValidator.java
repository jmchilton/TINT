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

import edu.umn.msi.tropix.webgui.client.utils.JObject;

public class ParametersValidator extends AbstractParameterValidator {
  private final ParameterSetMap parametersSpecificationMap;
  private static final String[] VALIDATIONS = {"minValue", "maxValue", "type", "isList", "regex"};

  public ParametersValidator(final ParameterSetMap parametersSpecificationMap) {
    this.parametersSpecificationMap = parametersSpecificationMap;
  }

  private void setValidationInfo(final ValidationInfo info, final int index, final String value) {
    switch(index) {
    case 0:
      info.setMinValString(value);
      break;
    case 1:
      info.setMaxValString(value);
      break;
    case 2:
      info.setType(value);
      break;
    case 3:
      info.setList(value.equals("1")); // This should maybe be true! -John
      break;
    case 4:
      info.setRegex(value);
      break;
    }
  }

  ValidationInfo getValidationInfo(final String parameterName) {
    final ValidationInfo info = new ValidationInfo();
    final JObject parameterObject = this.parametersSpecificationMap.getParameter(parameterName);
    final JObject validationObject = parameterObject.getJObjectOrNull("validation");
    if(validationObject == null) {
      return info;
    }

    for(int i = 0; i < ParametersValidator.VALIDATIONS.length; i++) {
      final String curValidationString = validationObject.getStringOrNull(ParametersValidator.VALIDATIONS[i]);
      if(curValidationString == null) {
        continue;
      }
      this.setValidationInfo(info, i, curValidationString);
    }

    return info;
  }

}
