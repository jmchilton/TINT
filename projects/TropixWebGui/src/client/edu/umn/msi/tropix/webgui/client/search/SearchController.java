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

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Supplier;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.smartgwt.client.widgets.Window;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.tab.Tab;

import edu.umn.msi.gwt.mvc.AppEvent;
import edu.umn.msi.gwt.mvc.ChangeEvent;
import edu.umn.msi.gwt.mvc.ChangeListener;
import edu.umn.msi.gwt.mvc.Controller;
import edu.umn.msi.gwt.mvc.Model;
import edu.umn.msi.tropix.client.search.models.GridData;
import edu.umn.msi.tropix.models.locations.Locations;
import edu.umn.msi.tropix.webgui.client.catalog.beans.ServiceBean;
import edu.umn.msi.tropix.webgui.client.components.ResultComponent;
import edu.umn.msi.tropix.webgui.client.mediators.LocationUpdateMediator;
import edu.umn.msi.tropix.webgui.client.modules.RequiresAnyModule;
import edu.umn.msi.tropix.webgui.client.search.SearchModel.SearchType;
import edu.umn.msi.tropix.webgui.services.object.SearchResult;
import edu.umn.msi.tropix.webgui.services.session.Module;

public class SearchController extends Controller implements Supplier<Tab>, RequiresAnyModule {
  private final LocationUpdateMediator updateMediator = LocationUpdateMediator.getInstance();
  private final SearchView searchView;
  private final SearchCollectionModel searchCollectionModel;
  private Supplier<? extends ResultComponent<List<GridData>, ? extends Window>> gridSearchResultComponentSupplier;
  private Supplier<? extends ResultComponent<List<ServiceBean>, ? extends Window>> catalogSearchResultComponentSupplier;
  private Supplier<? extends ResultComponent<List<SearchResult>, ? extends Window>> localSearchResultComponentSupplier;
  private static SearchController instance; // TODO: Eliminate need for this hack

  public static SearchController getInstance() {
    return instance;
  }

  private final ChangeListener searchModelChangeHandler = new ChangeListener() {
    public void modelChanged(final ChangeEvent event) {
      final SearchModel searchModel = (SearchModel) event.item;
      if(searchModel.getSearchType().equals(SearchModel.SearchType.LOCAL) && searchModel.isComplete()) {
        SearchController.this.updateMediator.onEvent(new LocationUpdateMediator.AddUpdateEvent(searchModel.getId(), null, Locations.MY_RECENT_SEARCHES_ID));
      }
    }
  };

  public SearchController() {
    instance = this;
    this.searchCollectionModel = new SearchCollectionModel();
    this.searchCollectionModel.addChangeListener(new ChangeListener() {
      public void modelChanged(final ChangeEvent event) {
        if(event.type == Model.Add) {
          final Model searchModel = event.item;
          searchModel.addChangeListener(SearchController.this.searchModelChangeHandler);
        } else if(event.type == Model.Remove) {
          final SearchModel searchModel = (SearchModel) event.item;
          searchModel.removeChangeListener(SearchController.this.searchModelChangeHandler);
          SearchController.this.updateMediator.onEvent(new LocationUpdateMediator.RemoveUpdateEvent(searchModel.getId(), null));
        }
      }
    });
    this.searchView = new SearchView(this, this.searchCollectionModel);
  }

  public Tab get() {
    return this.searchView.get();
  }

  public Iterable<SearchModel> getLocalSearchModels() {
    return this.searchCollectionModel.getSearchModels(SearchType.LOCAL);
  }

  private static <T> AsyncCallback<T> getCallback(final SearchModel searchModel, final Supplier<? extends ResultComponent<T, ? extends Window>> resultComponentSupplier) {
    return new AsyncCallback<T>() {
      public void onFailure(final Throwable arg0) {
        searchModel.fail();
      }

      public void onSuccess(final T results) {
        final ResultComponent<T, ? extends Window> resultComponent = resultComponentSupplier.get();
        resultComponent.setResults(results);
        final Layout layout = resultComponent.get();
        searchModel.setResults(results);
        searchModel.complete(layout);
        layout.show();
      }
    };
  }

  public void handleEvent(final AppEvent event) {
    final String id = ((SearchAppEvents.SearchAppEvent) event).getSearchId();
    final Layout layout = this.searchCollectionModel.getSearchModel(id).getLayout();
    if(layout != null) {
      if(event instanceof SearchAppEvents.HideSearchEvent) {
        layout.hide();
      } else if(event instanceof SearchAppEvents.RemoveSearchEvent) {
        layout.destroy();
        this.searchCollectionModel.remove(this.searchCollectionModel.getSearchModel(id));
      } else if(event instanceof SearchAppEvents.ShowSearchEvent) {
        layout.show();
      }
    }
  }

  public AsyncCallback<List<GridData>> startGridSearch(final String searchName) {
    final SearchModel searchModel = new SearchModel(searchName, SearchType.GRID);
    this.searchCollectionModel.add(searchModel);
    return SearchController.getCallback(searchModel, gridSearchResultComponentSupplier);
  }

  public AsyncCallback<List<SearchResult>> startLocalSearch(final String searchName) {
    final SearchModel searchModel = new SearchModel(searchName, SearchType.LOCAL);
    this.searchCollectionModel.add(searchModel);
    return SearchController.getCallback(searchModel, localSearchResultComponentSupplier);
  }

  public AsyncCallback<List<ServiceBean>> startCatalogSearch(final String searchName) {
    final SearchModel searchModel = new SearchModel(searchName, SearchType.CATALOG);
    this.searchCollectionModel.add(searchModel);
    return SearchController.getCallback(searchModel, catalogSearchResultComponentSupplier);
  }

  @Inject
  public void setGridSearchResultComponentSupplier(final Supplier<? extends ResultComponent<List<GridData>, ? extends Window>> gridSearchResultComponentSupplier) {
    this.gridSearchResultComponentSupplier = gridSearchResultComponentSupplier;
  }

  @Inject
  public void setCatalogSearchResultComponentSupplier(final Supplier<? extends ResultComponent<List<ServiceBean>, ? extends Window>> catalogSearchResultComponentSupplier) {
    this.catalogSearchResultComponentSupplier = catalogSearchResultComponentSupplier;
  }

  @Inject
  public void setLocalSearchResultComponentSupplier(final Supplier<? extends ResultComponent<List<SearchResult>, ? extends Window>> localSearchResultComponentSupplier) {
    this.localSearchResultComponentSupplier = localSearchResultComponentSupplier;
  }

  public Iterable<Module> requiresAnyModule() {
    return Arrays.<Module>asList(Module.LOCAL_SEARCH, Module.CATALOG, Module.GRID_SEARCH);
  }

}
