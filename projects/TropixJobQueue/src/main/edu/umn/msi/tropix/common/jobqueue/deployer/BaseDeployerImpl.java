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

package edu.umn.msi.tropix.common.jobqueue.deployer;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;

import javax.annotation.concurrent.NotThreadSafe;

import org.springframework.util.StringUtils;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

import edu.umn.msi.tropix.common.data.Repositories;
import edu.umn.msi.tropix.common.data.Repository;
import edu.umn.msi.tropix.common.execution.process.ProcessConfiguration;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.io.OutputContext;

@NotThreadSafe
public abstract class BaseDeployerImpl implements Deployer {
  private Repository repository = Repositories.getInstance();
  private DeployerContext deployerContext = new DeployerContextImpl();
  private boolean initialized = false;
  private String property;

  @VisibleForTesting
  public void setDeployerContext(final DeployerContext deployerContext) {
    this.deployerContext = deployerContext;
  }

  protected InputContext getResource(final String resourceName) {
    final URL localResource = getClass().getResource(resourceName);
    // Check for the resource locally first, if not there fetch it from the repository
    if(localResource != null) {
      return InputContexts.forUrl(localResource);
    } else {
      return repository.getResourceContext(getClass(), resourceName);
    }
  }

  protected String resourceToString(final String resourceName) {
    return InputContexts.toString(getResource(resourceName));
  }

  protected ImmutableMap.Builder<String, String> newReplacements() {
    return ImmutableMap.builder();
  }

  protected void copyResource(final String resourceName) {
    getResource(resourceName).get(getOutputContext(resourceName));
  }

  protected void copyResourceWithReplacements(final String resourceName, final Builder<String, String> replacements) {
    String scriptContents = resourceToString(resourceName);
    for(final Map.Entry<String, String> replacementEntry : replacements.build().entrySet()) {
      final String replaceFrom = Matcher.quoteReplacement(replacementEntry.getKey());
      final String replaceWith = Matcher.quoteReplacement(replacementEntry.getValue());
      scriptContents = scriptContents.replaceAll(replaceFrom, replaceWith);
    }
    getOutputContext(resourceName).put(scriptContents.getBytes());
  }

  protected void decompressResource(final String resourceName) {
    final File zipFile = getFile(resourceName);
    zipFile.getParentFile().mkdirs();
    getResource(resourceName).get(zipFile);
    decompress(zipFile);
  }

  protected String asDirectoryPath(final String directoryPath) {
    String path = "";
    if(StringUtils.hasText(directoryPath)) {
      final String sep = File.separator;
      if(directoryPath.endsWith(sep)) {
        path = directoryPath;
      } else {
        path = directoryPath + sep;
      }
    }
    return path;
  }

  protected String asDirectoryPath(final File file) {
    return asDirectoryPath(file.getAbsolutePath());
  }

  public String get() {
    if(!initialized) {
      deploy();
      initialized = true;
    }
    return property;
  }

  public String apply(final String propertyName) {
    return get();
  }

  protected void addProperty(final String propertyValue) {
    this.property = propertyValue;
  }

  protected abstract void deploy();

  protected File getDeploymentDirectory() {
    return deployerContext.getDeploymentDirectory();
  }

  public void setDeploymentDirectory(final File deploymentDirectory) {
    deployerContext.setDeploymentDirectory(deploymentDirectory);
  }

  protected void decompress(final File compressedFile) {
    deployerContext.decompress(compressedFile);
  }

  protected void makeExecutable(final File file) {
    deployerContext.makeExecutable(file);
  }

  protected int execute(final ProcessConfiguration processConfiguration) {
    return deployerContext.execute(processConfiguration);
  }

  protected int execute(final String application, final String... arguments) {
    return deployerContext.execute(application, arguments);
  }

  protected int executeInDirectory(final File directory, final String application, final String... arguments) {
    return deployerContext.executeInDirectory(directory, application, arguments);
  }

  protected File getFile(final String relativePath) {
    return deployerContext.getFile(relativePath);
  }

  protected OutputContext getOutputContext(final String relativePath) {
    return deployerContext.getOutputContext(relativePath);
  }

  protected boolean isLinux() {
    return deployerContext.isLinux();
  }

  protected boolean isMacOsX() {
    return deployerContext.isMacOsX();
  }

  protected boolean isUnix() {
    return deployerContext.isUnix();
  }

  protected boolean isWindows() {
    return deployerContext.isWindows();
  }

}
