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

package edu.umn.msi.tropix.common.jobqueue.execution.gram;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.exec.generated.JobDescriptionType;
import org.globus.exec.utils.ManagedJobFactoryConstants;
import org.globus.exec.utils.client.ManagedJobFactoryClientHelper;
import org.globus.wsrf.impl.security.authentication.Constants;

import edu.umn.msi.tropix.common.jobqueue.description.JobDescriptionTransformer;
import edu.umn.msi.tropix.common.jobqueue.utils.JobDescriptionUtils;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.grid.credentials.Credential;

public class GramJobSubmitterImpl implements GramJobSubmitter {
  private static final Log LOG = LogFactory.getLog(GramJobSubmitterImpl.class);
  private String factoryType = ManagedJobFactoryConstants.FACTORY_TYPE.PBS;
  private Integer messageProtectionType = Constants.SIGNATURE;
  private JobDescriptionTransformer jobDescriptionTransformer;
  private String serviceAddress;
  private GramJobFactory gramJobFactory;

  public GramJob createGramJob(final JobDescriptionType jobDescription, final Credential proxy) {
    jobDescriptionTransformer.transform(jobDescription);
    final GramJob gramJob = gramJobFactory.getGramJob(jobDescription);
    gramJob.setCredentials(proxy);
    gramJob.setMessageProtectionType(messageProtectionType);
    gramJob.setDelegationEnabled(false);
    final String localJobId = JobDescriptionUtils.getLocalJobId(jobDescription);
    final String submissionId = "uuid:" + localJobId;
    EndpointReferenceType factoryEndpoint;
    try {
      // TODO: Refactor this static cling / grid required code into another class
      factoryEndpoint = ManagedJobFactoryClientHelper.getFactoryEndpoint(serviceAddress, factoryType);
    } catch(final Exception e) {
      throw ExceptionUtils.convertException(e, "Failed to create managed job factory endpoint for address " + serviceAddress);
    }
    LOG.debug("Submitting gram job " + JobDescriptionUtils.serialize(jobDescription));
    gramJob.submit(factoryEndpoint, false, true, submissionId);
    return gramJob;
  }

  public void setFactoryType(final String factoryType) {
    this.factoryType = factoryType;
  }

  public void setServiceAddress(final String serviceAddress) {
    this.serviceAddress = serviceAddress;
  }

  public void setMessageProtectionType(final Integer messageProtectionType) {
    this.messageProtectionType = messageProtectionType;
  }

  public void setGramJobFactory(final GramJobFactory gramJobFactory) {
    this.gramJobFactory = gramJobFactory;
  }

  public void setJobDescriptionTransformer(final JobDescriptionTransformer jobDescriptionTransformer) {
    this.jobDescriptionTransformer = jobDescriptionTransformer;
  }
  
}
