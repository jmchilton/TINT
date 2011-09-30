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

package edu.umn.msi.tropix.webgui.server.aop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.easymock.classextension.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.webgui.client.utils.Lists;
import edu.umn.msi.tropix.webgui.server.models.BeanSanitizer;

public class SanitizingAspectTest {
  private FakeBeanSanitizer sanitizer;
  private SanitizingAspect aspect;
  
  private static class FakeBeanSanitizer implements BeanSanitizer {
    private Map<Object, Object> sanitizationMap = Maps.newHashMap();
    
    @SuppressWarnings("unchecked")
    public <T> T sanitize(final T object) {
      return (T) sanitizationMap.get(object);
    }
    
  }
   
  private static ProceedingJoinPoint getJoinPointWhichReturns(final Object object) {
    final ProceedingJoinPoint joinPoint = EasyMock.createMock(ProceedingJoinPoint.class);
    try {
      EasyMock.expect(joinPoint.proceed()).andStubReturn(object);
    } catch (Throwable e) {
      assert false;
    }
    EasyMock.replay(joinPoint);
    return joinPoint;
  }
  
  @BeforeMethod(groups = "unit")
  public void init() {
    sanitizer = new FakeBeanSanitizer();
    aspect = new SanitizingAspect(sanitizer);
  }
  
  @Test(groups = "unit")
  public void testSanitizeSimpleBean() throws Throwable {
    sanitizer.sanitizationMap.put("moo", "cow");
    assert aspect.sanitizeResults(getJoinPointWhichReturns("moo")).equals("cow");
  }

  @Test(groups = "unit")
  public void testSanitizeArrayList() throws Throwable {
    sanitizer.sanitizationMap.put("moo", "cow");
    sanitizer.sanitizationMap.put("1", "2");
    
    final Object results = aspect.sanitizeResults(getJoinPointWhichReturns(Lists.newArrayList("moo", "1")));
    assert (results instanceof ArrayList<?>);
    assert Iterables.elementsEqual((Iterable<?>) results, Lists.newArrayList("cow", "2"));
  }
  
  @Test(groups = "unit", expectedExceptions = IOException.class)
  public void testPassedExceptionOn() throws Throwable {
    final ProceedingJoinPoint joinPoint = EasyMock.createMock(ProceedingJoinPoint.class);
    try {
      EasyMock.expect(joinPoint.proceed()).andThrow(new IOException("moo"));
    } catch(final Throwable t) {
      assert false;
    }
    EasyMock.replay(joinPoint);
    aspect.sanitizeResults(joinPoint);
  }

  
  
}
