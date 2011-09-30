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

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.JavaScriptObject;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.SectionStackSection;

import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.utils.ListenerList;
import edu.umn.msi.tropix.webgui.client.utils.ListenerLists;

public class EnhancedSectionStack extends com.smartgwt.client.widgets.layout.SectionStack {
  private final List<SectionStackSection> sections = new LinkedList<SectionStackSection>();
  private final ListenerList<EnhancedSectionStack> expansionStateChangedListeners = ListenerLists.getInstance(); // new ListenerListImpl<SectionStack>();

  public EnhancedSectionStack() {
    super();
    this.addClickHandler(new ClickHandler() {
      public void onClick(final ClickEvent event) {
        fireExpansionStateChanged();
      }
    });
  }

  public void addExpansionStateChangedListener(final Listener<EnhancedSectionStack> listener) {
    this.expansionStateChangedListeners.add(listener);
  }

  private void fireExpansionStateChanged() {
    this.expansionStateChangedListeners.onEvent(this);
  }

  public EnhancedSectionStack(final JavaScriptObject jsObj) {
    super(jsObj);
    this.addClickHandler(new ClickHandler() {
      public void onClick(final ClickEvent event) {
        fireExpansionStateChanged();
      }
    });
  }

  public SectionStackSection getSectionStatckSection(final int position) {
    return this.sections.get(position);
  }

  public void addSection(final SectionStackSection section, final int position) {
    super.addSection(section, position);
    this.sections.add(section);
  }

  public void addSection(final SectionStackSection section) {
    super.addSection(section);
    this.sections.add(section);
  }

  public void collapseSection(final int index) {
    super.collapseSection(index);
    this.fireExpansionStateChanged();
  }

  public void expandSection(final int index) {
    super.expandSection(index);
    this.fireExpansionStateChanged();
  }

  public void collapseSection(final String sectionID) {
    super.collapseSection(sectionID);
    this.fireExpansionStateChanged();
  }

  public void expandSection(final String sectionID) {
    super.expandSection(sectionID);
    this.fireExpansionStateChanged();
  }

  public boolean sectionIsExpanded(final SectionStackSection stack) {
    for(int i = 0; i < this.sections.size(); i++) {
      if(this.sections.get(i) == stack) {
        return this.sectionIsExpanded(i);
      }
    }
    throw new IllegalStateException("No such section stack section");
  }

  /*
   * //DOESN'T WORK, DON'T KNOW WHY public void sectionHeaderClick(Canvas sectionHeader) { super.sectionHeaderClick(sectionHeader); fireExpansionStateChanged(); }
   */

}
