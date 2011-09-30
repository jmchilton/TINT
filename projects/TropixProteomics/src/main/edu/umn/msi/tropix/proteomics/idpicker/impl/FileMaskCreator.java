package edu.umn.msi.tropix.proteomics.idpicker.impl;

import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.proteomics.idpicker.parameters.IdPickerParameters;
import edu.umn.msi.tropix.proteomics.idpicker.parameters.Sample;

class FileMaskCreator {
  private static final String NEWLINE = System.getProperty("line.separator");
  private Iterable<Sample> samples;
  
  FileMaskCreator(final IdPickerParameters parameters) {
    this.samples = escapedSamples(parameters.getSample());
  }

  public String getAssembleLines() {
    final StringBuilder contents = new StringBuilder();
    for(final Sample sample : samples) {
      final String sampleName = sample.getName();
      contents.append(String.format("\"/%s\" \"%s/*idpXML\"%s", sampleName, sampleName, NEWLINE));
    }
    return contents.toString();
  }
  
  Iterable<String> getPepXmlPaths(final String sep) {
    final List<String> pepXmlPaths = Lists.newArrayList();
    for(final Sample sample : samples) {
      for(final String input : sample.getInput()) {
        pepXmlPaths.add(String.format("%s%s%s.pepXML", sample.getName(), sep, input));
      }
    }
    return pepXmlPaths;
  }
  
  Iterable<String> getSampleNames() {
    final List<String> sampleNames = Lists.newArrayList();
    for(final Sample sample : samples) {
      sampleNames.add(sample.getName());
    }
    return sampleNames;
  }


  String getQonvertLines() {
    return Joiner.on(NEWLINE).join(getPepXmlPaths("/"));
    /*
    final StringBuilder contents = new StringBuilder();
    for(final Sample sample : samples) {
      Preconditions.checkNotNull(sample.getInput());
      for(final String input : sample.getInput()) {
        contents.append(sample.getName() + "/" + input + ".pepXML" + NEWLINE);
      }
    }
    return contents.toString();
    */
  }
  
  private static Iterable<Sample> escapedSamples(final Sample[] samples) {
    Preconditions.checkNotNull(samples);
    final List<Sample> escapedSamples = Lists.newArrayList();
    for(Sample rawSample : samples) {
      final String rawSampleName = rawSample.getName();
      final Sample escapedSample = new Sample();
      escapedSample.setName(FilenameUtils.getName(rawSampleName));
      escapedSample.setInput(escapedInputs(rawSample.getInput()));
      escapedSamples.add(escapedSample);
    }
    return escapedSamples;
  }
  
  private static String[] escapedInputs(final String[] inputs) {
    Preconditions.checkNotNull(inputs);
    final String[] escapedInputs = new String[inputs.length];
    for(int i = 0; i < inputs.length; i++) {
      final String rawInput = inputs[i];
      escapedInputs[i] = FilenameUtils.getName(rawInput);
    }
    return escapedInputs;
  }
  
}
