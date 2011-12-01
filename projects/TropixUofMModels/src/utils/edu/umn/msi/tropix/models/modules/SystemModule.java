package edu.umn.msi.tropix.models.modules;

import static edu.umn.msi.tropix.models.utils.StockFileExtensionEnum.BOWTIE_INDEX;
import static edu.umn.msi.tropix.models.utils.StockFileExtensionEnum.FASTA;
import static edu.umn.msi.tropix.models.utils.StockFileExtensionEnum.FASTQ;
import static edu.umn.msi.tropix.models.utils.StockFileExtensionEnum.MASCOT_GENERIC_FORMAT;
import static edu.umn.msi.tropix.models.utils.StockFileExtensionEnum.MASCOT_OUTPUT;
import static edu.umn.msi.tropix.models.utils.StockFileExtensionEnum.MS2;
import static edu.umn.msi.tropix.models.utils.StockFileExtensionEnum.MZML;
import static edu.umn.msi.tropix.models.utils.StockFileExtensionEnum.MZXML;
import static edu.umn.msi.tropix.models.utils.StockFileExtensionEnum.OMSSA_OUTPUT;
import static edu.umn.msi.tropix.models.utils.StockFileExtensionEnum.PEPXML;
import static edu.umn.msi.tropix.models.utils.StockFileExtensionEnum.SCAFFOLD3_REPORT;
import static edu.umn.msi.tropix.models.utils.StockFileExtensionEnum.SCAFFOLD_REPORT;
import static edu.umn.msi.tropix.models.utils.StockFileExtensionEnum.TABULAR_XLS;
import static edu.umn.msi.tropix.models.utils.StockFileExtensionEnum.TEXT;
import static edu.umn.msi.tropix.models.utils.StockFileExtensionEnum.THERMO_RAW;
import static edu.umn.msi.tropix.models.utils.StockFileExtensionEnum.UNKNOWN;
import static edu.umn.msi.tropix.models.utils.StockFileExtensionEnum.XML;
import static edu.umn.msi.tropix.models.utils.StockFileExtensionEnum.ZIP;

import java.io.Serializable;

import com.google.common.collect.Lists;

import edu.umn.msi.tropix.models.utils.StockFileExtensionI;

public enum SystemModule implements Serializable {
  BASE(Lists.<StockFileExtensionI>newArrayList(ZIP, XML, TABULAR_XLS, TEXT, UNKNOWN)),
  LOCAL,
  GRID,
  SHARING,
  PROTIP(Lists.<StockFileExtensionI>newArrayList(OMSSA_OUTPUT, FASTA,
      MZXML, SCAFFOLD_REPORT, SCAFFOLD3_REPORT,
      BOWTIE_INDEX, THERMO_RAW,
      MASCOT_OUTPUT, MASCOT_GENERIC_FORMAT,
      PEPXML, MS2, MZML)),
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
