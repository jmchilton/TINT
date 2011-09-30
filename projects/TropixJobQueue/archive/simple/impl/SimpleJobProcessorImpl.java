package edu.umn.msi.tropix.common.jobqueue.jobprocessors.simple.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseExecutableJobProcessorImpl;
import edu.umn.msi.tropix.jobqueue.simplejobdescription.SimpleJobDescriptionType;

public class SimpleJobProcessorImpl extends BaseExecutableJobProcessorImpl {
  protected SimpleJobDescriptionType simpleJobDescription;

  private static String expandString(@Nullable String str, final Map<String, String> replacements) {
    if (str == null) {
      return null;
    }
    for (final Map.Entry<String, String> entry : replacements.entrySet()) {
      final String key = "${" + entry.getKey() + "}";
      str = str.replace(key, entry.getValue());
    }
    return str;
  }

  @Override
  protected void doPreprocessing() {
    final Map<String, String> replacements = new HashMap<String, String>();
    replacements.put("staging_directory", stagingDirectory.getAbsolutePath());

    this.jobDescription.getJobDescriptionType().setArgument(simpleJobDescription.getArgument());
    this.jobDescription.getJobDescriptionType().setCount(simpleJobDescription.getCount());
    this.jobDescription.getJobDescriptionType().setDirectory(simpleJobDescription.getDirectory());
    // TODO: set environment
    this.jobDescription.getJobDescriptionType().setExecutable(expandString(simpleJobDescription.getExecutable(), replacements));
    // TODO: set extensions..
    // TODO: set holdState
    // TODO: set local user id
    this.jobDescription.getJobDescriptionType().setLibraryPath(simpleJobDescription.getLibraryPath());
    this.jobDescription.getJobDescriptionType().setMaxCpuTime(simpleJobDescription.getMaxCpuTime());
    this.jobDescription.getJobDescriptionType().setMaxMemory(simpleJobDescription.getMaxMemory());
    this.jobDescription.getJobDescriptionType().setMaxTime(simpleJobDescription.getMaxTime());
    this.jobDescription.getJobDescriptionType().setMaxWallTime(simpleJobDescription.getMaxWallTime());
    this.jobDescription.getJobDescriptionType().setMinMemory(simpleJobDescription.getMinMemory());
    this.jobDescription.getJobDescriptionType().setProject(simpleJobDescription.getProject());
    this.jobDescription.getJobDescriptionType().setQueue(simpleJobDescription.getQueue());
    this.jobDescription.getJobDescriptionType().setStderr(expandString(simpleJobDescription.getStderr(), replacements));
    this.jobDescription.getJobDescriptionType().setStdin(expandString(simpleJobDescription.getStdin(), replacements));
    this.jobDescription.getJobDescriptionType().setStdout(expandString(simpleJobDescription.getStdout(), replacements));
    System.out.println("STDOUT -------------------------> " + jobDescription.getJobDescriptionType().getStdout());
  }

}
