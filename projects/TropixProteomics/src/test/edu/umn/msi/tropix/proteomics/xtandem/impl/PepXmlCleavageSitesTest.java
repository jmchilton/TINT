package edu.umn.msi.tropix.proteomics.xtandem.impl;

import org.testng.annotations.Test;

import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.proteomics.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SampleEnzyme;
import edu.umn.msi.tropix.proteomics.pepxml.MsmsPipelineAnalysis.MsmsRunSummary.SampleEnzyme.Specificity;

public class PepXmlCleavageSitesTest {

  @Test(groups = "unit")
  public void testUnknownDefaultsToTrypsin() {
    assert PepXmlCleavageSites.getEnzymeName("[L]|[R]").equals("trypsin");
  }

  @Test(groups = "unit")
  public void testDirectMappings() {
    assert PepXmlCleavageSites.getEnzymeName("[X]|[D]").equals("aspn") : PepXmlCleavageSites.getEnzymeName("[X]|[D]");
  }
  
  
  /* 
   * Tandem2XML output for [KR]|{P}
   * <sample_enzyme name="trypsin"><specificity cut="KR" no_cut="P" sense="C"/></sample_enzyme>
   */
  @Test(groups = "unit")
  public void testTrypsin() {
    final SampleEnzyme trypsinEnzyme = getSampleEnzymeForCleavageSite("[KR]|{P}");
    assert trypsinEnzyme.getName().equals("trypsin");
    final Specificity specifity = Iterables.getOnlyElement(trypsinEnzyme.getSpecificity());
    assert specifity.getCut().equals("KR");
    assert specifity.getNoCut().equals("P");
    assert specifity.getSense().equals("C");    
  }

  private SampleEnzyme getSampleEnzymeForCleavageSite(final String cleavageSite) {
    final SampleEnzyme trypsinEnzyme = PepXmlCleavageSites.getEnzymeFromCleavageSite(cleavageSite);
    return trypsinEnzyme;
  }
  
  @Test(groups = "unit")
  public void testAspn() {
    final SampleEnzyme aspnEnzyme = getSampleEnzymeForCleavageSite("[X]|[D]");
    assert aspnEnzyme.getName().equals("aspn");
    final Specificity specifity = Iterables.getOnlyElement(aspnEnzyme.getSpecificity());
    assert specifity.getCut().equals("D");
    assert specifity.getNoCut() == null;
    assert specifity.getSense().equals("N");    
  }

  /* 
   * Tandem2XML output for [X]|[D]
   * <sample_enzyme name="aspn"><specificity cut="D" sense="N"/></sample_enzyme>
   */
  
  /* 
   * Tandem2XML output for [FMWY]|{P}
   *   <sample_enzyme name="chymotrypsin"><specificity cut="YWFM" no_cut="P" sense="C"/></sample_enzyme>
   */

  /*
   * Tandem2XML output for [M]|{P}
   *   <sample_enzyme name="cnbr"><specificity cut="M" no_cut="P" sense="C"/></sample_enzyme>
   */
  
  /*
   * Tandem2XML output for [X]|[AKRS] 
   *  <sample_enzyme name="lysn_promisc"><specificity cut="KR" sense="N"/></sample_enzyme>
   */

  /*
   * Tandem2XML output for [X]|[X]
   * <sample_enzyme name="nonspecific"><specificity cut="" sense="C"/></sample_enzyme>
   */
  

  
}
