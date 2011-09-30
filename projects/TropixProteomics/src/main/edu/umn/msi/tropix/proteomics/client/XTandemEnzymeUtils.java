package edu.umn.msi.tropix.proteomics.client;

import edu.umn.msi.tropix.proteomics.enzyme.Enzyme;

/**
 * This class encapsulates logic for interfacing the way Tropix describes enzymes and the way X! Tandem does. 
 * 
 * @author John Chilton
 *
 */
public class XTandemEnzymeUtils {
  private final EnzymeUtils enzymeUtils;
  
  public XTandemEnzymeUtils() {
    this.enzymeUtils = new EnzymeUtils();
  }

  public String convertEnzymeToCleavageSite(final String enzymeName) {
    final String cleavageResidues = enzymeUtils.getCleaveResidues(enzymeName);
    final String notCleavageResidues = enzymeUtils.getNotCleaveResidues(enzymeName);
    final String term = enzymeUtils.getTerm(enzymeName);
  
    final String s1 = cleavageResidues.equals("") ? "[X]" : "[" + cleavageResidues + "]";
    final String s2 = notCleavageResidues.equals("") ? "[X]" : "{" + notCleavageResidues + "}";
  
    String cleavageSite;
    if(term.equals("n")) {
      cleavageSite = s2 + "|" + s1;
    } else {
      cleavageSite = s1 + "|" + s2;
    }
    return cleavageSite;
  }

  public String convertCleavageSiteToEnzymeName(final String cleavageSite) {
    final Enzyme enzyme = convertCleavageSiteToEnzyme(cleavageSite);
    return enzyme.getName();
  }

  public Enzyme convertCleavageSiteToEnzyme(final String cleavageSite) {
    Enzyme enzyme = null;
    for(final String enzymeName : enzymeUtils.getEnzymeNames()) {
      if(convertEnzymeToCleavageSite(enzymeName).equals(cleavageSite)) {
        enzyme = enzymeUtils.getEnzyme(enzymeName);
        break;
      }
    }
    return enzyme;
  }

}
