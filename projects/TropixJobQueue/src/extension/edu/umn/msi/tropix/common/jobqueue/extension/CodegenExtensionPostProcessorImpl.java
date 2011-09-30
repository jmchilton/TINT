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

package edu.umn.msi.tropix.common.jobqueue.extension;

import gov.nih.nci.cagrid.common.Utils;
import gov.nih.nci.cagrid.introduce.beans.extension.ServiceExtensionDescriptionType;
import gov.nih.nci.cagrid.introduce.beans.service.ServiceType;
import gov.nih.nci.cagrid.introduce.common.CommonTools;
import gov.nih.nci.cagrid.introduce.common.ServiceInformation;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionException;
import gov.nih.nci.cagrid.introduce.extension.CodegenExtensionPostProcessor;

import java.io.File;
import java.io.IOException;

public class CodegenExtensionPostProcessorImpl implements CodegenExtensionPostProcessor {

  public void postCodegen(final ServiceExtensionDescriptionType type, final ServiceInformation serviceInformation) throws CodegenExtensionException {
    final ServiceType service = serviceInformation.getServices().getService(0);
    final File directory = new File(serviceInformation.getBaseDirectory(), "src" + File.separator + CommonTools.getPackageDir(service) + File.separator);
    final File serviceDirecotry = new File(directory, "service");
    final File serviceImplFile = new File(serviceDirecotry, service.getName() + "Impl.java");
    try {
      final StringBuffer fileBuffer = Utils.fileToStringBuffer(serviceImplFile);
      final MethodBody createBody = new MethodBody(fileBuffer, " createJob()");
      createBody.setContents("    return JobContextFactory.createJob();" + System.getProperty("line.separator"));
      final MethodBody getBody = new MethodBody(fileBuffer, "getJob(edu.umn.msi.tropix.common.jobqueue.ticket.Ticket ticket)");
      getBody.setContents("    return JobContextFactory.getJob(ticket);" + System.getProperty("line.separator"));
      Utils.stringBufferToFile(fileBuffer, serviceImplFile.getAbsolutePath());
    } catch(final IOException e) {
      e.printStackTrace();
      throw new CodegenExtensionException(e);
    }
  }

}
