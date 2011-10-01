package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.OutputStream;
import java.util.Formatter;

import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter.MgfConversionOptions;
import edu.umn.msi.tropix.proteomics.conversion.Scan;

abstract class BaseMgfScanWriterImpl implements MgfScanWriter {
  private final OutputStream outputStream;
  private final MgfConversionOptions options;
  private final Formatter formatter;

  protected BaseMgfScanWriterImpl(final OutputStream outputStream, final MgfConversionOptions options) {
    this.outputStream = outputStream;
    this.options = options;
    formatter = new Formatter(outputStream);
  }

  public void writeScan(final Scan scan) {
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
      writeLine(String.format("%f %f", doubles[2 * i], doubles[2 * i + 1]));
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
    final float precursorMzValue = scan.getPrecursorMz();
    short precursorCharge = scan.getPrecursorCharge();
    if(precursorCharge == 0 && ConversionUtils.isPlus1ChargeState(doubles, precursorMzValue)) {
      precursorCharge = 1;
    }
    return precursorCharge;
  }

  protected void writePepMass(final Scan scan) {
    final float precursorMzValue = scan.getPrecursorMz();
    final Float intensity = scan.getPrecursorIntensity();

    if(intensity <= 0.0) {
      writePepMass(String.format("%f", precursorMzValue));
    } else {
      writePepMass(String.format("%f %f", precursorMzValue, intensity));
    }
  }

  protected void writeCharge(final Scan scan, final short precursorCharge) {
    if(precursorCharge != 0) {
      writeCharge(String.format("%d+", precursorCharge));
    }
  }

  protected void writeScans(final Scan scan) {
    writeHeader("SCANS",
        scan.getNumber() == scan.getAlt() ? String.format("%d", scan.getNumber()) : String.format("%d-%d", scan.getAlt(), scan.getNumber()));
  }

  protected void writeScanHeader(final Scan scan) {
    final short precursorCharge = getPrecursorCharge(scan);
    writePepMass(scan);
    writeCharge(scan, precursorCharge);
    writeScans(scan);
    writeTitle(scan, precursorCharge);
  }

  abstract void writeTitle(final Scan scan, final short precursorCharge);

}