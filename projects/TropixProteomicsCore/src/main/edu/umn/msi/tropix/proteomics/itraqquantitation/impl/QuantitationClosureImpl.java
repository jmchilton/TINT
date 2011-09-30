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

package edu.umn.msi.tropix.proteomics.itraqquantitation.impl;

import java.io.File;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.xml.XMLUtility;
import edu.umn.msi.tropix.proteomics.itraqquantitation.QuantificationResultsExporter;
import edu.umn.msi.tropix.proteomics.itraqquantitation.QuantitationOptions;
import edu.umn.msi.tropix.proteomics.itraqquantitation.impl.ITraqLabels.ITraqRatio;
import edu.umn.msi.tropix.proteomics.itraqquantitation.options.QuantificationType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.results.QuantificationResults;

class QuantitationClosureImpl implements Closure<QuantitationOptions> {
  private static final Log LOG = LogFactory.getLog(QuantitationClosureImpl.class);
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();

  private ITraqMatchBuilder iTraqMatchBuilder;
  private Quantifier quantifier = new QuantifierImpl();
  
  public void setItraqMatchBuilder(final ITraqMatchBuilder iTraqMatchBuilder) {
    this.iTraqMatchBuilder = iTraqMatchBuilder;
  }
  
  @VisibleForTesting
  public void setQuantifier(final Quantifier quantifier) {
    this.quantifier = quantifier;
  }

  public void apply(final QuantitationOptions options) {
    final List<ITraqLabel> labels = options.getQuantificationType() == QuantificationType.FOUR_PLEX ? ITraqLabels.get4PlexLabels() : ITraqLabels.get8PlexLabels();
    
    LOG.info("Building data entries for quantitation analysis");
    final List<ITraqMatch> iTraqMatchs = iTraqMatchBuilder.buildDataEntries(options.getInputMzxmlFiles(), options.getInputScaffoldReport(), new ITraqMatchBuilder.ITraqMatchBuilderOptions(labels));

    Function<Double, Double> trainingFunction = null;
    if(options.getWeights() != null) {
      LOG.info("Building iTraq matches for training data");
      //final List<ITraqMatch> trainingMatches = iTraqMatchBuilder.buildDataEntries(options.getInputMzxmlFiles(), options.getInputScaffoldReport(), new ITraqMatchBuilder.ITraqMatchBuilderOptions(labels));
      trainingFunction = new TrainingWeightFunctionImpl(options.getWeights());      
    }
    
    LOG.info("Building report summary for quantitation analysis");
    final ReportSummary summary = new ReportSummary(iTraqMatchs, labels);

    final Collection<ITraqRatio> iTraqRatios = ITraqLabels.buildRatios(labels);

    LOG.info("Running quantitation analysis.");
    final QuantificationResults results = quantifier.quantify(iTraqRatios, summary, trainingFunction);
    
    final File outputFile = options.getOutputFile();
    if(FilenameUtils.getExtension(outputFile.getName()).toLowerCase().equals("xml")) {
      final XMLUtility<QuantificationResults> resultXmlUtility = new XMLUtility<QuantificationResults>(QuantificationResults.class);
      
      LOG.info("Writing XML resluts");
      resultXmlUtility.serialize(results, outputFile);
    } else {
      final OutputStream outputStream = FILE_UTILS.getFileOutputStream(outputFile);
      try {
        QuantificationResultsExporter.writeAsSpreadsheet(results, outputStream);
      } finally {
        IO_UTILS.closeQuietly(outputStream);
      }
    }
    
  }    

}
