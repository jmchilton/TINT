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

import com.google.gwt.user.client.Command;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.toolbar.ToolStrip;

import edu.umn.msi.tropix.webgui.client.Resources;
import edu.umn.msi.tropix.webgui.client.catalog.beans.ServiceBean;
import edu.umn.msi.tropix.webgui.client.modules.ModuleManager;
import edu.umn.msi.tropix.webgui.client.widgets.SmartUtils;
import edu.umn.msi.tropix.webgui.services.session.Module;

/**
 * 
 * Copyright: (c) 2004-2007 Mayo Foundation for Medical Education and Research (MFMER). All rights reserved. MAYO, MAYO CLINIC, and the triple-shield Mayo logo are trademarks and service marks of MFMER.
 * 
 * Except as contained in the copyright notice above, or as used to identify MFMER as the author of this software, the trade names, trademarks, service marks, or product names of the copyright holder shall not be used in advertising, promotion or otherwise in connection with this
 * software without prior written authorization of the copyright holder.
 * 
 * Licensed under the Eclipse Public License, Version 1.0 (the "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * 
 * 
 * @author Asif Hossain <br>
 * 
 * <br>
 *         Created for Division of Biomedical Informatics, Mayo Foundation Create Date: Sep 5, 2008
 * 
 * @version 1.0
 * 
 *          <p>
 *          Change Log
 * 
 *          <PRE>
 * 
 * -----------------------------------------------------------------------------
 * 
 * </PRE>
 */
public class ServiceDetailsPanel extends VLayout {
  private final com.smartgwt.client.widgets.Label lblCount;
  private final Button btnEdit, btnNext, btnPrevious, btnBackToSearchResults, requestButton;
  private final CatalogServiceController web;
  private final VLayout spnlMain;
  private ServiceBean currentService;
  private boolean doUpdateServiceInResultList;

  public ServiceDetailsPanel(final CatalogServiceController web, final ModuleManager moduleManager) {
    this.web = web;

    this.spnlMain = new VLayout();
    this.spnlMain.setHeight("*");
    this.spnlMain.setWidth100();

    this.setWidth100();
    this.setHeight100();

    this.requestButton = SmartUtils.getButton("Request", Resources.REQUEST_16, new Command() {
      public void execute() {
        web.request(currentService);
      }      
    });
    /*
    this.requestButton.setTitle("Request");
    this.requestButton.setAutoFit(true);
    this.requestButton.setIcon(Resources.REQUEST_16);
    this.requestButton.setShowDisabledIcon(false);
    this.requestButton.addClickHandler(new ClickHandler() {
      public void onClick(final ClickEvent event) {
        
      }
    });
    */
    this.btnEdit = SmartUtils.getButton("Edit", Resources.EDIT, new Command() {
      public void execute() {
        edit(doUpdateServiceInResultList);
      }      
    });
    /*
    this.btnEdit = new Button();
    this.btnEdit.setTitle("Edit");
    this.btnEdit.setAutoFit(true);
    this.btnEdit.setIcon(Resources.EDIT);
    this.btnEdit.setShowDisabledIcon(false);
    this.btnEdit.addClickHandler(new ClickHandler() {
      public void onClick(final ClickEvent be) {
        edit(doUpdateServiceInResultList);
      }
    });
    this.btnEdit.setAutoFit(true);
    */
    this.btnNext = SmartUtils.getButton("Next", Resources.CONTROL_NEXT, new Command() {
      public void execute() {
        next();
      }      
    });
    /*
    this.btnNext = new Button();
    this.btnNext.setTitle("Next");
    this.btnNext.setIcon(Resources.CONTROL_NEXT);
    this.btnNext.setShowDisabledIcon(false);
    this.btnNext.addClickHandler(new ClickHandler() {
      public void onClick(final ClickEvent be) {
        next();
      }
    });
    this.btnNext.setAutoFit(true);
    */
    this.btnPrevious = SmartUtils.getButton("Previous", Resources.CONTROL_PREVIOUS, new Command() {
      public void execute() {
        previous();
      }      
    });
    /*
    this.btnPrevious = new Button();
    this.btnPrevious.setIcon(Resources.CONTROL_PREVIOUS);
    this.btnPrevious.setTitle("Previous");
    this.btnPrevious.addClickHandler(new ClickHandler() {
      public void onClick(final ClickEvent be) {
        previous();
      }
    });
    this.btnPrevious.setAutoFit(true);
    this.btnPrevious.setShowDisabledIcon(false);
    */

    this.btnBackToSearchResults = SmartUtils.getButton("Back", Resources.CONTROL_START, new Command() {
      public void execute() {
        backToSearchResult();
      }      
    });
    /*
    this.btnBackToSearchResults = new Button();
    this.btnBackToSearchResults.setTitle("Back");
    this.btnBackToSearchResults.setIcon(Resources.CONTROL_START);
    this.btnBackToSearchResults.setShowDisabledIcon(false);
    this.btnBackToSearchResults.setTooltip("Go back to search result");
    this.btnBackToSearchResults.addClickHandler(new ClickHandler() {
      public void onClick(final ClickEvent be) {
        backToSearchResult();
      }
    });
    this.btnBackToSearchResults.setAutoFit(true);
    */
    this.btnPrevious.disable();
    this.btnNext.disable();

    this.lblCount = new com.smartgwt.client.widgets.Label();
    this.lblCount.setAutoFit(true);
    this.lblCount.setWidth100();
    ToolStrip toolbar;
    toolbar = new ToolStrip();
    toolbar.setWidth100();
    if(moduleManager.containsModules(Module.REQUEST)) {
      toolbar.addMember(this.requestButton);
    }
    toolbar.addMember(this.btnBackToSearchResults);
    toolbar.addMember(this.btnPrevious);
    toolbar.addMember(this.btnNext);
    final com.smartgwt.client.widgets.Label filler = new com.smartgwt.client.widgets.Label();
    filler.setWidth("10px");
    toolbar.addMember(filler);
    toolbar.addMember(this.lblCount);

    this.addMember(toolbar);
    this.addMember(this.spnlMain);
  }

  protected void backToSearchResult() {
    this.web.showSearchResults(true);
  }

  protected void previous() {
    this.web.showPrevService();
  }

  protected void next() {
    this.web.showNextService();
  }

  protected void edit(final boolean inResultList) {
    this.web.edit(this.currentService, inResultList);
  }

  public void populateServiceDetails(final ServiceBean serviceBean, final int resultCount, final int posInList) {
    this.doUpdateServiceInResultList = true;
    this.currentService = serviceBean;
    this.lblCount.setContents(" " + (posInList + 1) + " of " + resultCount);
    if(posInList > 0) {
      this.btnPrevious.enable();
    } else {
      this.btnPrevious.disable();
    }
    if((posInList + 1) >= resultCount) {
      this.btnNext.disable();
    } else {
      this.btnNext.enable();
    }
    final Canvas showServiceCanvas = this.web.getShowCatalogServiceCanvas(serviceBean);
    showServiceCanvas.setHeight100();
    showServiceCanvas.setWidth100();
    SmartUtils.removeAndDestroyAllMembers(this.spnlMain);
    this.spnlMain.addMember(showServiceCanvas);
  }

}