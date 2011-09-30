package edu.umn.msi.tropix.proteomics.mascot;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

//import edu.umn.msi.tropix.proteomics.sequest.SequestParamTranslator;
import edu.umn.msi.tropix.common.io.FileCollection;
import edu.umn.msi.tropix.common.io.FileCollectionException;
import edu.umn.msi.tropix.common.io.TempDirectoryCreator;
import edu.umn.msi.tropix.common.io.fileset.utils.FileSetFileCollection;
import edu.umn.msi.tropix.common.jobqueue.ApplicationInformation;
import edu.umn.msi.tropix.common.jobqueue.impl.ApplicationInformationBean;
import edu.umn.msi.tropix.proteomics.ProteomicsJob;
import edu.umn.msi.tropix.proteomics.ProteomicsParameters;

public class MascotJob extends ProteomicsJob {
	private static Logger logger = Logger.getLogger(MascotJob.class);
	private MascotParamTranslator translator;
	TempDirectoryCreator tempDirectoryCreator;
	File applicationPath;
	File parentPath, paramFile, outputFile;
	ApplicationInformation appInfo;
	
	public MascotJob(File applicationPath, TempDirectoryCreator tempDirectoryCreator, MascotParamTranslator translator) throws Exception {
		this.applicationPath = applicationPath;
		this.tempDirectoryCreator  = tempDirectoryCreator;
		this.translator = translator;
	}
	
	public ApplicationInformation preprocess() throws IOException {
		logger.info("Preprocessing MascotJob.");
		try {
			parentPath = tempDirectoryCreator.getNewTempDirectory();
			logger.info("Parent path for Mascot executable created. " + parentPath.getAbsolutePath());
		} catch(IOException e) {
			logger.error("Error creating temp directory for MascotJob");
			throw new IOException("Failed to create a temp directory for MascotJob.");
		}
		 
		paramFile = new File(parentPath, "input.mime");
		String mascotParams = null; // translator.proteomicsInputToMascotInput((ProteomicsParameters) parameters, mzxml);
		logger.info("Mascot parameters obtained.");
		
		try {
			FileUtils.writeByteArrayToFile(paramFile, mascotParams.getBytes());
		} catch(IOException e) {
			logger.error("Error writing param file for MascotJob");
			throw new IOException("Failed to write parameter file for MascotJob.");
		}
		
		outputFile = new File(parentPath, "output.dat");
		
		StringBuffer inputArgs = new StringBuffer();
		// Mascot takes this in via STDIN so need to setup script to pull off this file and
		// give it to Mascot.
		inputArgs.append(paramFile.getAbsolutePath());
		// 1 means preform a normal search, see Chapter 7 of Mascot manual.
		inputArgs.append(" 1 -commandline -f " + outputFile.getAbsolutePath());
		appInfo = new ApplicationInformationBean(applicationPath.getAbsolutePath(), inputArgs.toString());
		return appInfo;
	}
	
	public void postprocess(boolean complete) throws IOException {
		try {
			results = FileSetFileCollection.fileCollectionToFileSet(new FileCollection(new File[]{outputFile}));
		} catch(FileCollectionException e) {
			throw new IOException("Failed to read Mascot output file.");			
		} finally {
			outputFile.delete();
			paramFile.delete();
			parentPath.delete();
		}
	}

}
