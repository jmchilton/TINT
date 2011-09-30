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

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.gwt.user.client.Command;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.form.fields.PasswordItem;
import com.smartgwt.client.widgets.form.fields.TextItem;

import edu.umn.msi.tropix.models.User;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.Form;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.session.LocalUserService;

public class CreateLocalUserWindowComponentSupplierImpl implements Supplier<WindowComponent<Window>> {

  public WindowComponentImpl<Window> get() {
    return new CreateLocalUserWindowComponentImpl();
  }

  private static class CreateLocalUserWindowComponentImpl extends WindowComponentImpl<Window> {
    private final Form form = new Form();
    private final TextItem usernameItem = new TextItem("username", "Username");
    private final PasswordItem passwordItem1 = new PasswordItem("password1", "Password");
    private final PasswordItem passwordItem2 = new PasswordItem("password2", "Reenter Password");
    private final TextItem firstNameItem = new TextItem("firstName", "First Name");
    private final TextItem lastNameItem = new TextItem("lastName", "Last Name");
    private final TextItem emailItem = new TextItem("email", "E-Mail");
    private final TextItem phoneItem = new TextItem("phone", "Phone");

    private final Button okButton = SmartUtils.getButton("Add User", Resources.ADD, new Command() {
      public void execute() {
        final String password = StringUtils.toString(passwordItem1.getValue());
        final User user = new User();
        user.setCagridId(StringUtils.toString(usernameItem.getValue()));
        user.setLastName(StringUtils.toString(lastNameItem.getValue()));
        user.setFirstName(StringUtils.toString(firstNameItem.getValue()));
        user.setEmail(StringUtils.toString(emailItem.getValue()));
        user.setPhone(StringUtils.toString(phoneItem.getValue()));
        LocalUserService.Util.getInstance().createLocalUser(user, password, new AsyncCallbackImpl<Void>() {
          @Override
          public void onSuccess(final Void ignored) {
            SC.say("User created.");
            get().destroy();
          }
        });
      }
    });

    CreateLocalUserWindowComponentImpl() {
      form.setItems(usernameItem, passwordItem1, passwordItem2, firstNameItem, lastNameItem, emailItem, phoneItem);
      form.setWidth(260);
      form.setColWidths(120, 140);
      form.setValidationPredicate(new Predicate<Form>() {
        public boolean apply(final Form form) {
          final String username = form.getValueAsString("username");
          final String pass1 = form.getValueAsString("password1");
          final String pass2 = form.getValueAsString("password2");
          return StringUtils.hasText(username) && StringUtils.hasText(pass1) && pass1.equals(pass2);
        }
      });
      SmartUtils.enabledWhenValid(okButton, form);
      final CanvasWithOpsLayout<Form> canvas = new CanvasWithOpsLayout<Form>(form, okButton);
      canvas.setAutoHeight();
      canvas.setAutoWidth();
      setWidget(PopOutWindowBuilder.titled("Add Local User").autoSized().withContents(canvas).get());
    }
  }

}
