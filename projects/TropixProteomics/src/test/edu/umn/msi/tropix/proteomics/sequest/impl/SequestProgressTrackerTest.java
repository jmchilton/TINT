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

package edu.umn.msi.tropix.proteomics.sequest.impl;

import org.easymock.EasyMock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.progress.ProgressTrackerCallback;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.EasyMockUtils.Reference;
import edu.umn.msi.tropix.proteomics.sequest.SequestLineCallback;

public class SequestProgressTrackerTest {
  private SequestLineCallback sequestLineCallback;
  private ProgressTrackerCallback progressCallback;
  private SequestLineCallbackSupplierImpl supplier;

  @BeforeMethod(groups = "unit")
  public void init() {
    supplier = new SequestLineCallbackSupplierImpl();
    supplier.setSequestOutputType(SequestOutputTypeEnum.PVM_OUTPUT);
    sequestLineCallback = supplier.get();
    progressCallback = EasyMock.createMock(ProgressTrackerCallback.class);
    sequestLineCallback.setProgressTrackerCallback(progressCallback);
  }

  private void expect(final String line, final Double expectedPercent) {
    final Reference<Double> doubleReference = EasyMockUtils.newReference();
    progressCallback.updateProgress(EasyMockUtils.record(doubleReference));
    EasyMock.replay(progressCallback);
    sequestLineCallback.processLine(line);
    EasyMockUtils.verifyAndReset(progressCallback);
    assert Math.abs(expectedPercent - doubleReference.get()) < .0000001;
  }

  @Test(groups = "unit")
  public void knownNumDtas() {
    test(SequestOutputTypeEnum.PVM_LOG, true);
  }

  @Test(groups = "unit")
  public void unkownNumDtas() {
    test(SequestOutputTypeEnum.PVM_LOG, false);
  }

  @Test(groups = "unit")
  public void outputTracker() {
    test(SequestOutputTypeEnum.PVM_OUTPUT, false);
  }

  @Test(groups = "unit")
  public void standardTracker() {
    test(SequestOutputTypeEnum.STANDARD_OUTPUT, false);
  }

  public void test(final SequestOutputTypeEnum type, final boolean knownNum) {
    supplier.setSequestOutputType(type);
    int iters = 1;
    if(knownNum) {
      iters = 2;
      sequestLineCallback.setDtaCount(iters * 66);
    }

    for(int j = 0; j < iters; j++) {
      sequestLineCallback.processLine("\n");
      sequestLineCallback.processLine("TurboSEQUEST - PVM Master v.27 (rev. 12), (c) 1998-2005");
      sequestLineCallback.processLine("Molecular Biotechnology, Univ. of Washington, J.Eng/S.Morgan/J.Yates");
      sequestLineCallback.processLine("Licensed to Thermo Electron Corp.");
      sequestLineCallback.processLine("\n");
      sequestLineCallback.processLine("NumHosts = 3, NumArch = 1");
      sequestLineCallback.processLine("\n");
      sequestLineCallback.processLine("  Arch:LINUX64  CPU:2  Tid:40000  Name:sequest4");
      sequestLineCallback.processLine("  Arch:LINUX64  CPU:4  Tid:80000  Name:sequest3");
      sequestLineCallback.processLine("  Arch:LINUX64  CPU:1  Tid:c0000  Name:sequest2");
      sequestLineCallback.processLine("\n");
      sequestLineCallback.processLine("Starting the SEQUEST task on 3 node(s)");
      sequestLineCallback.processLine("\n");
      sequestLineCallback.processLine("  Spawned the SEQUEST slave process [40078] on sequest4");
      sequestLineCallback.processLine("  Spawned the SEQUEST slave process [40079] on sequest4");
      sequestLineCallback.processLine("  Spawned the SEQUEST slave process [8009e] on sequest3");
      sequestLineCallback.processLine("  Spawned the SEQUEST slave process [8009f] on sequest3");
      sequestLineCallback.processLine("  Spawned the SEQUEST slave process [800a0] on sequest3");
      sequestLineCallback.processLine("  Spawned the SEQUEST slave process [800a1] on sequest3");
      sequestLineCallback.processLine("  Spawned the SEQUEST slave process [c0029] on sequest2");
      sequestLineCallback.processLine("\n");
      sequestLineCallback.processLine("Waiting for ready messages from 7 node(s)");
      sequestLineCallback.processLine("\n");
      sequestLineCallback.processLine("  1.  received ready messsage from sequest3(8009e)");
      sequestLineCallback.processLine("  2.  received ready messsage from sequest2(c0029)");
      sequestLineCallback.processLine("  3.  received ready messsage from sequest4(40079)");
      sequestLineCallback.processLine("  4.  received ready messsage from sequest3(800a0)");
      sequestLineCallback.processLine("  5.  received ready messsage from sequest3(8009f)");
      sequestLineCallback.processLine("  6.  received ready messsage from sequest4(40078)");
      sequestLineCallback.processLine("  7.  received ready messsage from sequest3(800a1)");
      sequestLineCallback.processLine("\n");
      sequestLineCallback.processLine("Spawned 7 slave processes");
      sequestLineCallback.processLine("\n");
      sequestLineCallback.processLine("Search run on sequest4 using 7 node(s)");
      sequestLineCallback.processLine("Params file = /tmp/sequest/20/sequest.params");
      sequestLineCallback.processLine("Peptide tol = 2.0000, fragment tol = 2.0000, MONO/MONO");
      sequestLineCallback.processLine("ion series nABY ABCDVWXYZ: 0 1 1 0.0 1.0 0.0 0.0 0.0 0.0 0.0 1.0 0.0");
      sequestLineCallback.processLine("Display top = 20/20, ion % = 0.0, CODE = 001010");
      sequestLineCallback.processLine("Protein database = /tmp/sequest/20/db.fasta");
      sequestLineCallback.processLine("\n");
      sequestLineCallback.processLine("Processing 66 dta's on 7 node(s)");
      sequestLineCallback.processLine("\n");
      for(int i = 0; i < 66; i++) {
        final Double expecting = (j / (1.0 * iters)) + ((i + 1) / (66.0 * iters));
        switch(type) {
        case PVM_LOG:
          expect("Searched dta file /tmp/sequest/20/data.1254.1255.2.dta on sequest4 \n", expecting);
          break;
        case PVM_OUTPUT:
          expect("Received a request, sending dta file /tmp/sequest/20/data.1254.1255.2.dta to sequest4 \n", expecting);
          break;
        case STANDARD_OUTPUT:
          expect(" Reading input file 071608samplerun.9987.10015.3.dta ... ", expecting);
          break;
        }
      }
    }
  }
}
