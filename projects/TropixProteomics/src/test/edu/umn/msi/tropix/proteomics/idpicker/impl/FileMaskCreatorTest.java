package edu.umn.msi.tropix.proteomics.idpicker.impl;

import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.Iterator;
import java.util.List;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.proteomics.idpicker.parameters.IdPickerParameters;
import edu.umn.msi.tropix.proteomics.idpicker.parameters.Sample;

public class FileMaskCreatorTest {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private List<Sample> samples;
  private Iterator<String> assembleFileLines;
  private Iterator<String> qonvertFileLines;
  
  @BeforeMethod(groups = "unit")
  public void init() {
    samples = Lists.newArrayList();
    assembleFileLines = null;
    qonvertFileLines = null;
  }

  @Test(groups = "unit")
  public void testOneSample() {
    addSample("test", "file");
    buildFileLines();
    assertNextQonvertLineIs("test/file.pepXML");
    assertNextAssembleLineIs("\"/test\" \"test/*idpXML\"");
    assert !qonvertFileLines.hasNext();
  }

  @Test(groups = "unit")
  public void testMultipleSamples() {
    addSample("foo", "bar1", "bar2");
    addSample("moo", "cow");
    buildFileLines();
    assertNextQonvertLineIs("foo/bar1.pepXML");
    assertNextQonvertLineIs("foo/bar2.pepXML");
    assertNextQonvertLineIs("moo/cow.pepXML");
    assertNextAssembleLineIs("\"/foo\" \"foo/*idpXML\"");
    assertNextAssembleLineIs("\"/moo\" \"moo/*idpXML\"");    
  }
  
  
  @Test(groups = "unit")
  public void testSampleCleaned() {
    addSample("../test..", "file");
    buildFileLines();
    assertNextQonvertLineIs("test../file.pepXML");
    assertNextAssembleLineIs("\"/test..\" \"test../*idpXML\"");
    assert !qonvertFileLines.hasNext();    
  }
  
  @Test(groups = "unit")
  public void testSampleInputCleaned() {
    addSample("moo", "../foo");
    buildFileLines();
    assertNextQonvertLineIs("moo/foo.pepXML");
    assertNextAssembleLineIs("\"/moo\" \"moo/*idpXML\"");
    assert !qonvertFileLines.hasNext();
  }
    
  private void assertNextAssembleLineIs(final String expected) {
    final String nextLine = assembleFileLines.next();
    assert expected.equals(nextLine)
      : String.format("Expected next assemble line [%s], but obtain [%s]", expected, nextLine);    
  }

  private void assertNextQonvertLineIs(final String expected) {
    final String nextLine = qonvertFileLines.next();
    assert expected.equals(nextLine)
      : String.format("Expected new qonvert line [%s], but obtain [%s]", expected, nextLine);
  }
  
  private void buildFileLines() {
    final IdPickerParameters parameters = new IdPickerParameters();
    parameters.setSample(Iterables.toArray(samples, Sample.class));
    final FileMaskCreator creator = new FileMaskCreator(parameters);
    assembleFileLines = stringToLines(creator.getAssembleLines());
    qonvertFileLines = stringToLines(creator.getQonvertLines());
  }
  
  private Iterator<String> stringToLines(final String contents) {
    final LineNumberReader reader = new LineNumberReader(new StringReader(contents));
    final List<String> lines = Lists.newArrayList();
    String line;
    while((line = IO_UTILS.readLine(reader)) != null) {
      lines.add(line);
    }
    return lines.iterator();
  }
  
  private void addSample(final String sampleName, final String... inputNames) {
    final Sample sample = new Sample(inputNames, sampleName);
    samples.add(sample);
  }

}
