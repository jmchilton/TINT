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

package edu.umn.msi.tropix.proteomics.test;

import java.util.LinkedList;
import java.util.List;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.jobqueue.test.BaseFileJobQueueContextImplTest;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.genomics.bowtie.BowtieJobProcessorFactory;
import edu.umn.msi.tropix.genomics.bowtie.input.BowtieInput;
import edu.umn.msi.tropix.genomics.service.impl.BowtieJobQueueContextImpl;
import edu.umn.msi.tropix.proteomics.convert.ProteomicsConvertJobFactory;
import edu.umn.msi.tropix.proteomics.convert.input.ConvertParameters;
import edu.umn.msi.tropix.proteomics.idpicker.IdPickerJobProcessorFactory;
import edu.umn.msi.tropix.proteomics.idpicker.parameters.IdPickerParameters;
import edu.umn.msi.tropix.proteomics.itraqquantitation.ITraqQuantitationJobProcessorFactory;
import edu.umn.msi.tropix.proteomics.itraqquantitation.options.QuantificationType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.training.QuantificationTrainingOptions;
import edu.umn.msi.tropix.proteomics.itraqquantitation.weight.QuantificationWeights;
import edu.umn.msi.tropix.proteomics.rawextract.RawExtractJobBuilder;
import edu.umn.msi.tropix.proteomics.scaffold.ScaffoldJobBuilder;
import edu.umn.msi.tropix.proteomics.scaffold.input.cagrid.Scaffold;
import edu.umn.msi.tropix.proteomics.service.impl.ITraqQuantitationJobQueueContextImpl;
import edu.umn.msi.tropix.proteomics.service.impl.IdPickerJobQueueContextImpl;
import edu.umn.msi.tropix.proteomics.service.impl.ProteomicsConvertJobQueueContextImpl;
import edu.umn.msi.tropix.proteomics.service.impl.RawExtractJobQueueContextImpl;
import edu.umn.msi.tropix.proteomics.service.impl.ScaffoldJobQueueContextImpl;
import edu.umn.msi.tropix.transfer.types.TransferResource;

public class JobQueueContextImplTest extends BaseFileJobQueueContextImplTest {
  @Test(groups = "unit")
  public void submitRawExtract() {
    submitRawExtract(false);
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void submitRawExtractException() {
    submitRawExtract(true);
  }

  @Test(groups = "unit")
  public void submitScaffold() {
    submitScaffold(false);
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void submitScaffoldException() {
    submitScaffold(true);
  }

  // TODO: Refine test by verifing correct contexts passed as inputs
  @Test(groups = "unit")
  public void submitITraqQuantitation() {
    final ITraqQuantitationJobQueueContextImpl context = new ITraqQuantitationJobQueueContextImpl();
    init(context);
    final ITraqQuantitationJobProcessorFactory iTraqQuantitationJobProcessorFactory = createMock(ITraqQuantitationJobProcessorFactory.class);
    context.setItraqQuantitationJobProcessorFactory(iTraqQuantitationJobProcessorFactory);

    final TransferResource mzxmlRef1 = getReference();
    final TransferResource mzxmlRef2 = getReference();
    getDownloadContext(mzxmlRef1);
    getDownloadContext(mzxmlRef2);
    final TransferResource reportRef = getReference();
    final InputContext reportContext = getDownloadContext(reportRef);

    iTraqQuantitationJobProcessorFactory.create(expectConfiguration(), EasyMock.<Iterable<InputContext>>anyObject(), EasyMock.same(reportContext),
        EasyMock.eq(QuantificationType.FOUR_PLEX), EasyMock.<QuantificationWeights>isNull());
    expectLastCallAndReturnFileJobProcessor();
    expectSubmitJob("ITraqQuantitation");
    doReplay();
    context.submitJob(new TransferResource[] {mzxmlRef1, mzxmlRef2}, reportRef, QuantificationType.FOUR_PLEX, null, getCredentialReference());
    doVerify();
  }

  @Test(groups = "unit")
  public void submitITraqQuantitationTraining() {
    final ITraqQuantitationJobQueueContextImpl context = new ITraqQuantitationJobQueueContextImpl();
    init(context);
    final ITraqQuantitationJobProcessorFactory iTraqQuantitationJobProcessorFactory = createMock(ITraqQuantitationJobProcessorFactory.class);
    context.setItraqQuantitationJobProcessorFactory(iTraqQuantitationJobProcessorFactory);

    final TransferResource mzxmlRef1 = getReference();
    final TransferResource mzxmlRef2 = getReference();
    getDownloadContext(mzxmlRef1);
    getDownloadContext(mzxmlRef2);
    final TransferResource reportRef = getReference();
    final InputContext reportContext = getDownloadContext(reportRef);

    iTraqQuantitationJobProcessorFactory.createTraining(expectConfiguration(), EasyMock.<Iterable<InputContext>>anyObject(),
        EasyMock.same(reportContext), EasyMock.eq(QuantificationType.FOUR_PLEX), EasyMock.<QuantificationTrainingOptions>isNull());
    expectLastCallAndReturnFileJobProcessor();
    expectSubmitJob("ITraqQuantitation");
    doReplay();
    context.submitTrainingJob(new TransferResource[] {mzxmlRef1, mzxmlRef2}, reportRef, QuantificationType.FOUR_PLEX, null, getCredentialReference());
    doVerify();
  }

  @Test(groups = "unit")
  public void submitBowtie() {
    final BowtieJobQueueContextImpl context = new BowtieJobQueueContextImpl();
    init(context);
    final BowtieJobProcessorFactory factory = createMock(BowtieJobProcessorFactory.class);
    context.setBowtieJobProcessorFactory(factory);

    final TransferResource db1 = getReference(), db2 = getReference(), indexRef = getReference();
    final InputContext indexContext = getDownloadContext(indexRef);
    getDownloadContext(db1);
    getDownloadContext(db2);
    final BowtieInput bowtieInput = new BowtieInput();
    factory.createBowtieJob(expectConfiguration(), EasyMock.same(bowtieInput), EasyMock.same(indexContext),
        EasyMock.<Iterable<InputContext>>anyObject());
    expectLastCallAndReturnFileJobProcessor();
    expectSubmitJob("Bowtie");
    doReplay();
    context.submitJob(indexRef, new TransferResource[] {db1, db2}, getCredentialReference(), bowtieInput);
    doVerify();
  }

  public void submitRawExtract(final boolean exception) {
    final RawExtractJobQueueContextImpl context = new RawExtractJobQueueContextImpl();
    init(context);
    final RawExtractJobBuilder rawExtractJobBuilder = createMock(RawExtractJobBuilder.class);
    context.setRawExtractJobBuilder(rawExtractJobBuilder);
    final TransferResource rawRef = getReference();
    final InputContext rawContext = getDownloadContext(rawRef);
    rawExtractJobBuilder.buildJob(expectConfiguration(), EasyMock.same(rawContext), EasyMock.eq("moo"), EasyMock.eq("cow"));
    expectLastCallAndReturnFileJobProcessor();
    expectSubmitJob("RawExtract", exception);
    doReplay();
    context.submitJob(rawRef, getCredentialReference(), "moo", "cow");
    doVerify();
  }

  // TODO: Refine test by verifing correct contexts passed as inputs
  public void submitScaffold(final boolean exception) {
    final ScaffoldJobQueueContextImpl context = new ScaffoldJobQueueContextImpl();
    init(context);
    final ScaffoldJobBuilder scaffoldJobBuilder = createMock(ScaffoldJobBuilder.class);
    context.setScaffoldJobBuilder(scaffoldJobBuilder);
    final List<TransferResource> references = new LinkedList<TransferResource>();
    final Scaffold scaffoldInput = new Scaffold();
    scaffoldJobBuilder.createScaffoldJob(expectConfiguration(), (edu.umn.msi.tropix.proteomics.scaffold.input.Scaffold) EasyMock.anyObject(),
        EasyMock.<Iterable<InputContext>>anyObject());
    expectLastCallAndReturnFileJobProcessor();
    expectSubmitJob("Scaffold", exception);
    doReplay();
    context.submitJob(references.toArray(new TransferResource[0]), getCredentialReference(), scaffoldInput);
    doVerify();
  }

  @Test(groups = "unit")
  public void testIdPicker() {
    final IdPickerJobProcessorFactory factory = createMock(IdPickerJobProcessorFactory.class);
    final IdPickerJobQueueContextImpl context = new IdPickerJobQueueContextImpl(factory);
    init(context);
    final IdPickerParameters params = new IdPickerParameters();

    final TransferResource mzxmlResource = getReference();
    final TransferResource databaseResource = getReference();
    final InputContext mzxmlContext = getDownloadContext(mzxmlResource);
    final InputContext databaseContext = getDownloadContext(databaseResource);

    // TODO: Verify order
    factory.create(expectConfiguration(), EasyMock.same(params), EasyMock.same(databaseContext),
        EasyMockUtils.<InputContext, Iterable<InputContext>>hasSameUniqueElements(Lists.newArrayList(mzxmlContext)));
    expectLastCallAndReturnFileJobProcessor();
    expectSubmitJob("IdPicker");
    doReplay();
    context.submitJob(new TransferResource[] {mzxmlResource}, databaseResource, getCredentialReference(), params);
    doVerify();
  }

  @Test(groups = "unit")
  public void testProteomicsConvert() {
    final ProteomicsConvertJobFactory factory = createMock(ProteomicsConvertJobFactory.class);
    final ProteomicsConvertJobQueueContextImpl context = new ProteomicsConvertJobQueueContextImpl(factory);
    init(context);
    final ConvertParameters params = new ConvertParameters();
    final TransferResource rawResource = getReference();
    final InputContext rawContext = getDownloadContext(rawResource);
    factory.create(expectConfiguration(), EasyMock.same(rawContext), EasyMock.same(params));
    expectLastCallAndReturnFileJobProcessor();
    expectSubmitJob("ProteomicsConvert");
    doReplay();
    context.submitJob(rawResource, getCredentialReference(), params);
    doVerify();
  }

}
