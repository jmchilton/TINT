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

package edu.umn.msi.tropix.client.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.common.collect.Closures;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.common.reflect.ReflectionHelper;
import edu.umn.msi.tropix.common.reflect.ReflectionHelpers;

public class GridServiceSupplierImpl<T extends GridService> implements GridServicesSupplier<T> {
  private static final Log LOG = LogFactory.getLog(GridServiceSupplierImpl.class);
  private static final ReflectionHelper REFLECTION_HELPER = ReflectionHelpers.getInstance();
  private List<Closure<? super T>> closures = Lists.newLinkedList();
  private Class<T> gridServiceClass;
  private Iterable<String> addressesSupplier;
  private final Set<String> problemServices = new HashSet<String>(); // Very small memory leak here...

  public Iterable<T> get() {
    final Set<T> serviceSet = new HashSet<T>();
    for(final String address : addressesSupplier) {
      final T gridService = REFLECTION_HELPER.newInstance(gridServiceClass);
      gridService.setServiceAddress(address);
      try {
        Closures.compose(closures).apply(gridService);

      } catch(final RuntimeException t) {
        if(!problemServices.contains(address)) {
          ExceptionUtils.logQuietly(LOG, t, "Failed to apply grid service closures to address " + address + ", skipping.");
          problemServices.add(address);
        }
        continue;
      }
      if(problemServices.contains(address)) {
        problemServices.remove(address);
      }
      serviceSet.add(gridService);
    }
    return serviceSet;
  }

  public void setGridServiceClass(final Class<T> gridServiceClass) {
    this.gridServiceClass = Preconditions.checkNotNull(gridServiceClass);
  }

  public void setAddressesSupplier(final Iterable<String> addressesSupplier) {
    this.addressesSupplier = Preconditions.checkNotNull(addressesSupplier);
  }

  public void setGridServiceModifiers(final List<Closure<? super T>> modifiers) {
    this.closures = Preconditions.checkNotNull(modifiers);
  }

  public boolean isEmpty() {
    return addressesSupplier.iterator().hasNext();
  }

}
