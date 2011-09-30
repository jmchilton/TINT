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

package edu.umn.msi.tropix.webgui.services.message;

import java.util.Collection;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.umn.msi.tropix.webgui.services.session.LoginListener;

public class MessageServiceClient implements AsyncCallback<Collection<Message>> {
  private enum State {
    CALLED, WAITING, STOPPED
  };

  private static MessageServiceClient instance = null;
  private State state = State.STOPPED;

  public void onFailure(final Throwable caught) {
    caught.printStackTrace();
    reschedule();
  }

  public void onSuccess(final Collection<Message> messages) {
    for(final Message message : messages) {
      try {
        message.respond();
      } catch(final Throwable t) {
        System.err.println("Error while executing respond() in onPayload.");
        t.printStackTrace();
      }
    }
    reschedule();
  }

  private void reschedule() {
    if(state == State.CALLED) {
      state = State.WAITING;
      new Timer() {
        public void run() {
          call();
        }
      }.schedule(1000);
    }
  }

  private void call() {
    state = State.CALLED;
    MessageService.Util.getInstance().getMessages(this);
  }

  private void start() {
    if(state == State.STOPPED) {
      call();
    }
  }

  private void stop() {
    state = State.STOPPED;
  }

  public static LoginListener getLoginListener() {
    if(instance == null) {
      instance = new MessageServiceClient();
    }
    return new LoginListener() {
      public void loginEvent() {
        instance.start();
      }

      public void logoutEvent() {
        instance.stop();
      }

      public void loginFailedEvent() {
      }
    };
  }

}
