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

package edu.umn.msi.tropix.proteomics.xtandem.impl;

import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseExecutableJobProcessorFactoryImpl;
import edu.umn.msi.tropix.proteomics.xtandem.XTandemParameterTranslator;

@JobType("XTandem")
public class XTandemJobProcessorFactoryImpl extends BaseExecutableJobProcessorFactoryImpl<XTandemJobProcessorImpl> {
  private XTandemParameterTranslator xTandemParameterTranslator;
  private String xslPath = null;

  protected XTandemJobProcessorImpl create() {
    final XTandemJobProcessorImpl jobProcessor = new XTandemJobProcessorImpl();
    jobProcessor.setXTandemParameterTranslator(xTandemParameterTranslator);
    jobProcessor.setXslPath(xslPath);
    return jobProcessor;
  }

  public void setParameterTranslator(final XTandemParameterTranslator tandemParameterTranslator) {
    xTandemParameterTranslator = tandemParameterTranslator;
  }

  public void setXslPath(final String xslPath) {
    this.xslPath = xslPath;
  }

}
