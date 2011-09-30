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

package edu.umn.msi.tropix.common.test;

import java.util.Locale;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.message.MessageSource;
import edu.umn.msi.tropix.common.message.impl.MessageSourceSpringImpl;

@ContextConfiguration(locations = "classpath:edu/umn/msi/tropix/common/test/messageContext.xml")
public class MessageSourceSpringImplTest extends AbstractTestNGSpringContextTests {

  @Test(groups = "unit")
  public void getMessage() {
    final MessageSource messages = (MessageSource) this.applicationContext.getBean("messageSource");
    assert messages.getMessage("moo").equals("Moo cow");
  }

  @Test(groups = "unit")
  public void clearCache() {
    final MessageSourceSpringImpl messages = (MessageSourceSpringImpl) this.applicationContext.getBean("messageSource");
    messages.clearCache();
  }

  @Test(groups = "unit")
  public void locale() {
    final MessageSourceSpringImpl messages = (MessageSourceSpringImpl) this.applicationContext.getBean("messageSource");
    messages.setLocale(new Locale("uk"));
    assert messages.getMessage("moo").equals("Uk Moo");
    assert messages.getMessage("moo2", null, "DEFAULT").equals("DEFAULT");
  }

  @Test(groups = "unit")
  public void getMessageWithArg() {
    final MessageSourceSpringImpl messages = (MessageSourceSpringImpl) this.applicationContext.getBean("messageSource");
    final String message = messages.getMessage("animal", new Object[] {"cow"});
    assert message.equals("The animal is cow.") : message;
  }

}
