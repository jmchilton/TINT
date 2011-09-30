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

package edu.umn.msi.tropix.webgui.client.forms;

import com.google.gwt.user.client.rpc.AsyncCallback;

import edu.umn.msi.tropix.webgui.services.forms.FormConfiguration;
import edu.umn.msi.tropix.webgui.services.forms.FormConfigurationService;
import edu.umn.msi.tropix.webgui.services.forms.ValidationProvider;

public class FormPanelFactory {

  public static void createParametersPanel(final String formKey, final AsyncCallback<FormPanelSupplier> outerCallback) {
    final FormPanelSupplier formSupplier = new FormPanelSupplier();
    final InputWidgetFactory inputWidgetFactory = new DefaultInputWidgetFactoryImpl();

    final AsyncCallback<FormConfiguration> callback = new AsyncCallback<FormConfiguration>() {

      public void onFailure(final Throwable throwable) {
        outerCallback.onFailure(throwable);
      }

      public void onSuccess(final FormConfiguration formConfiguration) {
        final String displayJson = formConfiguration.getDisplayConfiguration();
        final ValidationProvider validationProvider = formConfiguration.getValidationProvider();
        if(validationProvider != null) {
          formSupplier.setValidationProvider(validationProvider);
        }
        formSupplier.setFormConfiguration(displayJson);
        final FormWidgetFactoryImpl widgetFactory = new FormWidgetFactoryImpl();
        widgetFactory.setInputWidgetFactory(inputWidgetFactory);
        formSupplier.setFormWidgetFactory(widgetFactory);
        outerCallback.onSuccess(formSupplier);
      }
    };

    FormConfigurationService.Util.getInstance().getFormConfiguration(formKey, callback);
  }

}
