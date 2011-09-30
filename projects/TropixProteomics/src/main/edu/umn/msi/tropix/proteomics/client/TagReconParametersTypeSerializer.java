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

import org.springframework.util.StringUtils;

import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.models.proteomics.IdentificationType;
import edu.umn.msi.tropix.proteomics.bumbershoot.parameters.DirecTagParameters;
import edu.umn.msi.tropix.proteomics.bumbershoot.parameters.TagParameters;
import edu.umn.msi.tropix.proteomics.bumbershoot.parameters.TagReconParameters;
import edu.umn.msi.tropix.proteomics.parameters.ParameterUtils;

@ManagedBean
class TagReconParametersTypeSerializer extends IdentificationParametersTypeSerializer {

  TagReconParametersTypeSerializer() {
    super(IdentificationType.TAGRECON);
  }

  public TagParameters loadRawParameters(final InputContext inputContext) {
    return AxisXmlParameterUtils.deserialize(inputContext, TagParameters.class);
  }

  static String serialize(final TagParameters parameters) {
    return AxisXmlParameterUtils.serialize(parameters, TagParameters.getTypeDesc().getXmlType());
  }

  Map<String, String> loadParametersAndExpandMap(final InputContext parameterContext) {
    final TagParameters tagParameters = loadRawParameters(parameterContext);
    final Map<String, String> parametersMap = Maps.newHashMap();
    final DirecTagParameters direcTagParameters = tagParameters.getDirecTagParameters();
    if(direcTagParameters != null) {
      ParameterUtils.setMapFromParameters(direcTagParameters, parametersMap);
      final Integer maxResults = direcTagParameters.getMaxResults();
      if(maxResults != null) {
        parametersMap.put("direcTagMaxResults", Integer.toString(maxResults));
      }
    }
    final TagReconParameters tagReconParameters = tagParameters.getTagReconParameters();
    if(tagReconParameters != null) {
      ParameterUtils.setMapFromParameters(tagReconParameters, parametersMap);
      final Integer maxResults = tagReconParameters.getMaxResults();
      if(maxResults != null) {
        parametersMap.put("tagReconMaxResults", Integer.toString(maxResults));
      }
    }
    parametersMap.remove("maxResults");
    return parametersMap;
  }

  String serializeParameterMap(final Map<String, String> parameterMap) {
    final TagParameters tagParameters = new TagParameters();
    final DirecTagParameters direcTagParameters = new DirecTagParameters();
    ParameterUtils.setParametersFromMap(parameterMap, direcTagParameters);
    final String direcTagMaxResultsStr = parameterMap.get("direcTagMaxResults");
    if(StringUtils.hasText(direcTagMaxResultsStr)) {
      direcTagParameters.setMaxResults(Integer.parseInt(direcTagMaxResultsStr));
    }
    tagParameters.setDirecTagParameters(direcTagParameters);

    final TagReconParameters tagReconParameters = new TagReconParameters();
    ParameterUtils.setParametersFromMap(parameterMap, tagReconParameters);
    tagParameters.setTagReconParameters(tagReconParameters);
    final String tagReconMaxResultsStr = parameterMap.get("tagReconMaxResults");
    if(StringUtils.hasText(tagReconMaxResultsStr)) {
      tagReconParameters.setMaxResults(Integer.parseInt(tagReconMaxResultsStr));
    }
    return serialize(tagParameters);
  }
}
