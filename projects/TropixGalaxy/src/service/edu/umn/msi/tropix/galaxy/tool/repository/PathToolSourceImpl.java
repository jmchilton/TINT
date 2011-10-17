package edu.umn.msi.tropix.galaxy.tool.repository;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.io.FilenameUtils;
import org.python.google.common.collect.Lists;
import org.springframework.util.StringUtils;

import com.google.common.base.Preconditions;

import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.galaxy.GalaxyDataUtils;
import edu.umn.msi.tropix.galaxy.tool.ConfigFile;
import edu.umn.msi.tropix.galaxy.tool.ConfigFileType;
import edu.umn.msi.tropix.galaxy.tool.ConfigFiles;
import edu.umn.msi.tropix.galaxy.tool.InputType;
import edu.umn.msi.tropix.galaxy.tool.Test;
import edu.umn.msi.tropix.galaxy.tool.TestParam;
import edu.umn.msi.tropix.galaxy.tool.Tests;
import edu.umn.msi.tropix.galaxy.tool.Tool;
import edu.umn.msi.tropix.galaxy.xml.GalaxyXmlUtils;

public abstract class PathToolSourceImpl implements ToolSource {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  
  protected abstract List<String> getToolXmlPaths();
  
  @Nullable
  protected abstract InputStream getWrapperInputStream(final String path);
  
  @Nullable
  protected abstract InputStream getTestDataInputStream(final String path);
    
  public List<Tool> getTools() {
    final List<String> paths = getToolXmlPaths();
    final List<Tool> tools = Lists.newArrayListWithExpectedSize(paths.size());
    for(final String path : paths) {
      tools.add(getTool(path));
    }
    return tools;
  }
  
  private static String getWrapperFromCommand(final String command) {    
    final String trimmedCommand = command.trim();
    Preconditions.checkState(StringUtils.hasText(trimmedCommand));
    return trimmedCommand.split("\\s+")[0];
  }
  
  private Tool getTool(final String path) {
    final InputStream toolXmlInputStream = getWrapperInputStream(path);
    Preconditions.checkNotNull(toolXmlInputStream, String.format("Failed to load InputStream for path %s", path));
    final Tool tool = GalaxyXmlUtils.load(toolXmlInputStream);
    populateCommand(path, tool);
    populateTestData(path, tool);
    return tool;
  }

  private void populateTestData(final String path, final Tool tool) {
    final Map<String, InputType> inputTypeMap = GalaxyDataUtils.buildFlatParamMap(tool);
    final Tests tests = tool.getTests();
    if(tests == null) {
      return;
    }
    for(final Test test : tests.getTest()) {
      for(TestParam testParam : test.getParam()) {
        if(GalaxyDataUtils.DATA_PARAM_PREDICATE.apply(inputTypeMap.get(testParam.getName()))) {
          if(testParam.getEmbeddedValue() == null) {
            final String embeddedValue = getEmbeddedValue(testParam.getValue());
            testParam.setEmbeddedValue(embeddedValue);
          }
        }        
      }
    }
  }
  
  

  private String getEmbeddedValue(final String path) {
    final InputStream inputStream = getTestDataInputStream(path);
    return InputContexts.toString(InputContexts.forInputStream(inputStream));
  }
  
  
  private void populateCommand(final String path, final Tool tool) {
    final String command = tool.getCommand().getValue();
    final String wrapper = getWrapperFromCommand(command);
    final String expectedWrapperPath = FilenameUtils.normalize(FilenameUtils.getFullPath(path) + "/" + wrapper);
    final InputStream wrapperInputStream = getWrapperInputStream(expectedWrapperPath);
    if(wrapperInputStream != null) {
      final ConfigFile configFile = new ConfigFile();
      configFile.setType(ConfigFileType.LITERAL);
      final String contents = IO_UTILS.toString(wrapperInputStream);
      configFile.setName(wrapper);
      configFile.setValue(contents);
      if(tool.getConfigfiles() == null) {
        tool.setConfigfiles(new ConfigFiles());
      }
      tool.getConfigfiles().getConfigfile().add(configFile);
    }
  }
  
}
