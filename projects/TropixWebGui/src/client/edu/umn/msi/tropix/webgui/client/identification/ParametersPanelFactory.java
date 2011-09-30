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

package edu.umn.msi.tropix.webgui.client.identification;

import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;

import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.components.MetadataInputComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponentFactory;
import edu.umn.msi.tropix.webgui.services.protip.IdentificationParametersService;
import edu.umn.msi.tropix.webgui.services.protip.Parameters;

// Factory serves to separate the ParametersPanel class from the IdentificationParameters service 
// calls and the added complexity of asynchronicity.
public class ParametersPanelFactory {
  private TreeComponentFactory treeComponentFactory;
  private LocationFactory locationFactory;
  private MetadataInputComponentFactory metadataInputComponentFactory;

  @Inject
  public void setTreeComponentFactory(final TreeComponentFactory treeComponentFactory, final LocationFactory locationFactory, final MetadataInputComponentFactory metadataInputComponentFactory) {
    this.treeComponentFactory = treeComponentFactory;
    this.locationFactory = locationFactory;
    this.metadataInputComponentFactory = metadataInputComponentFactory;
  }

  // Use this version to just obtain a panel, use the other if you care about
  // when and if it is initialized properly.
  public ParametersPanel createParametersPanel(final String typeId, final boolean readOnly) {
    final AsyncCallback<ParametersPanel> nullCallback = new AsyncCallbackImpl<ParametersPanel>();
    return this.createParametersPanel(typeId, nullCallback, readOnly);
  }

  public ParametersPanel createParametersPanel(final String typeId, final AsyncCallback<ParametersPanel> outerCallback, final boolean readOnly, final String parametersId) {
    final ParametersPanel panel = new ParametersPanel(treeComponentFactory, locationFactory, metadataInputComponentFactory);
    final AsyncCallback<Parameters> callback = new ParametersCallback(typeId, panel, outerCallback, readOnly, parametersId);
    IdentificationParametersService.Util.getInstance().getParametersInformation(typeId, callback);
    return panel;
  }

  public ParametersPanel createParametersPanel(final String typeId, final AsyncCallback<ParametersPanel> outerCallback, final boolean readOnly) {
    final ParametersPanel panel = new ParametersPanel(treeComponentFactory, locationFactory, metadataInputComponentFactory);
    final AsyncCallback<Parameters> callback = new ParametersCallback(typeId, panel, outerCallback, readOnly, null);
    IdentificationParametersService.Util.getInstance().getParametersInformation(typeId, callback);
    return panel;
  }

  static class ParametersCallback implements AsyncCallback<Parameters> {
    private final ParametersPanel panel;
    private final AsyncCallback<ParametersPanel> outerCallback;
    private final String typeId;
    private final boolean readOnly;
    private final String parametersId;

    ParametersCallback(final String typeId, final ParametersPanel panel, final AsyncCallback<ParametersPanel> outerCallback, final boolean readOnly, final String parametersId) {
      this.typeId = typeId;
      this.panel = panel;
      this.outerCallback = outerCallback;
      this.readOnly = readOnly;
      this.parametersId = parametersId;
    }

    public void onFailure(final Throwable t) {
      this.outerCallback.onFailure(t);
    }

    public void onSuccess(final Parameters parameters) {
      this.panel.init(this.typeId, parameters.getSpecificationJSON(), parameters.getDisplayJSON(), this.readOnly);
      if(this.parametersId != null) {
        IdentificationParametersService.Util.getInstance().getParameterMap(this.parametersId, new AsyncCallbackImpl<Map<String, String>>() {
          @Override
          public void onSuccess(final Map<String, String> templateMap) {
            ParametersCallback.this.panel.setTemplateMap(templateMap);
            ParametersCallback.this.outerCallback.onSuccess(ParametersCallback.this.panel);
          }
        });
      } else {
        this.outerCallback.onSuccess(this.panel);
      }
    }
  }
}
