/********************************************************************************
 * Copyright (c) 2009 Regents of the University of Minnesota
 *
 * This Software was written at the Minnesota Supercomputing Institute
 * http://msi.umn.edu
 *
 * All rights reserved. The following statement of license applies
 * only to this file, and and not to the other files distributed with it
 * or derived therefrom.  This file is made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Minnesota Supercomputing Institute - initial API and implementation
 *******************************************************************************/

package edu.umn.msi.tropix.proteomics.xtandem.impl;

import edu.umn.msi.tropix.common.io.Directories;
import edu.umn.msi.tropix.models.xtandem.XTandemParameters;
import edu.umn.msi.tropix.proteomics.bioml.Bioml;
import edu.umn.msi.tropix.proteomics.bioml.Taxon;
import edu.umn.msi.tropix.proteomics.identification.IdentificationJobProcessorImpl;
import edu.umn.msi.tropix.proteomics.xml.BiomlUtility;
import edu.umn.msi.tropix.proteomics.xtandem.XTandemParameterTranslator;

public class XTandemJobProcessorImpl extends IdentificationJobProcessorImpl<XTandemParameters> {
  private static final String PARAM_PATH = "input.xml", OUTPUT_PATH = "output.xml", TAX_PATH = "taxonomy.xml";
  private static final String MZXML_PATH = "data.mzxml", DB_PATH = "db.fasta";

  private XTandemParameterTranslator xTandemParameterTranslator;
  private final BiomlUtility biomlUtility = new BiomlUtility();
  private String xslPath = null;

  @Override
  public void doPostprocessing() {
    if(this.wasCompletedNormally()) {
      getResourceTracker().add(getStagingDirectory().getInputContext(OUTPUT_PATH));
    }
  }

  @Override
  public void doPreprocessing() {
    super.getMzxml().get(getStagingDirectory().getOutputContext(MZXML_PATH));
    super.getDatabase().get(getStagingDirectory().getOutputContext(DB_PATH));
    final Bioml bioml = new Bioml();
    final Taxon taxon = new Taxon();
    taxon.setLabel("unspecified");
    final edu.umn.msi.tropix.proteomics.bioml.File biomlFile = new edu.umn.msi.tropix.proteomics.bioml.File();
    biomlFile.setFormat(edu.umn.msi.tropix.proteomics.bioml.FileFormat.PEPTIDE);
    biomlFile.setURL(Directories.buildAbsolutePath(getStagingDirectory(), DB_PATH));
    taxon.getFile().add(biomlFile);
    bioml.getTaxon().add(taxon);
    biomlUtility.serialize(bioml, getStagingDirectory().getOutputContext(TAX_PATH));
    final String outputAbsPath = Directories.buildAbsolutePath(getStagingDirectory(), OUTPUT_PATH);
    final String mzxmlAbsPath = Directories.buildAbsolutePath(getStagingDirectory(), MZXML_PATH);
    final String taxAbsPath = Directories.buildAbsolutePath(getStagingDirectory(), TAX_PATH);
    final String xtandemParamContents = xTandemParameterTranslator.getXTandemParameters(super.getParameters(), outputAbsPath, mzxmlAbsPath, "unspecified", taxAbsPath, xslPath);
    getStagingDirectory().getOutputContext(PARAM_PATH).put(xtandemParamContents.getBytes());
    getJobDescription().getJobDescriptionType().setArgument(new String[] {Directories.buildAbsolutePath(getStagingDirectory(), PARAM_PATH)});
  }

  public void setXTandemParameterTranslator(final XTandemParameterTranslator xTandemParameterTranslator) {
    this.xTandemParameterTranslator = xTandemParameterTranslator;
  }

  public void setXslPath(final String xslPath) {
    this.xslPath = xslPath;
  }

}
