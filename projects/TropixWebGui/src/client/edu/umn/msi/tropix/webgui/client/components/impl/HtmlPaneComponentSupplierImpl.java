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

import com.google.common.base.Supplier;
import com.smartgwt.client.types.ContentsType;
import com.smartgwt.client.widgets.HTMLPane;

import edu.umn.msi.tropix.webgui.client.components.CanvasComponent;
import edu.umn.msi.tropix.webgui.client.widgets.WidgetSupplierImpl;

public class HtmlPaneComponentSupplierImpl implements Supplier<CanvasComponent<HTMLPane>> {
  private String url;

  private static class HtmlPanelComponentImpl extends WidgetSupplierImpl<HTMLPane> implements CanvasComponent<HTMLPane> {

    HtmlPanelComponentImpl(final String url) {
      this.setWidget(new HTMLPane());
      this.get().setContentsType(ContentsType.PAGE);
      this.get().setContentsURL(url);
      this.get().setWidth100();
      this.get().setHeight100();
      this.get().setShowEdges(false);
    }
  }

  public void setUrl(final String url) {
    this.url = url;
  }

  public CanvasComponent<HTMLPane> get() {
    return new HtmlPanelComponentImpl(url);
  }

}
