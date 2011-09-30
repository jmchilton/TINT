package edu.umn.msi.tropix.proteomics.test;

import java.io.File;

import net.sourceforge.sashimi.mzxml.v3_0.MzXML;

import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import edu.umn.msi.tropix.proteomics.bioml.Bioml;
import edu.umn.msi.tropix.proteomics.parameters.specification.ParameterSet;

import edu.umn.msi.tropix.common.io.fileset.FileSet;
import edu.umn.msi.tropix.common.jobqueue.test.JobRunner;
import edu.umn.msi.tropix.common.xml.XMLUtility;
import edu.umn.msi.tropix.proteomics.ProteomicsParameters;
import edu.umn.msi.tropix.proteomics.mascot.*;
import edu.umn.msi.tropix.proteomics.utils.*;

import org.apache.commons.codec.binary.*;


public class MascotJobTest {
	@Parameters({ "fakeMascotPath", "mascotTempDirectoryPath", "instrumentsFilePath", "enzymeFilePath", "unimodPath",  "validMzXMLPath", "mascotParamPath", "paramSpecificationPath"  })	
	@Test(groups ={"mascot"})
	public void testFakeMascotJob(String fakeMascotPath, String tmpDirectoryPath, String instrumentsFilePath, String enzymesFilePath, String unimodPath, String mascotMzXMLPath, String mascotParamPath, String specPath) throws Exception {
		File mascotApp = new File(fakeMascotPath);
		File tmpDirectory = new File(tmpDirectoryPath);
		
		MascotParamTranslator translator = new MascotParamTranslator(instrumentsFilePath, enzymesFilePath, unimodPath);
		XMLUtility<MzXML> mzxmlWrapper = new XMLUtility<MzXML>("net.sourceforge.sashimi.mzxml.v3_0");
		MzXML mzxml = mzxmlWrapper.deserialize(mascotMzXMLPath);
		
		XMLUtility<Bioml> biomlWrapper = new XMLUtility<Bioml>("edu.umn.msi.tropix.proteomics.bioml");
		XMLUtility<ParameterSet> specificationWrapper = new XMLUtility<ParameterSet>("edu.umn.msi.tropix.proteomics.parameters.specification");
		
		Bioml input = biomlWrapper.deserialize(mascotParamPath);
		ParameterSet paramSet = specificationWrapper.deserialize(specPath);
		ProteomicsParameters parameters = new ProteomicsParameters(input, paramSet);
		
		assert false;
		/*
		MascotJob executable = new MascotJob(mascotApp, new TempDirectoryCreatorImpl(tmpDirectory), translator);
		executable.setInput(mzxml);
		executable.setProteomicsParameters(parameters);
		
		JobRunner.execute(executable);
		String output = new String(Base64.decodeBase64(executable.getResults().getFiles().get(0).getFileContents()));
		assert output.startsWith("Hello World!") : "Fake Mascot didn't write Hello World! to output file.";
		*/
	}
	
	

	@Parameters({ "mascotApplicationPath", "mascotTempDirectoryPath", "instrumentsFilePath", "enzymeFilePath", "unimodPath",  "validMzXMLPath", "mascotParamPath", "paramSpecificationPath"  })
	@Test(groups ={"mascot"})
	public void testMascotJob(String mascotPath, String tmpDirectoryPath, String instrumentsFilePath, String enzymesFilePath, String unimodPath, String mascotMzXMLPath, String mascotParamPath, String specPath) throws Exception {
		File mascotApp = new File(mascotPath);
		File tmpDirectory = new File(tmpDirectoryPath);
		
		MascotParamTranslator translator = new MascotParamTranslator(instrumentsFilePath, enzymesFilePath, unimodPath);
		XMLUtility<MzXML> mzxmlWrapper = new XMLUtility<MzXML>("net.sourceforge.sashimi.mzxml.v3_0");
		MzXML mzxml = mzxmlWrapper.deserialize(mascotMzXMLPath);
		
		XMLUtility<Bioml> biomlWrapper = new XMLUtility<Bioml>("edu.umn.msi.tropix.proteomics.bioml");
		XMLUtility<ParameterSet> specificationWrapper = new XMLUtility<ParameterSet>("edu.umn.msi.tropix.proteomics.parameters.specification");
		
		Bioml input = biomlWrapper.deserialize(mascotParamPath);
		ParameterSet paramSet = specificationWrapper.deserialize(specPath);
		ProteomicsParameters parameters = new ProteomicsParameters(input, paramSet);
		
		assert false;
		/*
		MascotJob executable = new MascotJob(mascotApp, new TempDirectoryCreatorImpl(tmpDirectory), translator);
		executable.setInput(mzxml);
		executable.setProteomicsParameters(parameters);
		
		JobRunner.execute(executable);
		*/	
	}
	
}
