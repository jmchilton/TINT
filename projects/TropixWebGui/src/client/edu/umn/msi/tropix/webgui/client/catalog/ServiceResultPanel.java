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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.Overflow;
import com.smartgwt.client.types.VerticalAlignment;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Img;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

import edu.umn.msi.tropix.webgui.client.catalog.beans.Category;
import edu.umn.msi.tropix.webgui.client.catalog.beans.CustomQueryBean;
import edu.umn.msi.tropix.webgui.client.catalog.beans.Highlight;
import edu.umn.msi.tropix.webgui.client.catalog.beans.HitInstance;
import edu.umn.msi.tropix.webgui.client.catalog.beans.Provider;
import edu.umn.msi.tropix.webgui.client.catalog.beans.ServiceBean;
import edu.umn.msi.tropix.webgui.client.constants.CatalogConstants;
import edu.umn.msi.tropix.webgui.client.search.SearchController;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.services.tropix.CatalogSearch;
import edu.umn.msi.tropix.webgui.services.tropix.CatalogServiceDefinition;

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
 *         Created for Division of Biomedical Informatics, Mayo Foundation Create Date: Sep 4, 2008
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
public class ServiceResultPanel extends VLayout {
  private final VLayout table;
  private final CatalogServiceController web;
  private List<ServiceBean> serviceBeans = new ArrayList<ServiceBean>();
  private List<Label> serviceLabels;

  private class LinkLabel extends Label {
    private String id;

    LinkLabel(final String text) {
      super("<span class=\"catalog-link\">" + text + "</span>");
      super.setWrap(false);
      super.setAutoFit(true);
    }

    public String getId() {
      return this.id;
    }

    public void setId(final String id) {
      this.id = id;
    }
  }

  private int currentPos;
  //private final Button btnRemoveFromList;
  private final Label lblBorder, description;
  private final List<String> colorList = new ArrayList<String>();
  private SearchController searchController;

  private SearchController getResultHandler() {
    if(this.searchController == null) {
      searchController = SearchController.getInstance();
    }
    return searchController;
  }

  public ServiceResultPanel(final CatalogServiceController web) {
    this.web = web;
    this.colorList.add("#CCFFCC");
    this.colorList.add("#FFFF00");
    this.colorList.add("#339999");
    this.colorList.add("#FF33FF");
    this.colorList.add("#00FFFF");
    this.colorList.add("#9966FF");
    this.colorList.add("#66FFCC");
    this.colorList.add("#FF0099");
    this.colorList.add("#6633FF");
    this.colorList.add("#CCCCCC");

    this.setWidth100();
    this.setHeight100();
    this.setPadding(5);
    this.table = new VLayout();
    this.table.setWidth100();
    this.table.setHeight100();
    this.table.setOverflow(Overflow.AUTO);
    this.table.setMembersMargin(8);
    // table = new FlexTable();
    // table.setWidth("100%");
    // table.setCellSpacing(8);

    this.lblBorder = new Label();
    this.description = new Label();
    this.description.setWidth100();
    this.description.setHeight(30);
    this.description.setValign(VerticalAlignment.CENTER);

    // lblBorder.setStyleName("pnlSearchResultBorder");

    //this.btnRemoveFromList = new Button("Remove From List");
    //this.btnRemoveFromList.setAutoFit(true);
    //this.btnRemoveFromList.addClickHandler(this);
    //this.btnRemoveFromList.setVisibility(Visibility.HIDDEN);
    this.lblBorder.setWidth100();
    this.lblBorder.setHeight(5);
    // addMember(lblBorder);
    this.addMember(this.description);
    //this.addMember(this.btnRemoveFromList);
    this.addMember(this.table);
  }

  public void populateTable(final List<ServiceBean> serviceBeans) {
    this.serviceBeans = serviceBeans;
    this.serviceLabels = new ArrayList<Label>();
    for(final Canvas member : this.table.getMembers()) {
      this.table.removeMember(member);
    }
    if(serviceBeans.size() == 0) {
      this.description.setContents(CatalogConstants.INSTANCE.searchResultsNumberEmpty());
    } else {
      this.description.setContents(serviceBeans.size() + " " + CatalogConstants.INSTANCE.searchResultsNumber());
    }
    int i = 0;
    for(final ServiceBean serviceBean : serviceBeans) {
      final int index = i;
      final List<HitInstance> hitInstances = serviceBean.getSearchHits();
      String titleText = StringUtils.sanitize(serviceBean.getName());
      String descriptionText = StringUtils.sanitize(serviceBean.getDescription());
      final List<String> references = new ArrayList<String>();
      final List<LinkLabel> attributes = new ArrayList<LinkLabel>();
      boolean isProviderMatch = false;
      //boolean isCategoryMatch = false;
      for(final HitInstance hitInstance : hitInstances) {
        if(hitInstance.getSource().equals(HitInstance.NAME)) {
          titleText = this.getFormattedHighlight(hitInstance.getText(), hitInstance.getHighlights(), "14");
        } else if(hitInstance.getSource().equals(HitInstance.DESCRIPTION)) {
          descriptionText = this.getFormattedHighlight(hitInstance.getText(), hitInstance.getHighlights(), "12");
        } else if(hitInstance.getSource().equals(HitInstance.PUBLISHER)) {
          this.getFormattedHighlight(hitInstance.getText(), hitInstance.getHighlights(), "12");
        } else if(hitInstance.getSource().equals(HitInstance.PROVIDER_ID)) {
          isProviderMatch = true;
        } else if(hitInstance.getSource().equals(HitInstance.REFERENCE)) {
          references.add(this.getFormattedHighlight(hitInstance.getText(), hitInstance.getHighlights(), "12"));
        } else if(hitInstance.getSource().equals(HitInstance.ATTRIBUTE)) {
          final String text = StringUtils.sanitize(hitInstance.getText());
          final LinkLabel attrLink = new LinkLabel(text);
          attrLink.setId(text);
          attributes.add(attrLink);
          attrLink.addClickHandler(new ClickHandler() {
            public void onClick(final ClickEvent event) {
              final CustomQueryBean queryBean = new CustomQueryBean();
              final List<String> values = new ArrayList<String>();
              values.add(((LinkLabel) event.getSource()).getId());
              queryBean.setValueIDs(values);
              CatalogSearch.Util.getInstance().advancedSearch(queryBean, ServiceResultPanel.this.getResultHandler().startCatalogSearch("Services with Attribute " + text));
            }
          });          
        }
        // else if(hitInstance.getSource().equals(HitInstance.CATEGORY_ID)) {
        // isCategoryMatch = true;
        // } 

      }

      final Label hLink = new Label("<span class=\"catalog-title\">" + titleText + "</span>");
      hLink.setWidth100();
      serviceLabels.add(hLink);
      //hLink.addClickHandler(this);

      final HLayout tblSearchResultElement = new HLayout();
      tblSearchResultElement.setWidth100();
      final HLayout hPanel = new HLayout();
      final HLayout hPanel1 = new HLayout();
      /*
       * if(StringUtils.hasText(serviceBean.getCategoryID())) { final Category category = CatalogGlobals.getCategories().get(serviceBean.getCategoryID()); if(category != null) { final Img image = new Img(GWT.getHostPageBaseURL() + "images/catalog/cat620.gif");
       * image.setTooltip("View all " + category.getName()); image.setSize("25", "25"); tblSearchResultElement.addMember(image); image.addStyleName("ImageCursor"); image.addClickHandler(new ClickHandler() { public void onClick(final ClickEvent event) { final String url =
       * "images/catalog/cat620.gif"; final String categoryPrefix = url.substring(url.lastIndexOf('/') + 1, url.lastIndexOf('.')); final String categoryID = ServiceResultPanel.this.getCategoryIDFromPrefix(categoryPrefix); GWT.log("cat prefix is " + categoryPrefix +
       * "catetgory id is " + categoryID, null); final CustomQueryBean queryBean = new CustomQueryBean(); queryBean.setCategoryID(categoryID); Search.Util.getInstance().advancedSearch(queryBean, ServiceResultPanel.this.getResultHandler().startCatalogSearch("Services of Type " +
       * category.getName())); } }); } }
       */

      hPanel.addMember(hLink);
      if(serviceBean.getStatus().equals("INACTIVE")) {
        // hPanel.addMember(new HTML("&nbsp;&nbsp;&nbsp;"));
        hPanel.addMember(this.getSpacer());
        final Img inactiveImg = new Img("icons/close.gif");
        inactiveImg.setTooltip("Inactive entry");
        // inactiveImg.addMouseListener(
        // new TooltipListener("Inactive entry", 10000 ,"gwt-DialogBox"));
        hPanel.addMember(inactiveImg);
        hPanel.addMember(this.getSpacer());
        // hPanel.addMember(new HTML("&nbsp;&nbsp;&nbsp;"));
      }

      Label lblSummary;
      if(descriptionText.trim().equals("")) {
        lblSummary = new Label(serviceBean.getDescription());
      } else {
        lblSummary = new Label(descriptionText);
      }
      lblSummary.setWidth100();
      hPanel1.addMember(this.getSpacer());
      hPanel1.addMember(lblSummary);
      lblSummary.setWidth100();

      final Label lblProvider = new Label("<b>Provider:</b>");
      lblProvider.setAutoFit(true);
      String provider = StringUtils.sanitize(serviceBean.getProvider());
      if(isProviderMatch) {
        provider = "<span style=\"background-color: " + colorList.get(0) + "\">" + provider + "</span>";
      }

      final LinkLabel providerLink = new LinkLabel(provider);
      //providerLink.setAssocBean(serviceBean);
      providerLink.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          //final ServiceBean serviceBean; //(ServiceBean) ((LinkLabel) event.getSource()).getAssocBean();
          GWT.log("getting provider for provider ID " + serviceBean.getProviderID(), null);
          final AsyncCallback<Provider> callback = new AsyncCallback<Provider>() {
            public void onFailure(final Throwable caught) {
              GWT.log("error in Providers " + caught.getMessage(), caught);
            }

            public void onSuccess(final Provider provider) {
              web.showProviderDetails(provider);
            }
          };
          CatalogServiceDefinition.Util.getInstance().getProvider(serviceBean.getProviderID(), serviceBean.getCatalogId(), callback);
        }
      });
      String viewProviderText = "View more entries from provider";
      final LinkLabel providerServiceLink = new LinkLabel(viewProviderText);
      providerServiceLink.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          CatalogUtils.getEntriesFromProvider(serviceBean.getProviderID(), serviceBean.getCatalogId(), getResultHandler().startCatalogSearch("Services from Provider " + serviceBean.getProvider()));
        }
      });

      final LinkLabel moreInfoLink  = new LinkLabel("View more service information");
      moreInfoLink.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          displayServiceDetails(serviceBean, index);
        }        
      });

      final LinkLabel requestLink  = new LinkLabel("Request service");
      requestLink.addClickHandler(new ClickHandler() {
        public void onClick(final ClickEvent event) {
          web.request(serviceBean);
        }
      });
      
      final HLayout optionsLayout = new HLayout();
      optionsLayout.setWidth100();
      optionsLayout.addMember(getSpacer());
      optionsLayout.addMember(getSpacer());
      optionsLayout.addMember(requestLink);
      optionsLayout.addMember(getPipeLabel());
      optionsLayout.addMember(moreInfoLink);
      final Canvas optionsFiller = new Canvas();
      optionsFiller.setWidth100();
      optionsLayout.addMember(optionsFiller);
      
      /*
      final Label lblRefs = new Label("References: ");
      final VLayout pnlRefs = new VLayout();
      pnlRefs.addMember(this.getSpacer());
      pnlRefs.addMember(this.getSpacer());
      pnlRefs.addMember(lblRefs);
      for(final String text : references) {
        pnlRefs.addMember(new Label(text)); // Already sanitized
      }
      final Label lblTags = new Label("Tags: ");
      final HLayout pnlTags = new HLayout();
      pnlTags.addMember(this.getSpacer());
      pnlTags.addMember(this.getSpacer());
      pnlTags.addMember(lblTags);
      for(final LinkLabel attr : attributes) {
        pnlTags.addMember(attr); // Alread sanitized
        pnlTags.addMember(this.getPipeLabel());
      }
      */
      final HLayout pnlProvider = new HLayout();
      pnlProvider.setWidth100();
      pnlProvider.addMember(this.getSpacer());
      pnlProvider.addMember(this.getSpacer());
      pnlProvider.addMember(lblProvider);
      pnlProvider.addMember(this.getSpacer());
      pnlProvider.addMember(providerLink); // Sanitized
      pnlProvider.addMember(getPipeLabel());
      pnlProvider.addMember(providerServiceLink); // Hard coded, no need to sanitize
      final Canvas width = new Canvas();
      width.setWidth100();
      pnlProvider.addMember(width);
      
      final VLayout resultContents = new VLayout();
      resultContents.setWidth100();
      resultContents.addMember(hPanel);
      resultContents.addMember(hPanel1);
      resultContents.addMember(optionsLayout);
      resultContents.addMember(pnlProvider);
      tblSearchResultElement.addMember(resultContents);
      table.addMember(tblSearchResultElement);

      if(i % 2 == 0) {
        tblSearchResultElement.setBackgroundColor("F2F2F2");
      }
      i++;
    }
    final Canvas canvas = new Canvas();
    canvas.setHeight100();
    table.addMember(canvas);

  }

  private Label getSpacer() {
    final Label label = new Label("&nbsp;&nbsp;&nbsp;");
    label.setAutoFit(true);
    return label;
  }

  private Label getPipeLabel() {
    final Label label = new Label("|");
    label.setAutoHeight();
    label.setWidth(24);
    label.setAlign(Alignment.CENTER);
    return label;
  }

  protected String getCategoryIDFromPrefix(final String categoryPrefix) {
    for(final String key : CatalogGlobals.getCategories().keySet()) {
      final Category value = CatalogGlobals.getCategories().get(key);
      if(value.getName().toLowerCase().startsWith(categoryPrefix)) {
        GWT.log("val name is " + value.getName(), null);
        return key;
      }
    }
    return null;
  }

  private String getFormattedHighlight(final String text, final List<Highlight> highlights, final String fontSize) {
    final StringBuilder html = new StringBuilder();
    int currentToken = 0;
    for(final Highlight highlight : highlights) {
      if(highlight.getBegin() > 1) {
        html.append(StringUtils.sanitize(text.substring(currentToken, highlight.getBegin() - 1)));
      }

      if(highlight.getId() < 10) {
        html.append("&nbsp;<span style='font-size:" + fontSize + "px; background-color:" + this.colorList.get(highlight.getId()) + "'>" + StringUtils.sanitize(text.substring(highlight.getBegin(), highlight.getEnd())) + "</span>&nbsp;");
      } else {
        html.append("&nbsp;<span style='font-size:" + fontSize + "px; font-weight:bolder'>" + StringUtils.sanitize(text.substring(highlight.getBegin(), highlight.getEnd())) + "</span>&nbsp;");
      }

      currentToken = highlight.getEnd();
    }
    html.append(text.substring(currentToken));
    return html.toString();
  }

  protected void displayServiceDetails(final ServiceBean serviceBean, final int posInList) {
    GWT.log("SearchResultPanel.displayServiceDetails --- " + "pos in list " + posInList, null);
    currentPos = posInList;
    web.showServiceDetails(serviceBean, serviceBeans, posInList);
  }

  public void populateTable() {
    if(this.serviceBeans != null) {
      this.populateTable(this.serviceBeans);
    }
  }

  public void displayPrevious() {
    if(this.currentPos > 0) {
      this.currentPos--;
      this.displayServiceDetails(this.serviceBeans.get(this.currentPos), this.currentPos);
    }
  }

  public void displayNext() {
    if(this.currentPos < (this.serviceBeans.size() - 1)) {
      this.currentPos++;
      this.displayServiceDetails(this.serviceBeans.get(this.currentPos), this.currentPos);
    }
  }

  public void displayCurrentService() {
    this.displayServiceDetails((this.serviceBeans.get(this.currentPos)), this.currentPos);
  }

  public List<ServiceBean> getServiceBeans() {
    return this.serviceBeans;
  }

  public void setServiceBeans(final List<ServiceBean> serviceBeans) {
    this.serviceBeans = serviceBeans;
  }

  public void showUpdatedService(final ServiceBean serviceBean, final boolean doUpdateServiceInResultList) {
    if(doUpdateServiceInResultList) {
      this.serviceBeans.set(this.currentPos, serviceBean);
      GWT.log("SearchResultPanel.showUpdatedService --- " + "current pos " + this.currentPos, null);
      this.displayServiceDetails(serviceBean, this.currentPos);
    } else {
      this.displayServiceDetails(serviceBean, -1);
    }
  }

  public void displayService(final int position) {
    this.currentPos = position;
    this.displayServiceDetails(this.serviceBeans.get(position), position);
  }
}
