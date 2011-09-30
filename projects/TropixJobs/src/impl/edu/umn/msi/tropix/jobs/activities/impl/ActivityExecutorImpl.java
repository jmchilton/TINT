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

package edu.umn.msi.tropix.jobs.activities.impl;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import edu.umn.msi.tropix.jobs.activities.ActivityContext;
import edu.umn.msi.tropix.jobs.activities.descriptions.ActivityDescription;

@ManagedBean
class ActivityExecutorImpl implements ActivityExecutor {
  private ActivityFactory<ActivityDescription> activityFactory;

  @Inject @Named("activityFactory")
  void setActivityFactory(final ActivityFactory<ActivityDescription> activityFactory) {
    this.activityFactory = activityFactory;
  }

  public void cancel(final ActivityDescription activityDescription, final ActivityContext activityContext) {
    final Activity activity = activityFactory.getActivity(activityDescription, activityContext);
    if(activity instanceof Cancellable) {
      ((Cancellable) activity).cancel();
    }
  }

  public void rollback(final ActivityDescription activityDescription, final ActivityContext activityContext) {
    final Activity activity = activityFactory.getActivity(activityDescription, activityContext);
    if(activity instanceof Revertable) {
      ((Revertable) activity).rollback();
    }
  }

  public void run(final ActivityDescription activityDescription, final ActivityContext activityContext) {
    final Activity activity = activityFactory.getActivity(activityDescription, activityContext);
    activity.run();
  }
}
