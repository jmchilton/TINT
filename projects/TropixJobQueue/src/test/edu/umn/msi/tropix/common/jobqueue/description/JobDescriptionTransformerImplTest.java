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

package edu.umn.msi.tropix.common.jobqueue.description;

import org.apache.axis.types.PositiveInteger;
import org.globus.exec.generated.JobDescriptionType;
import org.testng.annotations.Test;

public class JobDescriptionTransformerImplTest {

  @Test(groups = "unit")
  public void ops() {
    final JobDescriptionTransformerImpl transformer = new JobDescriptionTransformerImpl();
    transformer.setCount(12);
    assert transformer.getCount() == 12;

    transformer.setMaxCpuTime(100L);
    assert transformer.getMaxCpuTime() == 100L;

    transformer.setMaxMemory(145);
    assert transformer.getMaxMemory() == 145;

    transformer.setMaxWallTime(100004L);
    assert transformer.getMaxWallTime() == 100004L;

    transformer.setOverrideExisting(true);
    assert transformer.isOverrideExisting();

    transformer.setOverrideExisting(false);
    assert !transformer.isOverrideExisting();

    final JobDescriptionType jobDescription = new JobDescriptionType();
    transformer.transform(jobDescription);

    assert jobDescription.getMaxCpuTime() == 100L;
    assert jobDescription.getCount().equals(new PositiveInteger("12"));
  }
}
