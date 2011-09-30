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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.jobqueue.JobProcessorFactory;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfiguration;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfigurationFactories;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.grid.credentials.Credentials;
import edu.umn.msi.tropix.proteomics.identification.IdentificationJobProcessor;
import edu.umn.msi.tropix.proteomics.identification.IdentificationJobProcessorBuilderImpl;

public class IdentificationJobProcessorBuilderImplTest {

  @Test(groups = "unit")
  public void buildJob() {
    final IdentificationJobProcessorBuilderImpl<Object, IdentificationJobProcessor<Object>> builder = new IdentificationJobProcessorBuilderImpl<Object, IdentificationJobProcessor<Object>>();
    final Object parameters = new Object();
    final InputContext mzxmlPopulator = createMock(InputContext.class);
    final InputContext databasePopulator = createMock(InputContext.class);
    @SuppressWarnings("unchecked")
    final IdentificationJobProcessor<Object> processor = EasyMock.createMock(IdentificationJobProcessor.class);
    processor.setDatabase(databasePopulator);
    processor.setInputMzXML(mzxmlPopulator);
    processor.setInputParameters(parameters);

    final JobProcessorConfiguration configuration = JobProcessorConfigurationFactories.getInstance().get(Credentials.getMock("moo"));
    @SuppressWarnings("unchecked")
    final JobProcessorFactory<ExecutableJobDescription> processorFactory = EasyMock.createMock(JobProcessorFactory.class);
    builder.setJobProcessorFactory(processorFactory);
    expect(processorFactory.create(configuration)).andReturn(processor);
    replay(processorFactory, processor);
    builder.buildJob(configuration, mzxmlPopulator, parameters, databasePopulator);
    verify(processorFactory, processor);
  }
}
