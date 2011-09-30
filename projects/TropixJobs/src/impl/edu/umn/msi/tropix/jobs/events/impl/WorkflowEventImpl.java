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

import edu.umn.msi.tropix.jobs.events.EventBase;
import edu.umn.msi.tropix.jobs.events.WorkflowEvent;

public class WorkflowEventImpl extends EventBaseImpl implements WorkflowEvent {
  public WorkflowEventImpl(final EventBase eventBase) {
    super(eventBase);
  }
  
  private String step;

  private StepStatus stepStatus;
  
  public String getStep() {
    return step;
  }

  public void setStep(final String step) {
    this.step = step;
  }

  public StepStatus getStepStatus() {
    return stepStatus;
  }

  public void setStepStatus(final StepStatus stepStatus) {
    this.stepStatus = stepStatus;
  }
  
  @Override
  protected String fieldsToString() {
    return super.fieldsToString() + ",step=" + step + ",stepStatus=" + stepStatus;
  }

}
