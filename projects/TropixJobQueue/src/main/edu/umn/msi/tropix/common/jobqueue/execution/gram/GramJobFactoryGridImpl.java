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
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.exec.generated.JobDescriptionType;
import org.globus.exec.generated.StateEnumeration;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.globus.wsrf.impl.security.authorization.NoAuthorization;
import org.globus.wsrf.security.Constants;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.oasis.wsrf.properties.ResourceUnknownFaultType;

import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.grid.credentials.Credential;

public class GramJobFactoryGridImpl implements GramJobFactory {
  private static final Log LOG = LogFactory.getLog(GramJobFactoryGridImpl.class);  
  private Integer messageProtectionTypeOverride = null;
  private Integer transportProtectionTypeOverride = null;

  public GramJob getGramJob(final JobDescriptionType jobDescription) {
    return getGramJob(new org.globus.exec.client.HackedGramJob(jobDescription));
  }

  public GramJob getGramJob() {
    return getGramJob(new org.globus.exec.client.HackedGramJob());
  }

  public GramJob getGramJob(final org.globus.exec.client.HackedGramJob wrappedGramJob) {
    return new GramJobImpl(wrappedGramJob);
  }  
  
  private final class GramJobImpl implements GramJob {
    private final org.globus.exec.client.HackedGramJob wrappedGramJob;

    private GramJobImpl(final org.globus.exec.client.HackedGramJob wrappedGramJob) {
      this.wrappedGramJob = wrappedGramJob;
      
      wrappedGramJob.setMessageProtectionTypeOverride(messageProtectionTypeOverride);
      wrappedGramJob.setTransportProtectionTypeOverride(transportProtectionTypeOverride);      
      wrappedGramJob.setAuthorization(NoAuthorization.getInstance());
    }

    public void cancel() {
      try {
        wrappedGramJob.cancel();
      } catch(final Exception e) {
        ExceptionUtils.convertException(e);
      }
    }

    public StateEnumeration getState() {
      final StateEnumeration state = wrappedGramJob.getState();
      LOG.trace("getState called -- state is " + state);
      return state;
    }

    public void refreshStatus() {
      try {
        wrappedGramJob.refreshStatus();
      } catch(final ResourceUnknownFaultType fault) {
        throw new GramJob.GramJobNotFoundException(fault);
      } catch(final Exception e) {
        // Check to see if credential is expired
        final GSSCredential credential = wrappedGramJob.getCredentials();
        try {
          if(credential != null && credential.getRemainingLifetime() == 0) {
            throw new GramJob.GramJobCredentialExpiredException();
          }
        } catch(final GSSException gssException) {
          ExceptionUtils.logQuietly(LOG, gssException);
        }        
        throw ExceptionUtils.convertException(e);
      }
    }

    public void setDelegationEnabled(final boolean delegationEnabled) {
      wrappedGramJob.setDelegationEnabled(delegationEnabled);
    }

    public void setHandle(final String handle) {
      try {
        wrappedGramJob.setHandle(handle);
      } catch(final Exception e) {
        throw ExceptionUtils.convertException(e);
      }
    }

    public void submit(final EndpointReferenceType arg0, final boolean arg1, final boolean arg2, final String arg3) {
      try {
        wrappedGramJob.submit(arg0, arg1, arg2, arg3);
      } catch(final Exception e) {
        throw ExceptionUtils.convertException(e);
      }
    }

    public void setCredentials(final Credential proxy) {
      GSSCredential gssProxy;
      try {
        gssProxy = new GlobusGSSCredentialImpl(proxy.getGlobusCredential(), GSSCredential.INITIATE_AND_ACCEPT);
      } catch(final GSSException e) {
        throw ExceptionUtils.convertException(e);
      }
      wrappedGramJob.setCredentials(gssProxy);
    }

    public void setMessageProtectionType(final Integer messageProtectionType) {
      wrappedGramJob.setMessageProtectionType(messageProtectionType);
    }


    public String getHandle() {
      return wrappedGramJob.getHandle();
    }

  }
  

  public void setMessageProtectionTypeOverride(final Integer messageProtectionTypeOverride) {
    this.messageProtectionTypeOverride = messageProtectionTypeOverride;
  }

  public void setTransportProtectionTypeOverride(final Integer transportProtectionTypeOverride) {
    this.transportProtectionTypeOverride = transportProtectionTypeOverride;
  }
  
  public void setTargetGramDotNet(final boolean targetGramDotNet) {
    if(targetGramDotNet) {
      this.setMessageProtectionTypeOverride(Constants.SIGNATURE);
      this.setTransportProtectionTypeOverride(Constants.ENCRYPTION);
    }
  } 
    
}
