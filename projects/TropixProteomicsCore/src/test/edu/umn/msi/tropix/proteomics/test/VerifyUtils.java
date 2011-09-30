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

package edu.umn.msi.tropix.proteomics.test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.List;

import net.sourceforge.sashimi.mzxml.v3_0.MsRun;
import net.sourceforge.sashimi.mzxml.v3_0.MsRun.ParentFile;
import net.sourceforge.sashimi.mzxml.v3_0.MzXML;
import net.sourceforge.sashimi.mzxml.v3_0.Scan;
import net.sourceforge.sashimi.mzxml.v3_0.Scan.Peaks;
import net.sourceforge.sashimi.mzxml.v3_0.Scan.ScanOrigin;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import com.google.common.base.Function;

import edu.umn.msi.tropix.common.collect.Collections;
import edu.umn.msi.tropix.proteomics.DTAList;

public class VerifyUtils {

  public static void verifyMGF(final InputStream stream) {
    final LineIterator lineIterator = IOUtils.lineIterator(new InputStreamReader(stream));
    assert lineIterator.hasNext();
    String line = lineIterator.nextLine();
    assert line.startsWith("COM=");
    assert lineIterator.hasNext();
    line = lineIterator.nextLine();
    assert line.startsWith("CHARGE=");
    while(true) {
      if(!lineIterator.hasNext()) {
        break;
      }
      line = lineIterator.nextLine();
      assert line.equals("BEGIN IONS") : line;
      line = lineIterator.nextLine();
      assert line.matches("PEPMASS=(\\d*\\.?\\d*)( (\\d*\\.?\\d*))?") : "Does not appear to be a peptide mass line " + line;
      line = lineIterator.nextLine();
      assert line.matches("CHARGE=[\\d, \\+]+") : line;
      line = lineIterator.nextLine();
      assert line.startsWith("SCANS=") : line;
      line = lineIterator.nextLine();
      assert line.startsWith("TITLE=") : line;
      while(true) {
        line = lineIterator.nextLine();

        if(line.equals("END IONS")) {
          break;
        } else if(line.matches(RegexUtils.FLOATING_POINT_LITERAL + "\\s+" + RegexUtils.FLOATING_POINT_LITERAL)) {
          continue;
        } else {
          assert false : "MGF contains invalid line " + line;
        }
      }
    }
  }

  private static final Function<ParentFile, String> GET_PARENT_ID_FUNCTION = new Function<ParentFile, String>() {

    public String apply(final ParentFile parentFile) {
      return parentFile.getFileSha1();
    }

  };

  public static void verifyMzXML(final MzXML mzxml) {
    final MsRun run = mzxml.getMsRun();
    final List<ParentFile> parentFiles = run.getParentFile();
    for(final Scan scan : run.getScan()) {

      for(final ScanOrigin scanOrigin : scan.getScanOrigin()) {
        final String parentFileId = scanOrigin.getParentFileID();
        assert Collections.transform(parentFiles, GET_PARENT_ID_FUNCTION).contains(parentFileId);
      }
      assert scan.getNum() != null;
      assert scan.getPeaks() != null;
      for(final Peaks peaks : scan.getPeaks()) {
        final int len = peaks.getCompressedLen();
        assert len == (peaks.getValue().length * 4 / 3);
      }
    }
  }

  public static void verifyDTAList(final DTAList dtaList) {
    for(final DTAList.Entry entry : dtaList) {
      final byte[] bytes = entry.getContents();
      final LineIterator lineIterator = IOUtils.lineIterator(new StringReader(new String(bytes)));
      final String firstLine = lineIterator.nextLine();
      assert firstLine.matches("^\\d+\\.\\d+\\s+\\d+\\s*$");
      while(lineIterator.hasNext()) {
        final String line = lineIterator.nextLine();
        assert line.matches(RegexUtils.FLOATING_POINT_LITERAL + "\\s+" + RegexUtils.FLOATING_POINT_LITERAL + "\\s*") : "Line is not of form -- <double> <double>";
      }
      final String name = entry.getName();
      assert name.matches("^.*\\.\\d+\\.\\d+\\.\\d+\\.[dD][tT][aA]$");
    }
  }

}
