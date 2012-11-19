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

package edu.umn.msi.tropix.proteomics.conversion;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

public class DtaNameUtils {
  private static final Pattern DTA_PATTERN = Pattern.compile("(.*)\\.(\\d+)\\.(\\d+)\\.(\\d+)(\\.[dD][tT][aA])?$");

  public static final class DtaNameSummary {
    private final String basename;
    private final int start, end;
    private final short charge;

    public DtaNameSummary(final String basename, final int start, final int end, final short charge) {
      this.basename = basename;
      this.start = start;
      this.end = end;
      this.charge = charge;
    }

    public String getBasename() {
      return basename;
    }

    public int getStart() {
      return start;
    }

    public int getEnd() {
      return end;
    }

    public short getCharge() {
      return charge;
    }

  }

  public static boolean isDtaName(final String filename) {
    return DTA_PATTERN.matcher(FilenameUtils.getName(filename)).matches();
  }

  public static DtaNameSummary getDtaNameSummary(final String filename) {
    final Matcher matcher = DTA_PATTERN.matcher(FilenameUtils.getName(filename));
    matcher.matches();
    final String basename = matcher.group(1);
    final int start = Integer.parseInt(matcher.group(2));
    final int end = Integer.parseInt(matcher.group(3));
    final short charge = Short.parseShort(matcher.group(4));
    return new DtaNameSummary(basename, start, end, charge);
  }

}
