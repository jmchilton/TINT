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

import java.io.LineNumberReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.WillClose;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;

class ScaffoldReportParserImpl implements ScaffoldReportParser {
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();

  private static double parsePercent(final String percent) {
    return Double.parseDouble(percent.substring(0, percent.indexOf('%')));
  }

  public List<ScaffoldEntry> parse(@WillClose final Reader reader) {
    final LineNumberReader lineReader = new LineNumberReader(reader);
    try {
      final LinkedList<ScaffoldEntry> scaffoldEntries = Lists.newLinkedList();
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
        final String[] fileParts = spectrumName.split("\\.");

        final String spectraId = fileParts[0];
        final int alt = Integer.parseInt(fileParts[1]);
        final int number = Integer.parseInt(fileParts[2]);
        final short charge = Short.parseShort(fileParts[3]);

        final String proteinAccession = values[5];
        final double proteinProbability = parsePercent(values[8]);
        final String peptideSequence = values[17];
        final double piptideProbability = parsePercent(values[20]);

        scaffoldEntries.add(new ScaffoldEntry(spectraId, number, alt, charge, proteinAccession, proteinProbability, peptideSequence, piptideProbability));
      }
      return scaffoldEntries;
    } finally {
      IO_UTILS.closeQuietly(lineReader);
      IO_UTILS.closeQuietly(reader);
    }
  }

}
