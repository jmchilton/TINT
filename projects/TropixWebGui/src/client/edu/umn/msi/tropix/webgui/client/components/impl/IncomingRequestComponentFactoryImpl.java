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

import java.util.Arrays;

import com.google.common.base.Function;
import com.google.gwt.user.client.Command;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.util.ValueCallback;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.models.Request;
import edu.umn.msi.tropix.models.TropixObject;
import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.models.ModelFunctions;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.client.widgets.CanvasWithOpsLayout;
import edu.umn.msi.tropix.webgui.client.widgets.PageFrameSupplierImpl;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.PageFrameSupplierImpl.Section;
import edu.umn.msi.tropix.webgui.services.tropix.RequestService;

public class IncomingRequestComponentFactoryImpl implements ComponentFactory<Request, Canvas> {
  private final Function<TropixObject, String> iconFunction = ModelFunctions.getIconFunction16();

  public Canvas get(final Request request) {
    final String title = StringUtils.sanitize(request.getName()) + " (Incoming Request)";
    final String icon = iconFunction.apply(request);
    final MetadataSection metadataSection = new MetadataSection(request);
    metadataSection.set("Request State", StringUtils.sanitize(request.getState()));
    metadataSection.set("Destination Request Service URL", StringUtils.sanitize(request.getDestination()));

    final Section overviewSection = new Section();
    overviewSection.setTitle("Request Overview");
    final VLayout overviewContent = new VLayout();

    RequestService.Util.getInstance().getExpandedRequest(request.getId(), new AsyncCallbackImpl<Request>() {
      public void onSuccess(final Request expandedRequest) {
        overviewContent.addMember(SmartUtils.smartParagraph("Contact Info: <pre>" + StringUtils.sanitize(expandedRequest.getContact()) + "</pre>"));
        overviewContent.addMember(SmartUtils.smartParagraph("Status Report: <pre>" + StringUtils.sanitize(expandedRequest.getReport()) + "</pre>"));
        if(StringUtils.hasText(expandedRequest.getServiceInfo())) {
          overviewContent.addMember(SmartUtils.smartParagraph("Input: <pre>" + StringUtils.sanitize(expandedRequest.getServiceInfo()) + "</pre>"));
        }
      }
    });

    final Button completeButton = SmartUtils.getButton("Complete", Resources.OK, new Command() {
      public void execute() {
        RequestService.Util.getInstance().completeRequest(request.getId(), new AsyncCallbackImpl<Void>());
      }
    });
    final Button updateButton = SmartUtils.getButton("Update Status", Resources.EDIT, new Command() {
      public void execute() {
        SC.askforValue("Enter short status report", new ValueCallback() {
          public void execute(final String value) {
            RequestService.Util.getInstance().setReport(request.getId(), StringUtils.sanitize(value), new AsyncCallbackImpl<Void>());
          }
        });
      }
    });
    final CanvasWithOpsLayout<VLayout> canvas = new CanvasWithOpsLayout<VLayout>(overviewContent, completeButton, updateButton);
    overviewSection.setItem(canvas);
    final PageFrameSupplierImpl frameSupplier = new PageFrameSupplierImpl(title, icon, Arrays.asList(metadataSection, overviewSection));
    return frameSupplier.get();
  }

}
