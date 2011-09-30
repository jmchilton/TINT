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
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitIdPickerAnalysisDescription;
import edu.umn.msi.tropix.models.Database;
import edu.umn.msi.tropix.models.IdentificationAnalysis;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.proteomics.client.IdPickerUtils;
import edu.umn.msi.tropix.proteomics.idpicker.parameters.IdPickerParameters;
import edu.umn.msi.tropix.proteomics.service.IdPickerJobQueueContext;
import edu.umn.msi.tropix.storage.client.ModelStorageData;
import edu.umn.msi.tropix.transfer.types.TransferResource;

public class SubmitIdPickerActivityFactoryImplTest extends BaseSubmitActivityFactoryImplTest {

  @Test(groups = "unit")
  public void submit() {
    init();
    final SubmitIdPickerAnalysisDescription description = new SubmitIdPickerAnalysisDescription();
    final String serviceUrl = "http://IdPicker";
    description.setServiceUrl(serviceUrl);

    final Database d1 = new Database();
    final TropixFile f1 = TestUtils.getNewTropixFile();
    d1.setDatabaseFile(f1);

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

    getFactorySupport().saveObjects(d1, i1, i2, i3, f1, f3, f4, f5, f6);
    description.setDatabaseIds(IdList.forIterable(Lists.newArrayList(d1.getId())));
    description.setIdentificationIds(IdList.forIterable(Lists.newArrayList(i1.getId(), i2.getId(), i3.getId())));
    description.setDriverFileId(f6.getId());
    final ModelStorageData modelStorageData = getFactorySupport().getStorageDataFactory().getStorageData(f6.getFileId(), f6.getStorageServiceUrl(), getContext().getCredential());

    final IdPickerParameters parameters = new IdPickerParameters();
    modelStorageData.getUploadContext().put(IdPickerUtils.serialize(parameters).getBytes());

    final IdPickerJobQueueContext context = expectCreateJob(serviceUrl, IdPickerJobQueueContext.class);
    final Capture<IdPickerParameters> parametersCapture = EasyMockUtils.newCapture();
    context.submitJob(EasyMock.aryEq(new TransferResource[] {getDownload(f3.getId()), getDownload(f4.getId()), getDownload(f5.getId())}), EasyMock.eq(getDownload(f1.getId())), expectCredentialResource(), EasyMock.capture(parametersCapture));

    preRun(context);
    new SubmitIdPickerAnalysisActivityFactoryImpl(getFactorySupport(), getSubmitSupport()).getActivity(description, getContext()).run();
    postRun(description);
    parametersCapture.getValue().equals(parameters);
  }

}
