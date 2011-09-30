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

package edu.umn.msi.tropix.proteomics.tools;

import static java.lang.System.exit;
import static java.lang.System.out;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import net.sourceforge.sashimi.mzxml.v3_0.MzXML;

import org.apache.commons.io.FileUtils;

import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.proteomics.InMemoryDTAListImpl;
import edu.umn.msi.tropix.proteomics.conversion.DTAToMzXMLConverter;
import edu.umn.msi.tropix.proteomics.conversion.impl.DTAToMzXMLConverterImpl;
import edu.umn.msi.tropix.proteomics.xml.MzXMLUtility;

public class DTAToMzXML {

  public static void main(final String[] args) throws Exception {
    if(args.length < 1) {
      usage();
      System.exit(0);
    }
    Collection<File> files = null;

    if(args[0].equals("-files")) {
      if(args.length < 2) {
        out.println("No files specified.");
        usage();
        exit(-1);
      } else {
        files = new ArrayList<File>(args.length - 1);
        for(int i = 1; i < args.length; i++) {
          files.add(new File(args[i]));
        }
      }
    } else if(args[0].equals("-directory")) {
      File directory;
      if(args.length < 2) {
        directory = new File(System.getProperty("user.dir"));
      } else {
        directory = new File(args[2]);
      }
      files = FileUtilsFactory.getInstance().listFiles(directory, new String[] {"dta"}, false);
    } else {
      usage();
      exit(-1);
    }

    final InMemoryDTAListImpl dtaList = new InMemoryDTAListImpl();
    File firstFile = null;
    if(files.size() == 0) {
      out.println("No files found.");
      exit(-1);
    } else {
      firstFile = files.iterator().next();
    }
    for(final File file : files) {
      dtaList.add(FileUtils.readFileToByteArray(file), file.getName());
    }

    final DTAToMzXMLConverter dtaToMzXMLConverter = new DTAToMzXMLConverterImpl();
    final MzXML mzxml = dtaToMzXMLConverter.dtaToMzXML(dtaList, null);
    final String mzxmlName = firstFile.getName().substring(0, firstFile.getName().indexOf(".")) + ".mzXML";
    new MzXMLUtility().serialize(mzxml, mzxmlName);
  }

  private static void usage() {
    out.println("Usage: ( -files <dta files> ) or ( -directory [directory]) ");
  }

}