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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.jobqueue.progress.ProgressTrackerCallback;
import edu.umn.msi.tropix.proteomics.sequest.SequestLineCallback;
import edu.umn.msi.tropix.proteomics.sequest.SequestOutputType;

public class SequestLineCallbackSupplierImpl implements Supplier<SequestLineCallback> {
  private final Pattern numDtasLinePattern = Pattern.compile("\\s*Processing ([0-9]+) dta's on [0-9]+ node\\(s\\)\\s*");
  private SequestOutputType outputType;

  public SequestLineCallback get() {
    return new SequestLineCallbackImpl();
  }

  class SequestLineCallbackImpl implements SequestLineCallback {
    private ProgressTrackerCallback callback;
    private Integer numberOfDtaFiles = null;
    private int numberOfDtaFilesProcessed = 0;

    public void setProgressTrackerCallback(final ProgressTrackerCallback callback) {
      this.callback = callback;
    }

    public void processLine(final String line) {
      if(numberOfDtaFiles == null) {
        final Matcher lineMatcher = numDtasLinePattern.matcher(line);
        if(lineMatcher.matches()) {
          final String numDtasStr = lineMatcher.group(1);
          numberOfDtaFiles = Integer.parseInt(numDtasStr);
        }
      } else {
        if(outputType.getDtaLinePattern().matcher(line).matches()) {
          numberOfDtaFilesProcessed++;
          callback.updateProgress(numberOfDtaFilesProcessed / (1.0 * numberOfDtaFiles));
        }
      }
    }

    public void setDtaCount(final int dtaCount) {
      this.numberOfDtaFiles = dtaCount;
    }
  }

  public void setSequestOutputType(final SequestOutputType outputType) {
    this.outputType = outputType;
  }

}
