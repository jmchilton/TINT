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

package edu.umn.msi.tropix.proteomics.test;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.proteomics.client.EnzymeUtils;
import edu.umn.msi.tropix.proteomics.enzyme.Enzyme;

public class EnzymeTest {
  private EnzymeUtils enzymeUtils;

  @BeforeClass(groups = {"unit"})
  public void init() throws Exception {
    enzymeUtils = new EnzymeUtils();    
  }

  @Test(groups = {"unit"})
  public void testGetName() {
    assert enzymeUtils.getEnzyme("Trypsin").getName().equals("Trypsin") : "First enzyme not named Trypsin";
  }

  @Test(groups = {"unit"})
  public void testEnzymeUtils()  {
    final String enzymeName = "Chymotrypsin";
    final Enzyme chymotrypsin = enzymeUtils.getEnzyme(enzymeName);
    assert chymotrypsin != null : enzymeName + " not found.";
    assert enzymeUtils.getTerm(enzymeName).equals("c");
    assert enzymeUtils.getCleaveResidues(enzymeName).equals("FMWY");
    assert enzymeUtils.getNotCleaveResidues(enzymeName).equals("P");
    assert enzymeUtils.getEnzyme(enzymeName).getPepXmlName().equals("chymotrypsin");
  }
  
  /*
  @Test(groups = {"unit"})
  public void testGetTerm() {
    final String[] nterms = new String[] {"Asp-N", "Asp-N_ambic"};
    final List<String> ntermsList = Arrays.asList(nterms);

    for(final Enzyme enzyme : enzymes.getEnzymes()) {
      if(ntermsList.contains(enzyme.getName())) {
        assert enzyme.getTerm().equals(TermInfo.NTERM) : "Enzyme " + enzyme.getName() + " should be NTERM.";
      } else {
        assert enzyme.getTerm().equals(TermInfo.CTERM) : "Enzyme " + enzyme.getName() + " should be CTERM.";
      }
    }
  }

  @Test(groups = {"unit"})
  public void testCleaves() {
    final boolean[] pterm = new boolean[] {true, true, false, false, true, false, false, true, false, false, true, true, false, true, true};
    for(int i = 0; i < enzymes.getEnzymes().size(); i++) {
      final Enzyme enzyme = enzymes.getEnzymes().get(i);
      if(pterm[i]) {
        assert enzyme.getDoNotCleave().equals("P") : "Enzyme " + enzyme.getName() + " should not cleave P";
      } else {
        assert enzyme.getDoNotCleave().equals("") : "Enzyme " + enzyme.getName() + " should not cleave no AA";
      }
    }
    assert enzymes.getEnzymes().get(11).getDoCleave().equals("FYWLKR") : "Do cleave test 1";
    assert enzymes.getEnzymes().get(7).getDoCleave().equals("K") : "Do cleave test 2";
  }
  */

}
