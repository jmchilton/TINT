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

package edu.umn.msi.tropix.proteomics.rawextract.impl;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfiguration;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseExecutableJobProcessorFactoryImpl;
import edu.umn.msi.tropix.proteomics.conversion.DTAToMzXMLConverter;
import edu.umn.msi.tropix.proteomics.conversion.DTAToMzXMLOptions;
import edu.umn.msi.tropix.proteomics.rawextract.RawExtractJobBuilder;

@JobType("RawExtract")
public class RawExtractJobFactoryImpl extends BaseExecutableJobProcessorFactoryImpl<RawExtractJobProcessorImpl> implements RawExtractJobBuilder {
  private DTAToMzXMLConverter dtaToMzXMLConverter;
  private DTAToMzXMLOptions dtaToMxXMLOptions = null;
  private boolean producesMzxml = false;

  protected RawExtractJobProcessorImpl create() {
    final RawExtractJobProcessorImpl job = new RawExtractJobProcessorImpl();
    job.setDtaToMxXMLOptions(dtaToMxXMLOptions);
    job.setDtaToMzXMLConverter(dtaToMzXMLConverter);
    return job;
  }

  public RawExtractJobProcessorImpl buildJob(final JobProcessorConfiguration config, final InputContext rawFilePopulator, final String rawExtractParameters, final String rawExtractBaseName) {
    final RawExtractJobProcessorImpl job = create(config);
    job.setRawExtractParameters(rawExtractParameters);
    job.setRawFilePopulator(rawFilePopulator);
    job.setRawFileBaseName(rawExtractBaseName);
    job.setProducesMzxml(producesMzxml);
    return job;
  }

  public void setDtaToMxXMLOptions(final DTAToMzXMLOptions dtaToMxXMLOptions) {
    this.dtaToMxXMLOptions = dtaToMxXMLOptions;
  }

  public void setDtaToMzXMLConverter(final DTAToMzXMLConverter dtaToMzXMLConverter) {
    this.dtaToMzXMLConverter = dtaToMzXMLConverter;
  }

  public void setProducesMzxml(final boolean producesMzxml) {
    this.producesMzxml = producesMzxml;
  }
}
