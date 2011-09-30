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

package edu.umn.msi.tropix.jobs.events;

public interface WorkflowEvent extends EventBase {
  
  String getWorkflowId();
  
  String getStep();
  
  interface StepStatus {}
  
  final class StepPercentStatus implements StepStatus {
    private final float statusPercent;
    
    public static StepPercentStatus get(final float statusPercent) {
      return new StepPercentStatus(statusPercent);
    }
    
    private StepPercentStatus(final float statusPercent) {
      this.statusPercent = statusPercent;
    }
    
    public float getStatusPercent() {
      return statusPercent;
    }
    
    public String toString() {
      return "StepPercentStatus[percentComplete=" + statusPercent + "]";
    }
  }

  final class StepUpdateStatus implements StepStatus {
    private final String statusUpdate;
    
    public static StepUpdateStatus get(final String statusUpdate) {
      return new StepUpdateStatus(statusUpdate);
    }
    
    private StepUpdateStatus(final  String statusUpdate) {
      this.statusUpdate = statusUpdate;
    }
    
    public String getStatusUpdate() {
      return statusUpdate;
    }
    
    public String toString() {
      return "StepUpdateStatus[update=" + statusUpdate + "]";
    }
  }  
    
}
