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

package edu.umn.msi.tropix.proteomics.itraqquantitation.impl;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.jobqueue.JobProcessor;
import edu.umn.msi.tropix.common.jobqueue.description.InProcessJobDescription;
import edu.umn.msi.tropix.common.jobqueue.test.JobProcessorFactoryTest;
import edu.umn.msi.tropix.proteomics.itraqquantitation.QuantitationOptions;
import edu.umn.msi.tropix.proteomics.itraqquantitation.QuantitationTrainingOptions;
import edu.umn.msi.tropix.proteomics.itraqquantitation.options.QuantificationType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.training.QuantificationTrainingOptions;
import edu.umn.msi.tropix.proteomics.itraqquantitation.weight.QuantificationWeights;

public class ITraqQuantitationJobProcessorFactoryImplTest extends JobProcessorFactoryTest<ITraqQuantitationJobProcessorFactoryImpl> {
  private Iterable<InputContext> mzxmlContexts;
  private InputContext dataContext;
  private JobProcessor<InProcessJobDescription> processor;

  @BeforeMethod(groups = "unit")
  public void init() {
    setFactory(new ITraqQuantitationJobProcessorFactoryImpl());
    super.init();

    mzxmlContexts = getDownloadContexts(3);
    dataContext = getDownloadContext();

    int i = 0;
    for(InputContext mzxmlContext : mzxmlContexts) {
      mzxmlContext.get(getDirMockOutputContext("input" + i++ + ".mzxml"));
    }
    dataContext.get(getDirMockOutputContext("dataReport.xls"));

    getStagingDirectory().setup();
    replay();
  }

  private static class MockClosure<T> implements Closure<T> {
    private T input;

    public void apply(final T input) {
      this.input = input;
    }

  }

  @Test(groups = "unit")
  public void testQuantification() {
    final MockClosure<QuantitationOptions> quantitationClosure = new MockClosure<QuantitationOptions>();
    getFactory().setQuantitationClosure(quantitationClosure);
    final QuantificationWeights weights = new QuantificationWeights();

    processor = getFactory().create(getJobProcessorConfiguration(), mzxmlContexts, dataContext, QuantificationType.FOUR_PLEX, weights);
    final InProcessJobDescription description = processor.preprocess();
    description.execute();

    verifyAndReset();

    final QuantitationOptions options = quantitationClosure.input;
    assert options != null;
    assert options.getWeights() == weights;
    assert options.getQuantificationType() == QuantificationType.FOUR_PLEX;

    checkPostprocessAndGetResults();
  }

  @Test(groups = "unit")
  public void testTraining() {
    final MockClosure<QuantitationTrainingOptions> quantitationTrainingClosure = new MockClosure<QuantitationTrainingOptions>();
    getFactory().setQuantitationTrainingClosure(quantitationTrainingClosure);

    final QuantificationTrainingOptions inputOptions = new QuantificationTrainingOptions();
    processor = getFactory().createTraining(getJobProcessorConfiguration(), mzxmlContexts, dataContext, QuantificationType.EIGHT_PLEX, inputOptions);
    final InProcessJobDescription description = processor.preprocess();
    description.execute();

    verifyAndReset();
    final QuantitationTrainingOptions options = quantitationTrainingClosure.input;

    assert options != null;
    assert options.getTrainingOptions() == inputOptions;
    assert options.getQuantificationType() == QuantificationType.EIGHT_PLEX;

    checkPostprocessAndGetResults();
  }

  public void checkPostprocessAndGetResults() {
    getStagingDirectory().cleanUp();
    expectAddResource("output.xml");
    replay();
    processor.postprocess(true);
    verifyAndReset();
  }

}
