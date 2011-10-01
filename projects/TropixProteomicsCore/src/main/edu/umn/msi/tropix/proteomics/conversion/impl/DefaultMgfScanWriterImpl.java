package edu.umn.msi.tropix.proteomics.conversion.impl;

import java.io.OutputStream;

import edu.umn.msi.tropix.proteomics.conversion.DtaNameUtils;
import edu.umn.msi.tropix.proteomics.conversion.DtaNameUtils.DtaNameSummary;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter.MgfConversionOptions;
import edu.umn.msi.tropix.proteomics.conversion.Scan;

class DefaultMgfScanWriterImpl extends BaseMgfScanWriterImpl {

  DefaultMgfScanWriterImpl(final OutputStream outputStream, final MgfConversionOptions options) {
    super(outputStream, options);
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

  protected void writeTitle(final Scan scan, final short precursorCharge) {
    final String title = getTitle(scan, precursorCharge);
    if(DtaNameUtils.isDtaName(title)) {
      final DtaNameSummary dtaNameSummary = DtaNameUtils.getDtaNameSummary(title);
      writeTitle(String.format("%s.%05d.%05d.%d", dtaNameSummary.getBasename(), dtaNameSummary.getStart(), dtaNameSummary.getEnd(),
          dtaNameSummary.getCharge()));
    } else if(title != null) {
      writeTitle(title);
    }
  }
}