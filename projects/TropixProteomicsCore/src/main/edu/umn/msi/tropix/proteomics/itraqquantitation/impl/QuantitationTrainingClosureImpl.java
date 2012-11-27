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
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.google.common.collect.Maps;

import edu.umn.msi.tropix.common.collect.Closure;
import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.xml.AxisSerializationUtils;
import edu.umn.msi.tropix.common.xml.AxisSerializationUtilsFactory;
import edu.umn.msi.tropix.proteomics.itraqquantitation.QuantitationTrainingOptions;
import edu.umn.msi.tropix.proteomics.itraqquantitation.options.QuantificationType;
import edu.umn.msi.tropix.proteomics.itraqquantitation.training.QuantificationTrainingOptions;
import edu.umn.msi.tropix.proteomics.itraqquantitation.weight.QuantificationWeight;
import edu.umn.msi.tropix.proteomics.itraqquantitation.weight.QuantificationWeights;

public class QuantitationTrainingClosureImpl implements Closure<QuantitationTrainingOptions> {
  private static final Log LOG = LogFactory.getLog(QuantitationTrainingClosureImpl.class);
  private static final AxisSerializationUtils SERIALIZATION_UTILS = AxisSerializationUtilsFactory.getInstance();
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private static final QName WEIGHTS_QNAME = new QName("http://msi.umn.edu/tropix/proteomics/itraqquantitation/weight", "quantificationWeights");

  private ITraqMatchBuilder iTraqMatchBuilder;

  public void apply(final QuantitationTrainingOptions options) {
    final List<ITraqLabel> labels = options.getQuantificationType() == QuantificationType.FOUR_PLEX ? ITraqLabels.get4PlexLabels() : ITraqLabels
        .get8PlexLabels();
    final QuantificationTrainingOptions trainingOptions = options.getTrainingOptions();
    final double[] ratios = new double[labels.size()];
    if(ratios.length == 4) {
      ratios[0] = trainingOptions.getI114Proportion();
      ratios[1] = trainingOptions.getI115Proportion();
      ratios[2] = trainingOptions.getI116Proportion();
      ratios[3] = trainingOptions.getI117Proportion();
    } else {
      ratios[0] = trainingOptions.getI113Proportion();
      ratios[1] = trainingOptions.getI114Proportion();
      ratios[2] = trainingOptions.getI115Proportion();
      ratios[3] = trainingOptions.getI116Proportion();
      ratios[4] = trainingOptions.getI117Proportion();
      ratios[5] = trainingOptions.getI118Proportion();
      ratios[6] = trainingOptions.getI119Proportion();
      ratios[7] = trainingOptions.getI121Proportion();
    }

    LOG.info("Building data entries for quantitation analysis");
    final List<ITraqMatch> iTraqMatchs = iTraqMatchBuilder
        .buildDataEntries(options.getInputMzxmlFiles(), options.getInputScaffoldReport(),
            new ITraqMatchBuilder.ITraqMatchBuilderOptions(labels,
                edu.umn.msi.tropix.proteomics.itraqquantitation.QuantitationOptions.GroupType.PROTEIN, 1));

    final Map<ITraqLabel, double[]> intensities = Maps.newHashMap();
    final int numMatches = iTraqMatchs.size();
    for(ITraqLabel label : labels) {
      intensities.put(label, new double[numMatches]);
    }
    int i = 0;
    for(ITraqMatch match : iTraqMatchs) {
      for(ITraqLabel label : labels) {
        intensities.get(label)[i] = match.getScan().getIntensity(label);
      }
      i++;
    }

    final double[][] matrix = Variance.createVarianceMatrix(labels, intensities, ratios, trainingOptions.getNumBins());
    final QuantificationWeight[] weights = new QuantificationWeight[matrix.length];
    for(i = 0; i < matrix.length; i++) {
      weights[i] = new QuantificationWeight(matrix[i][0], matrix[i][1]);
    }

    final File outputFile = options.getOutputFile();
    final Writer outputWriter = FILE_UTILS.getFileWriter(outputFile);
    try {
      SERIALIZATION_UTILS.serialize(outputWriter, new QuantificationWeights(weights), WEIGHTS_QNAME);
    } finally {
      IO_UTILS.closeQuietly(outputWriter);
    }
  }

  public void setItraqMatchBuilder(final ITraqMatchBuilder iTraqMatchBuilder) {
    this.iTraqMatchBuilder = iTraqMatchBuilder;
  }

}
