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

import javax.persistence.Entity;

@Entity
public class SubmitProteomicsConvertDescription extends SubmitJobDescription {
  private String inputFormat;
  private String outputFormat;
  private String inputName;
  private String inputFileId;
  
  public String getInputFormat() {
    return inputFormat;
  }
  
  @Consumes
  public void setInputFormat(final String inputFormat) {
    this.inputFormat = inputFormat;
  }
  
  public String getOutputFormat() {
    return outputFormat;
  }
  
  @Consumes
  public void setOutputFormat(final String outputFormat) {
    this.outputFormat = outputFormat;
  }
  
  public String getInputName() {
    return inputName;
  }
  
  @Consumes
  public void setInputName(final String inputName) {
    this.inputName = inputName;
  }
  
  public String getInputFileId() {
    return inputFileId;
  }
  
  @Consumes
  public void setInputFileId(final String inputFileId) {
    this.inputFileId = inputFileId;
  }
  
}
