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

package edu.umn.msi.tropix.proteomics.omssa.impl;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.math.BigInteger;
import java.util.Arrays;

import javax.xml.namespace.QName;

import com.google.common.base.Supplier;

import edu.umn.msi.tropix.common.io.FileUtils;
import edu.umn.msi.tropix.common.io.FileUtilsFactory;
import edu.umn.msi.tropix.common.io.IOUtils;
import edu.umn.msi.tropix.common.io.IOUtilsFactory;
import edu.umn.msi.tropix.common.io.InputContext;
import edu.umn.msi.tropix.common.io.InputContexts;
import edu.umn.msi.tropix.common.io.TempFileSuppliers;
import edu.umn.msi.tropix.common.io.ZipUtils;
import edu.umn.msi.tropix.common.io.ZipUtilsFactory;
import edu.umn.msi.tropix.grid.xml.SerializationUtils;
import edu.umn.msi.tropix.grid.xml.SerializationUtilsFactory;
import edu.umn.msi.tropix.proteomics.conversion.MzXMLToMGFConverter;
import edu.umn.msi.tropix.proteomics.identification.IdentificationJobProcessorImpl;
import gov.nih.nlm.ncbi.omssa.MSInFile;
import gov.nih.nlm.ncbi.omssa.MSInFileMSInFile_infiletype;
import gov.nih.nlm.ncbi.omssa.MSOutFile;
import gov.nih.nlm.ncbi.omssa.MSOutFileMSOutFile_includerequest;
import gov.nih.nlm.ncbi.omssa.MSOutFileMSOutFile_includerequestValue;
import gov.nih.nlm.ncbi.omssa.MSOutFileMSOutFile_outfiletype;
import gov.nih.nlm.ncbi.omssa.MSSearchSettings;
import gov.nih.nlm.ncbi.omssa.MSSearchSettingsMSSearchSettings_infiles;
import gov.nih.nlm.ncbi.omssa.MSSearchSettingsMSSearchSettings_outfiles;
import gov.nih.nlm.ncbi.omssa.MSSerialDataFormat;
import gov.nih.nlm.ncbi.omssa.MSSpectrumFileType;

public class OmssaJobProcessorImpl extends IdentificationJobProcessorImpl<MSSearchSettings> {
  private static final QName QNAME = new QName("http://www.ncbi.nlm.nih.gov", "MSSearchSettings");
  private static final Supplier<File> TEMP_FILE_SUPPLIER = TempFileSuppliers.getDefaultTempFileSupplier();
  private static final FileUtils FILE_UTILS = FileUtilsFactory.getInstance();
  private static final IOUtils IO_UTILS = IOUtilsFactory.getInstance();
  private static final ZipUtils ZIP_UTILS = ZipUtilsFactory.getInstance();
  private final SerializationUtils serializationUtils = SerializationUtilsFactory.getInstance();
  private MzXMLToMGFConverter mzxmlToMGFConverter;
  private boolean zipOutput;

  @Override
  public void doPostprocessing() {
    if(this.wasCompletedNormally()) {
      final InputContext outputFileContext = getStagingDirectory().getInputContext("output.xml");
      if(zipOutput) {
        final OutputStream zipStream = getResourceTracker().newStream();
        ZIP_UTILS.zipContextsToStream(Arrays.<InputContext>asList(outputFileContext), Arrays.asList("output.omx"), zipStream);
        IO_UTILS.closeQuietly(zipStream);
      } else {
        getResourceTracker().add(outputFileContext);
      }
    }
  }

  @Override
  protected void doPreprocessing() {
    String paramPath, outputPath, mgfPath, databasePath;
    paramPath = "input.xml";
    outputPath = "output.xml";
    mgfPath = "data.mgf";
    databasePath = "db.fasta";

    File tempMzXMLFile = null, tempMgfFile = null;
    InputStream mzxmlInputStream = null;
    OutputStream mgfOutputStream = null;
    try {
      tempMzXMLFile = TEMP_FILE_SUPPLIER.get();
      tempMgfFile = TEMP_FILE_SUPPLIER.get();
      writeMzXML(tempMzXMLFile);
      mzxmlInputStream = FILE_UTILS.getFileInputStream(tempMzXMLFile);
      mgfOutputStream = FILE_UTILS.getFileOutputStream(tempMgfFile);
      mzxmlToMGFConverter.mzxmlToMGF(mzxmlInputStream, mgfOutputStream, null);
      FILE_UTILS.deleteQuietly(tempMzXMLFile);
      getStagingDirectory().getOutputContext(mgfPath).put(tempMgfFile);
    } finally {
      IO_UTILS.closeQuietly(mzxmlInputStream);
      IO_UTILS.closeQuietly(mgfOutputStream);
      FILE_UTILS.deleteQuietly(tempMzXMLFile);
      FILE_UTILS.deleteQuietly(tempMgfFile);
    }
    getDatabase().get(getStagingDirectory().getOutputContext(databasePath));

    // Create infile type
    final MSInFileMSInFile_infiletype infiletype = new MSInFileMSInFile_infiletype();
    final MSSpectrumFileType spectrumFileType = new MSSpectrumFileType();
    spectrumFileType.set_value(new BigInteger("7"));
    infiletype.setMSSpectrumFileType(spectrumFileType);

    // Create infiles object
    final MSSearchSettingsMSSearchSettings_infiles infiles = new MSSearchSettingsMSSearchSettings_infiles();
    final MSInFile msInFile = new MSInFile();
    msInFile.setMSInFile_infiletype(infiletype);
    msInFile.setMSInFile_infile(getStagingDirectory().getAbsolutePath() + getStagingDirectory().getSep() + mgfPath);
    infiles.setMSInFile(msInFile);

    // set input files
    getParameters().setMSSearchSettings_infiles(infiles);

    // Create output file type
    final MSOutFileMSOutFile_outfiletype outfiletype = new MSOutFileMSOutFile_outfiletype();
    final MSSerialDataFormat dataFormat = new MSSerialDataFormat();
    dataFormat.set_value(new BigInteger("3")); // XML
    outfiletype.setMSSerialDataFormat(dataFormat);

    // create outputfiles object
    final MSSearchSettingsMSSearchSettings_outfiles outfiles = new MSSearchSettingsMSSearchSettings_outfiles();
    final MSOutFile msOutFile = new MSOutFile();
    final MSOutFileMSOutFile_includerequest includeRequest = new MSOutFileMSOutFile_includerequest();
    includeRequest.setValue(MSOutFileMSOutFile_includerequestValue.fromString("true"));
    msOutFile.setMSOutFile_includerequest(includeRequest);
    msOutFile.setMSOutFile_outfiletype(outfiletype);
    msOutFile.setMSOutFile_outfile(getStagingDirectory().getAbsolutePath() + getStagingDirectory().getSep() + outputPath);
    outfiles.setMSOutFile(msOutFile);

    // set output file
    getParameters().setMSSearchSettings_outfiles(outfiles);

    getParameters().setMSSearchSettings_db("db.fasta");

    final StringWriter writer = new StringWriter();
    serializationUtils.serialize(writer, getParameters(), QNAME);
    IO_UTILS.closeQuietly(writer);
    InputContexts.forString(writer.toString()).get(getStagingDirectory().getOutputContext(paramPath));

    final String dbLibPath = getStagingDirectory().getAbsolutePath() + getStagingDirectory().getSep() + "db";
    getJobDescription().getJobDescriptionType().setDirectory(getStagingDirectory().getAbsolutePath());
    getJobDescription().getJobDescriptionType().setArgument(new String[] {"-d", dbLibPath, "-pm", getStagingDirectory().getAbsolutePath() + getStagingDirectory().getSep() + paramPath});
  }

  public void setMzxmlToMGFConverter(final MzXMLToMGFConverter mzxmlToMGFConverter) {
    this.mzxmlToMGFConverter = mzxmlToMGFConverter;
  }

  public void setZipOutput(final boolean zipOutput) {
    this.zipOutput = zipOutput;
  }

}
