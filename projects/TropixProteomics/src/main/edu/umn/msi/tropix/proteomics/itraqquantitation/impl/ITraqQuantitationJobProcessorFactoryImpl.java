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

import java.io.File;
import java.util.LinkedList;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.StagingDirectory;
import edu.umn.msi.tropix.common.jobqueue.JobProcessor;
import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfiguration;
import edu.umn.msi.tropix.common.jobqueue.description.BaseInProcessJobDescriptionImpl;
import edu.umn.msi.tropix.common.jobqueue.description.InProcessJobDescription;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseJobProcessorFactoryImpl;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseJobProcessorImpl;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.DisposableResourceTracker;
import edu.umn.msi.tropix.proteomics.itraqquantitation.ITraqQuantitationJobProcessorFactory;
import edu.umn.msi.tropix.proteomics.itraqquantitation.QuantitationOptions;
import edu.umn.msi.tropix.proteomics.itraqquantitation.QuantitationTrainingOptions;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.ReportParser.ReportType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.options.QuantificationType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.training.QuantificationTrainingOptions;
import edu.umn.msi.tropix.proteomics.itraqquantitation.weight.QuantificationWeights;

@JobType("ITraqQuantitation")
public class ITraqQuantitationJobProcessorFactoryImpl extends
    BaseJobProcessorFactoryImpl<ITraqQuantitationJobProcessorFactoryImpl.ITraqQuantitationJobProcessorImpl> implements
    ITraqQuantitationJobProcessorFactory {
  private Closure<QuantitationOptions> quantitationClosure;
  private Closure<QuantitationTrainingOptions> quantitationTrainingClosure;

  public JobProcessor<InProcessJobDescription> createTraining(final JobProcessorConfiguration config, final Iterable<InputContext> mzxmlContexts,
      final InputContext dataContext, final QuantificationType quantificationType, final QuantificationTrainingOptions options) {
    final StagingDirectory stagingDirectory = getAndSetupStagingDirectory(config);
    final DisposableResourceTracker disposableResourceTracker = getDisposableResourceTrackerSupplier().get();
    return new ITraqQuantitationJobProcessorImpl(stagingDirectory, disposableResourceTracker, mzxmlContexts, dataContext, quantificationType, options);
  }

  public JobProcessor<InProcessJobDescription> create(final JobProcessorConfiguration config, final Iterable<InputContext> mzxmlContexts,
      final InputContext dataContext, final QuantificationType quantificationType, @Nullable final QuantificationWeights weights) {
    final StagingDirectory stagingDirectory = getAndSetupStagingDirectory(config);
    final DisposableResourceTracker disposableResourceTracker = getDisposableResourceTrackerSupplier().get();
    return new ITraqQuantitationJobProcessorImpl(stagingDirectory, disposableResourceTracker, mzxmlContexts, dataContext, quantificationType, weights);
  }

  class ITraqQuantitationJobProcessorImpl extends BaseJobProcessorImpl<InProcessJobDescription> {
    private final Iterable<InputContext> mzxmlContexts;
    private final InputContext dataContext;
    private final QuantificationType quantificationType;
    @Nullable
    private final QuantificationWeights weights;
    @Nullable
    private final QuantificationTrainingOptions trainingOptions;

    ITraqQuantitationJobProcessorImpl(final StagingDirectory stagingDirectory, final DisposableResourceTracker disposableResourceTracker,
        final Iterable<InputContext> mzxmlContexts, final InputContext dataContext, final QuantificationType quantificationType,
        @Nullable final QuantificationWeights weights) {
      this.setStagingDirectory(stagingDirectory);
      this.setDisposableResourceTracker(disposableResourceTracker);
      this.mzxmlContexts = mzxmlContexts;
      this.dataContext = dataContext;
      this.quantificationType = quantificationType;
      this.weights = weights;
      this.trainingOptions = null;
    }

    ITraqQuantitationJobProcessorImpl(final StagingDirectory stagingDirectory, final DisposableResourceTracker disposableResourceTracker,
        final Iterable<InputContext> mzxmlContexts, final InputContext dataContext, final QuantificationType quantificationType,
        final QuantificationTrainingOptions options) {
      this.setStagingDirectory(stagingDirectory);
      this.setDisposableResourceTracker(disposableResourceTracker);
      this.mzxmlContexts = mzxmlContexts;
      this.dataContext = dataContext;
      this.quantificationType = quantificationType;
      this.weights = null;
      this.trainingOptions = options;
    }

    @Override
    protected void doPreprocessing() {
      int i = 0;
      final LinkedList<File> inputMzxmlFiles = Lists.newLinkedList();
      final File baseDir = new File(getStagingDirectory().getAbsolutePath());
      for(final InputContext inputMzxmlContext : mzxmlContexts) {
        final String filename = "input" + i++ + ".mzxml";
        inputMzxmlContext.get(getStagingDirectory().getOutputContext(filename));
        inputMzxmlFiles.add(new File(baseDir, filename));
      }
      final String reportName = "dataReport.xls";
      dataContext.get(getStagingDirectory().getOutputContext(reportName));
      File inputScaffoldReport = new File(baseDir, reportName);
      File outputFile = new File(baseDir, "output.xml");
      if(trainingOptions != null) {
        final QuantitationTrainingOptions options = QuantitationTrainingOptions
            .forInput(inputMzxmlFiles, new InputReport(inputScaffoldReport, ReportType.SCAFFOLD)).withOutput(outputFile)
            .ofType(quantificationType).withTrainingOptions(trainingOptions).get();
        super.setJobDescription(new BaseInProcessJobDescriptionImpl("ITraqQuantitation") {
          public void execute() {
            quantitationTrainingClosure.apply(options);
          }
        });
      } else {
        final QuantitationOptions options = QuantitationOptions
            .forInput(inputMzxmlFiles, new InputReport(inputScaffoldReport, ReportType.SCAFFOLD)).withOutput(outputFile)
            .ofType(quantificationType).withWeights(weights).get();
        super.setJobDescription(new BaseInProcessJobDescriptionImpl("ITraqQuantitation") {
          public void execute() {
            quantitationClosure.apply(options);
          }
        });
      }
    }

    @Override
    protected void doPostprocessing() {
      this.getResourceTracker().add(getStagingDirectory().getInputContext("output.xml"));
    }

  }

  public void setQuantitationClosure(final Closure<QuantitationOptions> quantitationClosure) {
    this.quantitationClosure = quantitationClosure;
  }

  public void setQuantitationTrainingClosure(final Closure<QuantitationTrainingOptions> quantitationTrainingClosure) {
    this.quantitationTrainingClosure = quantitationTrainingClosure;
  }

}
