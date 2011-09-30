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

package edu.umn.msi.tropix.proteomics.client;

import java.io.InputStream;

import javax.annotation.ManagedBean;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.proteomics.enzyme.Enzyme;
import edu.umn.msi.tropix.proteomics.enzyme.EnzymeSet;
import edu.umn.msi.tropix.proteomics.enzyme.TermInfo;
import edu.umn.msi.tropix.proteomics.xml.EnzymeSetUtility;

@ManagedBean
public class EnzymeUtils {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private static final EnzymeSetUtility ENZYME_SET_UTILITY = new EnzymeSetUtility();
  private static final Function<Enzyme, String> ENZYME_NAME_FUNCTION = new Function<Enzyme, String>() {
    public String apply(final Enzyme enzyme) {
      return enzyme.getName();
    }
  };
  private final EnzymeSet enzymes;
  
  public EnzymeUtils() {
    final InputStream enzymesStream = EnzymeUtils.class.getResourceAsStream("enzymes.xml");
    try {
      this.enzymes = ENZYME_SET_UTILITY.deserialize(enzymesStream);
    } finally {
      IO_UTILS.closeQuietly(enzymesStream);
    }
  }

  public Iterable<String> getEnzymeNames() {
    return Iterables.transform(enzymes.getEnzymes(), ENZYME_NAME_FUNCTION);
  }

  public Enzyme getEnzyme(final String enzymeName) {
    Enzyme enzyme = null;
    for(final Enzyme curEnzyme : enzymes.getEnzymes()) {
      if(curEnzyme.getName().equals(enzymeName)) {
        enzyme = curEnzyme;
        break;
      }
    }
    return enzyme;
  }

  // Return "n" or "c"
  public String getTerm(final String enzymeName) {
    if(getEnzyme(enzymeName).getTerm().equals(TermInfo.CTERM)) {
      return "c";
    } else {
      return "n";
    }
  }

  public String getCleaveResidues(final String enzymeName) {
    return getEnzyme(enzymeName).getDoCleave();
  }

  public String getNotCleaveResidues(final String enzymeName) {
    return getEnzyme(enzymeName).getDoNotCleave();
  }

}
