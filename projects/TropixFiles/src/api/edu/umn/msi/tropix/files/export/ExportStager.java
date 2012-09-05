package edu.umn.msi.tropix.files.export;

import java.io.File;

public interface ExportStager {
  File stageFilesForExport(final String userId, final String[] fileObjectIds);
}
