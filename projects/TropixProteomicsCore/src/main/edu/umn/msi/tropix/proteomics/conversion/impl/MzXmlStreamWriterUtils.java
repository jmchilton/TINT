package edu.umn.msi.tropix.proteomics.conversion.impl;

import static edu.umn.msi.tropix.proteomics.conversion.impl.XMLStreamWriterUtils.flush;
import static edu.umn.msi.tropix.proteomics.conversion.impl.XMLStreamWriterUtils.writeAttribute;
import static edu.umn.msi.tropix.proteomics.conversion.impl.XMLStreamWriterUtils.writeCharacters;
import static edu.umn.msi.tropix.proteomics.conversion.impl.XMLStreamWriterUtils.writeDefaultNamespace;
import static edu.umn.msi.tropix.proteomics.conversion.impl.XMLStreamWriterUtils.writeEndElement;
import static edu.umn.msi.tropix.proteomics.conversion.impl.XMLStreamWriterUtils.writeStartElement;

import java.io.OutputStream;

import javax.annotation.Nullable;
import javax.xml.stream.XMLStreamWriter;

import edu.umn.msi.tropix.proteomics.conversion.Scan;

public class MzXmlStreamWriterUtils {

  public static enum MzXmlParentFileType {
    RAW("RAWData"), PROCESSED("processedData");

    private final String value;

    private MzXmlParentFileType(final String value) {
      this.value = value;
    }

    public String getValue() {
      return value;
    }
  }

  public static XMLStreamWriter getMzxmlStreamWriter(final OutputStream outputStream) {

    final XMLStreamWriter xmlStreamWriter = XMLStreamWriterUtils.get(outputStream, "http://sashimi.sourceforge.net/schema_revision/mzXML_3.0");
    writeStartElement(xmlStreamWriter, "mzXML");
    writeDefaultNamespace(xmlStreamWriter, "http://sashimi.sourceforge.net/schema_revision/mzXML_3.0");
    writeStartElement(xmlStreamWriter, "msRun");
    return xmlStreamWriter;
  }

  public static void finishMzxmlStreamWriter(final XMLStreamWriter mzxmlStreamWriter) {
    writeEndElement(mzxmlStreamWriter); // Close <msRun>
    writeStartElement(mzxmlStreamWriter, "indexOffset");
    writeCharacters(mzxmlStreamWriter, "0");
    writeEndElement(mzxmlStreamWriter); // close indexOffset
    writeEndElement(mzxmlStreamWriter); // close mzxml
    flush(mzxmlStreamWriter);
  }

  public static void writeDataProcessing(final XMLStreamWriter mzxmlStreamWriter) {
    writeStartElement(mzxmlStreamWriter, "dataProcessing");
    writeStartElement(mzxmlStreamWriter, "software");
    writeAttribute(mzxmlStreamWriter, "type", "conversion");
    writeAttribute(mzxmlStreamWriter, "name", "ProTIP");
    writeAttribute(mzxmlStreamWriter, "version", "1.14+");
    writeEndElement(mzxmlStreamWriter);
    writeEndElement(mzxmlStreamWriter);
  }

  public static void addPrecursor(final XMLStreamWriter xmlStreamWriter, final Scan scan) {
    final short precursorCharge = scan.getPrecursorCharge();
    final double precursorMz = scan.getPrecursorMz();
    final double precursorIntensity = scan.getPrecursorIntensity();

    if(precursorMz != 0.0) {
      writeStartElement(xmlStreamWriter, "precursorMz");
      // Potentially writing out a precursorIntensity of 0.0 indicating we have no value
      writeAttribute(xmlStreamWriter, "precursorIntensity", Double.toString(precursorIntensity));
      if(precursorCharge > 0) {
        writeAttribute(xmlStreamWriter, "precursorCharge", Short.toString(precursorCharge));
      }
      writeCharacters(xmlStreamWriter, Double.toString(precursorMz));
      writeEndElement(xmlStreamWriter); // close <precursorMz>
    }

  }

  public static void addPeaks(final XMLStreamWriter xmlStreamWriter, final double[] peaks) {
    writeStartElement(xmlStreamWriter, "peaks");
    writeAttribute(xmlStreamWriter, "precision", "64");
    writeAttribute(xmlStreamWriter, "byteOrder", "network");
    writeAttribute(xmlStreamWriter, "pairOrder", "m/z-int");

    final byte[] encodedPeaks = ConversionUtils.doubles2bytes(peaks);
    final String base64Peaks = new String(org.apache.commons.codec.binary.Base64.encodeBase64(encodedPeaks));
    writeCharacters(xmlStreamWriter, base64Peaks);
    writeEndElement(xmlStreamWriter); // close peaks
  }

  public static void writeScan(final XMLStreamWriter xmlStreamWriter, final Scan scan, @Nullable final String parentSha1) {
    final double[] peaks = scan.getPeaks();
    if(ConversionUtils.isEmptyPeaks(peaks)) {
      return;
    }
    final String scanNumber = Long.toString(scan.getNumber());
    final int peaksCount = peaks.length / 2;

    writeStartElement(xmlStreamWriter, "scan");
    writeAttribute(xmlStreamWriter, "num", scanNumber);
    writeAttribute(xmlStreamWriter, "msLevel", Integer.toString(scan.getMsLevel()));
    writeAttribute(xmlStreamWriter, "peaksCount", Integer.toString(peaksCount));
    if(parentSha1 != null) {
      writeScanOrigin(xmlStreamWriter, scanNumber, parentSha1);
    }
    addPrecursor(xmlStreamWriter, scan);
    addPeaks(xmlStreamWriter, peaks);

    writeEndElement(xmlStreamWriter); // end <scan>
  }

  private static void writeScanOrigin(final XMLStreamWriter xmlStreamWriter, final String scanNumber, final String parentSha1) {
    writeStartElement(xmlStreamWriter, "scanOrigin");
    writeAttribute(xmlStreamWriter, "parentFileID", parentSha1);
    writeAttribute(xmlStreamWriter, "num", scanNumber);
    writeEndElement(xmlStreamWriter);
  }

  public static void writeParentFile(final XMLStreamWriter mzxmlStreamWriter, final MzxmlParentFile parentFile) {
    writeStartElement(mzxmlStreamWriter, "parentFile");
    writeAttribute(mzxmlStreamWriter, "fileName", parentFile.getSourceFileName());
    writeAttribute(mzxmlStreamWriter, "fileType", parentFile.getFileType().getValue());
    writeAttribute(mzxmlStreamWriter, "fileSha1", parentFile.getSha1());
    writeEndElement(mzxmlStreamWriter);
  }

}
