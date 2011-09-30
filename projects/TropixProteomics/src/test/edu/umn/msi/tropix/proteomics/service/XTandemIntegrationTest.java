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

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.jobqueue.client.Job;
import edu.umn.msi.tropix.common.jobqueue.test.IntegrationTestBase;
import edu.umn.msi.tropix.grid.io.impl.AdaptedCredentialedStagingDirectoryFactoryImpl;
import edu.umn.msi.tropix.models.xtandem.XTandemParameters;
import edu.umn.msi.tropix.proteomics.parameters.ParameterUtils;
import edu.umn.msi.tropix.proteomics.test.ProteomicsTests;
import edu.umn.msi.tropix.proteomics.xml.XMLConversionUtilities;
import edu.umn.msi.tropix.storage.client.StorageData;
import edu.umn.msi.tropix.transfer.types.TransferResource;

@ContextConfiguration(locations = "testXTandemContext.xml")
public class XTandemIntegrationTest extends IntegrationTestBase {

  @Autowired
  private XTandemJobQueueContext xTandemService;

  @Test(groups = "spring-xtandem")
  public void xtandem() {
    edu.umn.msi.tropix.grid.io.impl.AdaptedCredentialedStagingDirectoryFactoryImpl dir = (AdaptedCredentialedStagingDirectoryFactoryImpl) super.applicationContext.getBean("systemCredentialedStagingDirectoryFactory");
    edu.umn.msi.tropix.common.io.impl.StagingDirectorySupplierImpl dirSup = new edu.umn.msi.tropix.common.io.impl.StagingDirectorySupplierImpl();
    dirSup.setDeleteStagedFiles(false);
    dir.setStagingDirectoryFactory(dirSup);
    final InputStream mzxmlStream = ProteomicsTests.getResourceAsStream("readw.mzXML");
    final InputStream databaseStream = ProteomicsTests.getResourceAsStream("HUMAN.fasta");
    final List<TransferResource> refs = getReferences(Arrays.asList(mzxmlStream, databaseStream));
    final XTandemParameters parameters = new XTandemParameters();
    ParameterUtils.setParametersFromProperties(ProteomicsTests.getResourceAsStream("xTandemInclusive.properties"), parameters);

    xTandemService.submitJob(refs.get(0), refs.get(1), null, XMLConversionUtilities.convert(parameters));
    
    final Job job = new Job();
    job.setJobType("xtandem");
    job.setTicket(xTandemService.getTicket());
    assert super.pollJob(job, xTandemService);
    
    final StorageData storageData = getStorageData();
    xTandemService.getResults(new TransferResource[] {storageData.prepareUploadResource()}, null);
    InputContext inputContext = storageData.getDownloadContext();
    System.out.println("XTandem output " + InputContexts.toString(inputContext));

  }
}
