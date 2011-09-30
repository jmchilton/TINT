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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.testng.annotations.Test;

public class CacheIterableImplTest {

  static class PublicCachedIterableImpl<T> extends CachedIterableImpl<T> {
    @Override
    public Iterable<T> getInstance() {
      return super.getInstance();
    }
  }

  @Test(groups = "unit", timeOut=1000)
  public void getInstance() {
    final PublicCachedIterableImpl<String> cached = new PublicCachedIterableImpl<String>();
    final List<String> list = new LinkedList<String>();
    list.add("moo");
    list.add("cow");
    cached.setBaseIterable(list);
    final Iterable<String> iterable = cached.getInstance();
    list.remove("moo");
    assert iterable.iterator().next().equals("moo");
    assert iterable.iterator().next().equals("moo");
  }

  static class OverrideGetCachedInstance<T> extends CachedIterableImpl<T> {
    private Iterable<T> cachedInstance;

    @Override
    public Iterable<T> getCachedInstance() {
      return this.cachedInstance;
    }
  }

  @Test(groups = "unit", timeOut=1000)
  public void iterator() {
    final OverrideGetCachedInstance<String> cached = new OverrideGetCachedInstance<String>();
    final List<String> list = new LinkedList<String>();
    list.add("moo");
    list.add("cow");
    cached.cachedInstance = list;
    final Iterator<String> strs = cached.iterator();
    assert strs.next().equals("moo");
    assert strs.next().equals("cow");
  }
}
