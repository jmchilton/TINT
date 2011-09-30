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

package edu.umn.msi.tropix.webgui.services.message;

import com.smartgwt.client.util.SC;

public class InfoMessage extends Message {
  private static final long serialVersionUID = 1L;

  public InfoMessage(final String title, final String body) {
    this.title = title;
    this.body = body;
  }

  public InfoMessage() {}
  
  /**
   * Title of MyGWT Info popup.
   */
  private String title;

  /**
   * Body of MyGWT Info popup.
   */
  private String body;

  public String getTitle() {
    return this.title;
  }

  public String getBody() {
    return this.body;
  }

  public void setTitle(final String title) {
    this.title = title;
  }

  public void setBody(final String body) {
    this.body = body;
  }

  public void respond() {
    SC.say(this.title + " - " + this.body);
  }

}
