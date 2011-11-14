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

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.mail.MailSenderImpl;

public class MailSenderTest {

  @Test(groups = "unit")
  public void send() {
    final MailSenderImpl sender = new MailSenderImpl();
    final MailSender mailSender = EasyMock.createMock(MailSender.class);
    final SimpleMailMessage templateMessage = new SimpleMailMessage();

    sender.setMailSender(mailSender);
    sender.setTemplateMessage(templateMessage);

    final Capture<SimpleMailMessage> capturedMessage = new Capture<SimpleMailMessage>();
    mailSender.send(EasyMock.capture(capturedMessage));
    EasyMock.replay(mailSender);
    sender.send("Moo");
    EasyMock.verify(mailSender);
    final SimpleMailMessage message = capturedMessage.getValue();
    assert message.getText().equals("Moo");
  }

  /*
   * @Test(groups = "integration") public void testSend() { final MailSenderImpl sender = new MailSenderImpl(); final JavaMailSenderImpl iSender = new JavaMailSenderImpl();
   * 
   * // iSender.setHost("chilton.msi.umn.edu"); final SimpleMailMessage template = new SimpleMailMessage(); template.setFrom("chilton@msi.umn.edu"); template.setTo("chilton@msi.umn.edu"); template.setSubject("Test"); sender.setTemplateMessage(template);
   * sender.setMailSender(iSender); sender.send("Moo"); }
   */
}
