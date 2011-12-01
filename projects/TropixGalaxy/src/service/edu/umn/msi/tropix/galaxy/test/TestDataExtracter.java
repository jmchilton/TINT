package edu.umn.msi.tropix.galaxy.test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.galaxy.GalaxyDataUtils;
import edu.umn.msi.tropix.galaxy.inputs.Input;
import edu.umn.msi.tropix.galaxy.inputs.RootInput;
import edu.umn.msi.tropix.galaxy.test.TestDataExtracter.TestData.TestInputFile;
import edu.umn.msi.tropix.galaxy.tool.InputType;
import edu.umn.msi.tropix.galaxy.tool.Param;
import edu.umn.msi.tropix.galaxy.tool.ParamType;
import edu.umn.msi.tropix.galaxy.tool.Test;
import edu.umn.msi.tropix.galaxy.tool.TestOutput;
import edu.umn.msi.tropix.galaxy.tool.TestParam;
import edu.umn.msi.tropix.galaxy.tool.Tests;
import edu.umn.msi.tropix.galaxy.tool.Tool;
import edu.umn.msi.tropix.galaxy.tool.repository.GalaxyToolRepository;

@ManagedBean
public class TestDataExtracter {

  static class OutputChecker implements Closure<byte[]> {
    private TestOutput testOutput;

    public OutputChecker(final TestOutput testOutput) {
      this.testOutput = testOutput;
    }

    public void apply(final byte[] input) {
      final String expectedContents = testOutput.getEmbeddedValue();
      if(!Arrays.equals(input, expectedContents.getBytes())) {
        final String format = "Expected output file contents [%s], actual file contents [%s].";
        final String message = String.format(format, expectedContents, new String(input));
        throw new AssertionError(message);
      }
    }

  }

  public static class TestData {
    public static class TestInputFile {
      public byte[] getContents() {
        return contents;
      }

      public void setContents(final byte[] contents) {
        this.contents = contents;
      }

      public String getInputFileName() {
        return inputFileName;
      }

      public void setInputFileName(final String inputFileName) {
        this.inputFileName = inputFileName;
      }

      private byte[] contents;

      private String inputFileName;

    }

    private RootInput rootInput;
    private List<TestInputFile> inputFiles = Lists.newLinkedList();
    private List<Closure<byte[]>> outputFileChecker = Lists.newArrayList();

    public RootInput getRootInput() {
      return rootInput;
    }

    public void setRootInput(final RootInput rootInput) {
      this.rootInput = rootInput;
    }

    public List<TestInputFile> getInputFiles() {
      return inputFiles;
    }

    public void setInputFiles(final List<TestInputFile> inputFiles) {
      this.inputFiles = inputFiles;
    }

    public List<Closure<byte[]>> getOutputFileChecker() {
      return outputFileChecker;
    }

    public void setOutputFileChecker(final List<Closure<byte[]>> outputFileChecker) {
      this.outputFileChecker = outputFileChecker;
    }

  }

  @Inject
  public TestDataExtracter(final GalaxyToolRepository galaxyToolRepository) {
    this.galaxyToolRepository = galaxyToolRepository;
  }

  private GalaxyToolRepository galaxyToolRepository;

  public List<TestData> getTestCases(final String toolId) {
    final Tool tool = galaxyToolRepository.loadForToolId(toolId);
    ImmutableList.Builder<TestData> testCases = ImmutableList.builder();
    final Tests tests = tool.getTests();
    if(tests != null) {
      for(Test test : tests.getTest()) {
        final TestData testData = buildTestData(tool, test);
        testCases.add(testData);
      }
    }
    return testCases.build();
  }

  private TestData buildTestData(final Tool tool, final Test test) {
    final Map<String, InputType> inputTypeMap = GalaxyDataUtils.buildFlatParamMap(tool);
    final TestData testData = new TestData();
    final Map<String, String> testDataMap = Maps.newHashMap();
    for(final TestParam testParam : test.getParam()) {
      final InputType matchingInput = inputTypeMap.get(testParam.getName());
      if(matchingInput instanceof Param) {
        final Param matchingParam = (Param) matchingInput;
        if(matchingParam.getType() == ParamType.DATA) {
          final TestInputFile inputFile = new TestInputFile();
          final String fileContents = testParam.getEmbeddedValue();
          Preconditions.checkNotNull(fileContents);
          inputFile.setInputFileName(testParam.getName());
          inputFile.setContents(fileContents.getBytes());
          testData.getInputFiles().add(inputFile);
        }
        testDataMap.put(testParam.getName(), testParam.getValue());
      }
    }
    final List<Closure<byte[]>> checkers = Lists.newArrayList();
    for(final TestOutput output : test.getOutput()) {
      checkers.add(new OutputChecker(output));
    }
    testData.setOutputFileChecker(checkers);

    final RootInput rootInput = GalaxyDataUtils.buildRootInputSkeleton(tool);
    final Map<String, InputType> paramMap = GalaxyDataUtils.buildParamMap(tool);
    for(Map.Entry<String, InputType> paramEntry : paramMap.entrySet()) {
      final InputType paramInput = paramEntry.getValue();
      if(paramInput instanceof Param) {
        final Param param = (Param) paramInput;
        final Input input = GalaxyDataUtils.getFullyQualifiedInput(paramEntry.getKey(), rootInput.getInput());
        if(param.getType() == ParamType.DATA) {
          input.setValue(input.getName());
        } else {
          String key = input.getName();
          if(testDataMap.containsKey(key)) {
            input.setValue(testDataMap.get(key));
          }
        }
      }
      // TODO: Add filename
    }
    testData.setRootInput(rootInput);
    return testData;
  }

}
