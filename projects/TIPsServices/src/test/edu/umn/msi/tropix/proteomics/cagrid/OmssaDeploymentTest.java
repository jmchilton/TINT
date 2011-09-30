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

package edu.umn.msi.tropix.proteomics.cagrid;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.jobqueue.client.Job;
import edu.umn.msi.tropix.proteomics.service.OmssaJobQueueContext;
import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;
import edu.umn.msi.tropix.storage.client.StorageData;
import edu.umn.msi.tropix.transfer.types.TransferResource;
import gov.nih.nlm.ncbi.omssa.MSSearchSettings;


@ContextConfiguration(locations = "deployOmssaTestContext.xml")
public class OmssaDeploymentTest extends IntegrationTestBase {
  
  /*
  @Test(groups = "deployment")
  public void deployment() throws InterruptedException, IOException {
    final OmssaJobQueueContext context = (OmssaJobQueueContext) this.applicationContext.getBean("omssaService");
        
    final InputStream mzxmlStream = ProteomicsTests.getResourceAsStream("validMzXML.mzxml");
    final InputStream databaseStream = ProteomicsTests.getResourceAsStream("HUMAN.fasta");
    final InputStream properties = ProteomicsTests.getResourceAsStream("omssa.properties");
    final Properties props = new Properties();
    props.load(properties);
    final Map<String, String> map = Maps.fromProperties(props);
    final OmssaParametersFunctionImpl func = new OmssaParametersFunctionImpl();
    final MSSearchSettings settings = func.apply(map);
    final List<TransferResource> refs = super.getReferences(Arrays.asList(mzxmlStream, databaseStream));
    context.submitJob(refs.get(0), refs.get(1), null, settings);

    final Job job = new Job();
    job.setJobType("omssa");
    job.setTicket(context.getTicket());
    assert super.pollJob(job);
    
    final StorageData storageData = getStorageData();
    context.getResults(new TransferResource[] {storageData.prepareUploadResource()}, null);
    
  }
  */
}
