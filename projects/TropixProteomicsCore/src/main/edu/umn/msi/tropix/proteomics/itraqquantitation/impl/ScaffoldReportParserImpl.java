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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.WillClose;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.proteomics.conversion.DtaNameUtils;
import edu.umn.msi.tropix.proteomics.conversion.DtaNameUtils.DtaNameSummary;

public class ScaffoldReportParserImpl implements ReportExtractorImpl.ReportParser {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();

  private static double parsePercent(final String percent) {
    return Double.parseDouble(percent.substring(0, percent.indexOf('%')));
  }

  @VisibleForTesting
  static DtaNameSummary getNameSummary(final String spectrumName) {
    DtaNameSummary dtaNameSummary = null;
    if(DtaNameUtils.isDtaName(spectrumName)) {
      dtaNameSummary = DtaNameUtils.getDtaNameSummary(spectrumName);
    } else {
      final Pattern pattern = Pattern.compile("(.*?)(\\.+[mM][zZ][xX]?[mM][lL])?\\s+scan\\s+(\\d+)\\s+\\(charge (\\d+)\\)");
      final Matcher matcher = pattern.matcher(spectrumName);
      if(matcher.matches()) {
        dtaNameSummary = new DtaNameSummary(matcher.group(1), Integer.parseInt(matcher.group(3)), Integer.parseInt(matcher.group(3)),
            Short.parseShort(matcher.group(4)));
      }
    }
    return dtaNameSummary;
  }

  public List<ReportEntry> parse(@WillClose final InputStream inputStream) {
    return parse(new InputStreamReader(inputStream));
  }

  private List<ReportEntry> parse(@WillClose final Reader reader) {
    final LineNumberReader lineReader = new LineNumberReader(reader);
    try {
      final LinkedList<ReportEntry> scaffoldEntries = Lists.newLinkedList();
      boolean tableStart = false;
      while(!tableStart) {
        tableStart = IO_UTILS.readLine(lineReader).startsWith("Experiment name");
      }
      while(true) {
        // Current line of the table
        final String line = IO_UTILS.readLine(lineReader);

        // Report ends with END OF FILE, so check for this
        if(line.startsWith("END OF FILE")) {
          break;
        }

        final String[] values = line.split("\t");

        final String spectrumName = values[16];

        // DTA specific... Make it more generic
        DtaNameSummary dtaNameSummary = getNameSummary(spectrumName);
        final String spectraId = dtaNameSummary.getBasename();
        final int alt = dtaNameSummary.getStart();
        final int number = dtaNameSummary.getEnd();
        final short charge = dtaNameSummary.getCharge();

        final String proteinAccession = values[5];
        final double proteinProbability = parsePercent(values[8]);
        final String peptideSequence = values[17];
        final double piptideProbability = parsePercent(values[20]);

        scaffoldEntries.add(new ScaffoldEntry(spectraId, number, alt, charge, proteinAccession, proteinProbability, peptideSequence,
            piptideProbability));
      }
      return scaffoldEntries;
    } finally {
      IO_UTILS.closeQuietly(lineReader);
      IO_UTILS.closeQuietly(reader);
    }
  }

}
