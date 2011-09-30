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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import edu.umn.msi.tropix.webgui.client.utils.JArray;
import edu.umn.msi.tropix.webgui.client.utils.JObject;

public class ParameterSetMap {
  private Map<String, JObject> parametersSpecificationMap;

  public Iterator<String> getParameterNames() {
    return this.parametersSpecificationMap.keySet().iterator();
  }

  public ParameterSetMap(final String json) {
    this.initMap(JObject.Factory.create(json));
  }

  public JObject getParameter(final String name) {
    return this.parametersSpecificationMap.get(name);
  }

  private void initMap(final JObject parametersSpecification) {
    final JObject parametersObject = parametersSpecification.getJObject("parameterSet");
    final JArray parameterArray = parametersObject.getJArray("parameter");
    final Iterator<JObject> jObjectIterator = parameterArray.jObjectIterator();
    this.parametersSpecificationMap = new HashMap<String, JObject>(parameterArray.size());
    while(jObjectIterator.hasNext()) {
      final JObject parameter = jObjectIterator.next();
      String name = parameter.getStringOrNull("name");
      if(name != null) {
        this.parametersSpecificationMap.put(name, parameter);
      }
      if(parameter.containsKey("nameSet")) {
        final JObject nameSet = parameter.getJObject("nameSet");
        final JArray names = nameSet.getJArray("name");
        final Iterator<String> namesIterator = names.stringIterator();
        while(namesIterator.hasNext()) {
          name = namesIterator.next();
          this.parametersSpecificationMap.put(name, parameter);
        }
      }
    }
  }
}
