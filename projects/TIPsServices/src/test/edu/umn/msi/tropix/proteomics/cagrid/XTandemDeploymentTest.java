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

import java.io.File;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.cagrid.gaards.cds.delegated.stubs.types.DelegatedCredentialReference;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.io.StagingDirectory;
import edu.umn.msi.tropix.common.jobqueue.client.Job;
import edu.umn.msi.tropix.common.jobqueue.client.JobClientFactory;
import edu.umn.msi.tropix.common.jobqueue.client.JobClientFactoryManager;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.grid.credentials.DelegatedCredentialFactory;
import edu.umn.msi.tropix.grid.credentials.DelegatedCredentialResolver;
import edu.umn.msi.tropix.grid.gridftp.impl.GridFtpClientFactoryImpl;
import edu.umn.msi.tropix.grid.io.impl.GridFtpCredentialedStagingDirectoryFactoryImpl;
import edu.umn.msi.tropix.models.xtandem.XTandemParameters;
import edu.umn.msi.tropix.proteomics.parameters.ParameterUtils;
import edu.umn.msi.tropix.proteomics.service.XTandemJobQueueContext;
import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;
import edu.umn.msi.tropix.proteomics.xml.XMLConversionUtilities;
import edu.umn.msi.tropix.storage.client.StorageData;
import edu.umn.msi.tropix.transfer.http.client.HttpTransferClients;
import edu.umn.msi.tropix.transfer.types.HttpTransferResource;
import edu.umn.msi.tropix.transfer.types.TransferResource;


@ContextConfiguration(locations = "deployXTandemTestContext.xml")
public class XTandemDeploymentTest extends IntegrationTestBase {
  
  @Test(groups = {"deployment", "deployment-xtandem"})
  public void deployment() throws InterruptedException, GlobusCredentialException {
    Supplier<Credential> credentialSupplier = (Supplier<Credential>) this.applicationContext.getBean("hostProxySupplier");
    final Credential cred = credentialSupplier.get();
    
    /*
    DelegatedCredentialFactory dcFactory = (DelegatedCredentialFactory) applicationContext.getBean("delegatedCredentialFactory");
    DelegatedCredentialReference dcRef = dcFactory.createDelegatedCredential(cred);
    DelegatedCredentialResolver res = (DelegatedCredentialResolver) applicationContext.getBean("delegatedCredentialResolver");
    Credential dcCred = res.getDelgatedCredential(dcRef);
    HttpTransferResource r = (HttpTransferResource) super.getReference(ProteomicsTests.getResourceAsStream("readw.mzXML"));
    System.out.println("Url is " + r.getUrl());
    final InputContext iContext = HttpTransferClients.getInstance().getInputContext(r.getUrl());
    final GridFtpClientFactoryImpl ftpFactory = new GridFtpClientFactoryImpl("160.94.77.141", 2811, false);    
    GridFtpCredentialedStagingDirectoryFactoryImpl dir = new GridFtpCredentialedStagingDirectoryFactoryImpl();
    dir.setDeleteStagedFiles(false);
    dir.setGridFtpClientFactory(ftpFactory);
    dir.setSep("\\");
    dir.setTempDirectoryPath("C:\\GridFTPRoot");    
    final StagingDirectory directory = dir.get(dcCred);
    directory.setup();
    iContext.get(directory.getOutputContext("MooCow"));
    */
    
    final InputStream mzxmlStream = ProteomicsTests.getResourceAsStream("readw.mzXML");
    final InputStream databaseStream = ProteomicsTests.getResourceAsStream("HUMAN.fasta");
    final List<TransferResource> refs = super.getReferences(Arrays.asList(databaseStream, mzxmlStream));
    HttpTransferResource r1 = new HttpTransferResource();
    r1.setUrl("http://www.google.com/");
    System.out.println("Url is " + ((HttpTransferResource) refs.get(0)).getUrl());
    System.out.println("Url is " + ((HttpTransferResource) refs.get(1)).getUrl());
    //Thread.sleep(100000);
    JobClientFactoryManager manager = (JobClientFactoryManager) this.applicationContext.getBean("jobClientFactoryManager");
    JobClientFactory factory = manager.getFactory("http://160.94.77.141:8080/xtandem/services/cagrid/XTandem");
    System.out.println("Credential is " + cred);
    XTandemJobQueueContext context = factory.createJobContext(cred, "http://160.94.77.141:8080/xtandem/services/cagrid/XTandem", XTandemJobQueueContext.class);
    //final InputStream mzxmlStream = ProteomicsTests.class.getResourceAsStream("readw.mzXML");
    final XTandemParameters parameters = new XTandemParameters();
    ParameterUtils.setParametersFromProperties(ProteomicsTests.getResourceAsStream("xTandemInclusive.properties"), parameters);
    
    DelegatedCredentialFactory dcFactory = (DelegatedCredentialFactory) applicationContext.getBean("delegatedCredentialFactory");
    DelegatedCredentialReference dcRef = dcFactory.createDelegatedCredential(cred);
    System.out.println("DCREF IS " + dcRef);
    //context.submitJob(refs.get(0), refs.get(1), dcRef, XMLConversionUtilities.convert(parameters));
        
    final Job job = new Job();
    job.setJobType("xtandem");
    job.setTicket(context.getTicket());
    assert super.pollJob(job);
    
    final StorageData storageData = getStorageData();
    context.getResults(new TransferResource[] {storageData.prepareUploadResource()}, null);
    InputContext inputContext = storageData.getDownloadContext();
    System.out.println("XTandem output " + InputContexts.toString(inputContext));
    
  }
  
}
