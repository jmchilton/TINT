package edu.umn.msi.tropix.proteomics.client;

import java.util.Map;

import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

public class XTandemEnzymeUtilsTest {
  private static final Map<String, String> TANDEM_2_XML_ENZYMES = 
    ImmutableMap.<String, String>builder().put("argc", "[R]|{P}")
      .put("aspn", "[X]|[D]")
      .put("chymotrypsin", "[FMWY]|{P}")
      .put("clostripain", "[R]|[X]")
      .put("cnbr", "[M]|{P}")
      .put("elastase", "[AGILV]|{P}")
      .put("formicacid", "[D]|{P}")
      .put("gluc", "[DE]|{P}")
      .put("gluc_bicarb", "[E]|{P}")
      .put("iodosobenzoate", "[W]|[X]")
      // This seems like this was a typo
      //.put("lysc", "[K]|[P]")
      .put("lysc-p", "[K]|[X]")
      .put("lysn", "[X]|[K]")
      .put("lysn_promisc", "[X]|[AKRS]")
      .put("nonspecific", "[X]|[X]")
      .put("pepsina", "[FL]|[X]")
      .put("protein_endopeptidase", "[P]|[X]")
      .put("staph_protease", "[E]|[X]")
      .put("tca", "[FMWY]|{P},[KR]|{P},[X]|[D]")
      .put("trypsin", "[KR]|{P}")
      .put("trypsin/cnbr", "[KR]|{P},[M]|{P}")
      .put("trypsin_gluc", "[DEKR]|{P}")
      .put("trypsin_k", "[K]|{P}").build();
  
  @Test(groups = "unit")
  public void testMatchesTandem2XML() {
    final EnzymeUtils enzymeUtils = new EnzymeUtils();
    final XTandemEnzymeUtils xTandemEnzymeUtils = new XTandemEnzymeUtils();
    for(final String enzymeName : enzymeUtils.getEnzymeNames()) {
      final String cleavageSite = xTandemEnzymeUtils.convertEnzymeToCleavageSite(enzymeName);
      final String pepXmlName = enzymeUtils.getEnzyme(enzymeName).getPepXmlName();
      
      final String tandem2XmlCleavageSite = TANDEM_2_XML_ENZYMES.get(pepXmlName);
      if(tandem2XmlCleavageSite == null) {
        continue;
      }
      assert tandem2XmlCleavageSite.equals(cleavageSite) 
        : String.format("Expect Tandem2Out cleavage site [%s],  but produced [%s] for enzyme %s.", tandem2XmlCleavageSite, cleavageSite, enzymeName);
    }
  }
  
  
}
