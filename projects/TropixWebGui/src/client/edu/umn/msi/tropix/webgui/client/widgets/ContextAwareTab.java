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

import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

public class ContextAwareTab extends Tab {
  private TabSet tabSet;

  public static void addTabToTabSet(final Tab tab, final TabSet tabSet) {
    if(tab instanceof ContextAwareTab) {
      ((ContextAwareTab) tab).addToTabSet(tabSet);
    } else {
      tabSet.addTab(tab);
    }
  }

  public void addToTabSet(final TabSet tabSet) {
    this.tabSet = tabSet;
    tabSet.addTab(this);
  }

  public TabSet getTabSet() {
    return this.tabSet;
  }

  public void selectTab() {
    this.tabSet.selectTab(this);
  }

}
