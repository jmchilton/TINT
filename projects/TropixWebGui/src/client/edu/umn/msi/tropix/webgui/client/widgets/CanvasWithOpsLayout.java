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

package edu.umn.msi.tropix.webgui.client.widgets;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class CanvasWithOpsLayout<T extends Canvas> extends VLayout {
  private final T widget;
  private final String title;
  private final Canvas[] operationsCanvases;
  private final HLayout operations = new HLayout();

  public CanvasWithOpsLayout(final String title, final T widget, final Canvas... operations) {
    this.title = title;
    this.widget = widget;
    this.operationsCanvases = operations;
    this.init();
  }

  public CanvasWithOpsLayout(final T widget, final Canvas... operations) {
    this(null, widget, operations);
  }

  private void init() {
    this.setLayoutMargin(10);
    this.setMembersMargin(10);
    this.operations.setWidth100();
    this.operations.setHeight(20);
    this.operations.setMembersMargin(10);
    this.operations.setMembers(this.operationsCanvases);
    //operations.setAlign(Alignment.RIGHT);
    if(this.title != null) {
      final Label titleLabel = new Label(title);
      titleLabel.setHeight(20);
      titleLabel.setWrap(false);
      this.addMember(titleLabel);
    }
    this.addMember(this.widget);
    this.addMember(this.operations);
  }

  public void addOperation(final Canvas canvas) {
    this.operations.addMember(this.widget);
  }

  public T getWidget() {
    return this.widget;
  }

}
