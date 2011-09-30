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
import java.util.Map;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.models.xtandem.XTandemParameters;
import edu.umn.msi.tropix.proteomics.parameters.ParameterUtils;
import edu.umn.msi.tropix.proteomics.xtandem.XTandemTestUtils;

public class XTandemParameterUtilsTest {
  private static final XTandemParameterUtilsImpl PARAMETER_UTILS = new XTandemParameterUtilsImpl();

  /*
   * @Test(groups = "unit")
   * public void testPotentialCondense() {
   * final Map<String, String> map = new HashMap<String, String>();
   * map.put("potentialMod1Residues", "[ax");
   * map.put("potentialMod1Value", "3.1");
   * map.put("potentialMod2Residues", "a");
   * map.put("potentialMod2Value", "4.0");
   * map.put("potentialMod3Residues", "");
   * map.put("potentialMod3Value", "");
   * final XTandemParameterUtilsImpl utils = new XTandemParameterUtilsImpl();
   * utils.condensePotentialModifications(map, "potentialMod", 2, "potentialModificationMass");
   * 
   * final String potModMass = map.get("potentialModificationMass");
   * assert potModMass != null;
   * }
   */

  @Test(groups = {"unit"})
  public void testEnzymeToCleavageSite() {
    Map<String, String> map;
    final XTandemParameterUtilsImpl utils = new XTandemParameterUtilsImpl();

    map = new HashMap<String, String>();
    map.put("enzyme", "Trypsin");
    utils.convertEnzymeToCleavageSite("enzyme", "proteinCleavageSite", map);
    assert map.get("proteinCleavageSite").equals("[KR]|{P}");

    map = new HashMap<String, String>();
    map.put("enzyme", "Asp-N");
    utils.convertEnzymeToCleavageSite("enzyme", "proteinCleavageSite", map);
    assert map.get("proteinCleavageSite").equals("[X]|[D]");

    map = new HashMap<String, String>();
    map.put("enzyme", "CNBr");
    utils.convertEnzymeToCleavageSite("enzyme", "proteinCleavageSite", map);
    assert map.get("proteinCleavageSite").equals("[M]|{P}");
  }

  @Test(groups = "unit")
  public void testCleavageToEnzyme() {
    Map<String, String> map;
    final XTandemParameterUtilsImpl utils = new XTandemParameterUtilsImpl();

    map = new HashMap<String, String>();
    map.put("proteinCleavageSite", "[KR]|{P}");
    utils.convertCleavageSiteToEnzyme("enzyme", "proteinCleavageSite", map);
    assert map.get("enzyme").equals("Trypsin");
    assert !map.containsKey("proteinCleavageSite");

    map.clear();
    map.put("proteinCleavageSite", "[M]|{P}");
    utils.convertCleavageSiteToEnzyme("enzyme", "proteinCleavageSite", map);
    assert map.get("enzyme").equals("CNBr");
  }

  @Test(groups = "unit")
  public void condenseModifications() {
    final XTandemParameterUtilsImpl utils = new XTandemParameterUtilsImpl();

    final Map<String, String> map = new HashMap<String, String>();

    map.put("spectrumError", "1.0");
    map.put("fixedA", "-2.3");
    map.put("potentialB", "4.5");

    utils.condenseModifications(map, "fixed", "moo");

    assert map.get("moo").equals("-2.3@A") : map.get("moo");
    assert map.containsKey("potentialB");
    assert map.get("potentialB").equals("4.5");
    assert map.containsKey("spectrumError");
    assert !map.containsKey("fixedA");

    map.remove("moo");

    map.put("fixedC", "1232");
    map.put("fixedD", "-23.3");
    map.put("fixed[", "-23.3");
    utils.condenseModifications(map, "fixed", "moo");

    final String condensedValue = map.get("moo");
    assert condensedValue.contains("1232@C");
    assert condensedValue.contains("-23.3@D");
    assert condensedValue.contains("-23.3@[");

  }

  private Map<String, String> getXTandemParameterMap() {
    final XTandemParameters parameters = XTandemTestUtils.getXTandemParameters();
    return ParameterUtils.mapForParameters(parameters);
  }

  @Test(groups = "unit")
  public void testRefineModificationsTranslation() {
    final Map<String, String> parameters = getXTandemParameterMap();
    parameters.put("refinePotentialModificationMass", "-5.3@C");
    PARAMETER_UTILS.fromRaw(parameters);
    assert parameters.get("refinePotModC").equals("-5.3");
    PARAMETER_UTILS.toRaw(parameters);
    assert parameters.get("refinePotentialModificationMass").equals("-5.3@C");
  }

  @Test(groups = "unit")
  public void expandModifications() {
    final XTandemParameterUtilsImpl utils = new XTandemParameterUtilsImpl();

    Map<String, String> map = new HashMap<String, String>();

    map.put("spectrumError", "1.0");
    map.put("modificationMass", "-2.3@C,78.9@L,89@]");
    map.put("potentialB", "4.5");

    utils.expandModifications(map, "fixed", "modificationMass");
    assert !map.containsKey("modificationMass");
    assert map.get("fixedC").equals("-2.3");
    assert map.get("fixedL").equals("78.9");
    assert map.get("fixed]").equals("89");
    map = new HashMap<String, String>();

    map.put("spectrumError", "1.0");
    map.put("modificationMass", "-2.3@C");
    map.put("potentialB", "4.5");

    utils.expandModifications(map, "fixed", "modificationMass");
    assert !map.containsKey("modificationMass");
    assert map.get("fixedC").equals("-2.3");

    map = new HashMap<String, String>();

    map.put("spectrumError", "1.0");
    map.put("modificationMass", null);
    map.put("potentialB", "4.5");

    utils.expandModifications(map, "fixed", "modificationMass");
    assert !map.containsKey("modificationMass");

    map = new HashMap<String, String>();

    map.put("spectrumError", "1.0");
    map.put("modificationMass", "");
    map.put("potentialB", "4.5");

    utils.expandModifications(map, "fixed", "modificationMass");
    assert !map.containsKey("modificationMass");

  }

}
