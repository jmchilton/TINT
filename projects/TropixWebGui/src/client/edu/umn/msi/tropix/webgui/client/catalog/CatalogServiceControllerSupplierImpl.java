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

package edu.umn.msi.tropix.webgui.client.catalog;

import java.util.List;

import com.google.common.base.Supplier;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.smartgwt.client.types.Visibility;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.webgui.client.AsyncCallbackImpl;
import edu.umn.msi.tropix.webgui.client.catalog.beans.Provider;
import edu.umn.msi.tropix.webgui.client.catalog.beans.ServiceBean;
import edu.umn.msi.tropix.webgui.client.components.CanvasComponent;
import edu.umn.msi.tropix.webgui.client.components.ComponentFactory;
import edu.umn.msi.tropix.webgui.client.modules.ModuleManager;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.client.widgets.WidgetSupplierImpl;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils.Cleanable;
import edu.umn.msi.tropix.webgui.services.tropix.CatalogServiceDefinition;

public class CatalogServiceControllerSupplierImpl implements Supplier<CatalogServiceController> {
  private ComponentFactory<Provider, ? extends Command> showCatalogProviderComponentFactory;
  private ComponentFactory<ServiceBean, ? extends CanvasComponent<? extends Canvas>> showCatalogServiceComponentFactory;
  private ComponentFactory<ServiceBean, ? extends Command> requestServiceComponentFactory;
  private ModuleManager moduleManager;

  @Inject
  public void setRequestServiceComponentFactory(@Named("requestService") final ComponentFactory<ServiceBean, ? extends Command> requestServiceComponentFactory) {
    this.requestServiceComponentFactory = requestServiceComponentFactory;
  }

  @Inject
  public void setShowCatalogServiceComponentFactory(@Named("showCatalogService") final ComponentFactory<ServiceBean, ? extends CanvasComponent<? extends Canvas>> showCatalogServiceComponentFactory) {
    this.showCatalogServiceComponentFactory = showCatalogServiceComponentFactory;
  }

  @Inject
  public void setShowCatalogProviderComponentFactory(@Named("showCatalogProvider") final ComponentFactory<Provider, ? extends Command> showCatalogProviderComponentFactory) {
    this.showCatalogProviderComponentFactory = showCatalogProviderComponentFactory;
  }

  @Inject
  public void setModuleManager(final ModuleManager moduleManager) {
    this.moduleManager = moduleManager;
  }

  public CatalogServiceController get() {
    final CatalogServiceControllerImpl controller = new CatalogServiceControllerImpl();
    return controller;
  }

  private class CatalogServiceControllerImpl extends WidgetSupplierImpl<Layout> implements CatalogServiceController {
    private final VLayout spServiceDetails;
    private final VLayout spServiceDetailsEdit;
    private final VLayout spServiceProviderPanel;
    private final VLayout spSearchResult;
    private final ServiceResultPanel pnlSearchResult;
    private final ServiceDetailsPanel pnlServiceDetails;
    //private final ServiceProviderPanel pnlServiceProvider;

    public CatalogServiceControllerImpl() {
      this.setWidget(new VLayout());
      this.get().setWidth100();
      this.get().setHeight100();
      
      this.pnlSearchResult = new ServiceResultPanel(this);
      this.pnlServiceDetails = new ServiceDetailsPanel(this, moduleManager);
      //this.pnlServiceProvider = new ServiceProviderPanel(this);

      this.spSearchResult = this.pnlSearchResult;
      this.spServiceDetails = new VLayout();
      this.spServiceDetails.setHeight100();
      this.spServiceDetails.setWidth100();
      this.spServiceDetails.addMember(this.pnlServiceDetails);
      this.spServiceDetailsEdit = new VLayout();
      this.spServiceDetailsEdit.setWidth100();
      this.spServiceDetailsEdit.setHeight100();
      this.spServiceProviderPanel = new VLayout();
      this.spServiceProviderPanel.setWidth100();
      this.spServiceProviderPanel.setHeight100();

      //this.spServiceProviderPanel.addMember(this.pnlServiceProvider);
      this.spSearchResult.setVisibility(Visibility.HIDDEN);
      this.spServiceDetails.setVisibility(Visibility.HIDDEN);
      this.spServiceDetailsEdit.setVisibility(Visibility.HIDDEN);
      this.spServiceProviderPanel.setVisibility(Visibility.HIDDEN);

      this.get().addChild(this.spSearchResult);
      this.get().addChild(this.spServiceDetails);
      this.get().addChild(this.spServiceDetailsEdit);
      this.get().addChild(this.spServiceProviderPanel);
      
    }

    public void createNewProvider() {
      this.spServiceDetailsEdit.setVisibility(Visibility.HIDDEN);
      this.spSearchResult.setVisibility(Visibility.HIDDEN);
      this.spServiceDetails.setVisibility(Visibility.HIDDEN);
      this.spServiceProviderPanel.setVisibility(Visibility.INHERIT);
      //this.pnlServiceProvider.reset();
    }

    public void populateServiceDetails(final ServiceBean serviceBean, final int inputResultListSize, final int inputPosInList) {
      this.spSearchResult.setVisibility(Visibility.HIDDEN);
      this.spServiceDetailsEdit.setVisibility(Visibility.HIDDEN);
      this.spServiceProviderPanel.setVisibility(Visibility.HIDDEN);
      this.spServiceDetails.setVisibility(Visibility.INHERIT);
      int posInList = inputPosInList;
      int resultListSize = inputResultListSize;
      if(posInList == -1) {
        posInList = 1;
        resultListSize = 1;
      }
      this.pnlServiceDetails.populateServiceDetails(serviceBean, resultListSize, posInList);
    }

    public void edit(final ServiceBean serviceBean, final boolean inResultList) {
      this.spSearchResult.setVisibility(Visibility.HIDDEN);
      this.spServiceDetails.setVisibility(Visibility.HIDDEN);
      this.spServiceDetailsEdit.setVisibility(Visibility.INHERIT);
      this.spServiceProviderPanel.setVisibility(Visibility.HIDDEN);
    }

    public void showSearchResults(final boolean affectHistory) {
      this.reset("");
      if(this.pnlSearchResult.getServiceBeans() != null && this.pnlSearchResult.getServiceBeans().size() > 0) {
        this.spSearchResult.setVisibility(Visibility.INHERIT);
      }
    }

    public void showPrevService() {
      this.pnlSearchResult.displayPrevious();
    }

    public void showNextService() {
      this.pnlSearchResult.displayNext();
    }

    public void cancelEditService() {
      this.pnlSearchResult.displayCurrentService();
    }

    public void showUpdatedService(final ServiceBean serviceBean, final boolean doUpdateServiceInResultList) {
      this.pnlSearchResult.showUpdatedService(serviceBean, doUpdateServiceInResultList);
    }

    public void reset(final String message) {
      this.spSearchResult.setVisibility(Visibility.HIDDEN);
      this.spServiceDetails.setVisibility(Visibility.HIDDEN);
      this.spServiceDetailsEdit.setVisibility(Visibility.HIDDEN);
      this.spServiceProviderPanel.setVisibility(Visibility.HIDDEN);
    }

    public void populateSearchResults(final List<ServiceBean> serviceBeans) {
      this.spSearchResult.setVisibility(Visibility.INHERIT);
      this.pnlSearchResult.populateTable(serviceBeans);
    }

    public void createNewService() {
      this.spServiceDetailsEdit.setVisibility(Visibility.INHERIT);
      this.spSearchResult.setVisibility(Visibility.HIDDEN);
      this.spServiceDetails.setVisibility(Visibility.HIDDEN);
      this.spServiceProviderPanel.setVisibility(Visibility.HIDDEN);
    }

    public void displayService(final int position) {
      this.pnlSearchResult.displayService(position);
    }

    public void showSearchOnly() {
      this.spServiceDetailsEdit.setVisibility(Visibility.HIDDEN);
      this.spServiceProviderPanel.setVisibility(Visibility.HIDDEN);
    }

    public void updateProviderList() {
    }

    public void showProviderDetails(final Provider provider) {
      CatalogServiceControllerSupplierImpl.this.showCatalogProviderComponentFactory.get(provider).execute();
    }

    public Canvas getShowCatalogServiceCanvas(final ServiceBean serviceBean) {
      return CatalogServiceControllerSupplierImpl.this.showCatalogServiceComponentFactory.get(serviceBean).get();
    }

    public void request(final ServiceBean serviceBean) {
      if(serviceBean.isPopulated()) {
        requestServiceComponentFactory.get(serviceBean).execute();
      } else {
        final Cleanable loadingManager = SmartUtils.indicateLoading(get());
        final AsyncCallback<ServiceBean> callback = new AsyncCallbackImpl<ServiceBean>(loadingManager) {
          @Override
          public void onSuccess(final ServiceBean serviceBean) {
            request(serviceBean);
            loadingManager.close();
          }
        };
        CatalogServiceDefinition.Util.getInstance().getServiceDetails(serviceBean.getId(), serviceBean.getCatalogId(), callback);        
      }
    }

    // TODO: Remove loadingComplete on error...
    public void showServiceDetails(final ServiceBean serviceBean, final List<ServiceBean> serviceBeans, final int posInList) {
      final int size = serviceBeans.size();
      if(serviceBean.isPopulated()) {
        populateServiceDetails(serviceBean, size, posInList);
      } else {
        final Cleanable loadingManager = SmartUtils.indicateLoading(get());
        final AsyncCallback<ServiceBean> callback = new AsyncCallbackImpl<ServiceBean>(loadingManager) {
          @Override
          public void onSuccess(final ServiceBean serviceBean) {
            serviceBean.setLuceneScore(serviceBeans.get(posInList).getLuceneScore());
            serviceBeans.set(posInList, serviceBean);
            populateServiceDetails(serviceBean, size, posInList);
            loadingManager.close();
          }
        };
        CatalogServiceDefinition.Util.getInstance().getServiceDetails(serviceBean.getId(), serviceBean.getCatalogId(), callback);
      }     
    }
  }

}
