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

package edu.umn.msi.tropix.storage.core.access.fs;

import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.storage.core.access.fs.CascadingFunctionImpl;

public class CascadingFunctionImplTest {
  
  private final Function<String, String> f1 = new Function<String, String>() {
    public String apply(String arg) {
      if(arg.equals("moo")) {
        return "cow";
      } else if(arg.equals("none")) {
        return null;
      }
      return arg + "1";
    }    
  };
  
  private final Function<String, String> f2 = new Function<String, String>() {
    public String apply(String arg) {
      if(arg.equals("moo")) {
        return "cow2";
      } else if(arg.equals("none")) {
        return null;
      }
      return null;
    }    
  };
  
  @Test(groups = "unit")
  public void testApply() {
    @SuppressWarnings("unchecked")
    final CascadingFunctionImpl<String, String> function1Then2 = new CascadingFunctionImpl<String, String>(Lists.newArrayList(f1, f2));
    @SuppressWarnings("unchecked")
    final CascadingFunctionImpl<String, String> function2Then1 = new CascadingFunctionImpl<String, String>(Lists.newArrayList(f2, f1));

    assert function1Then2.apply("moo").equals("cow");
    assert function2Then1.apply("moo").equals("cow2");
    assert function1Then2.apply("none") == null;
    assert function2Then1.apply("none") == null;

    assert function2Then1.apply("other").equals("other1");    
  }
  
}
