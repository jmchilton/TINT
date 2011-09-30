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

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.globus.gsi.GlobusCredential;

import edu.umn.msi.tropix.common.reflect.ReflectionHelper;
import edu.umn.msi.tropix.common.reflect.ReflectionHelpers;
import edu.umn.msi.tropix.grid.GridServiceFactory;
import edu.umn.msi.tropix.grid.credentials.Credential;

public class GridServiceFactoryImpl<C> implements GridServiceFactory<C> {
  private static final ReflectionHelper REFLECTION_HELPER = ReflectionHelpers.getInstance();
  private final Class<? extends C> interfacesClientClass;

  public GridServiceFactoryImpl(final Class<? extends C> interfacesClientClass) {
    this.interfacesClientClass = interfacesClientClass;
  }
  
  private C getService(final Class<?>[] argTypes, final Object address, final Credential proxy) {
    final GlobusCredential globusCredential = proxy == null ? null : proxy.getGlobusCredential();
    final Object[] args = new Object[] {address, globusCredential};
    return REFLECTION_HELPER.newInstance(interfacesClientClass, argTypes, args);    
  }

  public C getService(final String address, final Credential proxy) {
    final Class<?>[] argTypes = new Class<?>[] {String.class, GlobusCredential.class};
    return getService(argTypes, address, proxy);
  }

  public C getService(final EndpointReferenceType epr, final Credential proxy) {
    final Class<?>[] argTypes = new Class<?>[] {EndpointReferenceType.class, GlobusCredential.class};
    return getService(argTypes, epr, proxy);
  }

}
