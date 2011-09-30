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

package edu.umn.msi.tropix.common.jobqueue.impl;

import javax.annotation.Resource;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.JobDescription;
import edu.umn.msi.tropix.common.jobqueue.JobProcessorRecoverer;

@ContextConfiguration(locations = "classpath:edu/umn/msi/tropix/common/jobqueue/impl/testJobProcessorRecovererContext.xml")
public class DispatchingJobProcessorRecovererImplTest extends AbstractTestNGSpringContextTests {

  @Resource
  private JobProcessorRecoverer<JobDescription> jobProcessorRecoverer;

  @Test(groups = "unit")
  public void recoverer() {
    final JobDescription d1 = new TestJobDescriptionImpl("testRecoverer1");
    assert jobProcessorRecoverer.recover(d1).toString().equals("recoverer1");
  }

}
