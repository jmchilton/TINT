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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.umn.msi.tropix.common.jobqueue.progress.ProgressTrackerCallback;
import edu.umn.msi.tropix.common.jobqueue.progress.ProgressTrackingLineCallback;

public class ScaffoldLineCallbackImpl implements ProgressTrackingLineCallback {
  private final Pattern percentPattern = Pattern.compile("\\s*%([0-9]+\\.[0-9]+)%\\s*");
  private ProgressTrackerCallback callback;

  public void processLine(final String line) {
    final Matcher matcher = percentPattern.matcher(line);
    if(matcher.matches()) {
      final double percent = Double.parseDouble(matcher.group(1));
      callback.updateProgress(percent / 100.0d);
    }
  }

  public void setProgressTrackerCallback(final ProgressTrackerCallback callback) {
    this.callback = callback;
  }
}
