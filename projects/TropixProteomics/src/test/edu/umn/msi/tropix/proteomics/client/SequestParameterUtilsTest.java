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

import java.util.HashMap;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.sequest.SequestParameters;

public class SequestParameterUtilsTest {

  @Test(groups = "unit")
  public void enzymeMapExpansion() {
    enzymeExpansion(true, "Trypsin");
    enzymeExpansion(true, "Asp-N");
  }

  @Test(groups = "unit")
  public void enzymeBeanExpansion() {
    enzymeExpansion(false, "Trypsin");
    enzymeExpansion(false, "Asp-N");
  }

  @Test(groups = "unit", expectedExceptions = RuntimeException.class)
  public void unknownEnzyme() {
    final SequestParameterUtilsImpl utils = new SequestParameterUtilsImpl();
    final EnzymeUtils enzymeUtils = new EnzymeUtils();
    utils.setEnzymeUtils(enzymeUtils);
    utils.expandEnzymeParameters("mooo", new HashMap<String, String>());
  }

  public void enzymeExpansion(final boolean mapTest, final String enzyme) {
    final SequestParameterUtilsImpl utils = new SequestParameterUtilsImpl();
    final EnzymeUtils enzymeUtils = new EnzymeUtils();
    utils.setEnzymeUtils(enzymeUtils);

    final String term = enzymeUtils.getTerm(enzyme);
    final String ncr = enzymeUtils.getNotCleaveResidues(enzyme);
    final String cr = enzymeUtils.getCleaveResidues(enzyme);
    assert term != null && ncr != null && cr != null;

    if(mapTest) {
      final HashMap<String, String> map = new HashMap<String, String>();
      map.put("enzyme", enzyme);
      utils.expandEnzymeParameters("enzyme", map);
      assert map.get("enzymeCleave").equals(cr.equals("") ? "-" : cr);
      assert map.get("enzymeNoCleave").equals(ncr.equals("") ? "-" : ncr);
      assert map.get("enzymeName").equals(enzyme);
      assert map.get("enzymeCTerm").equals(term.equalsIgnoreCase("C") ? "true" : "false");
    } else {
      final SequestParameters sequestParameters = new SequestParameters();
      utils.setEnzymeParameters(enzyme, sequestParameters);
      assert sequestParameters.getEnzymeCleave().equals(cr.equals("") ? "-" : cr);
      assert sequestParameters.getEnzymeNoCleave().equals(ncr.equals("") ? "-" : ncr);
      assert sequestParameters.getEnzymeName().equals(enzyme);
      assert sequestParameters.getEnzymeCTerm().equals(term.equalsIgnoreCase("C") ? true : false);
    }
  }

}
