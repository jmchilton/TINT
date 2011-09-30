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

package edu.umn.msi.tropix.genomics.bowtie.impl;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

import edu.umn.msi.tropix.common.collect.Predicates2;
import edu.umn.msi.tropix.genomics.bowtie.input.BowtieInput;
import edu.umn.msi.tropix.genomics.bowtie.input.InputFormat;
import edu.umn.msi.tropix.genomics.bowtie.input.InputType;

public class BowtieInputOptionsBuilderImpl implements BowtieInputOptionsBuilder {
  private static final Set<String> VALID_QUALITIES = Sets.newHashSet("phred33", "phred64", "solexa", "solexa1.3", "integer");
  private static final Set<String> VALID_ORIENTATIONS = Sets.newHashSet("ff", "fr", "rf");

  public String getCommandLineOptions(final BowtieInput input, final List<String> databasePaths, final String index, @Nullable final String outputPath) {
    final StringBuilder builder = new StringBuilder();
    if(input.isBest()) {
      builder.append(" --best ");
    }
    Preconditions.checkArgument(VALID_QUALITIES.contains(input.getInputQualities()));
    builder.append(" --" + input.getInputQualities() + "-quals ");
    final InputFormat format = InputFormat.valueOf(input.getInputsFormat());
    builder.append(" -" + format.getArg() + " ");
    Preconditions.checkArgument(VALID_ORIENTATIONS.contains(input.getMateOrientations()));
    builder.append(" --" + input.getMateOrientations() + " ");
    builder.append(" --maxins " + input.getMaximumInsertionSize() + " ");
    builder.append(" --minins " + input.getMinimumInsertionSize() + " ");
    builder.append(" --maqerr " + input.getMaxMismatchQuality() + " ");
    builder.append(" -k " + input.getMaxReportedAlignment() + " ");
    if(input.isNoFw()) {
      builder.append(" --nofw ");
    }
    if(input.isNoRc()) {
      builder.append(" --norc ");
    }
    builder.append(" --seedmms " + input.getSeedMismatches() + " ");
    if(input.isStrata()) {
      builder.append(" --strata ");
    }
    builder.append(" --trim3 " + input.getTrim3() + " ");
    builder.append(" --trim5 " + input.getTrim5() + " ");
    if(input.isTryHard()) {
      builder.append(" --tryhard ");
    }
    builder.append(" " + index + " ");

    final InputType inputType = InputType.valueOf(input.getInputsType());
    if(inputType.equals(InputType.PAIRED)) {
      Preconditions.checkArgument(databasePaths.size() % 2 == 0);
      final Iterable<String> evenNames = Iterables.filter(databasePaths, Predicates2.even());
      final Iterable<String> oddNames = Iterables.filter(databasePaths, Predicates2.odd());
      builder.append(" -1 " + Joiner.on(",").join(evenNames));
      builder.append(" -2 " + Joiner.on(",").join(oddNames));
    } else {
      if(inputType.equals(InputType.MIXED)) {
        builder.append(" -12 ");
      }
      builder.append(Joiner.on(",").join(databasePaths));
    }
    builder.append(" ");
    if(outputPath != null) {
      builder.append(" " + outputPath);
    }
    return builder.toString();
  }

}
