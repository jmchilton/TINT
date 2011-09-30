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

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.Layout;

import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescriptions;
import edu.umn.msi.tropix.jobs.activities.descriptions.CreateIdentificationParametersDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.JobDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.StringParameterSet;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.MetadataInputComponent;
import edu.umn.msi.tropix.webgui.client.components.MetadataInputComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.MetadataInputComponentFactory.MetadataOptions;
import edu.umn.msi.tropix.webgui.client.smart.handlers.CommandClickHandlerImpl;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.PopOutWindowBuilder;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.jobs.JobSubmitService;

public class SaveCommand implements Command {
  private final ParametersPanel parametersPanel;
  private final String parameterType;
  private final MetadataInputComponentFactory metadataInputComponentFactory;

  public SaveCommand(final MetadataInputComponentFactory metadataInputComponentFactory, final ParametersPanel panel, final String parametersType) {
    this.parametersPanel = panel;
    this.parameterType = parametersType;
    this.metadataInputComponentFactory = metadataInputComponentFactory;
  }

  private AsyncCallback<Void> getCallback(final String parentFolderId) {
    return new AsyncCallbackImpl<Void>() {
      @Override
      public void onSuccess(final Void saved) {
      }
    };
  }

  public void execute() {
    final Button okButton = SmartUtils.getButton("Save");
    okButton.setDisabled(true);
    final MetadataOptions metadataOptions = new MetadataOptions("identification parameters");
    metadataOptions.setIsValidListener(new Listener<Boolean>() {
      public void onEvent(final Boolean event) {
        okButton.setDisabled(!event);
      }
    });
    final MetadataInputComponent metadataCanvasSupplier = metadataInputComponentFactory.get(metadataOptions);
    okButton.addClickHandler(new CommandClickHandlerImpl(new Command() {
      public void execute() {
        final CreateIdentificationParametersDescription description = new CreateIdentificationParametersDescription();
        ActivityDescriptions.initCommonMetadata(description, metadataCanvasSupplier);
        description.setCommitted(true);
        description.setParameterType(parameterType);
        description.setCommitted(true);        
        description.setParameters(StringParameterSet.fromMap(parametersPanel.getParametersMap()));

        final JobDescription jobDescription = new JobDescription("Save parameters " + description.getName());
        description.setJobDescription(jobDescription);
        
        final Set<ActivityDescription> descriptions = new HashSet<ActivityDescription>();
        descriptions.add(description);
        JobSubmitService.Util.getInstance().submit(descriptions, new AsyncCallbackImpl<Void>());
      }      
    }));

    final Button cancelButton = SmartUtils.getButton("Cancel");
    final Layout layout = new CanvasWithOpsLayout<Canvas>(metadataCanvasSupplier.get(), okButton, cancelButton);
    final Window window = PopOutWindowBuilder.titled("Save Parameters").sized(400, 400).withContents(layout).withIcon(Resources.PARAMETERS_16).get();
    SmartUtils.destoryOnClick(window, cancelButton);    
    SmartUtils.destoryOnClick(window, okButton);
    window.show();
  }
}
