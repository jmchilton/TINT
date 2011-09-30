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

import org.testng.annotations.Test;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.genomics.bowtie.input.BowtieInput;

public class BowtieInputOptionsBuilderImplTest {
  private final BowtieInputOptionsBuilderImpl function = new BowtieInputOptionsBuilderImpl();
  private final BowtieInput input = new BowtieInput();
  private List<String> databasePaths = Lists.newArrayList("db1.fq");
  private final String index = "ecoli";
  private final String outputPath = null;

  private void contains(final String fragment) {
    final String cmdLine = function.getCommandLineOptions(input, databasePaths, index, outputPath);
    assert cmdLine.contains(fragment) : "Line [" + cmdLine + "] should contain fragment " + fragment;
  }

  private void notContains(final String fragment) {
    final String cmdLine = function.getCommandLineOptions(input, databasePaths, index, outputPath);
    assert !cmdLine.contains(fragment) : "Line [" + cmdLine + "] shouldn't contain fragment " + fragment;
  }

  @Test(groups = "unit")
  public void bowtieInputFunction() {
    input.setInputQualities("phred33");
    input.setInputsFormat("FASTA");
    input.setMateOrientations("ff");
    input.setInputsType("UNPAIRED");

    // Test best
    input.setBest(false);
    notContains("--best");
    input.setBest(true);
    contains(" --best ");

    contains(" --phred33-quals ");
    notContains("--integer-quals");
    input.setInputQualities("integer");
    contains(" --integer-quals ");
    notContains("--phred33-quals");

    input.setInputsFormat("FASTA");
    contains(" -f ");
    notContains(" -q ");
    input.setInputsFormat("FASTQ");
    contains(" -q ");
    notContains(" -f ");

    final String[] orientations = new String[] {"ff", "fr", "rf"};
    for(final String orientation : orientations) {
      input.setMateOrientations(orientation);
      contains(" --" + orientation + " ");
      for(final String notOrientation : orientations) {
        if(!orientation.equals(notOrientation)) {
          notContains(" --" + notOrientation + " ");
        }
      }
    }

    input.setMaximumInsertionSize(103);
    contains(" --maxins 103 ");

    input.setMinimumInsertionSize(32);
    contains(" --minins 32 ");

    input.setMaxMismatchQuality(40);
    contains(" --maqerr 40 ");

    input.setMaxReportedAlignment(3);
    contains(" -k 3 ");

    notContains(" --nofw ");
    notContains(" --norc ");
    input.setNoFw(true);
    contains(" --nofw ");
    notContains(" --norc ");
    input.setNoFw(false);
    input.setNoRc(true);
    contains(" --norc ");
    notContains(" --nofw ");

    input.setSeedMismatches(3);
    contains(" --seedmms 3 ");

    notContains(" --strata ");
    input.setStrata(true);
    contains(" --strata ");

    contains(" --trim3 0 ");
    contains(" --trim5 0 ");
    input.setTrim3(4);
    contains(" --trim3 4 ");
    input.setTrim5(41);
    contains(" --trim5 41 ");

    notContains(" --tryhard ");
    input.setTryHard(true);
    contains(" --tryhard ");

    contains(" db1.fq ");
    notContains(" -1 ");
    notContains(" -12 ");

    input.setInputsType("MIXED");
    contains(" -12 db1.fq ");

    databasePaths = Lists.newArrayList("db1.fq", "db2.fq");
    contains(" -12 db1.fq,db2.fq ");

    input.setInputsType("PAIRED");
    databasePaths = Lists.newArrayList("db1.fq", "db2.fq", "db3.fq", "db4.fq");
    contains(" -1 db1.fq,db3.fq ");
    contains(" -2 db2.fq,db4.fq ");

  }

}
