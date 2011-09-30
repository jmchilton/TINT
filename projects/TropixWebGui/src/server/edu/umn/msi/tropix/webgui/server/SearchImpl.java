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

package edu.umn.msi.tropix.webgui.server;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

import edu.mayo.bmi.bic.bobcat.api.BasicEntry;
import edu.mayo.bmi.bic.bobcat.api.CatalogOntAPI;
import edu.mayo.bmi.bic.bobcat.api.CoreRestriction;
import edu.mayo.bmi.bic.bobcat.api.CoreType;
import edu.mayo.bmi.bic.bobcat.api.FreeTextRestriction;
import edu.mayo.bmi.bic.bobcat.api.Highlight;
import edu.mayo.bmi.bic.bobcat.api.HitInstance;
import edu.mayo.bmi.bic.bobcat.api.Intersect;
import edu.mayo.bmi.bic.bobcat.api.Provider;
import edu.mayo.bmi.bic.bobcat.api.Query;
import edu.mayo.bmi.bic.bobcat.api.SearchHit;
import edu.mayo.bmi.bic.bobcat.api.Union;
import edu.umn.msi.tropix.client.catalog.CatalogClient;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.webgui.client.catalog.beans.CustomQueryBean;
import edu.umn.msi.tropix.webgui.client.catalog.beans.ServiceBean;
import edu.umn.msi.tropix.webgui.server.aop.ServiceMethod;
import edu.umn.msi.tropix.webgui.services.tropix.CatalogSearch;

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
 * <PRE>
 * 
 * </PRE>
 */
@ManagedBean
public class SearchImpl implements CatalogSearch {
  private static final Log LOG = LogFactory.getLog(SearchImpl.class);

  private final CatalogClient catalogClient;

  @Inject
  SearchImpl(final CatalogClient catalogClient) {
    this.catalogClient = catalogClient;
  }

  public static List<ServiceBean> convertBasicServiceBeanToServiceBean(final CatalogOntAPI catalogOntAPI, final List<BasicEntry> basicServiceBeans, final boolean removeInactive) throws Exception {
    final List<ServiceBean> serviceBeans = new ArrayList<ServiceBean>();
    for(final BasicEntry basicServiceBean : basicServiceBeans) {
      final ServiceBean serviceBean = new ServiceBean();
      BeanUtils.copyProperties(serviceBean, basicServiceBean);
      Provider[] providers = null;
      try {
        providers = catalogOntAPI.getProviders(new String[] {serviceBean.getProviderID()});
      } catch(final Exception e) {
        e.printStackTrace();
      }
      if(providers != null && providers.length > 0) {
        serviceBean.setProvider(providers[0].getName());
      }

      if(removeInactive) {
        if(serviceBean.getStatus().equals("ACTIVE")) {
          serviceBeans.add(serviceBean);
        }
      } else {
        serviceBeans.add(serviceBean);
      }

    }
    return serviceBeans;
  }

  public static List<edu.umn.msi.tropix.webgui.client.catalog.beans.HitInstance> getUIHitInsts(final HitInstance[] instances) {
    final List<edu.umn.msi.tropix.webgui.client.catalog.beans.HitInstance> hits = new ArrayList<edu.umn.msi.tropix.webgui.client.catalog.beans.HitInstance>();
    for(final HitInstance currentHit : instances) {
      final Highlight[] highlights = currentHit.getHighlights();
      List<edu.umn.msi.tropix.webgui.client.catalog.beans.Highlight> uiHighlights = Lists.newArrayList();
      if(highlights != null) {
        for(int j = 0; j < highlights.length; j++) {
          uiHighlights.add(new edu.umn.msi.tropix.webgui.client.catalog.beans.Highlight(highlights[j].getBegin(), highlights[j].getEnd(), highlights[j].getId()));
        }
      }
      hits.add(new edu.umn.msi.tropix.webgui.client.catalog.beans.HitInstance(currentHit.getScore(), currentHit.getText(), uiHighlights, currentHit.getSource().getValue(), currentHit.getAttributeID()));
    }
    return hits;
  }

  private List<ServiceBean> convertResults(final Multimap<String, SearchHit> shBeans) {
    final List<ServiceBean> serviceBeans = new LinkedList<ServiceBean>();
    for(final String catalogId : shBeans.keySet()) {
      final Collection<SearchHit> hits = shBeans.get(catalogId);
      BasicEntry[] bsBeans = null;
      try {
        bsBeans = this.catalogClient.getEntryAPI(catalogId).getBasicDetails(CatalogUtils.getServiceIDsFromSHBeanList(hits), 500);
      } catch(final RemoteException e) {
        ExceptionUtils.logQuietly(SearchImpl.LOG, e, "Failed to obtain basic details");
        continue;
      }
      if(bsBeans == null) {
        bsBeans = new BasicEntry[0];
      }
      try {
        serviceBeans.addAll(convertBasicServiceBeanToServiceBean(this.catalogClient.getOntologAPI(catalogId), Arrays.asList(bsBeans), true));
      } catch(final Exception e) {
        ExceptionUtils.logQuietly(SearchImpl.LOG, e, "Failed to convert catalog results");
        continue;
      }
      for(final SearchHit searchBean : hits) {
        for(final ServiceBean serviceBean : serviceBeans) {
          if(searchBean.getEntryID().equals(serviceBean.getId())) {
            serviceBean.setCatalogId(catalogId);
            if(searchBean.getInstances() != null && searchBean.getInstances().length > 0) {
              serviceBean.setSearchHits(getUIHitInsts(searchBean.getInstances()));
            }
            serviceBean.setLuceneScore(searchBean.getWeightedScore());
          }
        }
      }
    }
    return serviceBeans;

  }

  @ServiceMethod
  public List<ServiceBean> searchServices(final String searchTerm) {
    return this.convertResults(this.catalogClient.entrySearch(searchTerm));
  }

  private Query getFreeTextQuery(final String freeText) {
    final List<Query> queryList = Lists.newArrayListWithExpectedSize(4);
    Query query = new Query();
    query.setCoreRestriction(new CoreRestriction(CoreType.NAME, freeText));
    queryList.add(query);

    query = new Query();
    query.setCoreRestriction(new CoreRestriction(CoreType.DESCRIPTION, freeText));
    queryList.add(query);

    query = new Query();
    query.setCoreRestriction(new CoreRestriction(CoreType.REFERENCE, freeText));
    queryList.add(query);

    query = new Query();
    query.setFreeTextRestriction(new FreeTextRestriction(null, freeText));
    queryList.add(query);

    query = new Query();
    query.setUnion(new Union(queryList.toArray(new Query[4])));
    return query;
  }

  @ServiceMethod
  public List<ServiceBean> advancedSearch(final CustomQueryBean queryBean) {
    final List<ServiceBean> serviceBeans = new ArrayList<ServiceBean>();
    final List<Query> queryList = new ArrayList<Query>();
    if(StringUtils.isNotEmpty(queryBean.getFreeText())) {
      queryList.add(getFreeTextQuery(queryBean.getFreeText()));
    }
    if(StringUtils.isNotEmpty(queryBean.getAuthor())) {
      final Query query = new Query();
      query.setCoreRestriction(new CoreRestriction(CoreType.PUBLISHER, queryBean.getAuthor()));
      queryList.add(query);
    }
    if(StringUtils.isNotEmpty(queryBean.getCategoryID())) {
      final Query query = new Query();
      query.setCoreRestriction(new CoreRestriction(CoreType.CATEGORY_ID, queryBean.getCategoryID()));
      queryList.add(query);
    }
    if(StringUtils.isNotEmpty(queryBean.getProivderID())) {
      final Query query = new Query();
      query.setCoreRestriction(new CoreRestriction(CoreType.PROVIDER_ID, queryBean.getProivderID()));
      queryList.add(query);
    }
    if(StringUtils.isNotEmpty(queryBean.getStatus())) {
      final Query query = new Query();
      query.setCoreRestriction(new CoreRestriction(CoreType.STATUS, queryBean.getStatus()));
      queryList.add(query);
    }

    // TODO: Add back in value restriction
    /*
     * for (Iterator iterator = queryBean.getValueIDs().iterator(); iterator .hasNext();) {
     * 
     * String value = (String) iterator.next(); if (StringUtils.isNotEmpty(value)) { System.out.println("Field value is " + value); AutoCompleteHit[] hits = CatalogUtils .getCatalogSearchAPI().fieldValueAutoCompletion( value);
     * 
     * if (hits.length > 0) { String valueID = hits[0].getValueID(); Query query = new Query(); // String fieldID = CatalogUtils.getCatalogOntAPI() // .getFieldByValueID(valueID); query.setValueRestriction(new ValueRestriction(null, valueID)); queryList.add(query);
     * 
     * } } }
     */
    final Query currentQuery = new Query();
    if(queryBean.isIntersect()) {
      final Intersect intersect = new Intersect();
      intersect.setQueries(queryList.toArray(new Query[queryList.size()]));
      currentQuery.setIntersect(intersect);
    } else {
      final Union union = new Union();
      union.setQueries(queryList.toArray(new Query[queryList.size()]));
      currentQuery.setUnion(union);
    }
    final String catalogId = queryBean.getCatalogId();
    Multimap<String, SearchHit> hits;
    if(catalogId == null) {
      hits = this.catalogClient.customEntrySearch(currentQuery);
    } else {
      SearchHit[] searchHits;
      try {
        searchHits = this.catalogClient.getSearchAPI(catalogId).customEntrySearch(currentQuery);
      } catch(final RemoteException e) {
        throw new RuntimeException(e);
      }
      hits = HashMultimap.create();
      if(searchHits != null) {
        for(final SearchHit hit : searchHits) {
          hits.put(catalogId, hit);
        }
      }
    }
    serviceBeans.addAll(convertResults(hits));
    return serviceBeans;
  }

}
