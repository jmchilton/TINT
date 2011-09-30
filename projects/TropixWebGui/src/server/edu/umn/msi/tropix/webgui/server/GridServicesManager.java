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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Function;
import com.google.common.base.Supplier;

import edu.umn.msi.tropix.client.services.GridService;
import edu.umn.msi.tropix.webgui.server.aop.ServiceMethod;
import edu.umn.msi.tropix.webgui.services.gridservices.GetGridServices;

public class GridServicesManager implements GetGridServices {
  private static final Log LOG = LogFactory.getLog(GridServicesManager.class);
  private Function<String, Supplier<Iterable<GridService>>> map; // Should

  @ServiceMethod(readOnly = true)
  public Iterable<GridService> getServices(final String serviceType) {
    GridServicesManager.LOG.debug("getServices called with serviceType " + serviceType);
    return this.map.apply(serviceType).get();
  }

  // change the
  // name of this
  public void setMap(final Function<String, Supplier<Iterable<GridService>>> map) {
    this.map = map;
  }

}
