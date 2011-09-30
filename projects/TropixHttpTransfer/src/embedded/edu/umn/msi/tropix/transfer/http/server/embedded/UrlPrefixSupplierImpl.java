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

package edu.umn.msi.tropix.transfer.http.server.embedded;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.transfer.http.server.ServerConstants;

public class UrlPrefixSupplierImpl implements Supplier<String> {
  private Supplier<String> hostname;
  private HasPort hasPort;
  
  public String get() {
    return "http://" + hostname.get() + ":" + hasPort.getPort() + "?" + ServerConstants.KEY_PARAMETER_NAME + "=";
  }

  public void setHostname(final Supplier<String> hostname) {
    this.hostname = hostname;
  }
  
  public void setHasPort(final HasPort hasPort) {
    this.hasPort = hasPort;
  }

}
