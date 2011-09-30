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

package edu.umn.msi.tropix.webgui.server.xml;

import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.client.directory.GridUser;

public class GridUserFunctionImpl implements Function<GridUser, Map<String, String>> {

  public Map<String, String> apply(final GridUser gridUser) {
    final Map<String, String> map = Maps.newHashMapWithExpectedSize(4);
    // Using short field names to reduce data transferred, this is not a 
    // premature optimization.
    map.put("f", gridUser.getFirstName());
    map.put("l", gridUser.getLastName());
    map.put("g", gridUser.getGridId());
    map.put("i", gridUser.getInstitution());
    return map;
  }

}
