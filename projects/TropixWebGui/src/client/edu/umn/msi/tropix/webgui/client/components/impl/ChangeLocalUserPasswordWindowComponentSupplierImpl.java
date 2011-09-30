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

import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.WindowComponent;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.Form;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.session.LocalUserService;

public class ChangeLocalUserPasswordWindowComponentSupplierImpl implements Supplier<WindowComponent<Window>> {

  public WindowComponent<Window> get() {
    return new ChangeLocalUserPasswordWindowComponentImpl();
  }

  private static class ChangeLocalUserPasswordWindowComponentImpl extends WindowComponentImpl<Window> {
    private final Form form = new Form();
    private final PasswordItem currentPasswordItem = new PasswordItem("currentPassword", "Current Password");
    private final PasswordItem passwordItem1 = new PasswordItem("password1", "New Password");
    private final PasswordItem passwordItem2 = new PasswordItem("password2", "Reenter New Password");

    private final Button okButton = SmartUtils.getButton("Update", Resources.OK, new Command() {
      public void execute() {
        final String currentPassword = StringUtils.toString(currentPasswordItem.getValue());
        final String newPassword = StringUtils.toString(passwordItem1.getValue());
        LocalUserService.Util.getInstance().changePassword(currentPassword, newPassword, new AsyncCallbackImpl<Void>() {
          @Override
          public void onSuccess(final Void ignored) {
            SC.say("Password changed.");
            get().destroy();
          }
        });
      }
    });

    ChangeLocalUserPasswordWindowComponentImpl() {
      form.setItems(currentPasswordItem, passwordItem1, passwordItem2);
      form.setWidth(300);
      form.setColWidths(160, 140);
      form.setValidationPredicate(new Predicate<Form>() {
        public boolean apply(final Form form) {
          final String currentPassword = form.getValueAsString("currentPassword");
          final String pass1 = form.getValueAsString("password1");
          final String pass2 = form.getValueAsString("password2");
          return StringUtils.hasText(currentPassword) && StringUtils.hasText(pass1) && pass1.equals(pass2);
        }
      });
      SmartUtils.enabledWhenValid(okButton, form);
      final CanvasWithOpsLayout<Form> canvas = new CanvasWithOpsLayout<Form>(form, okButton);
      canvas.setAutoHeight();
      canvas.setAutoWidth();
      setWidget(PopOutWindowBuilder.titled("Change Password").autoSized().withContents(canvas).get());
    }

  }

}
