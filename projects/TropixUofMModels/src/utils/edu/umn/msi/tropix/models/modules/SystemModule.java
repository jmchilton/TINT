package edu.umn.msi.tropix.models.modules;

import java.io.Serializable;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.models.utils.StockFileExtensionI;
import static edu.umn.msi.tropix.models.utils.StockFileExtensionEnum.*;

public enum SystemModule implements Serializable {
  BASE(Lists.<StockFileExtensionI>newArrayList(ZIP, XML, TABULAR_XLS, TEXT, UNKNOWN)),
  LOCAL, 
  GRID, 
  SHARING, 
  PROTIP(Lists.<StockFileExtensionI>newArrayList(OMSSA_OUTPUT, FASTA, 
                                                 MZXML, SCAFFOLD_REPORT, SCAFFOLD3_REPORT,
                                                 BOWTIE_INDEX, THERMO_RAW,
                                                 MASCOT_OUTPUT, MASCOT_GENERIC_FORMAT, PEPXML)), 
  GENETIP(Lists.<StockFileExtensionI>newArrayList(FASTQ)), 
  GALAXY, 
  LOCAL_SEARCH, 
  REQUEST, 
  CATALOG, 
  GRID_SEARCH;
  
  private final Iterable<StockFileExtensionI> fileTypes;
  
  private SystemModule() {
    this(Lists.<StockFileExtensionI>newArrayList());
  }
  
  private SystemModule(final Iterable<StockFileExtensionI> fileTypes) {
    this.fileTypes = fileTypes;
  }
  
  public Iterable<StockFileExtensionI> getFileTypes() {
    return fileTypes;
  }

}
