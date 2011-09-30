package edu.umn.msi.tropix.galaxy.service;

import java.util.Map;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Maps;

import edu.umn.msi.tropix.galaxy.inputs.Input;
import edu.umn.msi.tropix.galaxy.inputs.RootInput;
import edu.umn.msi.tropix.galaxy.tool.Data;
import edu.umn.msi.tropix.galaxy.tool.Inputs;
import edu.umn.msi.tropix.galaxy.tool.Outputs;
import edu.umn.msi.tropix.galaxy.tool.Param;
import edu.umn.msi.tropix.galaxy.tool.ParamType;
import edu.umn.msi.tropix.galaxy.tool.Tool;

public class ContextBuilderTest {
  private Tool tool;
  private RootInput rootInput;
  private Outputs outputs;
  private Inputs inputs;
  private Map<String, String> outputMap;
  private Context context;
  
  @BeforeMethod(groups = "unit")
  public void init() {
    outputs = new Outputs();
    inputs = new Inputs();
    
    tool = new Tool();
    tool.setOutputs(outputs);
    tool.setInputs(inputs);
    
    rootInput = new RootInput();
    outputMap = Maps.newHashMap();
    context = null;
  }

  private void convert() {
    context = new ContextBuilder().buildContext(tool, rootInput, outputMap);
  }
  
  @Test(groups = "unit")
  public void testInput() {
    final Param param = new Param();
    param.setName("input1");
    param.setType(ParamType.TEXT);
    inputs.getInputElement().add(param);
    
    final Input input = new Input();
    input.setName("input1");
    input.setValue("val");
    rootInput.getInput().add(input);
    
    convert();
    
    final Context subContext = getSubContext("input1");
    assert subContext.toString().equals("val");
  }
  
  @Test(groups = "unit")
  public void testOutputs() {
    final Data data = new Data();
    data.setFormat("mgf");
    data.setLabel("output label");
    data.setName("outputName");
    outputs.getData().add(data);
    
    outputMap.put("outputName", "path/to/file");
    
    convert();
    
    final Context outputSubContext = getSubContext("outputName");
    assert outputSubContext.toString().equals("path/to/file");
    assert outputSubContext.get("format").equals("mgf");
    assert outputSubContext.get("label").equals("output label");
  }
  
  private Context getSubContext(final String name) {
    return (Context) context.get(name);
  }
  
}
