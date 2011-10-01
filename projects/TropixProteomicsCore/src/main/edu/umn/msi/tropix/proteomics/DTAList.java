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

package edu.umn.msi.tropix.proteomics;

/**
 * Abstraction around DTA files. Forcing DTA files to be accessed via iterator will allow us to not have them all loaded into memory at later point if need be.
 * 
 * @author John Chilton (chilton at msi dot umn dot edu)
 * 
 */
public interface DTAList extends Iterable<DTAList.Entry> {
  public interface Entry {
    /**
     * @return contents of the dta file represented by this entry
     */
    byte[] getContents();

    /**
     * @return filename of the dta file represented by this entry
     */
    String getName();
  }
}
