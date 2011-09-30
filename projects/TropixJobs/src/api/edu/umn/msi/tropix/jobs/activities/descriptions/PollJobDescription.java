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

import java.util.Arrays;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@Entity
public class PollJobDescription extends ActivityDescription implements ConsumesStorageServiceUrl {
  private String serviceUrl;
  private String ticket;
  
  private String storageServiceUrl;
  
  @OneToOne(cascade={CascadeType.ALL}, fetch=FetchType.EAGER)
  private IdList fileIds;
  
  @Produces
  public String getFileId() {
    return fileIds.getIds().get(0).getId();
  }
  
  public void setFileId(final String fileId) {
    this.fileIds = IdList.forIterable(Arrays.asList(fileId));
  }
    
  @Produces
  public IdList getFileIds() {
    return fileIds;
  }
  
  public void setFileIds(final IdList fileIds) {
    this.fileIds = fileIds;
  }
  
  public String getStorageServiceUrl() {
    return storageServiceUrl;
  }
  
  @Consumes
  public void setStorageServiceUrl(final String storageServiceUrl) {
    this.storageServiceUrl = storageServiceUrl;
  }
  
  public String getServiceUrl() {
    return serviceUrl;
  }
  
  @Consumes
  public void setServiceUrl(final String serviceUrl) {
    this.serviceUrl = serviceUrl;
  }
  
  public String getTicket() {
    return ticket;
  }
  
  @Consumes
  public void setTicket(final String ticket) {
    this.ticket = ticket;
  }
  
}
