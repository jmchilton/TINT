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

import java.io.StringWriter;
import java.util.Collection;
import java.util.Map;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.TestNGDataProviders;
import edu.umn.msi.tropix.grid.xml.SerializationUtils;
import edu.umn.msi.tropix.grid.xml.SerializationUtilsFactory;
import edu.umn.msi.tropix.jobs.activities.descriptions.IdList;
import edu.umn.msi.tropix.jobs.activities.descriptions.StringParameterSet;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitITraqQuantitationAnalysisDescription;
import edu.umn.msi.tropix.jobs.activities.descriptions.SubmitITraqQuantitationTrainingDescription;
import edu.umn.msi.tropix.jobs.activities.impl.Activity;
import edu.umn.msi.tropix.models.ITraqQuantitationTraining;
import edu.umn.msi.tropix.models.ProteomicsRun;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.proteomics.client.QNames;
import edu.umn.msi.tropix.proteomics.itraqquantitation.options.QuantificationType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.training.QuantificationTrainingOptions;
import edu.umn.msi.tropix.proteomics.itraqquantitation.weight.QuantificationWeight;
import edu.umn.msi.tropix.proteomics.itraqquantitation.weight.QuantificationWeights;
import edu.umn.msi.tropix.proteomics.service.ITraqQuantitationJobQueueContext;
import edu.umn.msi.tropix.transfer.types.TransferResource;

public class SubmitITraqQuantitationActivityFactorysTest extends BaseSubmitActivityFactoryImplTest {
  private TransferResource[] mzxmlInputs;
  private TransferResource dataReport;
  private Collection<String> runIds;
  private String reportId;
  private final String serviceUrl = "http://ITraqQuantitation";

  @BeforeMethod(groups = "unit")
  public void init() {
    super.init();

    final TropixFile reportFile = TestUtils.getNewTropixFile();
    reportId = reportFile.getId();

    final ProteomicsRun run1 = new ProteomicsRun(), run2 = new ProteomicsRun();
    final TropixFile mzxmlFile1 = TestUtils.getNewTropixFile(), mzxmlFile2 = TestUtils.getNewTropixFile();
    run1.setMzxml(mzxmlFile1);
    run2.setMzxml(mzxmlFile2);
    getFactorySupport().saveObjects(run1, run2, mzxmlFile1, mzxmlFile2, reportFile);
    runIds = Lists.newArrayList(run1.getId(), run2.getId());

    mzxmlInputs = new TransferResource[] {getDownload(mzxmlFile1.getId()), getDownload(mzxmlFile2.getId())};
    dataReport = getDownload(reportFile.getId());
  }

  @Test(groups = "unit", dataProvider = "bool1", dataProviderClass = TestNGDataProviders.class)
  public void submitAnalysis(final boolean useWeights) {
    final SubmitITraqQuantitationAnalysisJobActivityFactoryImpl factory = new SubmitITraqQuantitationAnalysisJobActivityFactoryImpl(getFactorySupport(), getSubmitSupport());
    final SubmitITraqQuantitationAnalysisDescription description = new SubmitITraqQuantitationAnalysisDescription();
    TestUtils.init(description);
    final QuantificationType type = QuantificationType.EIGHT_PLEX;
    description.setQuantificationType(type.getValue());
    description.setServiceUrl(serviceUrl);
    description.setRunIdList(IdList.forIterable(runIds));
    description.setReportFileId(reportId);

    final ITraqQuantitationJobQueueContext jobContext = expectCreateJob(serviceUrl, ITraqQuantitationJobQueueContext.class);

    final QuantificationWeight w1 = new QuantificationWeight(1.2, 3.4);
    QuantificationWeights weights = null;
    if(useWeights) {
      final TropixFile weightsFile = TestUtils.getNewTropixFile();
      final ITraqQuantitationTraining training = new ITraqQuantitationTraining();
      training.setTrainingFile(weightsFile);
      getFactorySupport().saveObjects(training, weightsFile);
      description.setTrainingId(training.getId());
      weights = new QuantificationWeights(new QuantificationWeight[] {w1});
      final SerializationUtils serializationUtils = SerializationUtilsFactory.getInstance();
      final StringWriter weightsWriter = new StringWriter();
      serializationUtils.serialize(weightsWriter, weights, QNames.QUANTIFICATION_WEIGHTS_QNAME);
      weightsWriter.flush();
      getPersistedData(weightsFile.getId()).getUploadContext().put(weightsWriter.toString().getBytes());
    }
    final Capture<QuantificationWeights> weightCapture = EasyMockUtils.newCapture();
    if(useWeights) {
      jobContext.submitJob(EasyMockUtils.arySame(mzxmlInputs), EasyMock.eq(dataReport), EasyMock.eq(type), EasyMock.capture(weightCapture), expectCredentialResource());
    } else {
      jobContext.submitJob(EasyMockUtils.arySame(mzxmlInputs), EasyMock.eq(dataReport), EasyMock.eq(type), EasyMock.<QuantificationWeights>isNull(), expectCredentialResource());
    }
    preRun(jobContext);
    final Activity activity = factory.getActivity(description, getContext());
    activity.run();
    postRun(description);
    if(useWeights) {
      final QuantificationWeights submittedWeights = weightCapture.getValue();
      assert submittedWeights.getWeight()[0].getIntensity() == 1.2;
      assert submittedWeights.getWeight()[0].getWeight() == 3.4;
    }
  }

  @Test(groups = "unit")
  public void submitTraining() {
    final SubmitITraqQuantitationTrainingJobActivityFactoryImpl factory = new SubmitITraqQuantitationTrainingJobActivityFactoryImpl(getFactorySupport(), getSubmitSupport());

    final SubmitITraqQuantitationTrainingDescription description = new SubmitITraqQuantitationTrainingDescription();
    TestUtils.init(description);
    final String serviceUrl = "http://ITraqQuantitation";
    final QuantificationType type = QuantificationType.FOUR_PLEX;
    description.setQuantificationType(type.getValue());
    description.setServiceUrl(serviceUrl);
    description.setRunIdList(IdList.forIterable(runIds));
    description.setReportFileId(reportId);

    final Map<String, String> optionsMap = Maps.newHashMap();
    optionsMap.put("numBins", "4");
    optionsMap.put("i113Proportion", "113.0");
    optionsMap.put("i114Proportion", "114.0");
    optionsMap.put("i115Proportion", "115.0");
    optionsMap.put("i116Proportion", "116.0");
    optionsMap.put("i117Proportion", "117.0");
    optionsMap.put("i118Proportion", "118.0");
    optionsMap.put("i119Proportion", "119.0");
    optionsMap.put("i121Proportion", "121.0");
    description.setParameterSet(StringParameterSet.fromMap(optionsMap));

    final QuantificationTrainingOptions trainingOptions = new QuantificationTrainingOptions();
    trainingOptions.setNumBins(4);
    trainingOptions.setI113Proportion(113.0);
    trainingOptions.setI114Proportion(114.0);
    trainingOptions.setI115Proportion(115.0);
    trainingOptions.setI116Proportion(116.0);
    trainingOptions.setI117Proportion(117.0);
    trainingOptions.setI118Proportion(118.0);
    trainingOptions.setI119Proportion(119.0);
    trainingOptions.setI121Proportion(121.0);

    final ITraqQuantitationJobQueueContext jobContext = expectCreateJob(serviceUrl, ITraqQuantitationJobQueueContext.class);
    jobContext.submitTrainingJob(EasyMockUtils.arySame(mzxmlInputs), EasyMock.eq(dataReport), EasyMock.eq(type), EasyMock.eq(trainingOptions), expectCredentialResource());
    preRun(jobContext);

    final Activity activity = factory.getActivity(description, getContext());
    activity.run();
    postRun(description);
  }

}
