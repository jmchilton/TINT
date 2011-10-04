/********************************************************************************
 * Copyright (c) 2009 Regents of the University of Minnesota
 *
 * This Software was written at the Minnesota Supercomputing Institute
 * http://msi.umn.edu
 *
 * All rights reserved. The following statement of license applies
 * only to this file, and and not to the other files distributed with it
 * or derived therefrom.  This file is made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 *******************************************************************************/

package edu.umn.msi.tropix.jobs.activities.descriptions;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import com.google.common.base.Preconditions;

@Entity
public class IdList implements Serializable, Iterable<String> {
  
  
  @javax.persistence.Id
  @Nonnull
  @GeneratedValue(strategy=GenerationType.AUTO)
  private String id;

  private Integer nextIndex = 0;
  
  @OrderBy("index")
  @OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
  private List<Id> ids = new LinkedList<Id>();

  public IdList() {}
  
  // Recursive copy constructor
  public IdList(final IdList idList) {
    final List<Id> newIds = new ArrayList<Id>(idList.getIds().size());
    for(Id existingId : idList.getIds()) {
      final Id newId = new Id();
      newId.setValue(existingId.getValue());
      newId.setIndex(nextIndex++);
      newIds.add(newId);
    }
    ids = newIds;
  }
  
  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }
  
  

  public List<Id> getIds() {
    return ids;
  }

  public void setIds(final List<Id> ids) {
    this.ids = ids;
  }

  public String[] toArray() {
    final String[] idArray = new String[ids.size()];
    int i = 0;
    for(Id id : ids) {
      idArray[i++] = id.getValue();
    }
    return idArray;
  }
  
  public List<String> toList() {
    return Arrays.asList(toArray());
  }

  public static IdList forIterable(final Iterable<String> strings) {
    final List<Id> idList = new ArrayList<Id>(); //new ArrayList<Id>(Iterables.size(strings)); // Not GWT compatible
    int index = 0;
    for(String idString : strings) {
      Preconditions.checkNotNull(idString);
      final Id id = new Id();
      id.setValue(idString);
      id.setIndex(index++);
      idList.add(id);
    }
    
    final IdList ids = new IdList();
    ids.setIds(idList);
    ids.nextIndex = index;
    return ids;
  }
  
  public void setValue(final String id, final int index) {
    final Id newId = new Id();
    newId.setValue(id);
    newId.setIndex(index);
    while(ids.size() <= index) {
      ids.add(null);
    }
    ids.set(index, newId);
    nextIndex = index + 1;
  }

  public void add(final String id) {
    setValue(id, nextIndex);
  }

  public Iterator<String> iterator() {
    return toList().iterator();
  }
  
}
