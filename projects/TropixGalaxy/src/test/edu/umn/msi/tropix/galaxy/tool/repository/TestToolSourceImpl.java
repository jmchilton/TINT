package edu.umn.msi.tropix.galaxy.tool.repository;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.galaxy.tool.Inputs;
import edu.umn.msi.tropix.galaxy.tool.Param;
import edu.umn.msi.tropix.galaxy.tool.Tool;

public class TestToolSourceImpl implements ToolSource {

  private final Tool testTool;
  
  public TestToolSourceImpl() {
    testTool = new Tool();
    testTool.setId("test-tool-id");
    final Inputs inputs = new Inputs();
    testTool.setInputs(inputs);
    addParam("test1");
  }

  private void addParam(final String name) {
    final Param testParam = new Param();
    testParam.setName(name);
    testTool.getInputs().getInputElement().add(testParam);
  }
  
  public void addParam() {
    addParam("test2");
  }
  
  public Tool getTool() {
    return testTool;
  }
  
  public Iterable<Tool> getTools() {
    return Lists.newArrayList(testTool);
  }

}
