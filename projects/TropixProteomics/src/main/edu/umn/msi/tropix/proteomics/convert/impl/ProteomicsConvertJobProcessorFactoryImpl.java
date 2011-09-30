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

package edu.umn.msi.tropix.proteomics.convert.impl;

import java.io.File;
import java.io.OutputStream;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.jobqueue.JobProcessor;
import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfiguration;
import edu.umn.msi.tropix.common.jobqueue.description.BaseInProcessJobDescriptionImpl;
import edu.umn.msi.tropix.common.jobqueue.description.InProcessJobDescription;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseJobProcessorFactoryImpl;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseJobProcessorImpl;
import edu.umn.msi.tropix.proteomics.conversion.MgfToMzxmlConverter;
import edu.umn.msi.tropix.proteomics.conversion.impl.ConversionUtils;
import edu.umn.msi.tropix.proteomics.convert.ProteomicsConvertJobFactory;
import edu.umn.msi.tropix.proteomics.convert.input.ConvertParameters;

@JobType("ProteomicsConvert")
class ProteomicsConvertJobProcessorFactoryImpl extends BaseJobProcessorFactoryImpl<ProteomicsConvertJobProcessorFactoryImpl.ProteomicsConvertJobProcessorImpl>
                                      implements ProteomicsConvertJobFactory {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  
  private final MgfToMzxmlConverter mgfToMzxmlConverter;
  
  ProteomicsConvertJobProcessorFactoryImpl(final MgfToMzxmlConverter mgfToMzxmlConverter) {
    this.mgfToMzxmlConverter = mgfToMzxmlConverter;
  }

  public JobProcessor<InProcessJobDescription> create(final JobProcessorConfiguration config, 
                                                      final InputContext sourceContext,
                                                      final ConvertParameters convertParameters) {
    final ProteomicsConvertJobProcessorImpl processor = new ProteomicsConvertJobProcessorImpl(sourceContext, convertParameters);
    processor.setStagingDirectory(getAndSetupStagingDirectory(config));
    initializeDisposableResourceTracker(processor);
    return processor;
  }

  class ProteomicsConvertJobProcessorImpl extends BaseJobProcessorImpl<InProcessJobDescription> {    
    private final InputContext sourceContext;
    private final ConvertParameters convertParameters;
    
    ProteomicsConvertJobProcessorImpl(final InputContext sourceContext, final ConvertParameters convertParameters) {
      this.sourceContext = sourceContext;
      this.convertParameters = convertParameters;
    }

    @Override
    protected void doPreprocessing() {
      final String sanitizedInput = ConversionUtils.getSanitizedName(convertParameters.getInputName(), ".mgf");
      sourceContext.get(super.getStagingDirectory().getOutputContext(sanitizedInput));
      setJobDescription(new BaseInProcessJobDescriptionImpl("ProteomicsConvert") {

        public void execute() {          
          final File stagingDirectoryFile = new File(getStagingDirectory().getAbsolutePath());
          final File sanitizedFile = new File(stagingDirectoryFile, sanitizedInput);
          final File mzxmlFile = new File(stagingDirectoryFile, "output");
          final OutputStream mzxmlStream = FILE_UTILS.getFileOutputStream(mzxmlFile);
          try {
            mgfToMzxmlConverter.mgfToMzXmxl(sanitizedFile, mzxmlStream);
          } finally {
            IO_UTILS.closeQuietly(mzxmlStream);
          }          
        }
      });
    }
    
    @Override
    protected void doPostprocessing() {
      getResourceTracker().add(getStagingDirectory().getInputContext("output"));
    }
    
  }
  
}
