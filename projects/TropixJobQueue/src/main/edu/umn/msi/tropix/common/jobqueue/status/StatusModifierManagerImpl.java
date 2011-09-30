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

package edu.umn.msi.tropix.common.jobqueue.status;

import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.MapMaker;

import edu.umn.msi.tropix.common.jobqueue.impl.StatusModifier;

// TODO: Make this a multimap somehow once there is more than one 
public class StatusModifierManagerImpl implements StatusModifierManager {
  private final ConcurrentMap<String, StatusModifier> modifierMap = new MapMaker().makeMap();

  public void registerStatusModifier(final String jobId, final StatusModifier statusModifier) {
    modifierMap.putIfAbsent(jobId, statusModifier);
  }

  public void unregisterStatusModifier(final String jobId, final StatusModifier statusModifier) {
    modifierMap.remove(jobId);
  }

  public void extendStatus(final String localJobId, final Status status) {
    final StatusModifier statusModifier = modifierMap.get(localJobId);
    if(statusModifier != null) {
      statusModifier.extendStatus(localJobId, status);
    }
  }

}
