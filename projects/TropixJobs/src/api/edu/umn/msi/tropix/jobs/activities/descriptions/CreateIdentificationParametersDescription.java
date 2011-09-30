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

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToOne;

@Entity
public class CreateIdentificationParametersDescription extends TropixObjectDescription implements ConsumesStorageServiceUrl {
  @OneToOne(cascade={CascadeType.ALL}) 
  private StringParameterSet parameters;
  private String parametersId; 
  private String parameterType;
  private String storageServiceUrl;
  
  public String getStorageServiceUrl() {
    return storageServiceUrl;
  }

  @Consumes
  public void setStorageServiceUrl(final String storageServiceUrl) {
    this.storageServiceUrl = storageServiceUrl;
  }

  public StringParameterSet getParameters() {
    return parameters;
  }

  @Consumes
  public void setParameters(final StringParameterSet parameters) {
    this.parameters = parameters;
  }
  
  @Produces
  public String getParametersId() {
    return parametersId;
  }
  
  public void setParametersId(final String parametersId) {
    this.parametersId = parametersId;
  }

  public String getParameterType() {
    return parameterType;
  }  
  
  @Consumes
  public void setParameterType(final String parameterType) {
    this.parameterType = parameterType;
  }
  
}
