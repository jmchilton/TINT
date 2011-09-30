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

import javax.annotation.Nullable;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.globus.gsi.GlobusCredential;

/**
 * Must be public to be instantiated in GridServiceFactoryImpl.
 * 
 */
public class TestClient {
  @Nullable private final EndpointReferenceType epr;
  @Nullable private final String address;
  private final GlobusCredential credential;
  
  public TestClient(final String address, final GlobusCredential credential) {
    this.address = address;
    this.credential = credential;
    this.epr = null;
  }
  
  public TestClient(final EndpointReferenceType epr, final GlobusCredential credential) {
    this.address = null;
    this.credential = credential;
    this.epr = epr;
  }

  @Nullable
  public String getAddress() {
    return address;
  }

  public GlobusCredential getCredential() {
    return credential;
  }

  @Nullable
  public EndpointReferenceType getEpr() {
    return epr;
  }
  
}