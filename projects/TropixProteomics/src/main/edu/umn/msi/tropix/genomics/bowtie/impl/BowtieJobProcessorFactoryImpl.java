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

package edu.umn.msi.tropix.genomics.bowtie.impl;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfiguration;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseExecutableJobProcessorFactoryImpl;
import edu.umn.msi.tropix.genomics.bowtie.BowtieJobProcessorFactory;
import edu.umn.msi.tropix.genomics.bowtie.input.BowtieInput;

@JobType("Bowtie")
public class BowtieJobProcessorFactoryImpl extends BaseExecutableJobProcessorFactoryImpl<BowtieJobProcessorImpl> implements BowtieJobProcessorFactory {

  protected BowtieJobProcessorImpl create() {
    return new BowtieJobProcessorImpl();
  }

  public BowtieJobProcessorImpl createBowtieJob(final JobProcessorConfiguration config, final BowtieInput bowtieInput, final InputContext indexContext, final Iterable<InputContext> databaseContexts) {
    final BowtieJobProcessorImpl processor = create(config);
    processor.setIndexInputContext(indexContext);
    processor.setDatabaseContexts(databaseContexts);
    processor.setInputParameters(bowtieInput);
    return processor;
  }

}
