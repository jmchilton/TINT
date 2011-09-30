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

package edu.umn.msi.tropix.webgui.server.resource.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.easymock.EasyMock;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import edu.umn.msi.tropix.webgui.server.resource.ResourceAccessor;

public class SpringResourceAccessorFactoryImplTest {

  @Test(groups = "unit", dataProvider = "bool1", dataProviderClass = TestNGDataProviders.class)
  public void testResources(final boolean useApply) throws IOException {
    final ApplicationContext context = EasyMock.createMock(ApplicationContext.class);
    final Resource resource = EasyMock.createMock(Resource.class);
    
    final InputStream stream = new ByteArrayInputStream("test".getBytes());
    EasyMock.expect(resource.getInputStream()).andStubReturn(stream);
    EasyMock.expect(context.getResource("moo")).andStubReturn(resource);
    
    EasyMock.replay(resource, context);
    
    final SpringResourceAccessorFactoryImpl factory = new SpringResourceAccessorFactoryImpl();
    factory.setApplicationContext(context);
    
    ResourceAccessor accessor;
    if(useApply) {
      accessor = factory.apply("moo");
    } else {
      accessor = factory.get();
      accessor.setResourceId("moo");
    }
    
    assert accessor.get() == stream;    
  }
  
}
