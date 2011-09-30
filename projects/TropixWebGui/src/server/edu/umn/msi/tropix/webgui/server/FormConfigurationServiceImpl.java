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

package edu.umn.msi.tropix.webgui.server;

import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Supplier;

import edu.umn.msi.tropix.webgui.server.aop.ServiceMethod;
import edu.umn.msi.tropix.webgui.server.xml.XMLResourceManager;
import edu.umn.msi.tropix.webgui.services.forms.FormConfiguration;
import edu.umn.msi.tropix.webgui.services.forms.FormConfigurationService;
import edu.umn.msi.tropix.webgui.services.forms.ValidationProvider;

public class FormConfigurationServiceImpl implements FormConfigurationService {
  private XMLResourceManager displayXmlResourceManager;
  private Function<String, Supplier<ValidationProvider>> formValidationSupplierFunction;

  @ServiceMethod(readOnly = true, secure = false)
  public FormConfiguration getFormConfiguration(final String id) {
    final FormConfiguration config = new FormConfiguration();
    final String displayJson = this.displayXmlResourceManager.getResourceJSON(id);
    final ValidationProvider validationProvider = this.formValidationSupplierFunction.apply(id).get();
    config.setDisplayConfiguration(displayJson);
    config.setValidationProvider(validationProvider);
    return config;
  }

  public void setFormValidationSupplierFunction(final Function<String, Supplier<ValidationProvider>> formValidationSupplierFunction) {
    this.formValidationSupplierFunction = formValidationSupplierFunction;
  }

  public void setFormValidationSupplierMap(final Map<String, Supplier<ValidationProvider>> formValidationSupplierMap) {
    this.setFormValidationSupplierFunction(Functions.<String, Supplier<ValidationProvider>>forMap(formValidationSupplierMap, null));
  }

  public void setDisplayXMLResourceManager(final XMLResourceManager displayXmlResourceManager) {
    this.displayXmlResourceManager = displayXmlResourceManager;
  }

}
