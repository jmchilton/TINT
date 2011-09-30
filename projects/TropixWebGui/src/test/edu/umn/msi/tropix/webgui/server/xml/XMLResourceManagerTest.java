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

package edu.umn.msi.tropix.webgui.server.xml;

import java.io.ByteArrayInputStream;
import java.util.Map;

import org.easymock.classextension.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.webgui.server.resource.ResourceAccessor;

public class XMLResourceManagerTest {
  private static final String SAMPLE_JSON = "{\"cow\":{\"foo\":{}}}";
  private static final String SAMPLE_XML = "<cow><foo /></cow>";
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private XMLResourceManager manager;
  private Map<String, String> xmlMap;
  
  @BeforeMethod(groups = "unit")
  public void init() {
    manager = new XMLResourceManager();
    manager.setResourceAccessorFunction(new Function<String, ResourceAccessor>(){
      public ResourceAccessor apply(final String name) {
        final ResourceAccessor accessor = EasyMock.createMock(ResourceAccessor.class);
        EasyMock.expect(accessor.get()).andStubReturn(new ByteArrayInputStream(xmlMap.get(name).getBytes()));
        EasyMock.replay(accessor);
        return accessor;
      }      
    });
    xmlMap = Maps.newHashMap();
  }
  
  @Test(groups = "unit")
  public void testGetXmlString() {
    xmlMap.put("moo", "cow");
    assert manager.getResourceXml("moo").equals("cow");
  }
  
  @Test(groups = "unit")
  public void testGetXmlStream() {
    xmlMap.put("moo", "cow");
    assert IO_UTILS.toString(manager.getResourceXmlStream("moo")).equals("cow");
  }
  
  @Test(groups = "unit")
  public void testGetJsonString() {
    final String xmlStr = SAMPLE_XML;
    xmlMap.put("moo", xmlStr);
    assert manager.getResourceJSON("moo").startsWith(SAMPLE_JSON);
  }
  
  @Test(groups = "unit")
  public void testGetJsonStream() {
    final String xmlStr = SAMPLE_XML;
    xmlMap.put("moo", xmlStr);
    assert IO_UTILS.toString(manager.getResourceJSONStream("moo")).equals(SAMPLE_JSON);
  }
  
  
}
