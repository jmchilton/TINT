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

package edu.umn.msi.tropix.proteomics.sequest.impl;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseExecutableJobProcessorFactoryImpl;
import edu.umn.msi.tropix.common.jobqueue.progress.LineProcessingFileProgressTracker;
import edu.umn.msi.tropix.common.jobqueue.progress.LineProcessingFileProgressTrackers;
import edu.umn.msi.tropix.proteomics.DTAListWriter;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToDTAConverter;
import edu.umn.msi.tropix.proteomics.sequest.SequestLineCallback;
import edu.umn.msi.tropix.proteomics.sequest.SequestParameterTranslator;

@JobType("Sequest")
@ManagedResource
public class SequestJobProcessorFactoryImpl extends BaseExecutableJobProcessorFactoryImpl<SequestJobProcessorImpl> {
  private SequestParameterTranslator parameterTranslator;
  private DTAListWriter dtaListWriter;
  private MzXMLToDTAConverter mzxmlToDtaConverter;
  private boolean includeParams = true;
  private boolean includeDta = true;
  private long allowedDroppedFiles = 0;
  private Supplier<LineProcessingFileProgressTracker> progressTrackerSupplier = LineProcessingFileProgressTrackers.getDefaultSupplier();
  private Supplier<SequestLineCallback> lineCallbackSupplier = new SequestLineCallbackSupplierImpl();

  @Override
  protected SequestJobProcessorImpl create() {
    final SequestJobProcessorImpl processor = new SequestJobProcessorImpl();
    processor.setDTAListWriter(dtaListWriter);
    processor.setMzXMLToDtaConverter(mzxmlToDtaConverter);
    processor.setParameterTranslator(parameterTranslator);
    processor.setIncludeParams(includeParams);
    processor.setincludeDta(includeDta);
    processor.setAllowedDroppedFiles(allowedDroppedFiles);
    processor.setProgressTracker(progressTrackerSupplier.get());
    processor.setSequestLineCallback(lineCallbackSupplier.get());
    return processor;
  }

  public void setSequestParameterTranslator(final SequestParameterTranslator sequestParameterTranslator) {
    this.parameterTranslator = sequestParameterTranslator;
  }

  public void setDtaListWriter(final DTAListWriter dtaListWriter) {
    this.dtaListWriter = dtaListWriter;
  }

  public void setMzXMLToDtaConverter(final MzXMLToDTAConverter mzxmlToDtaConverter) {
    this.mzxmlToDtaConverter = mzxmlToDtaConverter;
  }

  @ManagedOperation
  public void setIncludeParams(final boolean includeParams) {
    this.includeParams = includeParams;
  }

  @ManagedOperation
  public boolean isIncludeParams() {
    return includeParams;
  }

  @ManagedOperation
  public void setIncludeDta(final boolean includeDta) {
    this.includeDta = includeDta;
  }

  @ManagedOperation
  public boolean isIncludeDta() {
    return includeDta;
  }

  public void setProgressTrackerSupplier(final Supplier<LineProcessingFileProgressTracker> progressTrackerSupplier) {
    this.progressTrackerSupplier = progressTrackerSupplier;
  }

  public void setLineCallbackSupplier(final Supplier<SequestLineCallback> lineCallbackSupplier) {
    this.lineCallbackSupplier = lineCallbackSupplier;
  }

  @ManagedOperation
  public long getAllowedDroppedFiles() {
    return allowedDroppedFiles;
  }

  @ManagedOperation
  public void setAllowedDroppedFiles(final long allowedDroppedFiles) {
    this.allowedDroppedFiles = allowedDroppedFiles;
  }
}