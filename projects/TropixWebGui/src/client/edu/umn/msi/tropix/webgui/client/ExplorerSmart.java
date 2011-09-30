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

package edu.umn.msi.tropix.webgui.client;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Supplier;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tree.TreeGrid;

import edu.umn.msi.tropix.webgui.client.components.LoginCallback;
import edu.umn.msi.tropix.webgui.client.components.LoginComponent;
import edu.umn.msi.tropix.webgui.client.components.MainToolStripComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.LocationFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponent;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeComponentFactory;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeItem;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions;
import edu.umn.msi.tropix.webgui.client.components.tree.TreeOptions.SelectionType;
import edu.umn.msi.tropix.webgui.client.mediators.LoginMediator;
import edu.umn.msi.tropix.webgui.client.mediators.NavigationSelectionMediator;
import edu.umn.msi.tropix.webgui.client.mediators.NavigationSelectionMediator.NavigationSelection;
import edu.umn.msi.tropix.webgui.client.utils.Listener;
import edu.umn.msi.tropix.webgui.client.widgets.Frame;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.message.MessageServiceClient;
import edu.umn.msi.tropix.webgui.services.session.LoginInfo;
import edu.umn.msi.tropix.webgui.services.session.LoginListener;
import edu.umn.msi.tropix.webgui.services.session.LoginService;
import edu.umn.msi.tropix.webgui.services.session.SessionInfo;

//TODO: Separate out GUI logic into a new class
public class ExplorerSmart extends Explorer {
  private Supplier<? extends Canvas> pageCanvasSupplier;
  private MainToolStripComponent toolStripSupplier;
  private Supplier<TabSet> tabSetSupplier;
  private NavigationSelectionMediator navigationSelectionMediator;
  private TreeComponentFactory treeComponentFactory;
  private LoginComponent loginComponent;
  private LocationFactory locationFactory;
  private Session session;
  private LoginMediator loginMediator;

  @Inject
  ExplorerSmart(final EagerSingletons singletons) {
  }
  
  @Inject 
  public void setSession(final Session session) {
    this.session = session;
  }

  @Inject
  public void setLoginMediator(final LoginMediator loginMediator) {
    this.loginMediator = loginMediator;
  }
  
  @Inject
  public void setLocationFactory(final LocationFactory locationFactory) {
    this.locationFactory = locationFactory;
  }

  @Inject
  public void setLoginComponent(final LoginComponent loginComponent) {
    this.loginComponent = loginComponent;
  }

  @Inject
  public void setTreeComponentFactory(final TreeComponentFactory treeComponentFactory) {
    this.treeComponentFactory = treeComponentFactory;
  }

  @Inject
  public void setPageCanvasSupplier(@Named("page") final Supplier<? extends Layout> pageCanvasSupplier) {
    this.pageCanvasSupplier = pageCanvasSupplier;
  }

  @Inject
  public void setMainToolStripComponent(final MainToolStripComponent toolStripSupplier) {
    this.toolStripSupplier = toolStripSupplier;
  }

  @Inject
  public void setTabSetSupplier(@Named("main") final Supplier<TabSet> tabSetSupplier) {
    this.tabSetSupplier = tabSetSupplier;
  }

  @Inject
  public void setNavigationSelectionMediator(final NavigationSelectionMediator navigationSelectionMediator) {
    this.navigationSelectionMediator = navigationSelectionMediator;
  }

  private class SmartLoginListener implements LoginListener {
    private AsyncCallback<Boolean> validLogin = null;

    public void loginEvent() {
      if(this.validLogin != null) {
        this.validLogin.onSuccess(true);
      }
      this.validLogin = null;
      ExplorerSmart.this.handleLogin();
    }

    public void logoutEvent() {
      this.validLogin = null;
    }

    public void loginFailedEvent() {
      SC.say("Login failed. Please try again or contact Tropix Staff.");
      this.validLogin.onSuccess(false);
    }
  }

  private final SmartLoginListener loginListener = new SmartLoginListener();

  private void showLoginDialog(final List<String> authenticationSources) {
    loginComponent.showLoginDialog(authenticationSources, new LoginCallback() {
      public void loginAttempt(final String username, final String password, final String authentication, final AsyncCallback<Boolean> validLogin) {
        loginListener.validLogin = validLogin;
        loginMediator.loginAttempt(username, password, authentication);
      }
    });
  }

  private void handleLogin() {    
    final TreeComponent treeComponent = getProjectTreeComponent();
    initNavigationSelectionMediator(treeComponent);
    toolStripSupplier.setMainTreeComponent(treeComponent);

    drawPage(treeComponent);
  }

  private void drawPage(final TreeComponent treeComponent) {
    final VLayout main = getMainLayout();
    final HLayout titleLayout = getTitleLayout();
    final HLayout bodyLayout = getBodyLayout(treeComponent);

    main.addMember(titleLayout);
    main.addMember(toolStripSupplier.get());
    main.addMember(bodyLayout);
    
    main.draw();
  }

  private HLayout getBodyLayout(final TreeComponent treeComponent) {
    final VLayout sideLayout = getSideLayout(treeComponent);
    final VLayout pageLayout = getPageLayout();

    final HLayout bodyLayout = new HLayout();
    bodyLayout.setMargin(2);
    bodyLayout.setWidth100();
    bodyLayout.setHeight100();
    
    bodyLayout.addMember(sideLayout);
    bodyLayout.addMember(pageLayout);
    return bodyLayout;
  }

  private void initNavigationSelectionMediator(final TreeComponent treeComponent) {
    final Listener<NavigationSelection> navigationListener = new Listener<NavigationSelection>() {
      public void onEvent(final NavigationSelection navigationSelection) {
        // If the navigation wasn't initiated from the tree deselect everything
        // from the tree,
        // as to not cause confusion.
        if(navigationSelection.getSource() != treeComponent) {
          treeComponent.deselect();
        }
      }
    };
    
    navigationSelectionMediator.addNavigationSelectionChangedListener(navigationListener);
    treeComponent.addMultiSelectionListener(new Listener<Collection<TreeItem>>() {
      public void onEvent(final Collection<TreeItem> treeItems) {
        navigationSelectionMediator.onEvent(new NavigationSelection(treeItems, treeComponent));
      }
    });
  }

  private VLayout getSideLayout(final TreeComponent treeComponent) {
    final VLayout sideLayout = new VLayout();
    sideLayout.setWidth("30%");
    sideLayout.setHeight100();

    final TreeGrid canvas = treeComponent.get();
    final Frame treeFrame = new Frame();
    treeFrame.setTitle("Project Explorer");
    treeFrame.setShowHeaderIcon(false);
    treeFrame.addItem(canvas);

    sideLayout.addMember(treeFrame);
    sideLayout.setShowResizeBar(true);
    return sideLayout;
  }

  private VLayout getPageLayout() {
    final VLayout bodyLayout = new VLayout();
    bodyLayout.setWidth("70%");
    bodyLayout.setHeight100();

    final TabSet tabSet = getTabSet();
    final Canvas pageCanvas = getPageCanvas();
    
    bodyLayout.addMember(pageCanvas);
    bodyLayout.addMember(tabSet);
    return bodyLayout;
  }

  private TabSet getTabSet() {
    final TabSet tabSet = tabSetSupplier.get();
    tabSet.setWidth100();
    return tabSet;
  }

  private Canvas getPageCanvas() {
    final Canvas pageCanvas = pageCanvasSupplier.get();
    pageCanvas.setShowResizeBar(true);
    pageCanvas.setHeight("80%");
    return pageCanvas;
  }

  private TreeComponent getProjectTreeComponent() {
    final TreeOptions options = new TreeOptions();
    options.setInitialItems(locationFactory.getAllRootItems(null));
    options.setSelectionType(SelectionType.MULTIPlE);
    final TreeComponent treeComponent = treeComponentFactory.get(options);
    return treeComponent;
  }

  private HLayout getTitleLayout() {
    final HLayout titleLayout = new HLayout();
    titleLayout.setBackgroundImage(GWT.getHostPageBaseURL() + "images/tropix_background.png");
    titleLayout.setWidth100();
    titleLayout.setHeight(64);
    titleLayout.setMargin(0);
    
    final Img image = getInstitutionLogo();
    titleLayout.addChild(image);
    titleLayout.setAlign(Alignment.RIGHT);
    
    final Img logo = getTintLogo(image);
    titleLayout.addMember(logo);

    return titleLayout;
  }

  private Img getTintLogo(final Img image) {
    final Img logo = new Img(GWT.getHostPageBaseURL() + "images/tint_logo.png");
    logo.setSize("500", "64");
    logo.moveBelow(image);
    return logo;
  }

  private Img getInstitutionLogo() {
    final Img image = new Img(GWT.getHostPageBaseURL() + "images/inst_logo.png");
    image.setSize("300", "64");
    return image;
  }

  private VLayout getMainLayout() {
    final VLayout mainLayout = SmartUtils.getFullVLayout();
    mainLayout.setMargin(0);
    mainLayout.setMembersMargin(0);
    mainLayout.setShowResizeBar(false);
    mainLayout.setShowDragPlaceHolder(false);
    mainLayout.setShowCustomScrollbars(false);
    mainLayout.setLeaveScrollbarGap(false);
    return mainLayout;
  }

  @Override
  public void onModuleLoad() {
    loginMediator.addListener(this.loginListener);
    loginMediator.addListener(new LoggedOutChecker(loginMediator));
    loginMediator.addListener(new LoggedOutHandler());
    loginMediator.addListener(MessageServiceClient.getLoginListener());
    LoginService.Util.getInstance().getUserIfLoggedIn(new CheckUserLoginCallback());
  }

  private class CheckUserLoginCallback extends AsyncCallbackImpl<LoginInfo> {

    public void onSuccess(final LoginInfo loginInfo) {
      final SessionInfo sessionInfo = loginInfo.getSessionInfo();
      if(sessionInfo == null) {
        showLoginDialog(loginInfo.getAuthenticationServices());
      } else {
        session.init(sessionInfo);
        loginMediator.loginEvent();
      }
    }

  }

  // TODO: Figure out why this is here, probably remove it.
  public Canvas execute() {
    onModuleLoad();
    return null;
  }

  public StartupCommand get() {
    return new StartupCommand() {
      public void execute() {
        onModuleLoad();
      }
    };
  }

}
