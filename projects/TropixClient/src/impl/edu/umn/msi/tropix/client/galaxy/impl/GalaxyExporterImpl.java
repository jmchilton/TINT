package edu.umn.msi.tropix.client.galaxy.impl;

import java.io.File;
import java.util.Date;

import javax.annotation.ManagedBean;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang.time.DateFormatUtils;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.FilesystemPathsLibraryUpload;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.github.jmchilton.blend4j.galaxy.beans.LibraryContent;
import com.google.common.base.Supplier;
import com.google.common.collect.Iterables;
import com.sun.jersey.api.client.ClientResponse;

import edu.umn.msi.tropix.client.galaxy.GalaxyExportOptions;
import edu.umn.msi.tropix.client.galaxy.GalaxyExporter;
import edu.umn.msi.tropix.files.export.ExportStager;

@ManagedBean
public class GalaxyExporterImpl implements GalaxyExporter {

  @Inject
  public GalaxyExporterImpl(@Named("galaxyInstanceSupplier") final Supplier<GalaxyInstance> galaxyInstanceSupplier, final ExportStager exportStager) {
    this.galaxyInstanceSupplier = galaxyInstanceSupplier;
    this.exportStager = exportStager;
  }

  private Supplier<GalaxyInstance> galaxyInstanceSupplier;
  private final ExportStager exportStager;

  public void uploadFiles(final String userId, final GalaxyExportOptions exportOptions) {
    final File exportDirectory = exportStager.stageFilesForExport(userId, Iterables.toArray(exportOptions.getFileObjectIds(), String.class));
    try {
      Thread.sleep(10000);
    } catch(InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    final LibrariesClient client = galaxyInstanceSupplier.get().getLibrariesClient();

    final Library library = new Library();
    final String dateStr = DateFormatUtils.ISO_DATETIME_FORMAT.format(new Date());
    library.setName("TINT_upload_" + exportOptions.getName() + "_" + dateStr);
    library.setDescription("Auto-generated data library for TINT upload on " + dateStr);

    final Library createdLibrary = client.createLibrary(library);
    final String libraryId = createdLibrary.getId();
    final LibraryContent rootFolder = client.getRootFolder(libraryId);

    final FilesystemPathsLibraryUpload upload = new FilesystemPathsLibraryUpload();
    upload.setContent(exportDirectory.getAbsolutePath());
    upload.setFolderId(rootFolder.getId());
    final ClientResponse response = client.uploadFilesystemPathsRequest(libraryId, upload);
    if(response.getStatus() != 200) {
      throw new RuntimeException("Failed to upload file paths" + response.getEntity(String.class));
    }

  }

}
