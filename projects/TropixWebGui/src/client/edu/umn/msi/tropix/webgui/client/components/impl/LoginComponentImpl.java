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

package edu.umn.msi.tropix.webgui.client.components.impl;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.BooleanCallback;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.LoginCallback;
import edu.umn.msi.tropix.webgui.client.components.LoginComponent;
import edu.umn.msi.tropix.webgui.client.widgets.Buttons;
import edu.umn.msi.tropix.webgui.client.widgets.Form;

public class LoginComponentImpl implements LoginComponent {
  public void showLoginDialog(final List<String> authenticationSources, final LoginCallback callback) {
    new LoginWindow(authenticationSources, callback).execute();
  }

  private static class LoginWindow extends WindowComponentImpl<Window> {
    private final TextItem usernameItem;

    @Override
    public void execute() {
      super.execute();
      usernameItem.focusInItem();
    }

    LoginWindow(final List<String> authenticationSources, final LoginCallback callback) {
      this.setWidget(new Window());
      this.get().setAutoCenter(true);
      this.get().setIsModal(true);
      this.get().setShowCloseButton(false);
      this.get().setShowMinimizeButton(false);
      this.get().setHeaderIcon(Resources.PERSON_ABSOLUTE);
      this.get().setTitle("Please log in");

      final Img loginImage = new Img(GWT.getHostPageBaseURL() + "images/tint_login_logo.png");
      loginImage.setAlign(Alignment.CENTER);
      loginImage.setSize("320", "320");

      final Form loginForm = new Form();
      usernameItem = new TextItem("username", "Username");
      usernameItem.setTabIndex(1);
      final PasswordItem passwordItem = new PasswordItem("password", "Password");
      passwordItem.setTabIndex(2);
      final SelectItem selectionItem = new SelectItem("authentication", "Authentication");
      selectionItem.setTabIndex(3);
      selectionItem.setValueMap(authenticationSources.toArray(new String[authenticationSources.size()]));
      selectionItem.setValue(authenticationSources.get(0));
      final Button button = Buttons.getOkButton();
      button.setTitle("Log in");
      button.setTabIndex(4);

      final Command loginCommand = new Command() {
        public void execute() {
          final String username = loginForm.getValueAsString("username");
          final String password = loginForm.getValueAsString("password");
          final String authentication = loginForm.getValueAsString("authentication");
          callback.loginAttempt(username, password, authentication, new AsyncCallbackImpl<Boolean>() {
            public void onSuccess(final Boolean isValid) {
              if(isValid) {
                get().destroy();
              } else {
                SC.warn("Invalid username or password", new BooleanCallback() {
                  public void execute(final Boolean value) {
                  }
                });
              }
            }
          });
        }
      };
      button.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
        public void onClick(final com.smartgwt.client.widgets.events.ClickEvent event) {
          loginCommand.execute();
        }
      });

      usernameItem.addKeyPressHandler(new KeyPressHandler() {
        public void onKeyPress(final KeyPressEvent event) {
          if(event.getKeyName().equals("Enter")) {
            passwordItem.focusInItem();
          }
        }
      });
      passwordItem.addKeyPressHandler(new KeyPressHandler() {
        public void onKeyPress(final KeyPressEvent event) {
          if(event.getKeyName().equals("Enter")) {
            loginCommand.execute();
          }
        }
      });
      selectionItem.addKeyPressHandler(new KeyPressHandler() {
        public void onKeyPress(final KeyPressEvent event) {
          if(event.getKeyName().equals("Enter")) {
            loginCommand.execute();
          }
        }
      });

      final Label label = new Label("Log in as Guest");
      label.addClickHandler(new com.smartgwt.client.widgets.events.ClickHandler() {
        public void onClick(final com.smartgwt.client.widgets.events.ClickEvent event) {

        }
      });
      loginForm.setItems(usernameItem, passwordItem, selectionItem);
      loginForm.setWidth(310);
      loginForm.setColWidths(145, 165);
      loginForm.setAlign(Alignment.CENTER);

      final VLayout layout = new VLayout();
      layout.setDefaultLayoutAlign(Alignment.CENTER);
      layout.setAlign(Alignment.CENTER);

      layout.setWidth(320);
      layout.setHeight100();
      layout.addMember(loginImage);
      layout.addMember(loginForm);
      final HLayout hLayout = new HLayout();
      hLayout.setWidth100();
      hLayout.setDefaultLayoutAlign(Alignment.LEFT);
      hLayout.addMember(button);
      hLayout.setMargin(10);
      layout.addMember(hLayout);

      this.get().addItem(layout);
      this.get().setAutoSize(true);
    }
  }
}
