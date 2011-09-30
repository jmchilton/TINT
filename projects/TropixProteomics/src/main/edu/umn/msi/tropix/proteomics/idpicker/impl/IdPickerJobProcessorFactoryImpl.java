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

package edu.umn.msi.tropix.proteomics.idpicker.impl;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.jobqueue.FileJobProcessor;
import edu.umn.msi.tropix.common.jobqueue.JobType;
import edu.umn.msi.tropix.common.jobqueue.configuration.JobProcessorConfiguration;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseExecutableJobProcessorFactoryImpl;
import edu.umn.msi.tropix.proteomics.idpicker.IdPickerJobProcessorFactory;
import edu.umn.msi.tropix.proteomics.idpicker.parameters.IdPickerParameters;

@JobType("IdPicker")
public class IdPickerJobProcessorFactoryImpl extends BaseExecutableJobProcessorFactoryImpl<IdPickerJobProcessorImpl> implements
    IdPickerJobProcessorFactory {

  protected IdPickerJobProcessorImpl create() {
    return new IdPickerJobProcessorImpl();
  }

  public FileJobProcessor<ExecutableJobDescription> create(final JobProcessorConfiguration configuration, final IdPickerParameters idPickerParameters,
      final InputContext databaseContext, final Iterable<InputContext> pepXmlContexts) {
    final IdPickerJobProcessorImpl processor = super.create(configuration);
    processor.setParameters(idPickerParameters);
    processor.setPepXmlContexts(pepXmlContexts);
    processor.setDatabaseContext(databaseContext);
    return processor;
  }

}
