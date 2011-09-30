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

import java.util.Map;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import edu.umn.msi.tropix.models.sequest.SequestParameters;

@ManagedBean @Named("sequestParameterUtils")
class SequestParameterUtilsImpl implements ParameterMapUtils {
  private EnzymeUtils enzymeUtils = new EnzymeUtils();

  public void toRaw(final Map<String, String> parametersMap) {
    expandEnzymeParameters("enzymeName", parametersMap);    
  }
  
  public void fromRaw(final Map<String, String> parametersMap) {
  }
  
  
  /**
   * Using enzymeUtils instance, expands a simple enzyme name parameter into four parameters specifing the enzyme name, whether it is a C Terminal enzyme, and the cleaving and non cleaving residues of that enzymes.
   * 
   * @param enzymeNameKey
   *          Name of the parameter specifing the enzyme name in the parameter map.
   * @param parameterMap
   *          Parameter map the old parameter is removed from and new ones are added too.
   */
  public void expandEnzymeParameters(final String enzymeNameKey, final Map<String, String> parameterMap) {
    final String enzymeName = parameterMap.get(enzymeNameKey);
    if(enzymeName == null) {
      throw new IllegalArgumentException("enzymeNameKey not found!");
    }
    parameterMap.remove(enzymeNameKey);
    parameterMap.put("enzymeName", enzymeName);
    parameterMap.put("enzymeCTerm", Boolean.toString("c".equals(enzymeUtils.getTerm(enzymeName))));
    parameterMap.put("enzymeCleave", convertResidues(enzymeUtils.getCleaveResidues(enzymeName)));
    parameterMap.put("enzymeNoCleave", convertResidues(enzymeUtils.getNotCleaveResidues(enzymeName)));
  }

  /**
   * Converts string of residues into format Sequest wants.
   * 
   * @param residues
   *          String of residues such as "kac" or "a" or ""
   * @return Same residues as given but in the format Sequest wants, i.e. "-" instead of "".
   */
  protected String convertResidues(final String residues) {
    return residues.length() > 0 ? residues : "-";
  }

  /**
   * Sets four enzyme parameters in the sequest parameters bean using the the EnzymeUtils object and the given enzymeName. These parameters are enzymeName, enzymeCTerm, enzymeCleave, and enzymeNoCleave.
   * 
   * @param enzymeName
   * @param sequestParameters
   */
  public void setEnzymeParameters(final String enzymeName, final SequestParameters sequestParameters) {
    // NOTICE: If you change the name of these method calls, change the name of the parameters
    // in Map version above.
    sequestParameters.setEnzymeName(enzymeName);
    sequestParameters.setEnzymeCTerm("c".equals(enzymeUtils.getTerm(enzymeName)));
    sequestParameters.setEnzymeCleave(convertResidues(enzymeUtils.getCleaveResidues(enzymeName)));
    sequestParameters.setEnzymeNoCleave(convertResidues(enzymeUtils.getNotCleaveResidues(enzymeName)));
  }

  @Inject
  void setEnzymeUtils(final EnzymeUtils enzymeUtils) {
    this.enzymeUtils = enzymeUtils;
  }

}
