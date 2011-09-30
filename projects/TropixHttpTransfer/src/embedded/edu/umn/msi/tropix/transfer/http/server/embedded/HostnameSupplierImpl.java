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

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.util.StringUtils;

import com.google.common.base.Supplier;

public class HostnameSupplierImpl implements Supplier<String> {
  private String hostname = null;

  public String get() {
    String hostname;
    if(this.hostname != null) {
      hostname = this.hostname;
    } else {
      try {
        hostname = InetAddress.getLocalHost().getHostAddress();
      } catch(final UnknownHostException e) {
        throw new RuntimeException("No hostname specified and unable to obtain local host", e);
      }
    }
    return hostname;
  }

  public void setHostname(final String hostname) {
    // Verify the hostname is not an unexpanded Spring property like ${hostname}
    if(StringUtils.hasText(hostname) && !hostname.startsWith("${")) {
      this.hostname = hostname;
    }
  }

}
