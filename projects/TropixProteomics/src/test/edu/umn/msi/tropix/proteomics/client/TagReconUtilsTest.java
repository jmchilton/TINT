package edu.umn.msi.tropix.proteomics.client;

import java.util.Map;

import org.testng.annotations.Test;

import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.proteomics.bumbershoot.parameters.DirecTagParameters;
import edu.umn.msi.tropix.proteomics.bumbershoot.parameters.TagParameters;
import edu.umn.msi.tropix.proteomics.bumbershoot.parameters.TagReconParameters;

public class TagReconUtilsTest {
  private TagReconParametersTypeSerializer tagReconParametersTypeSerializer = new TagReconParametersTypeSerializer();

  @Test(groups = "unit")
  public void testSerialization() {
    final TagParameters tagParameters = new TagParameters();
    final DirecTagParameters direcTagParameters = new DirecTagParameters();
    tagParameters.setDirecTagParameters(direcTagParameters);
    direcTagParameters.setDeisotopingMode(2);

    final String serializedParameters = TagReconParametersTypeSerializer.serialize(tagParameters);
    final InputContext serializedParametersContext = InputContexts.forString(serializedParameters);
    final TagParameters convertedParameters = tagReconParametersTypeSerializer.loadRawParameters(serializedParametersContext);
    assert convertedParameters != null;
    assert convertedParameters.getDirecTagParameters().getDeisotopingMode() == 2;
  }

  @Test(groups = "unit")
  public void testToMapHandlesNull() {
    final TagParameters tagParameters = new TagParameters();
    toMap(tagParameters);
  }

  private Map<String, String> toMap(final TagParameters tagParameters) {
    final String serializedTagReconParameters = TagReconParametersTypeSerializer.serialize(tagParameters);
    final InputContext parameterContext = InputContexts.forString(serializedTagReconParameters);
    return tagReconParametersTypeSerializer.loadParametersAndExpandMap(parameterContext);
  }

  @Test(groups = "unit")
  public void testToMap() {
    final TagParameters tagParameters = new TagParameters();
    final DirecTagParameters direcTagParameters = new DirecTagParameters();
    direcTagParameters.setDeisotopingMode(1);
    direcTagParameters.setMaxResults(3);
    tagParameters.setDirecTagParameters(direcTagParameters);

    final TagReconParameters tagReconParameters = new TagReconParameters();
    tagReconParameters.setCleavageRules("Trypsin/P");
    tagReconParameters.setMaxResults(6);
    tagParameters.setTagReconParameters(tagReconParameters);

    final Map<String, String> parameterMap = toMap(tagParameters);

    assert parameterMap.get("deisotopingMode").equals("1");
    assert parameterMap.get("maxResults") == null;
    assert parameterMap.get("direcTagMaxResults").equals("3");
    assert parameterMap.get("cleavageRules").equals("Trypsin/P");
    assert parameterMap.get("tagReconMaxResults").equals("6");
  }

  @Test(groups = "unit")
  public void testFromMap() {
    final Map<String, String> parameterMap = Maps.newHashMap();
    parameterMap.put("tagLength", "4");
    parameterMap.put("direcTagMaxResults", "7");
    parameterMap.put("tagReconMaxResults", "8");
    parameterMap.put("deisotopingMode", "1");

    final String serializedParameters = tagReconParametersTypeSerializer.serializeParameterMap(parameterMap);
    final TagParameters tagParameters = tagReconParametersTypeSerializer.loadRawParameters(InputContexts.forString(serializedParameters));
    assert tagParameters.getDirecTagParameters().getDeisotopingMode() == 1;
    assert tagParameters.getDirecTagParameters().getMaxResults() == 7;
    assert tagParameters.getDirecTagParameters().getTagLength() == 4;

    assert tagParameters.getTagReconParameters().getDeisotopingMode() == 1;
    assert tagParameters.getTagReconParameters().getMaxResults() == 8;
  }

}
