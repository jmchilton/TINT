package edu.umn.msi.tropix.webgui.server.download;

import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.ManagedBean;
import javax.inject.Inject;

import org.apache.commons.io.FilenameUtils;

import com.google.common.base.Function;

import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.models.utils.StockFileExtensionEnum;
import edu.umn.msi.tropix.webgui.client.utils.Lists;

@ManagedBean
@FileDownloadHandlerType("bulkMgf")
public class BulkMgfDownloadHanderImpl implements FileDownloadHandler {
  private final MgfDownloadHelper mgfDownloadHelper;

  @Inject
  public BulkMgfDownloadHanderImpl(final MgfDownloadHelper mgfDownloadHelper) {
    this.mgfDownloadHelper = mgfDownloadHelper;
  }

  public void processDownloadRequest(final OutputStream stream, final Function<String, String> accessor) {
    final String runIdsString = accessor.apply("id");
    // final String style = accessor.apply("mgfStyle");
    final Iterable<String> runIds = Lists.newArrayList(runIdsString.split("\\s*,\\s*"));
    final ZipOutputStream zipOutputStream = new ZipOutputStream(stream);
    ZipEntry zipEntry;

    try {
      for(String runId : runIds) {
        final TropixFile file = mgfDownloadHelper.getMzXMLTropixFile(runId);
        final String mgfName = FilenameUtils.getBaseName(file.getName()) + StockFileExtensionEnum.MASCOT_GENERIC_FORMAT.getExtension();
        zipEntry = new ZipEntry(mgfName);
        zipOutputStream.putNextEntry(zipEntry);
        mgfDownloadHelper.writeMgf(file, accessor, zipOutputStream);
        zipOutputStream.closeEntry();
      }
      zipOutputStream.finish();
    } catch(IOException e) {
      throw new RuntimeException(e);
    }
  }

}
