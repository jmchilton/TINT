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

package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.xml.stream.XMLStreamWriter;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.proteomics.conversion.MgfToMzxmlConverter;
import edu.umn.msi.tropix.proteomics.conversion.MgfToMzxmlOptions;
import edu.umn.msi.tropix.proteomics.conversion.Scan;

public class MgfToMzXMLConverterStreamingImpl implements MgfToMzxmlConverter {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();

  abstract static class MgfParentFileExtracter {
    abstract Iterator<MzxmlParentFile> getAllParentFiles(final File mgfFile);
    
    abstract String getParentFileSha1ForScan(final Scan scan);
  }
  
  private static void handleParentFiles(final XMLStreamWriter mzxmlStreamWriter, final File sourceFile, final MgfParentFileExtracter writerStrategy) {
    final Iterator<MzxmlParentFile> parentFileIterator = writerStrategy.getAllParentFiles(sourceFile);
    while(parentFileIterator.hasNext()) {
      final MzxmlParentFile parentFile = parentFileIterator.next();
      MzXmlStreamWriterUtils.writeParentFile(mzxmlStreamWriter, parentFile);
    }
  }

  private static void addScans(final XMLStreamWriter xmlStreamWriter, final File mgfFile, final String parentSha1) {
    final InputStream mgfFileInputStream = FILE_UTILS.getFileInputStream(mgfFile);
    final Iterator<Scan> scanIter = new MgfParser().parserMgf(mgfFileInputStream);
    while(scanIter.hasNext()) {
      final Scan scan = scanIter.next();
      handleScan(xmlStreamWriter, scan);
    }
  }

  private static void handleScan(final XMLStreamWriter xmlStreamWriter, final Scan scan) {
    MzXmlStreamWriterUtils.writeScan(xmlStreamWriter, scan, null);
  }

  public void mgfToMzXmxl(final File mgfFile, final OutputStream mzxmlStream, final MgfToMzxmlOptions options) {
    final XMLStreamWriter mzxmlStreamWriter = MzXmlStreamWriterUtils.getMzxmlStreamWriter(mzxmlStream);
    final MgfParentFileExtracter parentFileExtracter = new DefaultMgfParentFileExtracterImpl();
    
    handleParentFiles(mzxmlStreamWriter, mgfFile, parentFileExtracter);
    MzXmlStreamWriterUtils.writeDataProcessing(mzxmlStreamWriter);
    addScans(mzxmlStreamWriter, mgfFile, null);

    MzXmlStreamWriterUtils.finishMzxmlStreamWriter(mzxmlStreamWriter);
  }
  
  public void mgfToMzXmxl(final File mgfFile, final OutputStream mzxmlStream) {
    mgfToMzXmxl(mgfFile, mzxmlStream, new MgfToMzxmlOptions());
  }

}
