package edu.umn.msi.tropix.proteomics.xtandem.impl;

import java.util.Map;

import org.springframework.util.StringUtils;

import com.google.common.collect.Maps;

import edu.umn.msi.tropix.proteomics.client.XTandemEnzymeUtils;
import edu.umn.msi.tropix.proteomics.enzyme.Enzyme;
import edu.umn.msi.tropix.proteomics.enzyme.TermInfo;
import edu.umn.msi.tropix.proteomics.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SampleEnzyme;
import edu.umn.msi.tropix.proteomics.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SampleEnzyme.Specificity;

class PepXmlCleavageSites {
  private static final XTandemEnzymeUtils ENZYME_UTILS = new XTandemEnzymeUtils();
  private static final String DEFAULT_ENZYME_NAME = "trypsin";
  private static final Map<String, String> CLEAVAGE_SITE_TO_PEPXML_ENZYME_NAME = Maps.newHashMap();
  
  
  
  static String getEnzymeName(final String cleavageSite) {    
    String name = CLEAVAGE_SITE_TO_PEPXML_ENZYME_NAME.get(cleavageSite);
    if(name == null) {
      name = DEFAULT_ENZYME_NAME;
    }
    return name;
  }
  
  static SampleEnzyme getEnzymeFromCleavageSite(final String cleavageSite) {
    final Enzyme enzyme = ENZYME_UTILS.convertCleavageSiteToEnzyme(cleavageSite);
    final SampleEnzyme sampleEnzyme = new SampleEnzyme();    
    sampleEnzyme.setName(enzyme.getPepXmlName());
    final Specificity specifity = new Specificity();
    specifity.setCut(residuesToCut(enzyme.getDoCleave()));
    specifity.setNoCut(residuesToCut(enzyme.getDoNotCleave()));
    specifity.setSense(getSense(enzyme.getTerm()));
    sampleEnzyme.getSpecificity().add(specifity);
    return sampleEnzyme;
  }
  
  private static String residuesToCut(final String residues) {
    return StringUtils.hasText(residues) ? residues : null;
  }
  
  private static String getSense(final TermInfo termInfo) {
    if(termInfo.equals(TermInfo.CTERM)) {
      return "C";
    } else {
      return "N";
    }
  }

  static {  
    CLEAVAGE_SITE_TO_PEPXML_ENZYME_NAME.put("[R]|{P}", "argc");
    CLEAVAGE_SITE_TO_PEPXML_ENZYME_NAME.put("[X]|[D]", "aspn");
    CLEAVAGE_SITE_TO_PEPXML_ENZYME_NAME.put("[FMWY]|{P}", "chymotrypsin");
    CLEAVAGE_SITE_TO_PEPXML_ENZYME_NAME.put("[R]|[X]", "clostripain");
    CLEAVAGE_SITE_TO_PEPXML_ENZYME_NAME.put("[M]|{P}", "cnbr");
    CLEAVAGE_SITE_TO_PEPXML_ENZYME_NAME.put("[AGILV]|{P}", "elastase");
    CLEAVAGE_SITE_TO_PEPXML_ENZYME_NAME.put("[D]|{P}", "formicacid");
    CLEAVAGE_SITE_TO_PEPXML_ENZYME_NAME.put("[DE]|{P}", "gluc");
    CLEAVAGE_SITE_TO_PEPXML_ENZYME_NAME.put("[E]|{P}", "gluc_bicarb");
    CLEAVAGE_SITE_TO_PEPXML_ENZYME_NAME.put("[W]|[X]", "iodosobenzoate");
    CLEAVAGE_SITE_TO_PEPXML_ENZYME_NAME.put("[K]|[P]", "lysc");
    CLEAVAGE_SITE_TO_PEPXML_ENZYME_NAME.put("[K]|[X]", "lysc-p");
    CLEAVAGE_SITE_TO_PEPXML_ENZYME_NAME.put("[X]|[K]", "lysn");
    CLEAVAGE_SITE_TO_PEPXML_ENZYME_NAME.put("[X]|[AKRS]", "lysn_promisc");
    CLEAVAGE_SITE_TO_PEPXML_ENZYME_NAME.put("[X]|[X]", "nonspecific");
    CLEAVAGE_SITE_TO_PEPXML_ENZYME_NAME.put("[FL]|[X]", "pepsina");
    CLEAVAGE_SITE_TO_PEPXML_ENZYME_NAME.put("[P]|[X]", "protein_endopeptidase");
    CLEAVAGE_SITE_TO_PEPXML_ENZYME_NAME.put("[E]|[X]", "staph_protease");
    CLEAVAGE_SITE_TO_PEPXML_ENZYME_NAME.put("[KR]|{P}", "trypsin");
    CLEAVAGE_SITE_TO_PEPXML_ENZYME_NAME.put("[DEKR]|{P}", "trypsin_gluc");
    CLEAVAGE_SITE_TO_PEPXML_ENZYME_NAME.put("[K]|{P}", "trypsin_k");
    CLEAVAGE_SITE_TO_PEPXML_ENZYME_NAME.put("[R]|{P}", "trypsin_r");
  }


}
