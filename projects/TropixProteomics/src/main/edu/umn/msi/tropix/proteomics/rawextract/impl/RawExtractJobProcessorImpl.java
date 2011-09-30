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

package edu.umn.msi.tropix.proteomics.rawextract.impl;

import java.io.File;
import java.io.OutputStream;

import net.sourceforge.sashimi.mzxml.v3_0.MzXML;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Iterables;

import edu.umn.msi.tropix.common.collect.StringPredicates;
import edu.umn.msi.tropix.common.io.HasStreamInputContext;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.OutputContext;
import edu.umn.msi.tropix.common.jobqueue.jobprocessors.BaseExecutableJobProcessorImpl;
import edu.umn.msi.tropix.proteomics.FileDTAListImpl;
import edu.umn.msi.tropix.proteomics.conversion.DTAToMzXMLConverter;
import edu.umn.msi.tropix.proteomics.conversion.DTAToMzXMLOptions;
import edu.umn.msi.tropix.proteomics.conversion.impl.ConversionUtils;
import edu.umn.msi.tropix.proteomics.conversion.impl.MzxmlVerifier;
import edu.umn.msi.tropix.proteomics.xml.MzXMLUtility;

//TODO: Implement a better data structure for parameters or verify them
public class RawExtractJobProcessorImpl extends BaseExecutableJobProcessorImpl {
  private static final Object LOCK_OBJECT = new Object();
  private static final Log LOG = LogFactory.getLog(RawExtractJobProcessorImpl.class);
  private static final String BASENAME_PARAMETER_NAME = "rawextract_basename";
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  // Set by builder (job specific)
  private InputContext rawFilePopulator;
  private String rawFileBaseName = "raw", rawExtractParameters = "";

  // Set by factory (configuration specific)
  private DTAToMzXMLConverter dtaToMzXMLConverter;
  private DTAToMzXMLOptions dtaToMxXMLOptions = null;
  private boolean producesMzxml = false;

  // The only job specific data that needs to be recovered to do postprocessing
  // is the rawFileBaseName, this is saved into the jobDescription at the preprocessing stage
  @Override
  protected void initialize() {
    this.rawFileBaseName = getParameter(BASENAME_PARAMETER_NAME);
  }

  public static String getSaneRawFileName(final String iName) {
    return ConversionUtils.getSanitizedName(iName == null ? "raw" : iName, ".RAW");
  }

  @Override
  protected void doPreprocessing() {
    // Save rawFileBaseName so it can be recovered by initialize if need be!
    saveParameter(BASENAME_PARAMETER_NAME, rawFileBaseName);

    // Download raw file to staging directory
    final String rawFileName = getSaneRawFileName(rawFileBaseName);
    final OutputContext rawFile = getStagingDirectory().getOutputContext(rawFileName);
    rawFilePopulator.get(rawFile);
    getJobDescription().getJobDescriptionType().setDirectory(getStagingDirectory().getAbsolutePath());
    getJobDescription().getJobDescriptionType().setArgument(
        new String[] {rawExtractParameters, "--mzXML", getStagingDirectory().getAbsolutePath() + File.separator + rawFileName});
  }

  private void checkMzxml(final String resource) {
    final InputContext inputContext = getStagingDirectory().getInputContext(resource);
    if(inputContext instanceof HasStreamInputContext) {
      final HasStreamInputContext hasStreamInputContext = (HasStreamInputContext) inputContext;
      if(!MzxmlVerifier.isValid(hasStreamInputContext.asInputStream())) {
        throw new RuntimeException("Problem with converted MzXML");
      }
    }
  }

  @Override
  protected void doPostprocessing() {
    if(!wasCompletedNormally()) {
      return;
    }

    if(producesMzxml) {
      final Iterable<String> resourceNames = getStagingDirectory().getResourceNames(null);
      final String mzxmlResource = Iterables.find(resourceNames, StringPredicates.matches(".*\\.[mM][zZ][xX][mM][lL]"));
      checkMzxml(mzxmlResource);
      final InputContext inputContext = getStagingDirectory().getInputContext(mzxmlResource);
      getResourceTracker().add(inputContext);
    } else {
      // Create DTAList data structure capturing all of the DTA files.
      LOG.debug("Creating DTAList in postprocess");
      final FileDTAListImpl dtaList = new FileDTAListImpl(getStagingDirectory());
      dtaList.populate(rawFileBaseName);

      // Convert DTA Files to MzXML
      LOG.debug("Converting DTAList to MzXML");
      synchronized(LOCK_OBJECT) {
        MzXML mzxml = dtaToMzXMLConverter.dtaToMzXML(dtaList, dtaToMxXMLOptions);
        LOG.debug("Serializing mzxml to a temp file");
        final OutputStream outputStream = getResourceTracker().newStream();
        try {
          new MzXMLUtility().serialize(mzxml, outputStream);
        } finally {
          IO_UTILS.closeQuietly(outputStream);
        }
        mzxml = null;
      }
    }
  }

  public void setDtaToMzXMLConverter(final DTAToMzXMLConverter dtaToMzXMLConverter) {
    this.dtaToMzXMLConverter = dtaToMzXMLConverter;
  }

  public void setDtaToMxXMLOptions(final DTAToMzXMLOptions dtaToMxXMLOptions) {
    this.dtaToMxXMLOptions = dtaToMxXMLOptions;
  }

  public void setRawFilePopulator(final InputContext rawFilePopulator) {
    this.rawFilePopulator = rawFilePopulator;
  }

  public void setRawFileBaseName(final String rawFileBaseName) {
    this.rawFileBaseName = rawFileBaseName;
  }

  public void setRawExtractParameters(final String rawExtractParameters) {
    this.rawExtractParameters = rawExtractParameters;
  }

  public void setProducesMzxml(final boolean producesMzxml) {
    this.producesMzxml = producesMzxml;
  }
}