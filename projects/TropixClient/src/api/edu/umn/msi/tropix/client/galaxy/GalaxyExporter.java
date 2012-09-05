package edu.umn.msi.tropix.client.galaxy;

public interface GalaxyExporter {
  void uploadFiles(final String userId, final GalaxyExportOptions exportOptions);
}