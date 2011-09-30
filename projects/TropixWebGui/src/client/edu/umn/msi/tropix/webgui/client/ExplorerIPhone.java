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

package edu.umn.msi.tropix.webgui.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;

import edu.umn.msi.tropix.webgui.client.forms.FormPanelFactory;
import edu.umn.msi.tropix.webgui.client.forms.FormPanelSupplier;
import edu.umn.msi.tropix.webgui.client.widgets.IPhonePanel;
import edu.umn.msi.tropix.webgui.client.widgets.IPhonePanel.PanelLabel;

public class ExplorerIPhone extends Explorer {

  public void onModuleLoad() {
    FormPanelFactory.createParametersPanel("loginIPhone", new AsyncCallback<FormPanelSupplier>() {
      public void onSuccess(final FormPanelSupplier formPanelSupplier) {
        final IPhonePanel loginPanel = new IPhonePanel("Tropix Explorer", null);
        final Panel panel = (Panel) formPanelSupplier.get();
        loginPanel.add(panel);

        final PanelLabel loginLabel = new PanelLabel("Login...", new Command() {
          public void execute() {
            // final Map<String, String> parameters = formPanelSupplier.getParametersMap();
            // LoginMediator.getInstance().loginAttempt(parameters.get("username"), parameters.get("password"));
          }
        });

        loginPanel.add(loginLabel);
        loginPanel.enter();
        /*
        LoginMediator.getInstance().addListener(MessageServiceClient.getLoginListener());
        LoginMediator.getInstance().addListener(new LoggedOutChecker());
        LoginMediator.getInstance().addListener(new LoginListener() {
          public void loginEvent() {
            loginPanel.add(new Label("logged in"));
          }

          public void logoutEvent() {
          }

          public void loginFailedEvent() {
            // TODO Auto-generated method stub

          }
        });
        */
      }

      public void onFailure(final Throwable t) {
        RootPanel.get("content").add(new Label("Failed to obtain parameters"));
      }
    });
  }
}
