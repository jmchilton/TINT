package edu.umn.msi.tropix.proteomics.sequest.impl;

import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;

public class SequestUtils {
  static final String DEFAULT_DATABASE_NAME = "db.fasta";

  public static String sanitizeDatabaseName(final String databaseName) {
    return getSanitizedName(databaseName, "fasta", DEFAULT_DATABASE_NAME);
  }

  // TODO: Now being used by X! Tandem also, move some place more generic and make this
  // class package protected again.
  public static String getSanitizedName(final String inputName, final String extension, final String fallback) {
    final String sanitizedDatabaseName;
    if(StringUtils.hasText(inputName)) {
      final String sanitizedBase = FilenameUtils.getBaseName(inputName).replaceAll("[^\\w_-]", "_");
      if(!StringUtils.hasText(sanitizedBase)) {
        sanitizedDatabaseName = fallback;
      } else {
        sanitizedDatabaseName = sanitizedBase + "." + extension;
      }
    } else {
      sanitizedDatabaseName = fallback;
    }
    return sanitizedDatabaseName;
  }

}
