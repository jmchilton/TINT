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

package edu.umn.msi.tropix.webgui.server.progress;

import java.util.UUID;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.base.Suppliers;

import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.webgui.server.messages.MessagePusher;
import edu.umn.msi.tropix.webgui.services.message.ProgressMessage;

public class ProgressTrackerImplTest {
  private ProgressTrackerImpl tracker;
  private MessagePusher<ProgressMessage> messagePusher;
  private ProgressMessage progressMessage;
  private String userId;

  @BeforeMethod(groups = "unit")
  public void init() {
    tracker = new ProgressTrackerImpl();
    messagePusher = EasyMock.createMock(MessagePusher.class);
    progressMessage = new ProgressMessage();
    tracker.setProgressMessageSupplier(Suppliers.ofInstance(progressMessage));
    tracker.setCometPusher(messagePusher);
    userId = UUID.randomUUID().toString();
    tracker.setUserGridId(userId);
  }

  @Test(groups = "unit")
  public void testComplete() {
    expect();
    tracker.complete();
    verify();
    assert progressMessage.getJobStatus() == ProgressMessage.JOB_COMPLETE;

    expect();
    tracker.fail();
    verify();
    assert progressMessage.getJobStatus() == ProgressMessage.JOB_FAILED;

    expect();
    tracker.update(.34f);
    verify();
    assert progressMessage.getPercent() == .34f;

    expect();
    tracker.update("moo");
    verify();
    assert progressMessage.getStepStatus().equals("moo");

    expect();
    tracker.update("step", 0.37f);
    verify();
    assert progressMessage.getStep().equals("step");
    assert progressMessage.getPercent() == 0.37f;

    tracker.update("step2", "stat");
    assert progressMessage.getStep().equals("step2");
    assert progressMessage.getStepStatus().equals("stat");

  }

  private void expect() {
    messagePusher.push(userId, progressMessage);
    EasyMock.replay(messagePusher);
  }

  private void verify() {
    EasyMockUtils.verifyAndReset(messagePusher);
  }

}
