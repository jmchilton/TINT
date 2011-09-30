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

import java.rmi.RemoteException;

import org.apache.axis.types.URI.MalformedURIException;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.jobqueue.client.Job;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.proteomics.service.RawExtractJobQueueContext;
import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;
import edu.umn.msi.tropix.proteomics.xml.MzXMLUtility;
import edu.umn.msi.tropix.storage.client.StorageData;
import edu.umn.msi.tropix.transfer.types.TransferResource;


public class RawExtractDeploymentTest {
  
  @Test(groups = "deployment")
  public void deployment() throws InterruptedException, GlobusCredentialException, MalformedURIException, RemoteException {
    /*
    
    GlobusCredential cred = new GlobusCredential("/home/john/chilton2.msi.umn.edu-cert.pem", "/home/john/chilton2.msi.umn.edu-key.pem");
    RawExtractInterfacesClient client = new RawExtractInterfacesClient("https://localhost:8443/rawextract/services/cagrid/RawExtract");
    RawExtractJobContextClient jobClient = client.getCaGridClient().createJob();
    jobClient.submitJob(null, null, null, null);

    final RawExtractJobQueueContext context = (RawExtractJobQueueContext) this.applicationContext.getBean("rawExtractService");
    final TransferResource resource = super.getReference(ProteomicsTests.getResourceAsStream("mrr.RAW"));
    context.submitJob(resource, null, "", "basename");
    
    final Job job = new Job();
    job.setJobType("rawextract");
    job.setTicket(context.getTicket());
    assert super.pollJob(job);
    
    final StorageData storageData = getStorageData();
    context.getResults(new TransferResource[] {storageData.prepareUploadResource()}, null);
    InputContext inputContext = storageData.getDownloadContext();
    final MzXMLUtility mzxmlUtility = new MzXMLUtility();
    mzxmlUtility.deserialize(inputContext); //new ByteArrayInputStream(outputStream.toByteArray()));
    */
  }
  
}
