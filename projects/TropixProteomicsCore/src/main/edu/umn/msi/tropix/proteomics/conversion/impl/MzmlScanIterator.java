package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.stream.XMLStreamReader;

import org.springframework.util.StringUtils;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import edu.umn.msi.tropix.proteomics.conversion.Scan;
import edu.umn.msi.tropix.proteomics.xml.AbstractXmlIterator;

public class MzmlScanIterator extends AbstractXmlIterator<Scan> {
  protected String lastRun = null;

  MzmlScanIterator(InputStream inputStream) {
    super(inputStream, "spectrum");
  }

  MzmlScanIterator(XMLStreamReader reader) {
    super(reader, "spectrum");
  }

  private static class PrecursorData {
    private short chargeState = Scan.DEFAULT_PRECURSOR_CHARGE;
    private double intensity = Scan.DEFAULT_PRECURSOR_INTENSITY;
    private double mz = Scan.DEFAULT_PRECURSOR_MZ;
  }

  private static class ScanData {
    double scanStartTime;
  }

  protected Scan parseNext() {
    final int index = Integer.parseInt(getAttributeValue("index"));
    final String id = getAttributeValue("id");
    final Pattern scanNumPattern = Pattern.compile(".*scan=(\\d+).*");
    final Matcher matcher = scanNumPattern.matcher(id);
    if(!matcher.matches()) {
      throw new IllegalStateException("Failed to parse scan number from mzml");
    }
    final int scanNum = Integer.parseInt(matcher.group(1));
    final Map<String, String> spectrumParams = parseCVParams();
    PrecursorData precursorData = null;
    ScanData scanData = null;
    while(!isStartOf("binaryDataArrayList")) {
      if(isStartOf("precursorList")) {
        precursorData = parsePrecursorData();
        continue;
      }
      if(isStartOf("scanList")) {
        scanData = parseScanData();
        continue;
      }
      advance();
    }
    BinaryDataArray mzArray = null;
    BinaryDataArray intensityArray = null;
    while(!isEndOf("binaryDataArrayList")) {
      if(isStartOf("binaryDataArray")) {
        final BinaryDataArray binaryDataArray = readBinaryDataArray();
        if(binaryDataArray.params.containsKey("m/z array")) {
          mzArray = binaryDataArray;
        } else if(binaryDataArray.params.containsKey("intensity array")) {
          intensityArray = binaryDataArray;
        }
      }
      advance();
    }
    if(!spectrumParams.containsKey("ms level")) {
      throw new IllegalStateException("Could not find ms level in params " + Iterables.toString(spectrumParams.keySet()));
    }
    final int msLevel = Integer.parseInt(spectrumParams.get("ms level"));
    if(mzArray == null) {
      throw new IllegalStateException("Could not find array of m/z values for scanIndex" + index);
    }
    if(intensityArray == null) {
      throw new IllegalStateException("Could not find array of intensity values for scan index " + index);
    }
    double[] mzValues = mzArray.parseData();
    double[] intensityValues = intensityArray.parseData();
    /*
     * System.out.println("m/z values (" + mzValues.length + "):" + Arrays.toString(mzValues));
     * System.out.println("intensity values (" + intensityValues.length + "):" + Arrays.toString(intensityValues));
     */
    final Scan scan = new Scan(msLevel, index, scanNum, mzValues, intensityValues);
    if(precursorData != null) {
      scan.setPrecursorCharge(precursorData.chargeState);
      scan.setPrecursorIntensity(precursorData.intensity);
      scan.setPrecursorMz(precursorData.mz);
    }
    if(scanData != null && scanData.scanStartTime > 0.0d) {
      scan.setRt(Math.round(scanData.scanStartTime * 1000L));
    }
    scan.setParentFileName(lastRun);
    return scan;
  }

  private ScanData parseScanData() {
    if(!getAttributeValue("count").equals("1")) {
      throw new IllegalStateException("Parsre cannot scans for one spectrum.");
    }
    if(!isStartOf("scan")) {
      XMLStreamReaderUtils.skipToElement(reader(), "scan");
    }
    // XMLStreamReaderUtils.skipToElement(reader(), "cvParam");
    final String scanStartTimeStr = parseCVParams().get("scan start time");
    final ScanData scanData = new ScanData();
    if(StringUtils.hasText(scanStartTimeStr)) {
      scanData.scanStartTime = Double.parseDouble(scanStartTimeStr);
    }
    return scanData;
  }

  private PrecursorData parsePrecursorData() {
    if(!getAttributeValue("count").equals("1")) {
      throw new IllegalStateException("Parser cannot handle multiple precursors.");
    }
    XMLStreamReaderUtils.skipToElement(reader(), "selectedIon");
    final Map<String, String> params = parseCVParams();
    PrecursorData data = new PrecursorData();
    if(params.containsKey("charge state")) {
      data.chargeState = Short.parseShort(params.get("charge state"));
    }
    if(params.containsKey("selected ion m/z")) {
      data.mz = Float.parseFloat(params.get("selected ion m/z"));
    }
    if(params.containsKey("intensity")) {
      data.intensity = Float.parseFloat(params.get("intensity"));
    }
    return data;
  }

  private BinaryDataArray readBinaryDataArray() {
    BinaryDataArray data = new BinaryDataArray();
    data.params = parseCVParams();
    if(!isStartOf("binary")) {
      XMLStreamReaderUtils.skipToElement(reader(), "binary");
    }
    data.data = XMLStreamReaderUtils.getElementText(reader());
    return data;
  }

  private static class BinaryDataArray {
    private Map<String, String> params;
    private String data;

    private double[] parseData() {
      boolean is64Bit = !params.containsKey("32-bit float");
      boolean isCompressed = params.containsKey("zlib compression");
      return ConversionUtils.extractDoublesFromBase64(data, is64Bit, true, isCompressed);
    }
  }

  private Map<String, String> parseCVParams() {
    final Map<String, String> params = Maps.newHashMap();
    advance();
    while(true) {
      final boolean isStartOfElement = XMLStreamReaderUtils.isStartElement(reader());
      final boolean isStartOfParam = isStartOf("cvParam");
      if(isStartOfElement && !isStartOfParam) {
        // System.out.println("Breaking on " + reader().getName().getLocalPart());
        break;
      }
      if(isStartOfParam) {
        params.put(getAttributeValue("name"), getAttributeValue("value"));
      }
      advance();
    }
    return params;
  }

  @Override
  protected void handleNext() {
    if(isStartOf("run")) {
      lastRun = getAttributeValue("id");
    }
  }

}
