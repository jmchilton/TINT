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

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.inject.Named;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.common.collect.InvertiableFunction;
import edu.umn.msi.tropix.common.reflect.ReflectionHelper;
import edu.umn.msi.tropix.common.reflect.ReflectionHelpers;
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

@ManagedBean @Named("omssaParametersFunction")
class OmssaParametersFunctionImpl implements InvertiableFunction<Map<String, String>, MSSearchSettings> {
  private static final ReflectionHelper REFLECTION_HELPER = ReflectionHelpers.getInstance();
  private static final BiMap<String, String> ION_MAP = HashBiMap.create(10);
  static {
    ION_MAP.put("A", "0");
    ION_MAP.put("B", "1");
    ION_MAP.put("C", "2");
    ION_MAP.put("X", "3");
    ION_MAP.put("Y", "4");
    ION_MAP.put("Z", "5");
    ION_MAP.put("Parent", "6");
    ION_MAP.put("Internal", "7");
    ION_MAP.put("Immonium", "8");
    ION_MAP.put("Unknown", "9");
  }
  private static final BiMap<String, String> SEARCH_MAP = HashBiMap.create(4);
  static {
    SEARCH_MAP.put("monoisotopic", "0");
    SEARCH_MAP.put("average", "1");
    SEARCH_MAP.put("monon15", "2");
    SEARCH_MAP.put("exact", "3");
  }
  public static final BiMap<String, String> ENZYME_MAP = HashBiMap.create(23);
  static {
    ENZYME_MAP.put("trypsin", "0");
    ENZYME_MAP.put("argc", "1");
    ENZYME_MAP.put("cnbr", "2");
    ENZYME_MAP.put("chymotrypsin", "3");
    ENZYME_MAP.put("formicacid", "4");
    ENZYME_MAP.put("lysc", "5");
    ENZYME_MAP.put("lysc-p", "6");
    ENZYME_MAP.put("pepsin-a", "7");
    ENZYME_MAP.put("tryp-cnbr", "8");
    ENZYME_MAP.put("tryp-chymo", "9");
    ENZYME_MAP.put("trypsin-p", "10");
    ENZYME_MAP.put("whole-protein", "11");
    ENZYME_MAP.put("aspn", "12");
    ENZYME_MAP.put("gluc", "13");
    ENZYME_MAP.put("aspngluc", "14");
    ENZYME_MAP.put("top-down", "15");
    ENZYME_MAP.put("semi-tryptic", "16");
    ENZYME_MAP.put("no-enzyme", "17");
    ENZYME_MAP.put("chymotrypsin-p", "18");
    ENZYME_MAP.put("aspn-de", "19");
    ENZYME_MAP.put("gluc-de", "20");
    ENZYME_MAP.put("max", "21");
    ENZYME_MAP.put("none", "255");
  }
  private static final BiMap<String, String> CALC_CHARGE_MAP = HashBiMap.create(3);
  static {
    CALC_CHARGE_MAP.put("calculate", "0");
    CALC_CHARGE_MAP.put("usefile", "1");
    CALC_CHARGE_MAP.put("userange", "2");
  }

  private static final BiMap<String, String> MODS_MAP = HashBiMap.create();
  static {
    MODS_MAP.put("methylk", "0");
    MODS_MAP.put("oxym", "1");
    MODS_MAP.put("carboxymethylc", "2");
    MODS_MAP.put("carbamidomethylc", "3");
    MODS_MAP.put("deamidationkq", "4");
    MODS_MAP.put("propionamidec", "5");
    MODS_MAP.put("phosphorylations", "6");
    MODS_MAP.put("phosphorylationt", "7");
    MODS_MAP.put("phosphorylationy", "8");
    MODS_MAP.put("ntermmcleave", "9");
    MODS_MAP.put("ntermacetyl", "10");
    MODS_MAP.put("ntermmethyl", "11");
    MODS_MAP.put("ntermtrimethyl", "12");
    MODS_MAP.put("methythiold", "13");
    MODS_MAP.put("methylq", "14");
    MODS_MAP.put("trimethylk", "15");
    MODS_MAP.put("methyld", "16");
    MODS_MAP.put("methyle", "17");
    MODS_MAP.put("ctermpepmethyl", "18");
    MODS_MAP.put("trideuteromethyld", "19");
    MODS_MAP.put("trideuteromethyle", "20");
    MODS_MAP.put("ctermpeptrideuteromethyl", "21");
    MODS_MAP.put("nformylmet", "22");
    MODS_MAP.put("twoamino3oxobutanoicacid", "23");
    MODS_MAP.put("acetylk", "24");
    MODS_MAP.put("ctermamide", "25");
    MODS_MAP.put("bmethylthiold", "26");
    MODS_MAP.put("carbamidomethylk", "27");
    MODS_MAP.put("carbamidometylh", "28");
    MODS_MAP.put("carbamidomethyld", "29");
    MODS_MAP.put("carbamidomethyle", "30");
    MODS_MAP.put("carbamylk", "31");
    MODS_MAP.put("ntermcarbamyl", "32");
    MODS_MAP.put("citrullinationr", "33");
    MODS_MAP.put("cysteicacidc", "34");
    MODS_MAP.put("diiodinationy", "35");
    MODS_MAP.put("dimethylk", "36");
    MODS_MAP.put("dimethylr", "37");
    MODS_MAP.put("ntermpepdimethyl", "38");
    MODS_MAP.put("dihydroxyf", "39");
    MODS_MAP.put("thioacetylk", "40");
    MODS_MAP.put("ntermpeptioacetyl", "41");
    MODS_MAP.put("farnesylationc", "42");
    MODS_MAP.put("formylk", "43");
    MODS_MAP.put("ntermpepformyl", "44");
    MODS_MAP.put("formylkynureninw", "45");
    MODS_MAP.put("phef", "46");
    MODS_MAP.put("gammacarboxyld", "47");
    MODS_MAP.put("gammacarboxyle", "48");
    MODS_MAP.put("geranylgeranylc", "49");
    MODS_MAP.put("ntermpepglucuronylg", "50");
    MODS_MAP.put("glutathionec", "51");
    MODS_MAP.put("glyglyk", "52");
    MODS_MAP.put("guanidinationk", "53");
    MODS_MAP.put("his2asnh", "54");
    MODS_MAP.put("his2asph", "55");
    MODS_MAP.put("ctermpephsem", "56");
    MODS_MAP.put("ctermpephselactm", "57");
    MODS_MAP.put("hydroxykynureninw", "58");
    MODS_MAP.put("hydroxylationd", "59");
    MODS_MAP.put("hydroxylationk", "60");
    MODS_MAP.put("hydroxylationn", "61");
    MODS_MAP.put("hydroxylationp", "62");
    MODS_MAP.put("hydroxylationf", "63");
    MODS_MAP.put("hydroxylationy", "64");
    MODS_MAP.put("iodinationy", "65");
    MODS_MAP.put("kynureninw", "66");
    MODS_MAP.put("lipoylk", "67");
    MODS_MAP.put("ctermpepmeester", "68");
    MODS_MAP.put("meesterd", "69");
    MODS_MAP.put("meestere", "70");
    MODS_MAP.put("meesters", "71");
    MODS_MAP.put("meestery", "72");
    MODS_MAP.put("methylc", "73");
    MODS_MAP.put("methylh", "74");
    MODS_MAP.put("methyln", "75");
    MODS_MAP.put("ntermpepmethyl", "76");
    MODS_MAP.put("methylr", "77");
    MODS_MAP.put("ntermpepmyristoyeylationg", "78");
    MODS_MAP.put("ntermpepmyristoyl4hg", "79");
    MODS_MAP.put("ntermpepmyristoylationg", "80");
    MODS_MAP.put("myristoylationk", "81");
    MODS_MAP.put("ntermformyl", "82");
    MODS_MAP.put("nemc", "83");
    MODS_MAP.put("nipcam", "84");
    MODS_MAP.put("nitrow", "85");
    MODS_MAP.put("nitroy", "86");
    MODS_MAP.put("ctermpepo18", "87");
    MODS_MAP.put("ctermpepdio18", "88");
    MODS_MAP.put("oxyh", "89");
    MODS_MAP.put("oxyw", "90");
    MODS_MAP.put("ppantetheines", "91");
    MODS_MAP.put("palmitoylationc", "92");
    MODS_MAP.put("palmitoylationk", "93");
    MODS_MAP.put("palmitoylations", "94");
    MODS_MAP.put("palmitoylationt", "95");
    MODS_MAP.put("phospholosss", "96");
    MODS_MAP.put("phospholosst", "97");
    MODS_MAP.put("phospholossy", "98");
    MODS_MAP.put("phosphoneutrallossc", "99");
    MODS_MAP.put("phosphoneutrallossd", "100");
    MODS_MAP.put("phosphoneutrallossh", "101");
    MODS_MAP.put("propionylk", "102");
    MODS_MAP.put("ntermpeppropionyl", "103");
    MODS_MAP.put("propionylheavyk", "104");
    MODS_MAP.put("ntermpeppropionylheavy", "105");
    MODS_MAP.put("pyridylk", "106");
    MODS_MAP.put("ntermpeppyridyl", "107");
    MODS_MAP.put("ntermpeppyrocmc", "108");
    MODS_MAP.put("ntermpeppyroe", "109");
    MODS_MAP.put("ntermpeppyroq", "110");
    MODS_MAP.put("pyroglutamicp", "111");
    MODS_MAP.put("spyridylethylc", "112");
    MODS_MAP.put("semetm", "113");
    MODS_MAP.put("sulfationy", "114");
    MODS_MAP.put("suphonem", "115");
    MODS_MAP.put("triiodinationy", "116");
    MODS_MAP.put("trimethylationr", "117");
    MODS_MAP.put("ntermpeptripalmitatec", "118");
    MODS_MAP.put("usermod1", "119");
    MODS_MAP.put("usermod2", "120");
    MODS_MAP.put("usermod3", "121");
    MODS_MAP.put("usermod4", "122");
    MODS_MAP.put("usermod5", "123");
    MODS_MAP.put("usermod6", "124");
    MODS_MAP.put("usermod7", "125");
    MODS_MAP.put("usermod8", "126");
    MODS_MAP.put("usermod9", "127");
    MODS_MAP.put("usermod10", "128");
    MODS_MAP.put("icatlight", "129");
    MODS_MAP.put("icatheavy", "130");
    MODS_MAP.put("camthiopropanoylk", "131");
    MODS_MAP.put("phosphoneutrallosss", "132");
    MODS_MAP.put("phosphoneutrallosst", "133");
    MODS_MAP.put("phosphoetdlosss", "134");
    MODS_MAP.put("phosphoetdlosst", "135");
    MODS_MAP.put("arg-13c6", "136");
    MODS_MAP.put("arg-13c6-15n4", "137");
    MODS_MAP.put("lys-13c6", "138");
    MODS_MAP.put("oxy18", "139");
    MODS_MAP.put("beta-elim-s", "140");
    MODS_MAP.put("beta-elim-t", "141");
    MODS_MAP.put("usermod11", "142");
    MODS_MAP.put("usermod12", "143");
    MODS_MAP.put("usermod13", "144");
    MODS_MAP.put("usermod14", "145");
    MODS_MAP.put("usermod15", "146");
    MODS_MAP.put("usermod16", "147");
    MODS_MAP.put("usermod17", "148");
    MODS_MAP.put("usermod18", "149");
    MODS_MAP.put("usermod19", "150");
    MODS_MAP.put("usermod20", "151");
    MODS_MAP.put("usermod21", "152");
    MODS_MAP.put("usermod22", "153");
    MODS_MAP.put("usermod23", "154");
    MODS_MAP.put("usermod24", "155");
    MODS_MAP.put("usermod25", "156");
    MODS_MAP.put("usermod26", "157");
    MODS_MAP.put("usermod27", "158");
    MODS_MAP.put("usermod28", "159");
    MODS_MAP.put("usermod29", "160");
    MODS_MAP.put("usermod30", "161");
    MODS_MAP.put("sulfinicacid", "162");
    MODS_MAP.put("arg2orn", "163");
    MODS_MAP.put("dehydro", "164");
    MODS_MAP.put("carboxykynurenin", "165");
    MODS_MAP.put("sumoylation", "166");
    MODS_MAP.put("iTRAQ114nterm", "167");
    MODS_MAP.put("iTRAQ114K", "168");
    MODS_MAP.put("iTRAQ114Y", "169");
    MODS_MAP.put("iTRAQ115nterm", "170");
    MODS_MAP.put("iTRAQ115K", "171");
    MODS_MAP.put("iTRAQ115Y", "172");
    MODS_MAP.put("iTRAQ116nterm", "173");
    MODS_MAP.put("iTRAQ116K", "174");
    MODS_MAP.put("iTRAQ116Y", "175");
    MODS_MAP.put("iTRAQ117nterm", "176");
    MODS_MAP.put("iTRAQ117K", "177");
    MODS_MAP.put("iTRAQ117Y", "178");
    MODS_MAP.put("mmts", "179");
    MODS_MAP.put("lys-2H4", "180");
    MODS_MAP.put("lys-13C615N2", "181");
    MODS_MAP.put("hexNAcN", "182");
    MODS_MAP.put("dHexHexNAcN", "183");
    MODS_MAP.put("hexNAcS", "184");
    MODS_MAP.put("hexNAcT", "185");
  }
  private static final String ION_PREFIX = "ion";
  private static final String NO_PROLINE_ION_PREFIX = "noprolineion";

  private static MSIonType[] getIons(final Map<String, String> parameterMap, final String prefix) {
    final LinkedList<MSIonType> ions = new LinkedList<MSIonType>();
    for(final String ion : ION_MAP.keySet()) {
      final String ionKey = prefix + ion;
      final String value = parameterMap.get(ionKey);
      if(value != null && Boolean.parseBoolean(parameterMap.get(ionKey))) {
        final MSIonType ionType = new MSIonType();
        final String ionValue = ION_MAP.get(ion);
        Preconditions.checkNotNull(ionValue);
        ionType.set_value(new BigInteger(ionValue));
        ions.add(ionType);
      }
    }
    final MSIonType[] ionArray = ions.toArray(new MSIonType[] {});
    return ionArray;
  }

  private static Map<String, String> fromIons(final MSIonType[] ions, final String prefix) {
    final HashMap<String, String> ionParams = new HashMap<String, String>();
    for(final String ion : ION_MAP.keySet()) {
      ionParams.put(prefix + ion, "false");
    }
    for(final MSIonType ion : ions) {
      final String ionKey = prefix + ION_MAP.inverse().get(ion.get_value().toString());
      ionParams.put(ionKey, "true");
    }
    return ionParams;
  }

  private enum Type {
    DOUBLE() {
      public Object parse(final String str) {
        return Double.parseDouble(str);
      }
    },
    INTEGER() {
      public Object parse(final String str) {
        return new BigInteger(str);
      }
    },
    BOOLEAN() {
      public Object parse(final String str) {
        return parseBoolean(str);
      }

      @Override
      public String toString(final Object object) {
        return binaryBigIntegerToString(new BigInteger(object.toString()));
      }
    },
    INVERSE_BOOLEAN() {
      public Object parse(final String str) {
        return parseBoolean(Boolean.toString(Boolean.parseBoolean(str) ^ true));
      }

      @Override
      public String toString(final Object object) {
        return inverseBinaryBigIntegerToString(new BigInteger(object.toString()));
      }
    };

    public abstract Object parse(String str);

    public String toString(final Object object) {
      return object.toString();
    }
  }

  private static BigInteger parseBoolean(final String str) {
    return Boolean.parseBoolean(str) ? BigInteger.ONE : BigInteger.ZERO;
  }

  private static String binaryBigIntegerToString(final BigInteger bigInt) {
    return bigInt.equals(BigInteger.ONE) ? "true" : "false";
  }

  private static String inverseBinaryBigIntegerToString(final BigInteger bigInt) {
    return bigInt.equals(BigInteger.ONE) ? "false" : "true";
  }

  private void handleSimpleParameter(final Map<String, String> parameterMap, final Object settings, final String parameterName, final Type type) {
    handleSimpleParameter(parameterMap, settings, parameterName, type, "setMSSearchSettings_");
  }

  private void inverseSimpleParameter(final Map<String, String> parameterMap, final Object settings, final String parameterName, final Type type) {
    inverseSimpleParameter(parameterMap, settings, parameterName, type, "getMSSearchSettings_");
  }

  private void handleSimpleParameter(final Map<String, String> parameterMap, final Object settings, final String parameterName, final Type type, final String prefix) {
    if(parameterMap.containsKey(parameterName)) {
      REFLECTION_HELPER.invoke(prefix + parameterName, settings, type.parse(parameterMap.get(parameterName)));
    }
  }

  private void inverseSimpleParameter(final Map<String, String> parameterMap, final Object settings, final String parameterName, final Type type, final String prefix) {
    final Object result = REFLECTION_HELPER.invoke(prefix + parameterName, settings);
    if(result != null) {
      parameterMap.put(parameterName, type.toString(result.toString()));
    }
  }

  private MSSearchType getSearchType(final String value) {
    final MSSearchType searchType = new MSSearchType();
    final String searchTypeValue = SEARCH_MAP.get(value);
    searchType.set_value(new BigInteger(searchTypeValue));
    return searchType;
  }

  private String fromSearchType(final MSSearchType searchType) {
    final String searchTypeValue = searchType.get_value().toString();
    final String value = SEARCH_MAP.inverse().get(searchTypeValue);
    Preconditions.checkNotNull(value);
    return value;
  }

  private boolean hasLength(final String str) {
    return str != null && str.length() > 0;
  }

  private MSMod[] getModArray(final String modStr) {
    String[] mods;
    if(modStr.contains(",")) {
      mods = modStr.split(",");
    } else {
      mods = new String[] {modStr};
    }
    final MSMod[] modArray = new MSMod[mods.length];
    int i = 0;
    for(final String mod : mods) {
      final String trimmedMod = mod.trim();
      final String modIndex = MODS_MAP.get(trimmedMod);
      final MSMod msMod = new MSMod(modIndex);
      modArray[i++] = msMod;
    }
    return modArray;
  }

  private String fromModArray(final MSMod[] mods) {
    String modStr;
    if(mods == null || mods.length == 0) {
      modStr = "";
    } else {
      modStr = Joiner.on(", ").join(Iterables.transform(Arrays.asList(mods), new Function<MSMod, String>() {
        public String apply(final MSMod mod) {
          return MODS_MAP.inverse().get(mod.get_value().toString());
        }
      }));
    }
    return modStr;
  }

  public MSSearchSettings apply(final Map<String, String> parameterMap) {
    final MSSearchSettings settings = new MSSearchSettings();

    if(parameterMap.containsKey("precursorsearchtype")) {
      final MSSearchSettingsMSSearchSettings_precursorsearchtype precursorsearchtype = new MSSearchSettingsMSSearchSettings_precursorsearchtype();
      precursorsearchtype.setMSSearchType(getSearchType(parameterMap.get("precursorsearchtype")));
      settings.setMSSearchSettings_precursorsearchtype(precursorsearchtype);
    }

    if(parameterMap.containsKey("productsearchtype")) {
      final MSSearchSettingsMSSearchSettings_productsearchtype producesearchtype = new MSSearchSettingsMSSearchSettings_productsearchtype();
      producesearchtype.setMSSearchType(getSearchType(parameterMap.get("productsearchtype")));
      settings.setMSSearchSettings_productsearchtype(producesearchtype);
    }

    final MSSearchSettingsMSSearchSettings_ionstosearch ionstosearch = new MSSearchSettingsMSSearchSettings_ionstosearch(getIons(parameterMap, ION_PREFIX));
    settings.setMSSearchSettings_ionstosearch(ionstosearch);

    settings.setMSSearchSettings_noprolineions(new MSSearchSettingsMSSearchSettings_noprolineions(getIons(parameterMap, NO_PROLINE_ION_PREFIX)));

    if(parameterMap.containsKey("zdep")) {
      final MSSearchSettingsMSSearchSettings_zdep zdep = new MSSearchSettingsMSSearchSettings_zdep();
      zdep.setMSZdependence(new MSZdependence(parseBoolean(parameterMap.get("zdep"))));
      settings.setMSSearchSettings_zdep(zdep);
    }

    if(parameterMap.containsKey("nmethionine")) {
      settings.setMSSearchSettings_nmethionine(new MSSearchSettingsMSSearchSettings_nmethionine(MSSearchSettingsMSSearchSettings_nmethionineValue.fromString(parameterMap.get("nmethionine"))));
    }

    // TODO: mod spect set

    if(parameterMap.containsKey("enzyme")) {
      settings.setMSSearchSettings_enzyme(new MSSearchSettingsMSSearchSettings_enzyme(new MSEnzymes(ENZYME_MAP.get(parameterMap.get("enzyme")))));
    }

    if(parameterMap.containsKey("reversesearch")) {
      settings.setMSSearchSettings_reversesearch(new MSSearchSettingsMSSearchSettings_reversesearch(MSSearchSettingsMSSearchSettings_reversesearchValue.fromString(parameterMap.get("reversesearch"))));
    }

    // ChangeHandling parameters
    final MSChargeHandle chargeHandle = new MSChargeHandle();
    settings.setMSSearchSettings_chargehandling(new MSSearchSettingsMSSearchSettings_chargehandling(chargeHandle));
    if(parameterMap.containsKey("calcplusone")) {
      chargeHandle.setMSChargeHandle_calcplusone(new MSChargeHandleMSChargeHandle_calcplusone(new MSCalcPlusOne(parseBoolean(parameterMap.get("calcplusone")))));
    }
    if(parameterMap.containsKey("calccharge")) {
      chargeHandle.setMSChargeHandle_calccharge(new MSChargeHandleMSChargeHandle_calccharge(new MSCalcCharge(CALC_CHARGE_MAP.get(parameterMap.get("calccharge")))));
    }
    if(parameterMap.containsKey("prodlesspre")) {
      chargeHandle.setMSChargeHandle_prodlesspre(new MSChargeHandleMSChargeHandle_prodlesspre(MSChargeHandleMSChargeHandle_prodlesspreValue.fromString(parameterMap.get("prodlesspre"))));
    }
    handleSimpleParameter(parameterMap, chargeHandle, "mincharge", Type.INTEGER, "setMSChargeHandle_");
    handleSimpleParameter(parameterMap, chargeHandle, "maxcharge", Type.INTEGER, "setMSChargeHandle_");
    handleSimpleParameter(parameterMap, chargeHandle, "considermult", Type.INTEGER, "setMSChargeHandle_");
    handleSimpleParameter(parameterMap, chargeHandle, "plusone", Type.DOUBLE, "setMSChargeHandle_");
    handleSimpleParameter(parameterMap, chargeHandle, "maxproductcharge", Type.INTEGER, "setMSChargeHandle_");

    handleSimpleParameter(parameterMap, settings, "peptol", Type.DOUBLE);
    handleSimpleParameter(parameterMap, settings, "msmstol", Type.DOUBLE);

    handleSimpleParameter(parameterMap, settings, "cutoff", Type.DOUBLE);
    handleSimpleParameter(parameterMap, settings, "cutlo", Type.DOUBLE);
    handleSimpleParameter(parameterMap, settings, "cuthi", Type.DOUBLE);
    handleSimpleParameter(parameterMap, settings, "cutinc", Type.DOUBLE);

    handleSimpleParameter(parameterMap, settings, "singlewin", Type.INTEGER);
    handleSimpleParameter(parameterMap, settings, "doublewin", Type.INTEGER);
    handleSimpleParameter(parameterMap, settings, "singlenum", Type.INTEGER);
    handleSimpleParameter(parameterMap, settings, "doublenum", Type.INTEGER);

    handleSimpleParameter(parameterMap, settings, "missedcleave", Type.INTEGER);
    handleSimpleParameter(parameterMap, settings, "hitlistlen", Type.INTEGER);
    handleSimpleParameter(parameterMap, settings, "tophitnum", Type.INTEGER);
    handleSimpleParameter(parameterMap, settings, "minhit", Type.INTEGER);
    handleSimpleParameter(parameterMap, settings, "minspectra", Type.INTEGER);
    handleSimpleParameter(parameterMap, settings, "scale", Type.INTEGER);
    handleSimpleParameter(parameterMap, settings, "maxmods", Type.INTEGER);

    if(hasLength(parameterMap.get("fixed"))) {
      settings.setMSSearchSettings_fixed(new MSSearchSettingsMSSearchSettings_fixed(getModArray(parameterMap.get("fixed"))));
    }
    if(hasLength(parameterMap.get("variable"))) {
      settings.setMSSearchSettings_variable(new MSSearchSettingsMSSearchSettings_variable(getModArray(parameterMap.get("variable"))));
    }

    // Ignoring tax id

    handleSimpleParameter(parameterMap, settings, "pseudocount", Type.INTEGER);
    handleSimpleParameter(parameterMap, settings, "searchb1", Type.INVERSE_BOOLEAN);
    handleSimpleParameter(parameterMap, settings, "searchctermproduct", Type.INVERSE_BOOLEAN);
    handleSimpleParameter(parameterMap, settings, "maxproductions", Type.INTEGER);

    handleSimpleParameter(parameterMap, settings, "maxnoenzyme", Type.INTEGER);
    handleSimpleParameter(parameterMap, settings, "minnoenzyme", Type.INTEGER);
    handleSimpleParameter(parameterMap, settings, "exactmass", Type.DOUBLE);

    settings.setMSSearchSettings_settingid(new BigInteger("0"));

    if(parameterMap.containsKey("researchthresh") || parameterMap.containsKey("subsetthresh") || parameterMap.containsKey("replacethresh")) {
      final MSIterativeSettings iSettings = new MSIterativeSettings();
      handleSimpleParameter(parameterMap, iSettings, "researchthresh", Type.DOUBLE, "setMSIterativeSettings_");
      handleSimpleParameter(parameterMap, iSettings, "subsetthresh", Type.DOUBLE, "setMSIterativeSettings_");
      handleSimpleParameter(parameterMap, iSettings, "replacethresh", Type.DOUBLE, "setMSIterativeSettings_");
      settings.setMSSearchSettings_iterativesettings(new MSSearchSettingsMSSearchSettings_iterativesettings(iSettings));
    }

    handleSimpleParameter(parameterMap, settings, "precursorcull", Type.INTEGER);
    handleSimpleParameter(parameterMap, settings, "nocorrelationscore", Type.BOOLEAN);
    handleSimpleParameter(parameterMap, settings, "probfollowingion", Type.DOUBLE);

    handleSimpleParameter(parameterMap, settings, "automassadjust", Type.DOUBLE);
    handleSimpleParameter(parameterMap, settings, "lomasscutoff", Type.DOUBLE);

    // Ignoring MSLibrarySettings

    // Ignoring othersettings

    return settings;
  }

  // TODO: Implement and test...
  public Map<String, String> inverse(final MSSearchSettings settings) {
    final Map<String, String> parameterMap = new HashMap<String, String>();
    final MSSearchSettingsMSSearchSettings_precursorsearchtype precursorsearchtype = settings.getMSSearchSettings_precursorsearchtype();
    if(precursorsearchtype != null && precursorsearchtype.getMSSearchType() != null) {
      parameterMap.put("precursorsearchtype", fromSearchType(precursorsearchtype.getMSSearchType()));
    }
    final MSSearchSettingsMSSearchSettings_productsearchtype productsearchtype = settings.getMSSearchSettings_productsearchtype();
    if(productsearchtype != null && productsearchtype.getMSSearchType() != null) {
      parameterMap.put("productsearchtype", fromSearchType(productsearchtype.getMSSearchType()));
    }
    final MSSearchSettingsMSSearchSettings_ionstosearch ionstosearch = settings.getMSSearchSettings_ionstosearch();
    if(ionstosearch != null && ionstosearch.getMSIonType() != null) {
      parameterMap.putAll(fromIons(ionstosearch.getMSIonType(), ION_PREFIX));
    }
    final MSSearchSettingsMSSearchSettings_noprolineions noprolineions = settings.getMSSearchSettings_noprolineions();
    if(noprolineions != null && noprolineions.getMSIonType() != null) {
      parameterMap.putAll(fromIons(noprolineions.getMSIonType(), NO_PROLINE_ION_PREFIX));
    }

    final MSSearchSettingsMSSearchSettings_zdep zdep = settings.getMSSearchSettings_zdep();
    if(zdep != null && zdep.getMSZdependence() != null) {
      parameterMap.put("zdep", binaryBigIntegerToString(zdep.getMSZdependence().get_value()));
    }

    if(parameterMap.containsKey("enzyme")) {
      settings.setMSSearchSettings_enzyme(new MSSearchSettingsMSSearchSettings_enzyme(new MSEnzymes(ENZYME_MAP.get(parameterMap.get("enzyme")))));
    }
    final MSSearchSettingsMSSearchSettings_enzyme enzyme = settings.getMSSearchSettings_enzyme();
    if(enzyme != null && enzyme.getMSEnzymes() != null) {
      parameterMap.put("enzyme", ENZYME_MAP.inverse().get(enzyme.getMSEnzymes().get_value().toString()));
    }

    final MSSearchSettingsMSSearchSettings_nmethionine nmethoione = settings.getMSSearchSettings_nmethionine();
    if(nmethoione != null && nmethoione.getValue() != null) {
      parameterMap.put("nmethionine", nmethoione.getValue().toString());
    }

    final MSSearchSettingsMSSearchSettings_reversesearch reversesearch = settings.getMSSearchSettings_reversesearch();
    if(reversesearch != null && reversesearch.getValue() != null) {
      parameterMap.put("reversesearch", reversesearch.getValue().toString());
    }

    // ChangeHandling parameters
    final MSSearchSettingsMSSearchSettings_chargehandling chargehandling = settings.getMSSearchSettings_chargehandling();
    if(chargehandling != null) {
      final MSChargeHandle chargeHandle = chargehandling.getMSChargeHandle();
      final MSChargeHandleMSChargeHandle_calcplusone calcplusone = chargeHandle.getMSChargeHandle_calcplusone();
      if(calcplusone != null && calcplusone.getMSCalcPlusOne() != null) {
        parameterMap.put("calcplusone", binaryBigIntegerToString(calcplusone.getMSCalcPlusOne().get_value()));
      }
      final MSChargeHandleMSChargeHandle_calccharge calccharge = chargeHandle.getMSChargeHandle_calccharge();
      if(calccharge != null && calccharge.getClass() != null) {
        parameterMap.put("calccharge", CALC_CHARGE_MAP.inverse().get(calccharge.getMSCalcCharge().get_value().toString()));
      }
      final MSChargeHandleMSChargeHandle_prodlesspre prodlesspre = chargeHandle.getMSChargeHandle_prodlesspre();
      if(prodlesspre != null && prodlesspre.getValue() != null) {
        parameterMap.put("prodlesspre", prodlesspre.getValue().toString());
      }
      inverseSimpleParameter(parameterMap, chargeHandle, "mincharge", Type.INTEGER, "getMSChargeHandle_");
      inverseSimpleParameter(parameterMap, chargeHandle, "maxcharge", Type.INTEGER, "getMSChargeHandle_");
      inverseSimpleParameter(parameterMap, chargeHandle, "considermult", Type.INTEGER, "getMSChargeHandle_");
      inverseSimpleParameter(parameterMap, chargeHandle, "plusone", Type.DOUBLE, "getMSChargeHandle_");
      inverseSimpleParameter(parameterMap, chargeHandle, "maxproductcharge", Type.INTEGER, "getMSChargeHandle_");
    }

    inverseSimpleParameter(parameterMap, settings, "peptol", Type.DOUBLE);
    inverseSimpleParameter(parameterMap, settings, "msmstol", Type.DOUBLE);

    inverseSimpleParameter(parameterMap, settings, "cutoff", Type.DOUBLE);
    inverseSimpleParameter(parameterMap, settings, "cutlo", Type.DOUBLE);
    inverseSimpleParameter(parameterMap, settings, "cuthi", Type.DOUBLE);
    inverseSimpleParameter(parameterMap, settings, "cutinc", Type.DOUBLE);

    inverseSimpleParameter(parameterMap, settings, "singlewin", Type.INTEGER);
    inverseSimpleParameter(parameterMap, settings, "doublewin", Type.INTEGER);
    inverseSimpleParameter(parameterMap, settings, "singlenum", Type.INTEGER);
    inverseSimpleParameter(parameterMap, settings, "doublenum", Type.INTEGER);

    inverseSimpleParameter(parameterMap, settings, "missedcleave", Type.INTEGER);
    inverseSimpleParameter(parameterMap, settings, "hitlistlen", Type.INTEGER);
    inverseSimpleParameter(parameterMap, settings, "tophitnum", Type.INTEGER);
    inverseSimpleParameter(parameterMap, settings, "minhit", Type.INTEGER);
    inverseSimpleParameter(parameterMap, settings, "minspectra", Type.INTEGER);
    inverseSimpleParameter(parameterMap, settings, "scale", Type.INTEGER);
    inverseSimpleParameter(parameterMap, settings, "maxmods", Type.INTEGER);

    inverseSimpleParameter(parameterMap, settings, "pseudocount", Type.INTEGER);
    inverseSimpleParameter(parameterMap, settings, "searchb1", Type.INVERSE_BOOLEAN);
    inverseSimpleParameter(parameterMap, settings, "searchctermproduct", Type.INVERSE_BOOLEAN);
    inverseSimpleParameter(parameterMap, settings, "maxproductions", Type.INTEGER);

    inverseSimpleParameter(parameterMap, settings, "maxnoenzyme", Type.INTEGER);
    inverseSimpleParameter(parameterMap, settings, "minnoenzyme", Type.INTEGER);
    inverseSimpleParameter(parameterMap, settings, "exactmass", Type.DOUBLE);

    inverseSimpleParameter(parameterMap, settings, "precursorcull", Type.INTEGER);
    inverseSimpleParameter(parameterMap, settings, "nocorrelationscore", Type.BOOLEAN);
    inverseSimpleParameter(parameterMap, settings, "probfollowingion", Type.DOUBLE);

    inverseSimpleParameter(parameterMap, settings, "automassadjust", Type.DOUBLE);
    inverseSimpleParameter(parameterMap, settings, "lomasscutoff", Type.DOUBLE);

    final MSSearchSettingsMSSearchSettings_iterativesettings iterativesettings = settings.getMSSearchSettings_iterativesettings();
    if(iterativesettings != null && iterativesettings.getMSIterativeSettings() != null) {
      final MSIterativeSettings iSettings = iterativesettings.getMSIterativeSettings();
      inverseSimpleParameter(parameterMap, iSettings, "researchthresh", Type.DOUBLE, "getMSIterativeSettings_");
      inverseSimpleParameter(parameterMap, iSettings, "subsetthresh", Type.DOUBLE, "getMSIterativeSettings_");
      inverseSimpleParameter(parameterMap, iSettings, "replacethresh", Type.DOUBLE, "getMSIterativeSettings_");
    }

    if(settings.getMSSearchSettings_fixed() != null) {
      parameterMap.put("fixed", fromModArray(settings.getMSSearchSettings_fixed().getMSMod()));
    }

    if(settings.getMSSearchSettings_variable() != null) {
      parameterMap.put("variable", fromModArray(settings.getMSSearchSettings_variable().getMSMod()));
    }

    return parameterMap;
  }
}
