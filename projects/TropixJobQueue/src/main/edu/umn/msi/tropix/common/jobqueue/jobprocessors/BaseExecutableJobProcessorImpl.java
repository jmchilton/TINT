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

package edu.umn.msi.tropix.common.jobqueue.jobprocessors;

import org.globus.exec.generated.JobDescriptionType;
import org.springframework.util.StringUtils;

import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescriptions;
import edu.umn.msi.tropix.common.jobqueue.utils.JobDescriptionUtils;

public class BaseExecutableJobProcessorImpl extends BaseJobProcessorImpl<ExecutableJobDescription> implements BaseExecutableJobProcessor {

  protected void saveParameter(final String name, final String value) {
    JobDescriptionUtils.setExtensionParameter(getJobDescription().getJobDescriptionType(), name, value);
  }

  protected String getParameter(final String name) {
    return JobDescriptionUtils.getExtensionParameter(getJobDescription().getJobDescriptionType(), name);
  }

  public void setJobDescription(final JobDescriptionType jobDescriptionType) {
    setJobDescription(ExecutableJobDescriptions.forJobDescriptionType(jobDescriptionType));
  }

  public ExecutableJobDescription preprocess() {
    final ExecutableJobDescription jobDescription = super.preprocess();
    final JobDescriptionType jobDescriptionType = jobDescription.getJobDescriptionType();
    if(!StringUtils.hasText(jobDescriptionType.getDirectory())) {
      jobDescriptionType.setDirectory(getStagingDirectory().getAbsolutePath());
    }
    return jobDescription;
  }

}