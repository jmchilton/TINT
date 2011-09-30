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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import edu.mayo.bmi.bic.bobcat.api.BasicEntry;
import edu.mayo.bmi.bic.bobcat.api.CatalogEntryAPI;
import edu.mayo.bmi.bic.bobcat.api.CatalogOntAPI;
import edu.mayo.bmi.bic.bobcat.api.Category;
import edu.mayo.bmi.bic.bobcat.api.CategoryFieldAssociation;
import edu.mayo.bmi.bic.bobcat.api.CoreRestriction;
import edu.mayo.bmi.bic.bobcat.api.CoreType;
import edu.mayo.bmi.bic.bobcat.api.Field;
import edu.mayo.bmi.bic.bobcat.api.FieldType;
import edu.mayo.bmi.bic.bobcat.api.FieldValue;
import edu.mayo.bmi.bic.bobcat.api.FullEntry;
import edu.mayo.bmi.bic.bobcat.api.Intersect;
import edu.mayo.bmi.bic.bobcat.api.OntologyConcept;
import edu.mayo.bmi.bic.bobcat.api.Provider;
import edu.mayo.bmi.bic.bobcat.api.Query;
import edu.mayo.bmi.bic.bobcat.api.Reference;
import edu.mayo.bmi.bic.bobcat.api.SearchHit;
import edu.mayo.bmi.bic.bobcat.api.StatusType;
import edu.umn.msi.tropix.client.catalog.CatalogClient;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.common.reflect.ReflectionHelper;
import edu.umn.msi.tropix.common.reflect.ReflectionHelpers;
import edu.umn.msi.tropix.labs.catalog.CatalogInstance;
import edu.umn.msi.tropix.webgui.client.catalog.beans.Attribute;
import edu.umn.msi.tropix.webgui.client.catalog.beans.ReferenceBean;
import edu.umn.msi.tropix.webgui.client.catalog.beans.ServiceBean;
import edu.umn.msi.tropix.webgui.client.utils.StringUtils;
import edu.umn.msi.tropix.webgui.server.aop.ServiceMethod;
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
@ManagedBean
public class ServiceDefinitionImpl implements CatalogServiceDefinition {
  private static final ReflectionHelper REFLECTION_HELPER = ReflectionHelpers.getInstance();
  private final Log log = LogFactory.getLog(ServiceDefinitionImpl.class);
  private final CatalogInstance localInstance;
  private final CatalogClient catalogClient;

  @Inject
  ServiceDefinitionImpl(final CatalogInstance localInstance, final CatalogClient catalogClient) {
    this.localInstance = localInstance;
    this.catalogClient = catalogClient;
  }

  @ServiceMethod
  public ServiceBean createService(final ServiceBean serviceBean) {
    try {
      final List<Query> queryList = new ArrayList<Query>();
      final Query mainQuery = new Query();
      Query query = new Query();
      query.setCoreRestriction(new CoreRestriction(CoreType.NAME, serviceBean.getName()));
      queryList.add(query);
      query = new Query();
      query.setCoreRestriction(new CoreRestriction(CoreType.PROVIDER_ID, serviceBean.getProviderID()));
      queryList.add(query);
      final Intersect intersect = new Intersect();
      intersect.setQueries(queryList.toArray(new Query[queryList.size()]));
      mainQuery.setIntersect(intersect);
      final SearchHit[] shBeans = this.localInstance.getCatalogSearchAPI().customEntrySearch(mainQuery);
      if(shBeans.length > 0) {
        final String[] ids = CatalogUtils.getServiceIDsFromSHBeanList(Arrays.asList(shBeans));
        final BasicEntry[] bsBeans = this.localInstance.getCatalogEntryAPI().getBasicDetails(ids, 100);
        for(final BasicEntry bsBean : bsBeans) {
          if(bsBean.getName().equals(serviceBean.getName())) {
            throw new RuntimeException("A service with the same name and provider already exists, please use a different name.");
          }
        }
      }

      final List<ServiceBean> serviceBeans = new ArrayList<ServiceBean>();
      serviceBeans.add(serviceBean);
      String newServiceID = "";
      if(serviceBean.getStatus().equals("ACTIVE")) {
        newServiceID = this.localInstance.getCatalogEntryAPI().addEntry(serviceBean.getName(), serviceBean.getDescription(), serviceBean.getCategory(), serviceBean.getProviderID(), StatusType.ACTIVE, serviceBean.getPublisher());
      } else {
        newServiceID = this.localInstance.getCatalogEntryAPI().addEntry(serviceBean.getName(), serviceBean.getDescription(), serviceBean.getCategory(), serviceBean.getProviderID(), StatusType.INACTIVE, serviceBean.getPublisher());
      }

      if(serviceBean.getReferences() != null) {
        for(final ReferenceBean refBean : serviceBean.getReferences()) {
          if(!refBean.isRemoveFromService()) {
            this.localInstance.getCatalogEntryAPI().addReference(newServiceID, refBean.getValue());
          }
        }
      }
      if(serviceBean.getFieldList() != null) {
        for(final edu.umn.msi.tropix.webgui.client.catalog.beans.Field field : serviceBean.getFieldList()) {
          if(field.getType().equals(FieldType.BOOLEAN.getValue())) {
            this.localInstance.getCatalogEntryAPI().addSimpleAttribute(newServiceID, field.getId());
          } else if(field.getType().equals(FieldType.ENUMERATED.getValue())) {
            this.localInstance.getCatalogEntryAPI().addValueAttribute(newServiceID, field.getId(), field.getSelectedValues().toArray(new String[field.getSelectedValues().size()]));
          } else if(field.getType().equals(FieldType.UNSTRUCTURED.getValue())) {
            this.localInstance.getCatalogEntryAPI().addFreeTextAttribute(newServiceID, field.getId(), field.getCurrentValue());
          }
        }
      }
      return serviceBean;
    } catch(final RemoteException e) {
      throw ExceptionUtils.convertException(e);
    }
  }

  @ServiceMethod
  public ServiceBean getServiceDetails(final String id, final String catalogId) {
    final CatalogOntAPI catalogOntAPI = this.catalogClient.getOntologAPI(catalogId);
    final CatalogEntryAPI catalogEntryAPI = this.catalogClient.getEntryAPI(catalogId);

    final Function<FullEntry, ServiceBean> toServiceFunction = new Function<FullEntry, ServiceBean>() {
      public ServiceBean apply(final FullEntry fullServiceBean) {
        try {
          final ServiceBean serviceBean = new ServiceBean();
          serviceBean.setCatalogId(catalogId);
          REFLECTION_HELPER.copyProperties(serviceBean, fullServiceBean);
          serviceBean.setDateServiceCreated(fullServiceBean.getCreated().getTime());
          serviceBean.setDateLastModified(fullServiceBean.getLastModified().getTime());
          final Provider[] providers = catalogOntAPI.getProviders(new String[] {serviceBean.getProviderID()});
          if(providers != null && providers.length > 0) {
            serviceBean.setProvider(providers[0].getName());
          }
          final Category[] categories = catalogOntAPI.getCategories(new String[] {serviceBean.getCategoryID()});
          if(categories != null && categories.length > 0) {
            serviceBean.setCategory(categories[0].getName());
          }
          serviceBean.setPopulated(true);
          final Reference[] refBeans = catalogEntryAPI.getReferences(fullServiceBean.getId());
          if(refBeans != null) {
            for(final Reference ref : refBeans) {
              final ReferenceBean referenceBean = new ReferenceBean(ref.getId() + "", ref.getReference(), false);
              serviceBean.getReferences().add(referenceBean);
            }
          }
          final List<Attribute> attributes = CatalogUtils.getAttributes(catalogOntAPI, catalogEntryAPI, id);
          serviceBean.setAttributes(attributes);
          return serviceBean;
        } catch(final RemoteException e) {
          throw ExceptionUtils.convertException(e, "Failed to convert FullEntry to ServiceBean");
        }
      }
    };
    FullEntry[] fsBeans;
    try {
      fsBeans = catalogEntryAPI.getFullDetails(new String[] {id});
    } catch(final RemoteException e) {
      throw new RuntimeException("Failed to getFullDetails from catalog entry api for id " + id + " and catalogId " + catalogId, e);
    }
    if(fsBeans != null) {
      return toServiceFunction.apply(fsBeans[0]);
    } else {
      throw new RuntimeException("No service was returned for service id " + id);
    }
  }

  @ServiceMethod
  public ServiceBean updateService(final ServiceBean serviceBean) {
    try {
      final List<Query> queryList = new ArrayList<Query>();
      final Query mainQuery = new Query();

      Query query = new Query();
      query.setCoreRestriction(new CoreRestriction(CoreType.NAME, serviceBean.getName()));
      queryList.add(query);

      query = new Query();
      query.setCoreRestriction(new CoreRestriction(CoreType.PROVIDER_ID, serviceBean.getProviderID()));
      queryList.add(query);

      final Intersect intersect = new Intersect();
      intersect.setQueries(queryList.toArray(new Query[queryList.size()]));
      mainQuery.setIntersect(intersect);
      final SearchHit[] shBeans = this.localInstance.getCatalogSearchAPI().customEntrySearch(mainQuery);
      if(shBeans.length > 0) {
        final String[] ids = CatalogUtils.getServiceIDsFromSHBeanList(Arrays.asList(shBeans));
        final BasicEntry[] bsBeans = this.localInstance.getCatalogEntryAPI().getBasicDetails(ids, 100);
        for(int i = 0; i < bsBeans.length; i++) {
          if(bsBeans[i].getName().equals(serviceBean.getName()) && !bsBeans[i].getId().equals(serviceBean.getId())) {
            throw new RuntimeException("A service with the same name and provider already exists, please use a different name.");
          }
        }
      }
      final String id = serviceBean.getId();
      final String name = serviceBean.getName();
      final String desc = serviceBean.getDescription();
      final String publisher = serviceBean.getPublisher();
      final StatusType active = "ACTIVE".equals(serviceBean.getStatus()) ? StatusType.ACTIVE : StatusType.INACTIVE;
      this.localInstance.getCatalogEntryAPI().updateEntry(id, name, desc, active, publisher);

      if(serviceBean.getReferences() != null) {
        for(final ReferenceBean refBean : serviceBean.getReferences()) {
          if(refBean.isHasChanged() && !refBean.isRemoveFromService()) {
            if(StringUtils.hasText(refBean.getId())) {
              this.log.debug("updating reference for service id: " + id + " new value " + refBean.getValue());
              this.localInstance.getCatalogEntryAPI().removeReference(refBean.getId());
              this.localInstance.getCatalogEntryAPI().addReference(serviceBean.getId(), refBean.getValue());
            } else { // if no id specified, add reference
              this.log.debug("adding new reference: for service id: " + serviceBean.getId() + " for reference " + refBean.getValue());
              this.localInstance.getCatalogEntryAPI().addReference(serviceBean.getId(), refBean.getValue());
            }
          }
          if(StringUtils.hasText(refBean.getId()) && refBean.isRemoveFromService()) {
            this.log.debug("removing reference: for service id: " + serviceBean.getId() + " for reference " + refBean.getValue());
            this.localInstance.getCatalogEntryAPI().removeReference(refBean.getId());
          }
        }
      }

      final ServiceBean oldBean = getServiceDetails(id, serviceBean.getCatalogId());
      final Set<String> attributesToRemove = Sets.newTreeSet();
      if(oldBean.getAttributes() != null) {
        for(final Attribute attribute : oldBean.getAttributes()) {
          attributesToRemove.add(attribute.getId());
        }
      }
      if(serviceBean.getFieldList() != null) {
        for(final edu.umn.msi.tropix.webgui.client.catalog.beans.Field field : serviceBean.getFieldList()) {
          if(field.getAttribute() != null) {
            attributesToRemove.remove(field.getAttribute().getId());
          }
          if(field.getType().equals(FieldType.BOOLEAN.getValue())) {
            if(field.getAttribute() == null) {
              this.localInstance.getCatalogEntryAPI().addSimpleAttribute(id, field.getId());
            }
          } else if(field.getType().equals(FieldType.ENUMERATED.getValue())) {
            final String[] values = field.getSelectedValues().toArray(new String[field.getSelectedValues().size()]);
            log.debug("Selected Values: " + Arrays.toString(values));
            if(field.getAttribute() != null) {
              this.localInstance.getCatalogEntryAPI().updateValueAttribute(field.getAttribute().getId(), values);
            } else {
              this.localInstance.getCatalogEntryAPI().addValueAttribute(id, field.getId(), values);
            }
          } else if(field.getType().equals(FieldType.UNSTRUCTURED.getValue())) {
            if(field.getAttribute() != null) {
              this.localInstance.getCatalogEntryAPI().updateFreeTextAttribute(field.getAttribute().getId(), field.getCurrentValue());
            } else {
              this.localInstance.getCatalogEntryAPI().addFreeTextAttribute(id, field.getId(), field.getCurrentValue());
            }
          }
        }
      }
      for(final String attributeToRemove : attributesToRemove) {
        this.localInstance.getCatalogEntryAPI().removeAttribute(attributeToRemove);
      }
      return getServiceDetails(serviceBean.getId(), serviceBean.getCatalogId());
    } catch(final RemoteException e) {
      throw ExceptionUtils.convertException(e);
    }
  }

  @ServiceMethod(secure = false)
  public HashMap<String, edu.umn.msi.tropix.webgui.client.catalog.beans.Category> getCategories() {
    final HashMap<String, edu.umn.msi.tropix.webgui.client.catalog.beans.Category> categoryList = Maps.newHashMap();
    Category[] categories;
    try {
      categories = this.localInstance.getCatalogOntAPI().getCategories(null);
    } catch(final RemoteException e) {
      throw ExceptionUtils.convertException(e, "Failed to obtain categories.");
    }

    for(final Category category : categories) {
      final CategoryFieldAssociation[] fieldAssoc = category.getFieldAssociations();
      edu.umn.msi.tropix.webgui.client.catalog.beans.CategoryFieldAssociation[] uiFieldAssoc = null;
      if(fieldAssoc != null) {
        uiFieldAssoc = new edu.umn.msi.tropix.webgui.client.catalog.beans.CategoryFieldAssociation[fieldAssoc.length];
        for(int i = 0; i < fieldAssoc.length; i++) {
          uiFieldAssoc[i] = new edu.umn.msi.tropix.webgui.client.catalog.beans.CategoryFieldAssociation(fieldAssoc[i].getFieldID(), fieldAssoc[i].isMultiSelectable());
        }
      }

      final edu.umn.msi.tropix.webgui.client.catalog.beans.Category uiCategory = new edu.umn.msi.tropix.webgui.client.catalog.beans.Category(category.getId(), category.getRevision(), new SimpleDateFormat("MM/dd/yyyy").format(category.getCreated().getTime()),
          new SimpleDateFormat("MM/dd/yyyy").format(category.getLastModified().getTime()), category.getName(), category.getDescription(), uiFieldAssoc);

      categoryList.put(category.getId(), uiCategory);
    }

    return categoryList;
  }

  @ServiceMethod(secure = false)
  public HashMap<String, edu.umn.msi.tropix.webgui.client.catalog.beans.Field> getFields(final List<String> fieldIDs) {
    final HashMap<String, edu.umn.msi.tropix.webgui.client.catalog.beans.Field> fieldList = Maps.newHashMap();
    Field[] fields;
    try {
      if(fieldIDs == null) {
        fields = this.localInstance.getCatalogOntAPI().getFields(null);
      } else if(fieldIDs.size() > 0) {
        fields = this.localInstance.getCatalogOntAPI().getFields(Iterables.toArray(fieldIDs, String.class));
      } else {
        fields = new Field[0];
      }

      for(final Field field : fields) {
        final FieldValue[] fieldValues = this.localInstance.getCatalogOntAPI().getAssociatedValues(field.getId());
        final ArrayList<edu.umn.msi.tropix.webgui.client.catalog.beans.FieldValue> uiFieldValues = Lists.newArrayListWithCapacity(fieldValues.length);
        for(final FieldValue fieldValue : fieldValues) {

          uiFieldValues.add(new edu.umn.msi.tropix.webgui.client.catalog.beans.FieldValue(fieldValue.getId(), fieldValue.getRevision(), "", "", null, fieldValue.getValue(), null, null));
        }

        final OntologyConcept[] concepts = field.getConcepts();
        edu.umn.msi.tropix.webgui.client.catalog.beans.OntologyConcept[] uiConcepts = null;
        if(concepts != null) {
          uiConcepts = new edu.umn.msi.tropix.webgui.client.catalog.beans.OntologyConcept[concepts.length];
          for(int i = 0; i < concepts.length; i++) {
            uiConcepts[i] = new edu.umn.msi.tropix.webgui.client.catalog.beans.OntologyConcept(concepts[i].getOntology(), concepts[i].getCode(), concepts[i].getBioportalURL().toString());
          }
        }

        final edu.umn.msi.tropix.webgui.client.catalog.beans.Field uiField = new edu.umn.msi.tropix.webgui.client.catalog.beans.Field(field.getId(), field.getRevision(), new SimpleDateFormat("MM/dd/yyyy").format(field.getCreated().getTime()), new SimpleDateFormat("MM/dd/yyyy")
            .format(field.getLastModified().getTime()), field.getName(), field.getType().getValue(), uiFieldValues);

        fieldList.put(field.getId(), uiField);
      }
    } catch(final RemoteException e) {
      throw ExceptionUtils.convertException(e);
    }
    return fieldList;
  }

  @ServiceMethod
  public void addProvider(final edu.umn.msi.tropix.webgui.client.catalog.beans.Provider provider, final edu.umn.msi.tropix.webgui.client.catalog.beans.TemplateAssociation templates) {
    try {
      this.localInstance.getCatalogOntAPI().addProvider(provider.getName(), provider.getContact(), provider.getAddress(), provider.getPhone(), provider.getEmail(), provider.getWebsite());
    } catch(final Exception e) {
      ExceptionUtils.logQuietly(this.log, e, "Failed to add provider");
      throw new RuntimeException("Failed to add provider");
    }
  }

  @ServiceMethod
  public edu.umn.msi.tropix.webgui.client.catalog.beans.Provider updateProvider(final edu.umn.msi.tropix.webgui.client.catalog.beans.Provider provider) {
    try {
      this.localInstance.getCatalogOntAPI().updateProvider(provider.getId(), provider.getName(), provider.getContact(), provider.getAddress(), provider.getPhone(), provider.getEmail(), provider.getWebsite());
      final Provider[] providers = this.localInstance.getCatalogOntAPI().getProviders(new String[] {provider.getId()});
      return providers.length > 0 ? CatalogUtils.getUIProvider(providers[0]) : null;
    } catch(final Exception e) {
      throw ExceptionUtils.convertException(e);
    }
  }

  private HashMap<String, edu.umn.msi.tropix.webgui.client.catalog.beans.Provider> buildProviderMap(final Iterable<Provider> providers) {
    final HashMap<String, edu.umn.msi.tropix.webgui.client.catalog.beans.Provider> providerList = Maps.newHashMap();
    for(final Provider provider : providers) {
      providerList.put(provider.getId(), CatalogUtils.getUIProvider(provider));
    }
    return providerList;
  }

  @ServiceMethod(secure = false)
  public HashMap<String, edu.umn.msi.tropix.webgui.client.catalog.beans.Provider> getLocalProviders() {
    try {
      return this.buildProviderMap(Arrays.asList(this.localInstance.getCatalogOntAPI().getProviders(null)));
    } catch(final RemoteException e) {
      throw ExceptionUtils.convertException(e);
    }
  }

  @ServiceMethod(secure = false)
  public edu.umn.msi.tropix.webgui.client.catalog.beans.Provider getProvider(final String providerID, final String catalogId) {
    try {
      return CatalogUtils.getUIProvider(this.catalogClient.getOntologAPI(catalogId).getProviders(new String[] {providerID})[0]);
    } catch(final RemoteException e) {
      throw ExceptionUtils.convertException(e);
    }
  }

  @ServiceMethod(secure = false)
  public List<edu.umn.msi.tropix.webgui.client.catalog.beans.Provider> getProviders() {
    final LinkedList<edu.umn.msi.tropix.webgui.client.catalog.beans.Provider> uiProviders = Lists.newLinkedList();
    for(final String catalogId : this.catalogClient.getCatalogIds()) {
      try {
        final Provider[] providers = this.catalogClient.getOntologAPI(catalogId).getProviders(null);
        if(providers != null) {
          for(final Provider provider : this.catalogClient.getOntologAPI(catalogId).getProviders(null)) {
            final edu.umn.msi.tropix.webgui.client.catalog.beans.Provider uiProvider = CatalogUtils.getUIProvider(provider);
            uiProvider.setCatalogId(catalogId);
            uiProviders.add(uiProvider);
          }
        }
      } catch(final RemoteException e) {
        ExceptionUtils.logQuietly(this.log, e, "Failed to obtain providers from catalog client with id " + catalogId);
      }
    }
    return uiProviders;
  }

}
