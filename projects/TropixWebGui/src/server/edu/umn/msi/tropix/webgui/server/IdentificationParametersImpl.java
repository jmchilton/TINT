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

import javax.inject.Inject;

import edu.umn.msi.tropix.webgui.server.aop.ServiceMethod;
import edu.umn.msi.tropix.webgui.server.xml.XMLResourceManager;
import edu.umn.msi.tropix.webgui.services.protip.IdentificationParametersService;
import edu.umn.msi.tropix.webgui.services.protip.Parameters;

public class IdentificationParametersImpl implements IdentificationParametersService {
  private static final long serialVersionUID = 1L;
  private XMLResourceManager specificationManager;
  private XMLResourceManager displayManager;
  private ParameterLoader parameterLoader;

  @ServiceMethod(readOnly = true)
  public Parameters getParametersInformation(final String id) {
    final Parameters params = new Parameters();
    params.setDisplayJSON(displayManager.getResourceJSON(id));
    params.setSpecificationJSON(specificationManager.getResourceJSON(id));
    return params;
  }

  @ServiceMethod(readOnly = true)
  public Map<String, String> getParameterMap(final String id) {
    return parameterLoader.loadParameterMap(id);
  }

  public void setSpecificationManager(final XMLResourceManager specificationManager) {
    this.specificationManager = specificationManager;
  }

  public void setDisplayManager(final XMLResourceManager displayManager) {
    this.displayManager = displayManager;
  }

  @Inject
  public void setParameterLoader(final ParameterLoader parameterLoader) {
    this.parameterLoader = parameterLoader;
  }

}
