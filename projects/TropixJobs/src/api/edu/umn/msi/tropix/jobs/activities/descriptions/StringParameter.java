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

import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

/**
 * In the metadata database we initially handled parameters in a highly structured way, 
 * having a table for Sequest parameters and for X! Tandem parameters
 * each with dozens of columns. That approach was very heavy, this class represents
 * the opposite approach where each parameter set is represented as essentially
 * a map of keys to values.
 * 
 * This approach may not prove to be ideal, and this might not be the 
 * best way to map that concept for GWT and JPA.
 * 
 * @author john
 *
 */
@Entity
public class StringParameter implements Serializable {
  @Id
  @Nonnull
  @GeneratedValue(strategy=GenerationType.AUTO)
  private String id;
  private String key;
  
  @Column(length = 4096)
  private String value;

  @ManyToOne(fetch=FetchType.EAGER)
  private StringParameterSet parameterSet;

  public String getId() {
    return id;
  }
  
  public void setId(final String id) {
    this.id = id;
  }
  
  public String getKey() {
    return key;
  }
  
  public void setKey(final String key) {
    this.key = key;
  }
  
  public String getValue() {
    return value;
  }
  
  public void setValue(final String value) {
    this.value = value;
  }
  
  public StringParameterSet getParameterSet() {
    return parameterSet;
  }

  public void setParameterSet(final StringParameterSet parameterSet) {
    this.parameterSet = parameterSet;
  }
    
}
