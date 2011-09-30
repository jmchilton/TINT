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

package edu.umn.msi.tropix.common.jobqueue.test;

import org.globus.exec.generated.JobDescriptionType;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.utils.JobDescriptionUtils;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.Credentials;

public class JobDescriptionUtilsTest {

  private JobDescriptionType getJobDescription() {
    return new JobDescriptionType();
  }

  @Test(groups = {"unit"})
  public void proxy() throws GlobusCredentialException {
    final JobDescriptionType jobDescription = getJobDescription();
    final GlobusCredential proxy = new GlobusCredential(JobQueueTest.class.getResourceAsStream("proxy"));
    JobDescriptionUtils.setProxy(jobDescription, Credentials.get(proxy));

    final Credential loadedProxy = JobDescriptionUtils.getProxy(jobDescription);
    assert loadedProxy.getIdentity().equals(proxy.getIdentity());
  }

  @Test(groups = "unit")
  public void nullProxy() {
    final JobDescriptionType jobDescription = getJobDescription();
    JobDescriptionUtils.setProxy(jobDescription, null);
    final Credential loadedProxy = JobDescriptionUtils.getProxy(jobDescription);
    assert loadedProxy == null;
  }

  @Test(groups = {"unit"})
  public void id() {
    final JobDescriptionType jobDescription = getJobDescription();
    final String setId = "123456";
    JobDescriptionUtils.setLocalJobId(jobDescription, setId);
    final String obtainedId = JobDescriptionUtils.getLocalJobId(jobDescription);
    assert obtainedId.equals(setId);
  }

  @Test(groups = {"unit"})
  public void type() {
    final JobDescriptionType jobDescription = getJobDescription();
    String setType = "Sequest";
    JobDescriptionUtils.setJobType(jobDescription, setType);
    String obtainedType = JobDescriptionUtils.getJobType(jobDescription);
    assert obtainedType.equals(setType);
    setType = "XTandem";
    JobDescriptionUtils.setJobType(jobDescription, setType);
    obtainedType = JobDescriptionUtils.getJobType(jobDescription);
    assert obtainedType.equals("XTandem") : obtainedType;
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void deserializationException() {
    JobDescriptionUtils.deserialize("not valid xml");
  }

  @Test(groups = {"unit"})
  public void serialization() {
    final JobDescriptionType jobDescription = getJobDescription();

    jobDescription.setDirectory("/tmp");
    jobDescription.setExecutable("/usr/bin/echo");

    String xml = JobDescriptionUtils.serialize(jobDescription);
    assert xml.contains("/tmp");
    assert xml.contains("/usr/bin/echo");

    final String setType = "Sequest";
    JobDescriptionUtils.setJobType(jobDescription, setType);

    xml = JobDescriptionUtils.serialize(jobDescription);
    xml.contains("Sequest");

    final JobDescriptionType recovered = JobDescriptionUtils.deserialize(xml);
    assert JobDescriptionUtils.getJobType(recovered).equals("Sequest");
  }
}
