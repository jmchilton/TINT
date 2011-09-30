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

package edu.umn.msi.tropix.proteomics.omssa.impl;

import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseExecutableJobProcessorFactoryImpl;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter;

@JobType("Omssa")
public class OmssaJobProcessorFactoryImpl extends BaseExecutableJobProcessorFactoryImpl<OmssaJobProcessorImpl> {
  private MzXMLToMGFConverter mzxmlToMGFConverter;
  private boolean zipOutput = true;

  protected OmssaJobProcessorImpl create() {
    final OmssaJobProcessorImpl jobProcessor = new OmssaJobProcessorImpl();
    jobProcessor.setMzxmlToMGFConverter(mzxmlToMGFConverter);
    jobProcessor.setZipOutput(zipOutput);
    return jobProcessor;
  }

  public void setMxXMLToMGFConverter(final MzXMLToMGFConverter mzxmlToMGFConverter) {
    this.mzxmlToMGFConverter = mzxmlToMGFConverter;
  }

  public void setZipOutput(final boolean zipOutput) {
    this.zipOutput = zipOutput;
  }
}
