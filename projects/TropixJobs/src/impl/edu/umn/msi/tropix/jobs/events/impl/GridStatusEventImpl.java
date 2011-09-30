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

package edu.umn.msi.tropix.jobs.events.impl;

import edu.umn.msi.tropix.common.jobqueue.status.Status;
import edu.umn.msi.tropix.common.jobqueue.status.StatusUtils;
import edu.umn.msi.tropix.jobs.events.EventBase;
import edu.umn.msi.tropix.jobs.events.GridStatusEvent;

class GridStatusEventImpl extends EventBaseImpl implements GridStatusEvent {
  private final Status status;

  public GridStatusEventImpl(final EventBase other, final Status status) {
    super(other);
    this.status = status;
  }

  public Status getStatus() {
    return status;
  }

  @Override
  protected String fieldsToString() {
    return super.fieldsToString() + ",status=" + (status != null ? StatusUtils.toString(status) : "null");
  }

}
