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

package edu.umn.msi.tropix.webgui.client.search;

import edu.umn.msi.gwt.mvc.AppEvent;

public class SearchAppEvents {
  public static final int SEARCH_APP_EVENT_BASE = 4000;

  public interface SearchAppEvent {
    String getSearchId();
  }

  static class SearchAppEventImpl extends AppEvent implements SearchAppEvent {
    private final String searchId;

    protected SearchAppEventImpl(final int eventType, final String searchId) {
      super(eventType);
      this.searchId = searchId;
    }

    public String getSearchId() {
      return this.searchId;
    }
  }

  public static class ShowSearchEvent extends SearchAppEventImpl {
    public ShowSearchEvent(final String searchId) {
      super(SearchAppEvents.SEARCH_APP_EVENT_BASE + 0, searchId);
    }
  }

  public static class RemoveSearchEvent extends SearchAppEventImpl {
    public RemoveSearchEvent(final String searchId) {
      super(SearchAppEvents.SEARCH_APP_EVENT_BASE + 1, searchId);
    }
  }

  public static class HideSearchEvent extends SearchAppEventImpl {
    public HideSearchEvent(final String searchId) {
      super(SearchAppEvents.SEARCH_APP_EVENT_BASE + 2, searchId);
    }
  }

}
