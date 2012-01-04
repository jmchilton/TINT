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

import javax.annotation.Nullable;
import javax.persistence.Entity;

@Entity
public class CreateTropixFileDescription extends TropixObjectDescription implements ConsumesStorageServiceUrl {
  private String filename;

  private String fileId;
  @Nullable private String extension;
  private String storageServiceUrl;

  @Override
  protected String fieldsToString() {
    return super.fieldsToString() + ",fileId=" + fileId + ",extension=" + extension +",storageServiceUrl=" + storageServiceUrl;
  }

  public String getFilename() {
    return filename;
  }

  @Consumes
  public void setFilename(final String filename) {
    this.filename = filename;
  }

  public String getFileId() {
    return fileId;
  }

  @Consumes
  public void setFileId(final String fileId) {
    this.fileId = fileId;
  }

  public String getExtension() {
    return extension;
  }

  @Consumes
  public void setExtension(final String extension) {
    this.extension = extension;
  }

  public String getStorageServiceUrl() {
    return storageServiceUrl;
  }

  @Consumes
  public void setStorageServiceUrl(final String storageServiceUrl) {
    this.storageServiceUrl = storageServiceUrl;
  }

}
