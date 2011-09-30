package edu.umn.msi.tropix.galaxy.tool.repository;

import java.net.URL;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.galaxy.tool.ConfigFile;
import edu.umn.msi.tropix.galaxy.tool.ConfigFileType;
import edu.umn.msi.tropix.galaxy.tool.TestParam;
import edu.umn.msi.tropix.galaxy.tool.Tool;

public class ClassPathToolSourceImplTest {

  @Test(groups = "unit")
  public void testSimpleLoad() {
    final ToolSource toolSource = new ClassPathToolSourceImpl(Lists.newArrayList("/edu/umn/msi/tropix/galaxy/tool/repository/simple.xml"));
    final Tool tool = Iterables.getOnlyElement(toolSource.getTools());
    assert tool.getCommand().getInterpreter().equals("python");
    final ConfigFile firstConfigFile = tool.getConfigfiles().getConfigfile().get(0);
    assert firstConfigFile.getType() == ConfigFileType.LITERAL;
    Assert.assertEquals(firstConfigFile.getName(), "simple_wrapper.py");
    Assert.assertEquals(firstConfigFile.getValue().trim(), "SIMPLE WRAPPER CONTENTS");
  }
  
  @Test(groups = "unit")
  public void testTestParams() {
    final ToolSource toolSource = new ClassPathToolSourceImpl(Lists.newArrayList("/edu/umn/msi/tropix/galaxy/tool/repository/catWrapper.xml"));
    final Tool tool = Iterables.getOnlyElement(toolSource.getTools());
    
    final edu.umn.msi.tropix.galaxy.tool.Test test = tool.getTests().getTest().get(0);
    final TestParam param = test.getParam().get(0);
    final URL inputBed = ClassPathToolSourceImplTest.class.getResource("1.bed");
    final String inputBedContents = InputContexts.toString(InputContexts.forUrl(inputBed));
    assert param.getEmbeddedValue().equals(inputBedContents);
  }
  
}
