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

package edu.umn.msi.tropix.webgui.server.resource;

import java.util.Map;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;

public class MappedResourceAccesorFunctionUtilsTest {

  @Test(groups = "unit")
  public void testCreate() {
    // Check constructor
    new MappedResourceAccesorFunctionUtils();
    
    final Map<String, String> keyMap = ImmutableMap.<String, String>builder().put("moo", "/path/to/cow").build();
    final ResourceAccessor accessor = EasyMock.createMock(ResourceAccessor.class);
    final Map<String, ResourceAccessor> resourceMap = ImmutableMap.<String, ResourceAccessor>builder().put("/path/to/cow", accessor).build();
    
    // Assert keyMap is converted to a function properly and accessorFunction is composed properly. moo => /path/to/cow => accessor
    assert MappedResourceAccesorFunctionUtils.create(keyMap, Functions.forMap(resourceMap)).apply("moo") == accessor;
    
  }
  
}
