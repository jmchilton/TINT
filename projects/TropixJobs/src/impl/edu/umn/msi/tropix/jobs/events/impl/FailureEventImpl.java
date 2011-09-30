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

import edu.umn.msi.tropix.common.logging.ExceptionUtils;
import edu.umn.msi.tropix.jobs.events.EventBase;
import edu.umn.msi.tropix.jobs.events.FailureEvent;

public class FailureEventImpl extends EventBaseImpl implements FailureEvent {
  private String reason;
  private String stackTrace;
  private Throwable throwable;

  public FailureEventImpl() {
    super();
  }

  public FailureEventImpl(final EventBase other) {
    super(other);
  }

  public String getReason() {
    return reason;
  }

  public void setReason(final String reason) {
    this.reason = reason;
  }

  @Override
  protected String fieldsToString() {
    return super.fieldsToString() + ",reason=" + reason + ",stackTrace=" + ExceptionUtils.toString(throwable);
  }

  public String getStackTrace() {
    return stackTrace;
  }

  public void setStackTrace(final String stackTrace) {
    this.stackTrace = stackTrace;
  }

  public Throwable getThrowable() {
    return throwable;
  }

  public void setThrowable(final Throwable throwable) {
    this.throwable = throwable;
  }

}
