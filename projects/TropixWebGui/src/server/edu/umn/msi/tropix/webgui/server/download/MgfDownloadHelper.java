package edu.umn.msi.tropix.webgui.server.download;

import java.io.OutputStream;

import com.google.common.base.Function;

import edu.umn.msi.tropix.models.TropixFile;

public interface MgfDownloadHelper {

  void writeMgf(final String objectId, final Function<String, String> accessor, final OutputStream outputStream);

  void writeMgf(final TropixFile mzxmlTropixFile, final Function<String, String> accessor, final OutputStream outputStream);

  TropixFile getMzXMLTropixFile(final String objectId);

}