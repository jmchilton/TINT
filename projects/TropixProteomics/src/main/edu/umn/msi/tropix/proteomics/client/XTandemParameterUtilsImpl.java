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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.ManagedBean;
import javax.inject.Named;

import org.springframework.util.StringUtils;

@ManagedBean
@Named("xTandemParameterUtils")
class XTandemParameterUtilsImpl implements ParameterMapUtils {
  private static final XTandemEnzymeUtils ENZYME_UTILS = new XTandemEnzymeUtils();

  public void toRaw(final Map<String, String> guiParameters) {
    condenseModifications(guiParameters, "fixed", "residueModificationMass");
    condenseModifications(guiParameters, "potential", "residuePotentialModificationMass");
    condenseModifications(guiParameters, "refinePotMod", "refinePotentialModificationMass");
    convertEnzymeToCleavageSite("enzyme", "proteinCleavageSite", guiParameters);
  }

  public void fromRaw(final Map<String, String> guiParameters) {
    expandModifications(guiParameters, "fixed", "residueModificationMass");
    expandModifications(guiParameters, "potential", "residuePotentialModificationMass");
    expandModifications(guiParameters, "refinePotMod", "refinePotentialModificationMass");
    convertCleavageSiteToEnzyme("enzyme", "proteinCleavageSite", guiParameters);
  }

  public void condenseModifications(final Map<String, String> expandedMap, final String prefix, final String modKey) {
    final StringBuilder condensedValue = new StringBuilder();
    boolean firstAddition = true;
    final Set<String> keysToRemove = new HashSet<String>();
    for(final Map.Entry<String, String> entry : expandedMap.entrySet()) {
      final String key = entry.getKey();
      final String value = entry.getValue();
      if(key.startsWith(prefix)) {
        keysToRemove.add(key);
        if(!StringUtils.hasText(value)) {
          continue;
        }

        final Double doubleValue;
        try {
          doubleValue = Double.parseDouble(value);
        } catch(final NumberFormatException nfe) {
          throw new NumberFormatException(String.format("Failed to convert value %s for key %s", value, key));
        }
        if(doubleValue == null || doubleValue == 0.0d) {
          continue;
        }
        if(firstAddition) {
          firstAddition = false;
        } else {
          condensedValue.append(",");
        }
        condensedValue.append(value + "@" + key.substring(prefix.length()));
      }
    }
    expandedMap.keySet().removeAll(keysToRemove);
    expandedMap.put(modKey, condensedValue.toString());
  }

  public void expandModifications(final Map<String, String> condensedMap, final String prefix, final String modKey) {
    final String condensedString = condensedMap.get(modKey);
    condensedMap.remove(modKey);
    if(condensedString == null || condensedString.trim().equals("")) {
      return;
    }
    String[] mods;
    if(condensedString.contains(",")) {
      mods = condensedString.split(",");
    } else {
      mods = new String[] {condensedString};
    }
    for(final String mod : mods) {
      final int atIdx = mod.indexOf("@");
      final String residue = mod.substring(atIdx + 1);
      final String value = mod.substring(0, atIdx);
      condensedMap.put(prefix + residue, value);
    }
  }

  public void convertCleavageSiteToEnzyme(final String enzymeKey, final String cleavageSiteKey, final Map<String, String> map) {
    final String cleavageSite = map.remove(cleavageSiteKey);
    final String enzymeName = ENZYME_UTILS.convertCleavageSiteToEnzymeName(cleavageSite);
    map.put(enzymeKey, enzymeName);
  }

  public void convertEnzymeToCleavageSite(final String enzymeKey, final String cleavageSiteKey, final Map<String, String> map) {
    final String enzymeName = map.remove(enzymeKey);
    final String cleavageSite = ENZYME_UTILS.convertEnzymeToCleavageSite(enzymeName);
    map.put(cleavageSiteKey, cleavageSite);
  }

}
