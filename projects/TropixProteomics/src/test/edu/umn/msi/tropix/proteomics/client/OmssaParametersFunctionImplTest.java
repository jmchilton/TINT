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

import java.io.StringWriter;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.reflect.ReflectionHelper;
import edu.umn.msi.tropix.common.reflect.ReflectionHelpers;
import edu.umn.msi.tropix.grid.xml.SerializationUtils;
import edu.umn.msi.tropix.grid.xml.SerializationUtilsFactory;
import gov.nih.nlm.ncbi.omssa.MSCalcCharge;
import gov.nih.nlm.ncbi.omssa.MSCalcPlusOne;
import gov.nih.nlm.ncbi.omssa.MSChargeHandle;
import gov.nih.nlm.ncbi.omssa.MSChargeHandleMSChargeHandle_calccharge;
import gov.nih.nlm.ncbi.omssa.MSChargeHandleMSChargeHandle_calcplusone;
import gov.nih.nlm.ncbi.omssa.MSChargeHandleMSChargeHandle_prodlesspre;
import gov.nih.nlm.ncbi.omssa.MSChargeHandleMSChargeHandle_prodlesspreValue;
import gov.nih.nlm.ncbi.omssa.MSEnzymes;
import gov.nih.nlm.ncbi.omssa.MSIonType;
import gov.nih.nlm.ncbi.omssa.MSIterativeSettings;
import gov.nih.nlm.ncbi.omssa.MSMod;
import gov.nih.nlm.ncbi.omssa.MSSearchSettings;
import gov.nih.nlm.ncbi.omssa.MSSearchSettingsMSSearchSettings_chargehandling;
import gov.nih.nlm.ncbi.omssa.MSSearchSettingsMSSearchSettings_enzyme;
import gov.nih.nlm.ncbi.omssa.MSSearchSettingsMSSearchSettings_fixed;
import gov.nih.nlm.ncbi.omssa.MSSearchSettingsMSSearchSettings_ionstosearch;
import gov.nih.nlm.ncbi.omssa.MSSearchSettingsMSSearchSettings_iterativesettings;
import gov.nih.nlm.ncbi.omssa.MSSearchSettingsMSSearchSettings_nmethionine;
import gov.nih.nlm.ncbi.omssa.MSSearchSettingsMSSearchSettings_nmethionineValue;
import gov.nih.nlm.ncbi.omssa.MSSearchSettingsMSSearchSettings_noprolineions;
import gov.nih.nlm.ncbi.omssa.MSSearchSettingsMSSearchSettings_precursorsearchtype;
import gov.nih.nlm.ncbi.omssa.MSSearchSettingsMSSearchSettings_productsearchtype;
import gov.nih.nlm.ncbi.omssa.MSSearchSettingsMSSearchSettings_reversesearch;
import gov.nih.nlm.ncbi.omssa.MSSearchSettingsMSSearchSettings_reversesearchValue;
import gov.nih.nlm.ncbi.omssa.MSSearchSettingsMSSearchSettings_variable;
import gov.nih.nlm.ncbi.omssa.MSSearchSettingsMSSearchSettings_zdep;
import gov.nih.nlm.ncbi.omssa.MSSearchType;
import gov.nih.nlm.ncbi.omssa.MSZdependence;

public class OmssaParametersFunctionImplTest {
  private static final QName QNAME = new QName("http://www.ncbi.nlm.nih.gov", "MSSearchSettings");

  private final SerializationUtils serializationUtils = SerializationUtilsFactory.getInstance();
  private final ReflectionHelper reflectionHelper = ReflectionHelpers.getInstance();

  @Test(groups = "unit")
  public void inverse() {
    final OmssaParametersFunctionImpl func = new OmssaParametersFunctionImpl();
    final MSSearchSettings settings = new MSSearchSettings();
    Map<String, String> parameters;

    // ionstosearch test
    MSIonType[] ionsType;
    ionsType = new MSIonType[] {new MSIonType(new BigInteger("1"))};
    MSSearchSettingsMSSearchSettings_ionstosearch ionstosearch = new MSSearchSettingsMSSearchSettings_ionstosearch(ionsType);
    settings.setMSSearchSettings_ionstosearch(ionstosearch);

    parameters = func.inverse(settings);
    assert parameters.get("ionA").equals("false");
    assert parameters.get("ionB").equals("true");
    assert parameters.get("ionC").equals("false");

    ionsType = new MSIonType[] {new MSIonType(new BigInteger("0")), new MSIonType(new BigInteger("2"))};
    ionstosearch = new MSSearchSettingsMSSearchSettings_ionstosearch(ionsType);
    settings.setMSSearchSettings_ionstosearch(ionstosearch);

    parameters = func.inverse(settings);
    assert parameters.get("ionA").equals("true");
    assert parameters.get("ionB").equals("false");
    assert parameters.get("ionC").equals("true");

    // noprolineions test
    assert parameters.get("noprolineionA") == null;
    settings.setMSSearchSettings_noprolineions(new MSSearchSettingsMSSearchSettings_noprolineions(ionsType));
    parameters = func.inverse(settings);
    assert parameters.get("noprolineionA").equals("true");
    assert parameters.get("noprolineionB").equals("false");
    assert parameters.get("noprolineionC").equals("true");

    // precurosr searchtype test
    settings.setMSSearchSettings_precursorsearchtype(new MSSearchSettingsMSSearchSettings_precursorsearchtype(new MSSearchType(new BigInteger("0"))));
    parameters = func.inverse(settings);
    assert parameters.get("precursorsearchtype").equals("monoisotopic");
    settings.setMSSearchSettings_precursorsearchtype(new MSSearchSettingsMSSearchSettings_precursorsearchtype(new MSSearchType(new BigInteger("2"))));
    parameters = func.inverse(settings);
    assert parameters.get("precursorsearchtype").equals("monon15") : parameters.get("precursorsearchtype");

    // product search type test
    settings.setMSSearchSettings_productsearchtype(new MSSearchSettingsMSSearchSettings_productsearchtype(new MSSearchType(new BigInteger("0"))));
    parameters = func.inverse(settings);
    assert parameters.get("productsearchtype").equals("monoisotopic");
    settings.setMSSearchSettings_productsearchtype(new MSSearchSettingsMSSearchSettings_productsearchtype(new MSSearchType(new BigInteger("2"))));
    parameters = func.inverse(settings);
    assert parameters.get("productsearchtype").equals("monon15");

    // zdep
    assert parameters.get("zdep") == null;
    settings.setMSSearchSettings_zdep(new MSSearchSettingsMSSearchSettings_zdep(new MSZdependence(BigInteger.ONE)));
    parameters = func.inverse(settings);
    assert parameters.get("zdep").equals("true");
    settings.setMSSearchSettings_zdep(new MSSearchSettingsMSSearchSettings_zdep(new MSZdependence(BigInteger.ZERO)));
    parameters = func.inverse(settings);
    assert parameters.get("zdep").equals("false");

    for(int i = 0; i < 20; i++) {
      final BigInteger integer = BigInteger.valueOf(i);
      settings.setMSSearchSettings_enzyme(new MSSearchSettingsMSSearchSettings_enzyme(new MSEnzymes(integer)));
      parameters = func.inverse(settings);
      assert parameters.get("enzyme").equals(OmssaParametersFunctionImpl.ENZYME_MAP.inverse().get("" + i));
    }

    // nmethoione
    settings.setMSSearchSettings_nmethionine(new MSSearchSettingsMSSearchSettings_nmethionine(MSSearchSettingsMSSearchSettings_nmethionineValue.fromString("true")));
    parameters = func.inverse(settings);
    assert parameters.get("nmethionine").equals("true");
    settings.setMSSearchSettings_nmethionine(new MSSearchSettingsMSSearchSettings_nmethionine(MSSearchSettingsMSSearchSettings_nmethionineValue.fromString("false")));
    parameters = func.inverse(settings);
    assert parameters.get("nmethionine").equals("false");

    // reversesearch
    settings.setMSSearchSettings_reversesearch(new MSSearchSettingsMSSearchSettings_reversesearch(MSSearchSettingsMSSearchSettings_reversesearchValue.value1));
    parameters = func.inverse(settings);
    assert parameters.get("reversesearch").equals("true");
    settings.setMSSearchSettings_reversesearch(new MSSearchSettingsMSSearchSettings_reversesearch(MSSearchSettingsMSSearchSettings_reversesearchValue.value2));
    parameters = func.inverse(settings);
    assert parameters.get("reversesearch").equals("false");

    final MSChargeHandle handle = new MSChargeHandle();
    handle.setMSChargeHandle_calccharge(new MSChargeHandleMSChargeHandle_calccharge(new MSCalcCharge(BigInteger.ZERO)));
    settings.setMSSearchSettings_chargehandling(new MSSearchSettingsMSSearchSettings_chargehandling(handle));
    parameters = func.inverse(settings);
    assert parameters.get("calccharge").equals("calculate");
    handle.setMSChargeHandle_calccharge(new MSChargeHandleMSChargeHandle_calccharge(new MSCalcCharge(BigInteger.ONE)));
    parameters = func.inverse(settings);
    assert parameters.get("calccharge").equals("usefile");
    handle.setMSChargeHandle_calccharge(new MSChargeHandleMSChargeHandle_calccharge(new MSCalcCharge(new BigInteger("2"))));
    parameters = func.inverse(settings);
    assert parameters.get("calccharge").equals("userange");

    handle.setMSChargeHandle_calcplusone(new MSChargeHandleMSChargeHandle_calcplusone(new MSCalcPlusOne(BigInteger.ONE))); // .getMSCalcPlusOne().get_value().equals(new BigInteger("1"))
    parameters = func.inverse(settings);
    assert parameters.get("calcplusone").equals("true");
    handle.setMSChargeHandle_calcplusone(new MSChargeHandleMSChargeHandle_calcplusone(new MSCalcPlusOne(BigInteger.ZERO))); // .getMSCalcPlusOne().get_value().equals(new BigInteger("1"))
    parameters = func.inverse(settings);
    assert parameters.get("calcplusone").equals("false");

    handle.setMSChargeHandle_prodlesspre(new MSChargeHandleMSChargeHandle_prodlesspre(MSChargeHandleMSChargeHandle_prodlesspreValue.fromString("false")));
    parameters = func.inverse(settings);
    assert parameters.get("prodlesspre").equals("false");
    handle.setMSChargeHandle_prodlesspre(new MSChargeHandleMSChargeHandle_prodlesspre(MSChargeHandleMSChargeHandle_prodlesspreValue.fromString("true")));
    parameters = func.inverse(settings);
    assert parameters.get("prodlesspre").equals("true");

    Map<String, String> booleanParams = new HashMap<String, String>();
    booleanParams.put("nocorrelationscore", "MSSearchSettings_nocorrelationscore");

    for(final Map.Entry<String, String> entry : booleanParams.entrySet()) {
      reflectionHelper.invoke("set" + entry.getValue(), settings, BigInteger.ONE);
      parameters = func.inverse(settings);
      assert parameters.get(entry.getKey()).equals("true") : parameters.get(entry.getKey());
      reflectionHelper.invoke("set" + entry.getValue(), settings, BigInteger.ZERO);
      parameters = func.inverse(settings);
      assert parameters.get(entry.getKey()).equals("false");
    }

    booleanParams = new HashMap<String, String>();
    booleanParams.put("searchb1", "MSSearchSettings_searchb1");
    booleanParams.put("searchctermproduct", "MSSearchSettings_searchctermproduct");
    for(final Map.Entry<String, String> entry : booleanParams.entrySet()) {
      reflectionHelper.invoke("set" + entry.getValue(), settings, BigInteger.ZERO);
      parameters = func.inverse(settings);
      assert parameters.get(entry.getKey()).equals("true") : parameters.get(entry.getKey());
      reflectionHelper.invoke("set" + entry.getValue(), settings, BigInteger.ONE);
      parameters = func.inverse(settings);
      assert parameters.get(entry.getKey()).equals("false");
    }

    settings.setMSSearchSettings_peptol(10.2);
    parameters = func.inverse(settings);
    assert Double.parseDouble(parameters.get("peptol")) == 10.2;

    final Map<String, String> doubleParams = new HashMap<String, String>();
    doubleParams.put("peptol", "MSSearchSettings_peptol");
    doubleParams.put("msmstol", "MSSearchSettings_msmstol");
    doubleParams.put("cutoff", "MSSearchSettings_cutoff");
    doubleParams.put("cutlo", "MSSearchSettings_cutlo");
    doubleParams.put("cuthi", "MSSearchSettings_cuthi");
    doubleParams.put("cutinc", "MSSearchSettings_cutinc");
    doubleParams.put("exactmass", "MSSearchSettings_exactmass");
    doubleParams.put("probfollowingion", "MSSearchSettings_probfollowingion");
    doubleParams.put("automassadjust", "MSSearchSettings_automassadjust");
    doubleParams.put("lomasscutoff", "MSSearchSettings_lomasscutoff");

    for(final Map.Entry<String, String> entry : doubleParams.entrySet()) {
      reflectionHelper.invoke("set" + entry.getValue(), settings, 0.134);
      parameters = func.inverse(settings);
      assert parameters.get(entry.getKey()).equals("0.134");
      reflectionHelper.invoke("set" + entry.getValue(), settings, 3.14);
      parameters = func.inverse(settings);
      assert parameters.get(entry.getKey()).equals("3.14");
    }

    settings.getMSSearchSettings_chargehandling().getMSChargeHandle().setMSChargeHandle_plusone(1.2);
    parameters = func.inverse(settings);
    assert parameters.get("plusone").equals("1.2");

    doubleParams.clear();
    doubleParams.put("researchthresh", "MSIterativeSettings_researchthresh");
    doubleParams.put("subsetthresh", "MSIterativeSettings_subsetthresh");
    doubleParams.put("replacethresh", "MSIterativeSettings_replacethresh");

    settings.setMSSearchSettings_iterativesettings(new MSSearchSettingsMSSearchSettings_iterativesettings(new MSIterativeSettings()));
    for(final Map.Entry<String, String> entry : doubleParams.entrySet()) {
      reflectionHelper.invoke("set" + entry.getValue(), settings.getMSSearchSettings_iterativesettings().getMSIterativeSettings(), 0.134);
      parameters = func.inverse(settings);
      assert parameters.get(entry.getKey()).equals("0.134");
      reflectionHelper.invoke("set" + entry.getValue(), settings.getMSSearchSettings_iterativesettings().getMSIterativeSettings(), 3.14);
      parameters = func.inverse(settings);
      assert parameters.get(entry.getKey()).equals("3.14");
    }

    final Map<String, String> intParams = new HashMap<String, String>();
    intParams.put("singlewin", "MSSearchSettings_singlewin");
    intParams.put("doublewin", "MSSearchSettings_doublewin");
    intParams.put("singlenum", "MSSearchSettings_singlenum");
    intParams.put("doublenum", "MSSearchSettings_doublenum");
    intParams.put("missedcleave", "MSSearchSettings_missedcleave");
    intParams.put("tophitnum", "MSSearchSettings_tophitnum");
    intParams.put("minhit", "MSSearchSettings_minhit");
    intParams.put("minspectra", "MSSearchSettings_minspectra");
    intParams.put("scale", "MSSearchSettings_scale");
    intParams.put("maxmods", "MSSearchSettings_maxmods");
    intParams.put("pseudocount", "MSSearchSettings_pseudocount");
    intParams.put("maxproductions", "MSSearchSettings_maxproductions");
    intParams.put("maxnoenzyme", "MSSearchSettings_maxnoenzyme");
    intParams.put("minnoenzyme", "MSSearchSettings_minnoenzyme");
    intParams.put("precursorcull", "MSSearchSettings_precursorcull");
    intParams.put("hitlistlen", "MSSearchSettings_hitlistlen");

    for(final Map.Entry<String, String> entry : intParams.entrySet()) {
      reflectionHelper.invoke("set" + entry.getValue(), settings, new BigInteger("5"));
      parameters = func.inverse(settings);
      assert parameters.get(entry.getKey()).equals("5");

      reflectionHelper.invoke("set" + entry.getValue(), settings, new BigInteger("3"));
      parameters = func.inverse(settings);
      assert parameters.get(entry.getKey()).equals("3");
    }

    intParams.clear();
    intParams.put("mincharge", "MSChargeHandle_mincharge");
    intParams.put("maxcharge", "MSChargeHandle_maxcharge");
    intParams.put("considermult", "MSChargeHandle_considermult");
    intParams.put("maxproductcharge", "MSChargeHandle_maxproductcharge");
    for(final Map.Entry<String, String> entry : intParams.entrySet()) {
      reflectionHelper.invoke("set" + entry.getValue(), settings.getMSSearchSettings_chargehandling().getMSChargeHandle(), new BigInteger("5"));
      parameters = func.inverse(settings);
      assert parameters.get(entry.getKey()).equals("5");

      reflectionHelper.invoke("set" + entry.getValue(), settings.getMSSearchSettings_chargehandling().getMSChargeHandle(), new BigInteger("3"));
      parameters = func.inverse(settings);
      assert parameters.get(entry.getKey()).equals("3");
    }

    MSMod[] mods = new MSMod[] {new MSMod("2")};
    settings.setMSSearchSettings_fixed(new MSSearchSettingsMSSearchSettings_fixed(mods));
    parameters = func.inverse(settings);
    assert parameters.get("fixed").equals("carboxymethylc") : parameters.get("fixed");

    mods = new MSMod[] {};
    settings.setMSSearchSettings_fixed(new MSSearchSettingsMSSearchSettings_fixed(mods));
    parameters = func.inverse(settings);
    assert parameters.get("fixed").equals("") : parameters.get("fixed");

    mods = new MSMod[] {new MSMod("3"), new MSMod("5")};
    settings.setMSSearchSettings_fixed(new MSSearchSettingsMSSearchSettings_fixed(mods));
    parameters = func.inverse(settings);
    assert parameters.get("fixed").equals("carbamidomethylc, propionamidec") : parameters.get("fixed");

    mods = new MSMod[] {new MSMod("2")};
    settings.setMSSearchSettings_variable(new MSSearchSettingsMSSearchSettings_variable(mods));
    parameters = func.inverse(settings);
    assert parameters.get("variable").equals("carboxymethylc") : parameters.get("variable");

    mods = new MSMod[] {};
    settings.setMSSearchSettings_variable(new MSSearchSettingsMSSearchSettings_variable(mods));
    parameters = func.inverse(settings);
    assert parameters.get("variable").equals("") : parameters.get("variable");

    mods = new MSMod[] {new MSMod("3"), new MSMod("5")};
    settings.setMSSearchSettings_variable(new MSSearchSettingsMSSearchSettings_variable(mods));
    parameters = func.inverse(settings);
    assert parameters.get("variable").equals("carbamidomethylc, propionamidec") : parameters.get("variable");

  }

  @Test(groups = "unit")
  public void params() {
    final OmssaParametersFunctionImpl func = new OmssaParametersFunctionImpl();
    String xml;

    final Map<String, String> parameters = new HashMap<String, String>();
    MSSearchSettings settings = null;

    // ionstosearch test
    parameters.put("ionA", "false");
    parameters.put("ionB", "false");
    parameters.put("ionC", "false");
    parameters.put("ionX", "false");
    parameters.put("ionY", "false");
    parameters.put("ionZ", "false");
    settings = func.apply(parameters);
    assert settings.getMSSearchSettings_ionstosearch().getMSIonType().length == 0;

    parameters.put("ionZ", "true");
    settings = func.apply(parameters);
    MSIonType[] ions = settings.getMSSearchSettings_ionstosearch().getMSIonType();
    assert ions.length == 1 : ions.length;
    assert ions[0] != null : "Null ion found";
    assert ions[0].get_value().equals(new BigInteger("5"));

    // noprolineions test
    assert settings.getMSSearchSettings_noprolineions().getMSIonType().length == 0;
    parameters.put("noprolineionImmonium", "true");
    parameters.put("noprolineionParent", "true");
    parameters.put("noprolineionY", "false");

    settings = func.apply(parameters);
    ions = settings.getMSSearchSettings_noprolineions().getMSIonType();
    assert ions.length == 2 : ions.length;
    boolean maxFound = false, parentFound = false;
    for(final MSIonType ion : ions) {
      if(ion.get_value().equals(new BigInteger("8"))) {
        maxFound = true;
      }
      if(ion.get_value().equals(new BigInteger("6"))) {
        parentFound = true;
      }
    }
    assert maxFound && parentFound;

    // SearchType
    parameters.put("precursorsearchtype", "monoisotopic");
    settings = func.apply(parameters);
    assert settings.getMSSearchSettings_precursorsearchtype().getMSSearchType().get_value().equals(new BigInteger("0"));
    parameters.put("precursorsearchtype", "monon15");
    settings = func.apply(parameters);
    assert settings.getMSSearchSettings_precursorsearchtype().getMSSearchType().get_value().equals(new BigInteger("2"));

    parameters.put("productsearchtype", "average");
    settings = func.apply(parameters);
    assert settings.getMSSearchSettings_productsearchtype().getMSSearchType().get_value().equals(new BigInteger("1"));
    parameters.put("productsearchtype", "exact");
    settings = func.apply(parameters);
    assert settings.getMSSearchSettings_productsearchtype().getMSSearchType().get_value().equals(new BigInteger("3"));

    // zdep
    parameters.put("zdep", "true");
    settings = func.apply(parameters);
    assert settings.getMSSearchSettings_zdep().getMSZdependence().get_value().equals(new BigInteger("1"));
    parameters.put("zdep", "false");
    settings = func.apply(parameters);
    assert settings.getMSSearchSettings_zdep().getMSZdependence().get_value().equals(new BigInteger("0"));

    for(final String enzyme : OmssaParametersFunctionImpl.ENZYME_MAP.keySet()) {
      final String value = OmssaParametersFunctionImpl.ENZYME_MAP.get(enzyme);
      parameters.put("enzyme", enzyme);
      settings = func.apply(parameters);
      assert settings.getMSSearchSettings_enzyme().getMSEnzymes().get_value().equals(new BigInteger(value));
    }

    parameters.put("nmethionine", "true");
    settings = func.apply(parameters);
    assert settings.getMSSearchSettings_nmethionine().getValue().equals(MSSearchSettingsMSSearchSettings_nmethionineValue.value1);
    parameters.put("nmethionine", "false");
    settings = func.apply(parameters);
    assert settings.getMSSearchSettings_nmethionine().getValue().equals(MSSearchSettingsMSSearchSettings_nmethionineValue.value2);

    parameters.put("reversesearch", "true");
    settings = func.apply(parameters);
    assert settings.getMSSearchSettings_reversesearch().getValue().equals(MSSearchSettingsMSSearchSettings_reversesearchValue.value1);
    parameters.put("reversesearch", "false");
    settings = func.apply(parameters);
    assert settings.getMSSearchSettings_reversesearch().getValue().equals(MSSearchSettingsMSSearchSettings_reversesearchValue.value2);

    parameters.put("calccharge", "calculate");
    settings = func.apply(parameters);
    assert settings.getMSSearchSettings_chargehandling().getMSChargeHandle().getMSChargeHandle_calccharge().getMSCalcCharge().get_value().equals(new BigInteger("0"));
    parameters.put("calccharge", "usefile");
    settings = func.apply(parameters);
    assert settings.getMSSearchSettings_chargehandling().getMSChargeHandle().getMSChargeHandle_calccharge().getMSCalcCharge().get_value().equals(new BigInteger("1"));
    parameters.put("calccharge", "userange");
    settings = func.apply(parameters);
    assert settings.getMSSearchSettings_chargehandling().getMSChargeHandle().getMSChargeHandle_calccharge().getMSCalcCharge().get_value().equals(new BigInteger("2"));

    parameters.put("calcplusone", "true");
    settings = func.apply(parameters);
    assert settings.getMSSearchSettings_chargehandling().getMSChargeHandle().getMSChargeHandle_calcplusone().getMSCalcPlusOne().get_value().equals(new BigInteger("1"));
    parameters.put("calcplusone", "false");
    settings = func.apply(parameters);
    assert settings.getMSSearchSettings_chargehandling().getMSChargeHandle().getMSChargeHandle_calcplusone().getMSCalcPlusOne().get_value().equals(new BigInteger("0"));

    parameters.put("prodlesspre", "false");
    settings = func.apply(parameters);
    assert settings.getMSSearchSettings_chargehandling().getMSChargeHandle().getMSChargeHandle_prodlesspre().getValue().getValue().equals("false");
    parameters.put("prodlesspre", "true");
    settings = func.apply(parameters);
    assert settings.getMSSearchSettings_chargehandling().getMSChargeHandle().getMSChargeHandle_prodlesspre().getValue().getValue().equals("true");

    Map<String, String> booleanParams = new HashMap<String, String>();
    booleanParams.put("nocorrelationscore", "MSSearchSettings_nocorrelationscore");

    for(final String booleanParam : booleanParams.keySet()) {
      final String el = booleanParams.get(booleanParam);
      parameters.put(booleanParam, "true");
      settings = func.apply(parameters);
      xml = toString(settings);
      assert xml.contains(getElement(el, "1")) || xml.contains(getElement(el, "true")) : xml;
      parameters.put(booleanParam, "false");
      settings = func.apply(parameters);
      xml = toString(settings);
      assert xml.contains(getElement(el, "0")) || xml.contains(getElement(el, "false")) : xml;
    }

    booleanParams = new HashMap<String, String>();
    booleanParams.put("searchb1", "MSSearchSettings_searchb1");
    booleanParams.put("searchctermproduct", "MSSearchSettings_searchctermproduct");
    for(final String booleanParam : booleanParams.keySet()) {
      final String el = booleanParams.get(booleanParam);
      parameters.put(booleanParam, "false");
      settings = func.apply(parameters);
      xml = toString(settings);
      assert xml.contains(getElement(el, "1")) : xml;
      parameters.put(booleanParam, "true");
      settings = func.apply(parameters);
      xml = toString(settings);
      assert xml.contains(getElement(el, "0")) : xml;
    }

    final Map<String, String> doubleParams = new HashMap<String, String>();
    doubleParams.put("peptol", "MSSearchSettings_peptol");
    doubleParams.put("msmstol", "MSSearchSettings_msmstol");
    doubleParams.put("cutoff", "MSSearchSettings_cutoff");
    doubleParams.put("cutlo", "MSSearchSettings_cutlo");
    doubleParams.put("cuthi", "MSSearchSettings_cuthi");
    doubleParams.put("cutinc", "MSSearchSettings_cutinc");
    doubleParams.put("exactmass", "MSSearchSettings_exactmass");
    doubleParams.put("probfollowingion", "MSSearchSettings_probfollowingion");
    doubleParams.put("automassadjust", "MSSearchSettings_automassadjust");
    doubleParams.put("lomasscutoff", "MSSearchSettings_lomasscutoff");

    doubleParams.put("researchthresh", "MSIterativeSettings_researchthresh");
    doubleParams.put("subsetthresh", "MSIterativeSettings_subsetthresh");
    doubleParams.put("replacethresh", "MSIterativeSettings_replacethresh");

    doubleParams.put("plusone", "MSChargeHandle_plusone");

    for(final String dParam : doubleParams.keySet()) {
      final String el = doubleParams.get(dParam);
      parameters.put(dParam, "0.134");
      settings = func.apply(parameters);
      xml = toString(settings);
      assert xml.contains(getElement(el, "0.134"));
      parameters.put(dParam, "3.14");
      settings = func.apply(parameters);
      xml = toString(settings);
      assert xml.contains(getElement(el, "3.14"));
    }

    final Map<String, String> intParams = new HashMap<String, String>();
    intParams.put("singlewin", "MSSearchSettings_singlewin");
    intParams.put("doublewin", "MSSearchSettings_doublewin");
    intParams.put("singlenum", "MSSearchSettings_singlenum");
    intParams.put("doublenum", "MSSearchSettings_doublenum");
    intParams.put("missedcleave", "MSSearchSettings_missedcleave");
    intParams.put("tophitnum", "MSSearchSettings_tophitnum");
    intParams.put("minhit", "MSSearchSettings_minhit");
    intParams.put("minspectra", "MSSearchSettings_minspectra");
    intParams.put("scale", "MSSearchSettings_scale");
    intParams.put("maxmods", "MSSearchSettings_maxmods");
    intParams.put("pseudocount", "MSSearchSettings_pseudocount");
    intParams.put("maxproductions", "MSSearchSettings_maxproductions");
    intParams.put("maxnoenzyme", "MSSearchSettings_maxnoenzyme");
    intParams.put("minnoenzyme", "MSSearchSettings_minnoenzyme");
    intParams.put("precursorcull", "MSSearchSettings_precursorcull");
    intParams.put("hitlistlen", "MSSearchSettings_hitlistlen");
    intParams.put("mincharge", "MSChargeHandle_mincharge");
    intParams.put("maxcharge", "MSChargeHandle_maxcharge");
    intParams.put("considermult", "MSChargeHandle_considermult");
    intParams.put("maxproductcharge", "MSChargeHandle_maxproductcharge");

    for(final String iParam : intParams.keySet()) {
      final String el = intParams.get(iParam);
      parameters.put(iParam, "4");
      settings = func.apply(parameters);
      xml = toString(settings);
      assert xml.contains(getElement(el, "4"));
      parameters.put(iParam, "8");
      settings = func.apply(parameters);
      xml = toString(settings);
      assert xml.contains(getElement(el, "8"));
    }

    MSSearchSettingsMSSearchSettings_fixed fixed;
    MSSearchSettingsMSSearchSettings_variable var;
    parameters.put("fixed", "");

    settings = func.apply(parameters);
    fixed = settings.getMSSearchSettings_fixed();
    var = settings.getMSSearchSettings_variable();
    assert fixed == null || fixed.getMSMod() == null || fixed.getMSMod().length == 0;
    assert var == null || var.getMSMod() == null || var.getMSMod().length == 0;

    parameters.put("fixed", "trimethylk");
    settings = func.apply(parameters);
    fixed = settings.getMSSearchSettings_fixed();
    var = settings.getMSSearchSettings_variable();
    assert fixed.getMSMod().length == 1;
    assertNotNull(fixed.getMSMod());
    assert var == null || var.getMSMod() == null || var.getMSMod().length == 0;

    parameters.remove("fixed");
    parameters.put("variable", "");
    settings = func.apply(parameters);
    fixed = settings.getMSSearchSettings_fixed();
    var = settings.getMSSearchSettings_variable();
    assert fixed == null || fixed.getMSMod() == null || fixed.getMSMod().length == 0;
    assert var == null || var.getMSMod() == null || var.getMSMod().length == 0;

    parameters.put("variable", "trimethylk");
    settings = func.apply(parameters);
    fixed = settings.getMSSearchSettings_fixed();
    var = settings.getMSSearchSettings_variable();
    assert fixed == null || fixed.getMSMod() == null || fixed.getMSMod().length == 0;
    assert var.getMSMod().length == 1;
    assertNotNull(var.getMSMod());

    parameters.put("variable", "icatheavy,trimethylk");
    settings = func.apply(parameters);
    fixed = settings.getMSSearchSettings_fixed();
    var = settings.getMSSearchSettings_variable();
    assert fixed == null || fixed.getMSMod() == null || fixed.getMSMod().length == 0;
    assert var.getMSMod().length == 2;
    assertNotNull(var.getMSMod());

    parameters.put("variable", "icatheavy, trimethylk ");
    settings = func.apply(parameters);
    fixed = settings.getMSSearchSettings_fixed();
    var = settings.getMSSearchSettings_variable();
    assert fixed == null || fixed.getMSMod() == null || fixed.getMSMod().length == 0;
    assert var.getMSMod().length == 2;
    assertNotNull(var.getMSMod());

  }

  private void assertNotNull(final Object[] array) {
    for(final Object object : array) {
      assert object != null;
    }
  }

  private String getElement(final String elementName, final String value) {
    return "<ns1:" + elementName + ">" + value + "</ns1:" + elementName + ">";
  }

  private String toString(final MSSearchSettings settings) {
    final StringWriter writer = new StringWriter();
    serializationUtils.serialize(writer, settings, QNAME);
    return writer.getBuffer().toString();
  }

}
