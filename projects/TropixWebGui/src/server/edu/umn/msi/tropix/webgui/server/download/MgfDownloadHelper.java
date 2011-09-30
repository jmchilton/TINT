package edu.umn.msi.tropix.webgui.server.download;

import java.io.OutputStream;

import edu.umn.msi.tropix.models.TropixFile;

public interface MgfDownloadHelper {

  void writeMgf(final String objectId, final String style, final OutputStream outputStream);
  
  void writeMgf(final TropixFile mzxmlTropixFile, final String style, final OutputStream outputStream);  
  
  TropixFile getMzXMLTropixFile(final String objectId);

}