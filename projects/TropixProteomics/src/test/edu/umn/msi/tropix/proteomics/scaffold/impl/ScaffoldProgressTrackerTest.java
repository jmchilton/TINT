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

package edu.umn.msi.tropix.proteomics.scaffold.impl;

import java.io.IOException;

import org.easymock.EasyMock;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.common.jobqueue.progress.ProgressTrackerCallback;
import edu.umn.msi.tropix.common.jobqueue.progress.ProgressTrackingLineCallback;
import edu.umn.msi.tropix.common.test.EasyMockUtils;
import edu.umn.msi.tropix.common.test.EasyMockUtils.Reference;

public class ScaffoldProgressTrackerTest {
  private ProgressTrackerCallback callback;
  private ProgressTrackingLineCallback lineCallback;

  private void expect(final String line, final Double expectedPercent) {
    final Reference<Double> percentReference = EasyMockUtils.newReference();
    callback.updateProgress(EasyMockUtils.record(percentReference));
    EasyMock.replay(callback);
    lineCallback.processLine(line);
    EasyMockUtils.verifyAndReset(callback);
    assert Math.abs(percentReference.get() - expectedPercent) < 0.0000001d : "Expected " + expectedPercent + " obtained " + percentReference.get();
  }

  @Test(groups = "unit")
  public void mockTracker() throws IOException {
    callback = EasyMock.createMock(ProgressTrackerCallback.class);
    lineCallback = new ScaffoldLineCallbackImpl();
    lineCallback.setProgressTrackerCallback(callback);

    lineCallback.processLine("[12:48:49] Java Version: 1.4.2_07\n");
    lineCallback.processLine("[12:48:49] Linux i386 2.6.16.54-0.2.5-smp\n");
    lineCallback.processLine("[12:48:50] Launching Scaffold Batch Module...\n");
    lineCallback.processLine("[12:48:50] Scaffold Version: Scaffold-01_07_00\n");
    lineCallback.processLine("[12:48:50] Maximum Memory: 508MB\n");
    lineCallback.processLine("[12:48:50] Reading driver file \"/tmp/scaffold/4/input.xml\".\n");
    lineCallback.processLine("#Initializing user space\n");
    lineCallback.processLine("#Creating data manager\n");
    lineCallback.processLine("#building Experiment object model\n");
    lineCallback.processLine("#building Spectrum object model\n");
    expect("%0.2%\n", 0.002d);
    lineCallback.processLine("#building PeakList object model\n");
    lineCallback.processLine("#building Spectrum Identification object model\n");
    expect("%0.3%\n", 0.003d);
    lineCallback.processLine("#building Spectrum Group object model\n");
    lineCallback.processLine("#building Spectrum Group Holder object model\n");
    lineCallback.processLine("#building Database Search object model\n");
    expect("%0.45%\n", 0.0045d);
    lineCallback.processLine("#building Spectrum Annotation object model\n");
    lineCallback.processLine("#building Spectrum Cluster object model\n");
    lineCallback.processLine("#building Peptide Identification object model\n");
    expect("%0.6%\n", 0.006d);
    lineCallback.processLine("#building Peptide Group object model\n");
    lineCallback.processLine("#building Peptide Group Holder object model\n");
    lineCallback.processLine("#building Modification object model\n");
    expect("%0.75%\n", 0.0075d);
    lineCallback.processLine("#building Node Connection object model\n");
    lineCallback.processLine("#building Protein Identification object model\n");
    lineCallback.processLine("#building Redundant Protein object model\n");
    expect("%0.9%\n", 0.009d);
    lineCallback.processLine("#building Protein Sequence object model\n");
    lineCallback.processLine("#building Spectrum Identification Cache object model\n");
    lineCallback.processLine("#\n");
    lineCallback.processLine("Found file /tmp/scaffold/4/input/s.zip\n");
    lineCallback.processLine("[12:48:50] Regrouping proteins across experiment...\n");
    lineCallback.processLine("[12:48:50] Finished regrouping proteins across experiment.\n");
    lineCallback.processLine("#Bio Sample 1\n");
    lineCallback.processLine("#Loading: s.zip\n");
    expect("%3.32%\n", 0.0332d);
    expect("%4.48%\n", 0.0448d);
    expect("%5.64%\n", 0.0564d);
    expect("%6.8%\n", 0.068d);
    expect("%7.96%\n", 0.0796d);
    expect("%9.12%\n", 0.0912d);
    expect("%10.28%\n", 0.1028d);
    expect("%11.44%\n", 0.1144d);
    expect("%12.6%\n", 0.126d);
    expect("%13.76%\n", 0.1376d);
    expect("%14.92%\n", 0.1492d);
    expect("%16.08%\n", 0.1608d);
    expect("%17.24%\n", 0.1724d);
    expect("%18.4%\n", 0.184d);
    expect("%19.56%\n", 0.1956d);
    expect("%20.72%\n", 0.2072d);
    expect("%21.88%\n", 0.2188d);
    expect("%24.21%\n", 0.2421d);
    expect("%25.37%\n", 0.2537d);
    expect("%26.53%\n", 0.2653d);
    expect("%27.69%\n", 0.2769d);
    expect("%28.85%\n", 0.2885d);
    lineCallback.processLine("#Adding Spectrum #12\n");
    lineCallback.processLine("#Collating Spectra\n");
    lineCallback.processLine("#Organizing Previous Spectra\n");
    lineCallback.processLine("#\n");
    lineCallback.processLine("#\n");
    lineCallback.processLine("#Collating Spectra\n");
    lineCallback.processLine("#\n");
    lineCallback.processLine("#\n");
    expect("%40.78%\n", 0.4078d);
    lineCallback.processLine("#Bio Sample 1\n");
    lineCallback.processLine("#Data imported successfully\n");
    lineCallback.processLine("#Data loaded, cleaning up\n");
    lineCallback.processLine("#Flushing data cache\n");
    expect("%44.02%\n", 0.4402d);
    lineCallback.processLine("#Flushing index files: 0% complete\n");
    lineCallback.processLine("#Flushing write caches\n");
    lineCallback.processLine("#Flushing Storable Protein Name file\n");
    lineCallback.processLine("#Flushing Peptide Group file\n");
    lineCallback.processLine("#Flushing Spectrum Cluster file\n");
    lineCallback.processLine("#Flushing Spectrum Annotation file\n");
    lineCallback.processLine("#Flushing Storable Accession Number file\n");
    lineCallback.processLine("#Flushing Spectrum Identification Cache file\n");
    lineCallback.processLine("#Flushing Modification Type Ids file\n");
    lineCallback.processLine("#Flushing Peak List file\n");
    lineCallback.processLine("#Flushing Protein Node Connections file\n");
    lineCallback.processLine("#Analysis Complete\n");
    lineCallback.processLine("#Saving...\n");
    lineCallback.processLine("#Flushing data cache\n");
    lineCallback.processLine("#Saving file test.sfd\n");
    lineCallback.processLine("#Did not save file\n");
    expect("%46.4%\n", 0.464d);
    lineCallback.processLine("#Scaffold Batch Analysis Complete!\n");
    lineCallback.processLine("100% FINISHED! (success) Scaffold Batch Analysis Complete!\n");
  }
}
