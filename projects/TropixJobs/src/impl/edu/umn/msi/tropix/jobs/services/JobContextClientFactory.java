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

package edu.umn.msi.tropix.jobs.services;

import javax.annotation.Nullable;

import edu.umn.msi.tropix.grid.credentials.Credential;

public interface JobContextClientFactory {
  <T> T createJobContext(@Nullable Credential proxy, String serviceUrl, Class<T> interfaceClass);

  <T> T getJobClient(@Nullable Credential proxy, String serviceUrl, String ticket, Class<T> interfaceClass);

  <T> T getServiceClient(@Nullable Credential proxy, String serviceUrl, Class<T> interfaceClass);
}
