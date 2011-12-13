package edu.umn.msi.tropix.proteomics.sequest.impl;

import org.apache.commons.io.FilenameUtils;
import org.springframework.util.StringUtils;

class SequestUtils {
  static final String DEFAULT_DATABASE_NAME ="db.fasta";
  
  public static String sanitizeDatabaseName(final String databaseName) {
    final String sanitizedDatabaseName;
    if(StringUtils.hasText(databaseName)) {
      final String sanitizedBase = FilenameUtils.getBaseName(databaseName).replaceAll("[^\\w_-]", "_");
      if(!StringUtils.hasText(sanitizedBase)) {
        sanitizedDatabaseName = DEFAULT_DATABASE_NAME;
      } else {
        sanitizedDatabaseName = sanitizedBase + ".fasta";              
      }
    } else {
      sanitizedDatabaseName = DEFAULT_DATABASE_NAME;
    }
    return sanitizedDatabaseName;    
  }
  
}
