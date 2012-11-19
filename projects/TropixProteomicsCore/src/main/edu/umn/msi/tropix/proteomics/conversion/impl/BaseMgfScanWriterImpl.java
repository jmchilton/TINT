package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.OutputStream;
import java.util.Formatter;
import java.util.List;

import com.google.common.base.Function;

import edu.umn.msi.tropix.proteomics.conversion.DtaNameUtils;
import edu.umn.msi.tropix.proteomics.conversion.DtaNameUtils.DtaNameSummary;
import edu.umn.msi.tropix.proteomics.conversion.MultiChargeScan;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter.MgfConversionOptions;
import edu.umn.msi.tropix.proteomics.conversion.Scan;

abstract class BaseMgfScanWriterImpl implements MgfScanWriter {
  private final OutputStream outputStream;
  private final MgfConversionOptions options;
  private final Formatter formatter;
  private final List<Function<Scan, Scan>> scanTransformers;

  protected BaseMgfScanWriterImpl(final OutputStream outputStream, final MgfConversionOptions options) {
    this.outputStream = outputStream;
    this.options = options;
    formatter = new Formatter(outputStream);
    this.scanTransformers = options.getScanTransformers();
  }

  public void writeScan(final Scan inputScan) {
    Scan scan = inputScan;
    for(final Function<Scan, Scan> scanTransformer : scanTransformers) {
      scan = scanTransformer.apply(scan);
    }
    final int msLevel = scan.getMsLevel();
    if(msLevel != 2) {
      return;
    }
    final double[] peaks = scan.getPeaks();
    if(ConversionUtils.isEmptyPeaks(peaks)) {
      return;
    }
    writeLine("BEGIN IONS");
    writeScanHeader(scan);
    writePeaks(peaks);
  }

  private void writePeaks(final double[] doubles) {
    final int numPairs = doubles.length / 2;
    for(int i = 0; i < numPairs; i++) {
      writeLine(formatPair(doubles[2 * i], doubles[2 * i + 1]));
    }
    writeLine("END IONS");
    formatter.flush();
  }

  protected void writeLine(final String line) {
    formatter.format("%s%s", line, MzXMLToMGFConverterStreamingImpl.NEWLINE);
  }

  protected void writeHeader(final String headerName, final String headerValue) {
    writeLine(String.format("%s=%s", headerName, headerValue));
  }

  protected void writeTitle(final String title) {
    writeHeader("TITLE", title);
  }

  protected void writePepMass(final String pepMass) {
    writeHeader("PEPMASS", pepMass);
  }

  protected void writeCharge(final String charge) {
    writeHeader("CHARGE", charge);
  }

  protected short getPrecursorCharge(final Scan scan) {
    final double[] doubles = scan.getPeaks();
    final double precursorMzValue = scan.getPrecursorMz();
    short precursorCharge = scan.getPrecursorCharge();
    if(precursorCharge == 0 && ConversionUtils.isPlus1ChargeState(doubles, precursorMzValue)) {
      precursorCharge = 1;
    }
    return precursorCharge;
  }

  protected void writePepMass(final Scan scan) {
    final double precursorMzValue = scan.getPrecursorMz();
    final Double intensity = scan.getPrecursorIntensity();

    if(intensity <= 0.0) {
      writePepMass(formatDouble(precursorMzValue));
    } else {
      writePepMass(formatPair(precursorMzValue, intensity));
    }
  }

  protected String formatPair(final double value1, final double value2) {
    return String.format("%s %s", formatDouble(value1), formatDouble(value2));
  }

  protected String formatDouble(double value) {
    return String.format("%f", value);
  }

  protected void writeCharge(final Scan scan) {
    if(scan instanceof MultiChargeScan) {
      final MultiChargeScan mScan = (MultiChargeScan) scan;
      final StringBuilder chargesStr = new StringBuilder();
      boolean first = true;
      for(short precursorCharge : mScan.getCharges()) {
        if(!first) {
          chargesStr.append(" and ");
        } else {
          first = false;
        }
        chargesStr.append(String.format("%d+", precursorCharge));
      }
      writeCharge(chargesStr.toString());
    } else {
      final short precursorCharge = getPrecursorCharge(scan);
      if(precursorCharge != 0) {
        writeCharge(String.format("%d+", precursorCharge));
      }
    }
  }

  protected void writeScans(final Scan scan) {
    writeHeader("SCANS", getScansText(scan));
  }

  protected String getScansText(final Scan scan) {
    return scan.getNumber() == scan.getAlt() ? String.format("%d", scan.getNumber()) : String.format("%d-%d", scan.getAlt(), scan.getNumber());
  }

  protected void writeScanHeader(final Scan scan) {
    writePepMass(scan);

    writeCharge(scan);
    if(doWriteScanNumbers()) {
      writeScans(scan);
    }
    final short precursorCharge = getPrecursorCharge(scan);
    writeTitle(scan, precursorCharge);
  }

  private static String getTitle(final Scan scan, final int charge) {
    String title;
    if(!scan.isParentFileNameExplicit()) {
      title = scan.getParentName() + "." + scan.getNumber() + "." + scan.getNumber() + "." + charge;
    } else {
      title = scan.getParentFileName();
    }
    return title;
  }

  protected String getDefaultTitle(final Scan scan, final short precursorCharge) {
    String title = getTitle(scan, precursorCharge);
    if(DtaNameUtils.isDtaName(title)) {
      final DtaNameSummary dtaNameSummary = DtaNameUtils.getDtaNameSummary(title);
      title = String.format("%s.%05d.%05d.%d", dtaNameSummary.getBasename(), dtaNameSummary.getStart(), dtaNameSummary.getEnd(),
          dtaNameSummary.getCharge());
    }
    return title;
  }

  abstract void writeTitle(final Scan scan, final short precursorCharge);

  protected boolean doWriteScanNumbers() {
    return true;
  }

}