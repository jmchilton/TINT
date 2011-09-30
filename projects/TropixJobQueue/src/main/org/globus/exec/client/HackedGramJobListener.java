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

package org.globus.exec.client;

/**
 * This interface is used to allow client-side objects to
 * listen for status changes of GramJob objects.
 *
 */
public interface HackedGramJobListener {

  /**
   * This method is used to notify the implementer when the status of a
   * GramJob has changed.
   *
   * @param job The GramJob whose status has changed.
   */
  void stateChanged(HackedGramJob job);

}
