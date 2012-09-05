package edu.umn.msi.tropix.client.galaxy.impl;

import org.testng.annotations.Test;

import com.github.jmchilton.blend4j.galaxy.GalaxyInstance;
import com.github.jmchilton.blend4j.galaxy.LibrariesClient;
import com.github.jmchilton.blend4j.galaxy.beans.Library;
import com.google.common.base.Suppliers;

import edu.umn.msi.tropix.client.galaxy.GalaxyExportOptions;

public class GalaxyExporterImplTest {

  @Test(groups = "unit")
  public void blendTest() {
    final GalaxyInstance galaxyInstance = null;
    final LibrariesClient client = galaxyInstance.getLibrariesClient();
    for(final Library library : client.getLibraries()) {
      System.out.println(library);
    }
    GalaxyExporterImpl uploader = new GalaxyExporterImpl(Suppliers.ofInstance(galaxyInstance), null);
    final GalaxyExportOptions exportOptions = new GalaxyExportOptions();
    exportOptions.setName("ExportTest");
    exportOptions.getFileObjectIds().add("12345");
    uploader.uploadFiles(null, exportOptions);
  }

}
