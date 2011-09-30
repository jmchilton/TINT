package edu.umn.msi.tropix.proteomics.mascot;

import java.io.IOException;
import java.io.StringWriter;
import java.io.StringReader;

import org.apache.log4j.Logger;

import edu.umn.msi.tropix.common.xml.XMLException;
import edu.umn.msi.tropix.common.xml.XMLUtility;
import edu.umn.msi.tropix.proteomics.enzyme.EnzymeSet;
import edu.umn.msi.tropix.proteomics.BiomlWriter;
import edu.umn.msi.tropix.proteomics.Instruments;
import edu.umn.msi.tropix.proteomics.ProteomicsParameters;
import edu.umn.msi.tropix.proteomics.Unimod;
import edu.umn.msi.tropix.proteomics.UnimodId;
import net.sourceforge.sashimi.mzxml.v3_0.*;
import org.unimod.*;


// TODO: Test units, test num results
// TODO: Implement DB, implement fixed and variable modifications
public class MascotParamTranslator {
	private static final String FORMVER_STRING = "1.01";
	private static final String FORMAT_STRING = "Mascot generic";
	private Instruments instruments;
	private EnzymeSet enzymes; 
	private Unimod unimod;
	private static Logger logger = Logger.getLogger(MascotParamTranslator.class);
		
	public MascotParamTranslator(String instrumentsFilePath, String enzymesFilePath, String unimodPath) throws IOException, XMLException  {
		instruments = new Instruments(instrumentsFilePath);
		XMLUtility wrapper = new XMLUtility("edu.umn.msi.tropix.proteomics.enzyme");
		enzymes = (EnzymeSet) wrapper.deserialize(enzymesFilePath);
		unimod = new Unimod(unimodPath);
	}
	
	private  String convertUnits(String inputUnits) throws IllegalArgumentException {
		if(inputUnits.equals("amu")) {
			return "Da";
		} else if(inputUnits.equals("mmu")) {
			return "mmu";
		} else if(inputUnits.equals("ppm")) {
			return "ppm";
		} else {
			throw new IllegalArgumentException("Invalid units found. " + inputUnits);
		}
	}

	private String getDB(ProteomicsParameters input) {
		return "SwissProt";
	}
	
	private Object getNumResults(ProteomicsParameters input) {
		// Must be one of AUTO, 10, 20, 30, 40, 50
		int[] possibilities = new int[] { 10, 20, 30, 40, 50 };
		int numResults = Integer.parseInt(input.getVariableValue("general", "num_hits"));
		int closest = possibilities[0];
		for(int possibility : possibilities) {
			if(Math.abs(closest - numResults) > Math.abs(possibility - numResults)) {
				closest = possibility;
			}
		}
		return new Integer(closest);
	}
	
	private String getMass(ProteomicsParameters input) {
		int type = Integer.parseInt(input.getVariableValue("general", "mass_type_fragment"));
		if(type == 1) {
			return "Monoisotopic";
		} else {
			return "Average";
		}
	}
	
	public void writeMods(MascotMIMEWriter writer, String rawModifications, String type) throws IllegalArgumentException, IOException {
	 	//
		//String rawModifications = input.getVariableValue("general", "fixed_modifications");
		if(rawModifications == null || rawModifications.trim().equals("")) {
			return;
		}
		
		String[] modificationIdStrings = rawModifications.trim().split("\\s*,\\s*");
		UnimodId[] unimodIds = new UnimodId[modificationIdStrings.length];
		UnimodId unimodId;
		for(int i = 0; i < unimodIds.length; i++) {
			unimodId = new UnimodId(modificationIdStrings[i]);
			ModT mod = unimod.getModification(unimodId);
			SpecificityT spec = unimod.getSpecificity(mod, unimodId);
			String mascotInput = mod.getTitle() + " (" + spec.getSite() + ")";
			writer.write(type, mascotInput);
		}
	}
	
	public void writeFixedMods(MascotMIMEWriter writer, ProteomicsParameters input) throws IllegalArgumentException, IOException {
		String rawModifications = input.getVariableValue("general", "fixed_modifications");
		writeMods(writer, rawModifications, "MODS"); 
	}

	public void writeVariableMods(MascotMIMEWriter writer, ProteomicsParameters input) throws IllegalArgumentException, IOException {
		String rawModifications = input.getVariableValue("general", "variable_modifications");
		writeMods(writer, rawModifications, "IT_MODS");
	}

	public String proteomicsInputToMascotInput(ProteomicsParameters input, MzXML mzxml) throws IllegalArgumentException {
		StringWriter stringWriter = new StringWriter();
		MascotMIMEWriter writer = null;
		try {			
			writer = new MascotMIMEWriter(stringWriter, MascotMIMEWriter.DEFAULT_BOUNDARY);
			
			// Fixed Parameters 
			writer.write("COM", input.getVariableValue("mascot, com"));
			writer.write("USERNAME", input.getVariableValue("mascot, username"));
			writer.write("USEREMAIL", input.getVariableValue("mascot, user_email"));

			writer.write("INTERMEDIATE", input.getVariableValue("mascot, intermediate"));
			// Search types 'PMF', 'SQ', 'MIS'
			writer.write("SEARCH", input.getVariableValue("mascot, search"));
			writer.write("FORMVER", input.getVariableValue("mascot, form_version"));
			writer.write("FORMAT", input.getVariableValue("mascot, format"));
			 
			//???, not documented in list of parameters, just appears in sample.
			//writer.write("OVERVIEW", "OFF");

			// Skipped Paramters
			// webpage does touch REPTYPE, ACCESSION, RTINSECONDS, SCANS, SEG
			
			writer.write("TAXONOMY", input.getVariableValue("mascot, taxonomy"));
			writer.write("QUANTITATION", input.getVariableValue("mascot, quantitation"));

			// Ignored Parameters
			// Doesn't matter, the file will specify this so value will be ignored
			writer.write("CHARGE", input.getVariableValue("mascot, charge"));
			// I think this may be PEPMASS in MGF file.
			writer.write("PRECURSOR", input.getVariableValue("mascot, precursor"));
			
			writer.write("DB", getDB(input));
			writer.write("REPORT", input.getVariableValue("mascot", "report"));
			writer.write("CLE", input.getVariableValue("general", "enzyme")); 
			writer.write("MASS", getMass(input));
			
			writer.write("INSTRUMENT", input.getVariableValue("general", "instrument"));
			writer.write("ERRORTOLERANT", input.getVariableValue("mascot","error_tolerant"));
			writer.write("DECOY", input.getVariableValue("mascot", "decoy"));
			writer.write("PEP_ISOTOPE_ERROR", input.getVariableValue("mascot", "isotope_error"));
						
			writeFixedMods(writer, input);
			writeVariableMods(writer, input);
			
			//writer.write("SEG", input.getVariableValue("mascot", "seg"));

			// 0 to 9
			writer.write("PFA", input.getVariableValue("general", "missed_cleavages"));
			
			writer.write("TOL", input.getVariableValue("general", "peptide_mass_tolerance"));
			writer.write("TOLU", convertUnits(input.getVariableValue("general", "peptide_mass_units")));
			
			writer.write("ITOL", input.getVariableValue("general", "fragment_mass_tolerance"));
			writer.write("ITOLU", convertUnits(input.getVariableValue("general", "fragment_mass_units"))); 
			
			try {
			  // TODO:
				String mgfContents = null; //new String(MzXMLConversionUtils.mzXMLToMGF(mzxml));
				writer.writeFile("input.mgf", new StringReader(mgfContents));
			} catch(Exception e) {
				throw new IllegalArgumentException("Problems encountered with mzXML input.",e);
			}
	
			writer.close();
		} catch(IOException e) {
			// Won't get IOException on StringWriter
			assert false;
		}	
		return stringWriter.getBuffer().toString();
	}
}
