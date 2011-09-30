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

package edu.umn.msi.tropix.common.jobqueue;

import javax.annotation.Nullable;

/**
 * Implementation of JobProcessorRecoverer that always returns null for jobs that cannot be recovered but were persisted for some reason.
 * 
 * @author John Chilton
 * 
 * @param <T>
 *          type of {@code JobDescription} this class "recovers" {@code JobProcessor}s for.
 */
public final class NullJobProcessorRecovererImpl<T extends JobDescription> implements JobProcessorRecoverer<T> {

  @Nullable
  public JobProcessor<T> recover(final T jobDescription) {
    return null;
  }

}
