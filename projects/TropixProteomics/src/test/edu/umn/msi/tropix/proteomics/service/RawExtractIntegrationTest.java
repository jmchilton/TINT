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

package edu.umn.msi.tropix.proteomics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.client.Job;
import edu.umn.msi.tropix.common.jobqueue.test.IntegrationTestBase;
import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;
import edu.umn.msi.tropix.storage.client.StorageData;
import edu.umn.msi.tropix.transfer.types.TransferResource;

@ContextConfiguration(locations = "testRawExtractContext.xml")
public class RawExtractIntegrationTest extends IntegrationTestBase {
  
  @Autowired
  private RawExtractJobQueueContext rawExtractService; 
  
  @Test(groups="integration")
  public void rawExtract() {
    final StorageData mzxmlData = getStorageData();
    mzxmlData.getUploadContext().put(ProteomicsTests.getResourceAsStream("mrr.RAW"));
    final TransferResource transferResource = mzxmlData.prepareDownloadResource();
    rawExtractService.submitJob(transferResource, null, "", "mrr");
    
    final Job job = new Job();
    job.setJobType("rawExtract");
    job.setTicket(rawExtractService.getTicket());
    assert super.pollJob(job, rawExtractService);    
  }
  
}
