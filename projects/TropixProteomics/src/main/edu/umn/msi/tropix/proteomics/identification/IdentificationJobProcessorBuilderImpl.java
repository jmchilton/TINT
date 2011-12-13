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

package edu.umn.msi.tropix.proteomics.identification;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.jobqueue.FileJobProcessor;
import edu.umn.msi.tropix.common.jobqueue.JobProcessorFactory;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfiguration;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;

public class IdentificationJobProcessorBuilderImpl<T, U extends IdentificationJobProcessor<T>> implements IdentificationJobProcessorBuilder<T> {
  private JobProcessorFactory<ExecutableJobDescription> jobProcessorFactory;

  public FileJobProcessor<ExecutableJobDescription> buildJob(final JobProcessorConfiguration config, final InputContext mzxmlPopulator, final T parameters, final InputContext databasePopulator, final String databaseName) {
    @SuppressWarnings("unchecked")
    final U jobProcessor = (U) jobProcessorFactory.create(config);
    jobProcessor.setDatabase(databasePopulator);
    jobProcessor.setInputMzXML(mzxmlPopulator);
    jobProcessor.setInputParameters(parameters);
    jobProcessor.setDatabaseName(databaseName);
    return jobProcessor;
  }

  public void setJobProcessorFactory(final JobProcessorFactory<ExecutableJobDescription> jobProcessorFactory) {
    this.jobProcessorFactory = jobProcessorFactory;
  }
}
