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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Supplier;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VisibilityMode;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.SectionStackSection;

import edu.umn.msi.tropix.webgui.client.utils.Listener;

public class PageFrameSupplierImpl extends WidgetSupplierImpl<Frame> implements Supplier<Frame> {
  private final Collection<Section> sections;
  private final EnhancedSectionStack sectionStack = new EnhancedSectionStack();
  private final Map<String, Boolean> expandedMap;

  public PageFrameSupplierImpl(final String title, final String icon, final Collection<Section> sections) {
    this(title, icon, sections, new HashMap<String, Boolean>());
  }

  public PageFrameSupplierImpl(final String title, final String icon, final Collection<Section> sections, final Map<String, Boolean> expandMap) {
    this.sections = new ArrayList<Section>(sections.size());
    this.expandedMap = expandMap;

    this.sectionStack.setCanResizeSections(false);
    this.sectionStack.setScrollSectionIntoView(true);
    this.sectionStack.setVisibilityMode(VisibilityMode.MULTIPLE);
    this.sectionStack.setLeaveScrollbarGap(true);
    this.sectionStack.setHeight100();
    this.sectionStack.setOverflow(Overflow.AUTO);
    this.sectionStack.addExpansionStateChangedListener(new Listener<EnhancedSectionStack>() {
      public void onEvent(final EnhancedSectionStack event) {
        recalculateExpansions();
      }
    });

    for(final Section section : sections) {
      if(section != null) {
        this.sections.add(section);
        if(section.sectionStackSection == null) {
          section.init();
        }
        final SectionStackSection ssSection = section.sectionStackSection;

        Boolean isExpanded = this.expandedMap.get(section.title);
        if(isExpanded == null) {
          isExpanded = section.expanded;
          this.expandedMap.put(section.title, isExpanded);
        } else {
          ssSection.setExpanded(isExpanded);
        }
        ssSection.setCanCollapse(true);
        this.sectionStack.addSection(ssSection);
      }
    }

    this.setWidget(new Frame());
    this.get().setTitle(title);
    this.get().setHeaderIcon(icon, 16, 16);
    this.get().addItem(this.sectionStack);
  }

  private void recalculateExpansions() {
    for(final Section section : sections) {
      if(section != null) {
        final boolean expanded = sectionStack.sectionIsExpanded(section.sectionStackSection);
        expandedMap.put(section.title, Boolean.valueOf(expanded));
      }
    }
  }

  EnhancedSectionStack getSectionStack() {
    return sectionStack;
  }

  public static class Section {
    private boolean resizeable, expanded;
    private String title;
    private Canvas item;
    private SectionStackSection sectionStackSection;

    public void setResizeable(final boolean resizeable) {
      this.resizeable = resizeable;
    }

    public void setExpanded(final boolean expanded) {
      this.expanded = expanded;
    }

    public void setTitle(final String title) {
      this.title = title;
    }

    public void setItem(final Canvas item) {
      this.item = item;
    }

    public void init() {
      final SectionStackSection section = new SectionStackSection();
      section.addItem(this.item);
      section.setResizeable(this.resizeable);
      section.setExpanded(this.expanded);
      section.setTitle(this.title);
      this.sectionStackSection = section;
    }

  }
}
