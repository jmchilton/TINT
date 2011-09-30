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

/**
 * 
 * @author John Chilton
 * 
 * @param <T>
 *          type of object which describes job to execute.
 */
public interface JobProcessor<T extends JobDescription> {

  /**
   * Precondition: preprocess has not previously been called.
   * 
   * @return A full description of the job to execute.
   */
  T preprocess();

  /**
   * Precondition: preprocess has been called, and the job described by the JobDescriptionType object it returned has been executed.
   * 
   * @param completedNormally
   *          Indicates whether the job completed normally, if false some error occurred, either in Java or the job.
   */
  void postprocess(boolean completedNormally);

}
