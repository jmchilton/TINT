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

package edu.umn.msi.tropix.proteomics.service.impl;


import com.google.common.base.Function;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.jobqueue.description.ExecutableJobDescription;
import edu.umn.msi.tropix.common.jobqueue.service.impl.GridFileJobQueueContextImpl;
import edu.umn.msi.tropix.common.logging.LogExceptions;
import edu.umn.msi.tropix.credential.types.CredentialResource;
import edu.umn.msi.tropix.grid.credentials.Credential;
import edu.umn.msi.tropix.proteomics.identification.IdentificationJobProcessorBuilder;
import edu.umn.msi.tropix.proteomics.service.IdentificationJobQueueContext;
import edu.umn.msi.tropix.transfer.types.TransferResource;

public class IdentificationJobQueueContextImpl<T, U> extends GridFileJobQueueContextImpl<ExecutableJobDescription> implements IdentificationJobQueueContext<T> {
  private IdentificationJobProcessorBuilder<U> identificationJobProcessorBuilder = null;
  private final Function<T, U> xmlConversionFunction;

  protected IdentificationJobQueueContextImpl(final Function<T, U> xmlConversionFunction) {
    this.xmlConversionFunction = xmlConversionFunction;
  }

  @LogExceptions
  public void submitJob(final TransferResource mzxml, final TransferResource database, final CredentialResource dcReference, final T parameters) {
    try {
      final Credential proxy = getProxy(dcReference);
      final InputContext databasePopulator = getDownloadContext(database, proxy);
      final InputContext mzxmlPopulator = getDownloadContext(mzxml, proxy);
      final U actualParameters = xmlConversionFunction.apply(parameters);
      submitJob(identificationJobProcessorBuilder.buildJob(getConfiguration(proxy), mzxmlPopulator, actualParameters, databasePopulator));
    } catch(RuntimeException e) {
      e.printStackTrace();
      throw e;
    }
  }

  public void setIdentificationJobProcessorBuilder(final IdentificationJobProcessorBuilder<U> identificationJobProcessorBuilder) {
    this.identificationJobProcessorBuilder = identificationJobProcessorBuilder;
  }

}
