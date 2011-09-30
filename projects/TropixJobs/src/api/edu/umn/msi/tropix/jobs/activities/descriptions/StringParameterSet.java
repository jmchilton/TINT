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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class StringParameterSet implements Serializable {
  @Id
  @GeneratedValue(strategy=GenerationType.AUTO)
  private String id;

  @OneToMany(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
  private Set<StringParameter> parameters = new HashSet<StringParameter>();
  
  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }

  public Set<StringParameter> getParameters() {
    return parameters;
  }

  public void setParameters(final Set<StringParameter> parameters) {
    this.parameters = parameters;
  }
  
  public void addParameter(final StringParameter parameter) {
    parameters.add(parameter);
  }
  
  /**
   * @return This parameter set as a {@link Map}. Changes made to this {@link Map}
   * do not affect the set defined by this object.   
   */
  public Map<String, String> toMap() {
    final Map<String, String> map = new HashMap<String, String>(parameters.size());
    for(StringParameter parameter : parameters) {
      map.put(parameter.getKey(), parameter.getValue());
    }
    return map;
  }

  public static StringParameterSet fromMap(final Map<String, String> parameterMap) {
    StringParameterSet set = new StringParameterSet();
    for(Map.Entry<String, String> entry : parameterMap.entrySet()) {
      StringParameter parameter = new StringParameter();
      parameter.setKey(entry.getKey());
      parameter.setValue(entry.getValue());
      set.addParameter(parameter);
    }
    return set;
  }
  
}
