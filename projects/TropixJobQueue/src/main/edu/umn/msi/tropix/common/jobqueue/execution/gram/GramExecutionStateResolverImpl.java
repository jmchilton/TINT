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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.exec.generated.StateEnumeration;

import edu.umn.msi.tropix.common.jobqueue.execution.ExecutionState;
import edu.umn.msi.tropix.common.logging.ExceptionUtils;

public class GramExecutionStateResolverImpl implements GramExecutionStateResolver {
  private static final Log LOG = LogFactory.getLog(GramExecutionStateResolverImpl.class);
  private static final Map<StateEnumeration, ExecutionState> STATE_CONVERSION_MAP = new HashMap<StateEnumeration, ExecutionState>();
  static {
    STATE_CONVERSION_MAP.put(StateEnumeration.Active, ExecutionState.RUNNING);
    STATE_CONVERSION_MAP.put(StateEnumeration.CleanUp, ExecutionState.RUNNING);
    STATE_CONVERSION_MAP.put(StateEnumeration.Done, ExecutionState.COMPLETE);
    STATE_CONVERSION_MAP.put(StateEnumeration.Failed, ExecutionState.FAILED);
    STATE_CONVERSION_MAP.put(StateEnumeration.Pending, ExecutionState.PENDING);
    STATE_CONVERSION_MAP.put(StateEnumeration.StageIn, ExecutionState.PENDING);
    STATE_CONVERSION_MAP.put(StateEnumeration.StageOut, ExecutionState.RUNNING);
    STATE_CONVERSION_MAP.put(StateEnumeration.Suspended, ExecutionState.RUNNING);
    STATE_CONVERSION_MAP.put(StateEnumeration.Unsubmitted, ExecutionState.PENDING);
  }
  private GramJobResolver gramJobResolver;

  public ExecutionState getState(final GramExecutionJob gramExecutionJob) {
    final GramJob job = gramJobResolver.getGramJob(gramExecutionJob);
    try {
      job.refreshStatus();
    } catch(final GramJob.GramJobNotFoundException e) {
      ExceptionUtils.logQuietly(LOG, e, "Gram job not found");
      return ExecutionState.ABSENT;
    } catch(final GramJob.GramJobCredentialExpiredException e) {
      ExceptionUtils.logQuietly(LOG, e, "Gram job credential expired");
      return ExecutionState.ABSENT;
    } catch(final Exception e) {
      throw ExceptionUtils.convertException(e, "Failed to refresh status of gram job.");
    }
    StateEnumeration enumeration = job.getState();
    LOG.debug("State is " + enumeration);
    if(enumeration == null) {
      enumeration = StateEnumeration.Unsubmitted;
    }
    return STATE_CONVERSION_MAP.get(enumeration);
  }

  public void setGramJobResolver(final GramJobResolver gramJobResolver) {
    this.gramJobResolver = gramJobResolver;
  }

}
