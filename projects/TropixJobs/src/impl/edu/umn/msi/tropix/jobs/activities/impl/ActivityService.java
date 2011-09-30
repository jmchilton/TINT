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

import java.util.Collection;

import edu.umn.msi.tropix.jobs.activities.ActivityContext;

interface ActivityService {
  void save(ActivityContext activityContext);  
  
  /**
   * 
   * @return ActivityContexts with at least one incomplete ActivityDescription.
   */
  Collection<ActivityContext> loadActivityContexts();
}
