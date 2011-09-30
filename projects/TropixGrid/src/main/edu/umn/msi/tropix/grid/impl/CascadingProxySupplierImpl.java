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

package edu.umn.msi.tropix.grid.impl;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.grid.credentials.Credential;

public class CascadingProxySupplierImpl implements Supplier<Credential> {
  private Iterable<Supplier<Credential>> proxySuppliers;

  public Credential get() {
    RuntimeException lastException = null;
    for(final Supplier<Credential> proxySupplier : proxySuppliers) {
      try {
        return proxySupplier.get();
      } catch(final RuntimeException e) {
        lastException = e;
      }
    }
    throw new RuntimeException("Failed to obtain proxy from any proxy supplier.", lastException);
  }

  public void setProxySuppliers(final Iterable<Supplier<Credential>> proxySuppliers) {
    this.proxySuppliers = proxySuppliers;
  }
}
