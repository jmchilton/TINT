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

import java.util.HashMap;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class GWTDownloadFormPanel extends FormPanel implements Command {
  public static final String DOWNLOAD_TYPE_SIMPLE = "simple";
  private Hidden type = new Hidden("downloadType");
  private Hidden id = new Hidden("id");
  private HashMap<String, Hidden> parameters = new HashMap<String, Hidden>();
  private String filename;
  private VerticalPanel vp = new VerticalPanel();

  public GWTDownloadFormPanel(final String... extraParameters) {
    this(true, extraParameters);
  }

  public GWTDownloadFormPanel(final boolean init, final String... extraParameters) {
    makeHidden();
    if(init) {
      this.init(extraParameters);
    }
  }

  public void addToPanel(final Widget widget) {
    this.vp.add(this.vp);
  }

  // I want this to be a hidden element on the page, but it seems to need to
  // have a non-negative width and height to work properly.
  private void makeHidden() {
    setWidth("1px");
    setHeight("1px");
  }

  public void init(final String[] extraParameters) {
    // setMethod(FormMethod.POST);
    this.setMethod(FormPanel.METHOD_POST);
    // setEncoding(Encoding.NORMAL);
    this.setEncoding(FormPanel.ENCODING_URLENCODED);

    if(extraParameters != null) {
      for(final String extraParameter : extraParameters) {
        addExtraParameter(extraParameter);
      }
    }

    try {
      this.vp.add(this.type);
      this.vp.add(this.id);
      this.setWidget(this.vp);
    } catch(final Exception e) {
      e.printStackTrace();
    }
    this.parameters.put("downloadType", this.type);
    this.parameters.put("id", this.id);

    this.setType(GWTDownloadFormPanel.DOWNLOAD_TYPE_SIMPLE);
  }

  private void addExtraParameter(final String extraParameter) {
    final Hidden item = new Hidden(extraParameter);
    this.vp.add(item);
    this.parameters.put(extraParameter, item);
  }

  public void setParameter(final String parameterName, final Object parameterValue) {
    if(!parameters.containsKey(parameterName)) {
      addExtraParameter(parameterName);
    }
    final Hidden hidden = parameters.get(parameterName);
    hidden.setValue(parameterValue.toString());
  }

  protected void setAction() {
    this.setAction(GWT.getHostPageBaseURL() + "download/" + this.getSanitizedFilename());
  }

  private String getSanitizedFilename() {
    // Remove non word characters/non periods from file name to ensure there are
    // no problems with
    // the resulting url.
    return this.filename.replaceAll("[^\\w\\.]", "_");
  }

  public void setId(final String id) {
    // setValue("id",id);
    this.setParameter("id", id);
  }

  public void setType(final String downloadType) {
    this.setParameter("downloadType", downloadType);
  }

  public void setFilename(final String filename) {
    this.filename = filename;
    this.setAction();
  }

  public void execute() {
    GWTDownloadFormPanel.this.submit();
  }
}
