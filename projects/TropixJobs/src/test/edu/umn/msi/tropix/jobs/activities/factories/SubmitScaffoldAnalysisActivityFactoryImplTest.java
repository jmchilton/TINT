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

package edu.umn.msi.tropix.jobs.activities.factories;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.jobs.activities.descriptions.IdList;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitScaffoldAnalysisDescription;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.proteomics.scaffold.input.Experiment;
import edu.umn.msi.tropix.proteomics.scaffold.input.Scaffold;
import edu.umn.msi.tropix.proteomics.service.ScaffoldJobQueueContext;
import edu.umn.msi.tropix.proteomics.xml.ScaffoldUtility;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.transfer.types.TransferResource;

public class SubmitScaffoldAnalysisActivityFactoryImplTest extends BaseSubmitActivityFactoryImplTest {

  @Test(groups = "unit")
  public void submit() {
    init();
    final SubmitScaffoldAnalysisDescription description = new SubmitScaffoldAnalysisDescription();
    final String serviceUrl = "http://Scaffold";
    description.setServiceUrl(serviceUrl);

    final Database d1 = new Database();
    final TropixFile f1 = TestUtils.getNewTropixFile();
    d1.setDatabaseFile(f1);

    final Database d2 = new Database();
    final TropixFile f2 = TestUtils.getNewTropixFile();
    d2.setDatabaseFile(f2);

    final IdentificationAnalysis i1 = new IdentificationAnalysis();
    final TropixFile f3 = TestUtils.getNewTropixFile();
    i1.setOutput(f3);

    final IdentificationAnalysis i2 = new IdentificationAnalysis();
    final TropixFile f4 = TestUtils.getNewTropixFile();
    i2.setOutput(f4);

    final IdentificationAnalysis i3 = new IdentificationAnalysis();
    final TropixFile f5 = TestUtils.getNewTropixFile();
    i3.setOutput(f5);

    final TropixFile f6 = TestUtils.getNewTropixFile();

    getFactorySupport().saveObjects(d1, d2, i1, i2, i3, f1, f2, f3, f4, f5, f6);
    description.setDatabaseIds(IdList.forIterable(Lists.newArrayList(d1.getId(), d2.getId())));
    description.setIdentificationIds(IdList.forIterable(Lists.newArrayList(i1.getId(), i2.getId(), i3.getId())));
    description.setDriverFileId(f6.getId());
    final ModelStorageData modelStorageData = getFactorySupport().getStorageDataFactory().getStorageData(f6.getFileId(), f6.getStorageServiceUrl(), getContext().getCredential());
    final Scaffold scaffold = new Scaffold();
    final Experiment experiment = new Experiment();
    experiment.setName("Exp Name");
    scaffold.setExperiment(experiment);
    new ScaffoldUtility().serialize(scaffold, modelStorageData.getUploadContext());

    final ScaffoldJobQueueContext context = expectCreateJob(serviceUrl, ScaffoldJobQueueContext.class);
    final Capture<edu.umn.msi.tropix.proteomics.scaffold.input.cagrid.Scaffold> scaffoldCapture = EasyMockUtils.newCapture();
    context.submitJob(EasyMock.aryEq(new TransferResource[] {getDownload(f1.getId()), getDownload(f2.getId()), getDownload(f3.getId()), getDownload(f4.getId()), getDownload(f5.getId())}), expectCredentialResource(), EasyMock.capture(scaffoldCapture));

    preRun(context);
    new SubmitScaffoldAnalysisActivityFactoryImpl(getFactorySupport(), getSubmitSupport()).getActivity(description, getContext()).run();
    postRun(description);
    scaffoldCapture.getValue().getExperiment().getName().equals("Exp Name");
  }

}
