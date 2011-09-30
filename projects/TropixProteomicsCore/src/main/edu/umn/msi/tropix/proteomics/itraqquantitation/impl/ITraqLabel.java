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

package edu.umn.msi.tropix.proteomics.itraqquantitation.impl;

interface ITraqLabel {
  /**
   * 
   * @return a human readable representation of this label.
   */
  String getLabel();
  
  /**
   * 
   * @param mtoz m/z value of peak
   * @return whether a peak with m/z of {@code mtoz} fits with 
   * this label.
   */
  boolean fitsPeak(double mtoz);
}
