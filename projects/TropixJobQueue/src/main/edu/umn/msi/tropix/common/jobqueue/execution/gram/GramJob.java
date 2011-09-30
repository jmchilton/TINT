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

package edu.umn.msi.tropix.common.jobqueue.execution.gram;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.globus.exec.generated.StateEnumeration;
import org.oasis.wsrf.properties.ResourceUnknownFaultType;

import edu.umn.msi.tropix.grid.credentials.Credential;

/**
 * This interface along with the associated implementation and factory is thin wrapper around org.globus.exec.client.GramJob. The use of an interface and the abstract factory pattern eases testing. This wrapper also wraps checked exceptions in unchecked exceptions to match the
 * coding style used else where in Tropix.
 * 
 * @author John
 * 
 */
public interface GramJob {

  void cancel();

  StateEnumeration getState();

  void refreshStatus() throws GramJobNotFoundException, GramJobCredentialExpiredException;

  void setHandle(String handle);

  String getHandle();

  void setCredentials(Credential proxy);

  void setMessageProtectionType(Integer messageProtectionType);

  void setDelegationEnabled(boolean delegationEnable);

  void submit(EndpointReferenceType factoryEndpoint, boolean arg1, boolean arg2, String submissionId);

  public static class GramJobCredentialExpiredException extends RuntimeException {
    private static final long serialVersionUID = -8463772277358934375L;
  }

  public static class GramJobNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -6387196407792264921L;
    private final ResourceUnknownFaultType fault;

    public GramJobNotFoundException(final ResourceUnknownFaultType fault) {
      super(fault);
      this.fault = fault;
    }

    public ResourceUnknownFaultType getFault() {
      return fault;
    }
  }

}
