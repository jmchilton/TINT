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

package edu.umn.msi.tropix.common.jobqueue.configuration;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.grid.credentials.Credentials;

public class JobProcessorConfigurationFactoriesTest {

  @Test(groups = "unit")
  public void testConstructor() {
    new JobProcessorConfigurationFactories();
  }
  
  @Test(groups = "unit")
  public void testGetCredential() {
    final JobProcessorConfiguration config1 = JobProcessorConfigurationFactories.getInstance().get(Credentials.getMock("moo"));
    assert config1.getCredential().getIdentity().equals("moo");
  }
  
  @Test(groups = "unit")
  public void testEqualsAndHashCodeForConfiguration() {
    final JobProcessorConfiguration config1 = JobProcessorConfigurationFactories.getInstance().get(Credentials.getMock("moo"));
    final JobProcessorConfiguration config2 = JobProcessorConfigurationFactories.getInstance().get(config1.getCredential());
    assert config1.equals(config2);
    assert config1.hashCode() == config2.hashCode();
  }

  
}
