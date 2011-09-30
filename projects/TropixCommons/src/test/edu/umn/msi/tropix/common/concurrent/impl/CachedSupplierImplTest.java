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

package edu.umn.msi.tropix.common.concurrent.impl;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.test.EasyMockUtils;

public class CachedSupplierImplTest {

  static class PublicCachedSupplierImpl<T> extends CachedSupplierImpl<T> {
    @Override
    public T getInstance() {
      return super.getInstance();
    }
  }

  @Test(groups = "unit", timeOut=1000)
  public void getInstance() {
    final PublicCachedSupplierImpl<String> cached = new PublicCachedSupplierImpl<String>();
    final Supplier<String> supplier = EasyMockUtils.createMockSupplier();
    cached.setBaseSupplier(supplier);
    EasyMock.expect(supplier.get()).andReturn("cow");
    EasyMock.replay(supplier);
    assert cached.getInstance().equals("cow");
    EasyMock.verify(supplier);
  }

  static class OverrideGetCachedInstance<T> extends CachedSupplierImpl<T> {
    private T cachedInstance;

    @Override
    public T getCachedInstance() {
      return this.cachedInstance;
    }
  }

  @Test(groups = "unit", timeOut=1000)
  public void instance() {
    final OverrideGetCachedInstance<String> cached = new OverrideGetCachedInstance<String>();
    cached.cachedInstance = "cow";
    assert cached.get().equals("cow");
  }

}
