package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;

import com.google.common.collect.UnmodifiableIterator;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IORuntimeException;
import edu.umn.msi.tropix.proteomics.conversion.Scan;

public class PeakListParserImpl implements PeakListParser {
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private XmlPeakListParser xmlParser = new XmlPeakListParserImpl();
  private MgfParser mgfParser = new MgfParser();

  enum PeakFileType {
    XML, MGF
  };

  public UnmodifiableIterator<Scan> parse(File peakFile, final PeakListParserOptions options) {
    final FileReader fileReader = FILE_UTILS.getFileReader(peakFile);
    UnmodifiableIterator<Scan> scanIterator = null;
    try {
      final char[] firstBytes = new char[1024 * 5];
      fileReader.read(firstBytes);
      final PeakFileType peakFileType = detectPeakFileType(new String(firstBytes));
      final FileInputStream inputStream = FILE_UTILS.getFileInputStream(peakFile);
      // This is very problematic, we have to start closing the input stream in the parser.
      if(peakFileType == PeakFileType.XML) {
        scanIterator = xmlParser.parse(inputStream);
      } else {
        scanIterator = mgfParser.parserMgf(inputStream, options);
      }
    } catch(IOException e) {
      throw new IORuntimeException(e);
    }
    return scanIterator;
  }

  private PeakFileType detectPeakFileType(final String start) {
    int xmlLocation = checkForPos(start, new String[] {"<?xml", "<mzXML", "<mzML"});
    int mgfLocation = checkForPos(start, "BEGIN IONS");
    if(xmlLocation < mgfLocation) {
      return PeakFileType.XML;
    } else if(mgfLocation < Integer.MAX_VALUE) {
      return PeakFileType.MGF;
    } else {
      throw new RuntimeException("Failed to determine peak list file type");
    }
  }

  private int checkForPos(final String query, final String[] targets) {
    int minPos = Integer.MAX_VALUE;
    for(final String target : targets) {
      minPos = Math.min(minPos, checkForPos(query, target));
    }
    return minPos;
  }

  private int checkForPos(final String query, final String target) {
    int index = query.indexOf(target);
    return index < 0 ? Integer.MAX_VALUE : index;
  }

}
