package edu.umn.msi.tropix.proteomics.xtandem;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.io.Writer;
import java.io.Reader;
import java.io.FileReader;
import java.io.StringReader;
import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;

import edu.umn.msi.tropix.common.xml.XMLException;
import edu.umn.msi.tropix.common.xml.XMLUtility;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.proteomics.bioml.*;
import edu.umn.msi.tropix.proteomics.BiomlWriter;
import edu.umn.msi.tropix.proteomics.EnzymeUtils;
import edu.umn.msi.tropix.proteomics.Instruments;
import edu.umn.msi.tropix.proteomics.ProteomicsParameters;
import edu.umn.msi.tropix.proteomics.Unimod;
import edu.umn.msi.tropix.proteomics.UnimodId;

public class XTandemGenericParameterTranslator implements XTandemParameterTranslator {
	private Instruments instruments;
	private EnzymeUtils enzymeUtils;
	private Unimod unimod;
	private String defaultsPath;
	private String taxonomyPath;
	private String xslPath;
	private static final String[] IONS = new String[] {"a","b","c","x", "y","z"};
	private static Logger logger = Logger.getLogger(XTandemGenericParameterTranslator.class);
	
	public XTandemGenericParameterTranslator(String instrumentsFilePath, String enzymesFilePath, String unimodPath, String taxonomyPath, String defaultsPath, String xslPath) throws IOException, XMLException  {
		instruments = new Instruments(instrumentsFilePath);
		//XMLUtility wrapper = new XMLUtility("edu.umn.msi.tropix.proteomics.enzyme");
		//enzymes = (EnzymeSet) wrapper.deserialize(enzymesFilePath);
		enzymeUtils = new EnzymeUtils(enzymesFilePath);
		unimod = new Unimod(unimodPath);
		this.taxonomyPath  = taxonomyPath;
		this.defaultsPath = defaultsPath;
		this.xslPath = xslPath;
	}
	
	// TODO: Investigate if this makes any sense...
	private String getCleavageSite(ProteomicsParameters input) {
		String enzymeName = input.getVariableValue("general", "enzyme");
		String cleaveResidues = enzymeUtils.getCleaveResidues(enzymeName);
		String notCleaveResidues = enzymeUtils.getNotCleaveResidues(enzymeName);
		if(cleaveResidues.equals("")) {
			cleaveResidues = "[X]";
		} else {
			cleaveResidues = '[' + cleaveResidues + ']';
		}
		if(notCleaveResidues.equals("")) {
			notCleaveResidues = "[X]";
		} else {
			notCleaveResidues = '{' + notCleaveResidues + '}';
		}
		
		if(enzymeUtils.getTerm(enzymeName).equals("c")) {
			return cleaveResidues + "|" + notCleaveResidues;
		} else {
			return notCleaveResidues + "|" + cleaveResidues;
		}
	}
	
	private String getTaxon(ProteomicsParameters params) {
		return "human"; // ?
	}

	private HashMap<String, String> getIonMap(ProteomicsParameters params) {
		String instrumentName = params.getVariableValue("general", "instrument");
		HashMap<String,String> ionMap = new HashMap<String,String>();
		for(String ion : IONS) {
			if(instruments.ionUsed(instrumentName, ion + " series ions")) {
				ionMap.put(ion, "yes");
			} else {
				ionMap.put(ion, "no");
			}
		}
		return ionMap;
	}
	
	private String convertBoolean(String zeroOrOne) {
		if(zeroOrOne.trim().equals("0")) {
			return "no";
		} else {
			return "yes";
		}
	}
	
	private String convertUnits(String units) {
		if(units.equals("amu")) {
			return "Da";
		} else if(units.equals("mmu")) {
			return "Da"; // Be sure to convert mmu to da
		} else if(units.equals("ppm")) {
			return "ppm";
		} else {
			logger.warn("Unknown units " + units + " encountered.");
			return "Da";
		}
	}
	
	private UnimodId[] getModificationIds(String rawModifications) throws IllegalArgumentException {
		String[] modifications;
		if(rawModifications != null && !rawModifications.trim().equals("")) {
			modifications = rawModifications.split("\\s*,\\s*");
		} else {
			modifications = new String[0];
		}
		UnimodId[] unimodIds = new UnimodId[modifications.length];
		for(int i = 0; i < unimodIds.length; i++) {
			unimodIds[i] = new UnimodId(modifications[i]);
		}
		return unimodIds;
	}
	
	private Map<String, Double> fetchModifications(String rawModificationsString, boolean monoMasses) throws IllegalArgumentException {
		UnimodId[] unimodIds = getModificationIds(rawModificationsString);
		return unimod.aggregateModifications(unimodIds, monoMasses);
	}

	private String getModificationString(Map<String, Double> modificationMap, boolean fixed) {
		StringBuffer modString = new StringBuffer();
		String residue;
		boolean first = true;
		for(String key : modificationMap.keySet()) {
			residue = null;
			if(key.length() == 1) { // Simple residue
				residue = key;
			} else if(!fixed) {
				if(key.equals("N-term")) {
					residue = "[";
				} else if(key.equals("C-term")) {
					residue = "]";
				}
				// Sequest and X! Tandem don't support variable Protein x-term PTMs
			}
			if(residue != null) {
				if(first) {
					first = false;
					modString.append(",");
				}
				modString.append(modificationMap.get(key) + "@" + residue);
			}
		}
		return modString.toString();
	}
	
	
	public String proteomicsInputToXTandemInput(ProteomicsParameters params, String inputFilePath, String outputFilePath) {
		BiomlWriter xInput = new BiomlWriter();
		
		String rawFixedModifications = params.getVariableValue("general", "fixed_modifications");
		String rawVariableModifications = params.getVariableValue("general", "variable_modifications");
		boolean mono = Integer.parseInt(params.getVariableValue("general", "mass_type_fragment")) == 1 ? true : false;
		Map<String, Double> fixedMap = fetchModifications(rawFixedModifications, mono);
		Map<String, Double> variableMap = fetchModifications(rawVariableModifications, mono);
		for(String site : new String[]{"Protein C-term", "Protein N-term", "C-term", "N-term"}) {
			if(!fixedMap.containsKey(site)) {
				fixedMap.put(site, 0.0);
			}
		}
		
		
		xInput.addHeader("Paths");
		xInput.addVariable("list path", "taxonomy information", taxonomyPath);
		xInput.addVariable("list path", "default parameters", defaultsPath);
		
		xInput.addHeader("Output");
		xInput.addVariable("output", "path", outputFilePath);
		xInput.addVariable("output", "xsl path", xslPath);
		
		xInput.addVariable("output", "histograms", params.getVariableValue("xtandem", "output_histograms"));
		xInput.addVariable("output", "histogram column width", params.getVariableValue("xtandem", "histogram_column_width"));
		xInput.addVariable("output", "maximum valid expectation value", params.getVariableValue("xtandem", "maximum_valid_expectation_value"));
		xInput.addVariable("output", "one sequence copy", params.getVariableValue("xtandem", "one_sequence_copy"));
		xInput.addVariable("output", "parameters", params.getVariableValue("xtandem", "output_parameters"));
		xInput.addVariable("output", "path hashing", params.getVariableValue("xtandem", "path_hashing"));
		xInput.addVariable("output", "proteins", params.getVariableValue("xtandem", "output_proteins"));
		xInput.addVariable("output", "results", params.getVariableValue("xtandem", "output_results"));
		xInput.addVariable("output", "sort results by", params.getVariableValue("xtandem", "sort_results_by"));
		xInput.addVariable("output", "sequences", params.getVariableValue("xtandem", "output_sequences"));
		xInput.addVariable("output", "spectra", params.getVariableValue("xtandem", "output_spectra")); 
		
		// Keep default for rest, should come back to this though...
		
		xInput.addHeader("Protein general");
		xInput.addVariable("protein", "cleavage C-terminal mass change", fixedMap.get("C-term"));
		xInput.addVariable("protein", "cleavage N-terminal mass change", fixedMap.get("N-term"));
		xInput.addVariable("protein", "C-terminal residue modification mass", fixedMap.get("Protein C-term"));
		xInput.addVariable("protein", "N-terminal residue modification mass", fixedMap.get("Protein N-term"));

		
		// Maybe an advanced option for this, maybe expand enzyme model to allow this,
		// but I don't think Sequest would support this...
		xInput.addVariable("protein", "cleavage semi", params.getVariableValue("xtandem", "cleavage_semi"));
		xInput.addVariable("protein", "cleavage site", getCleavageSite(params));
		xInput.addVariable("protein", "taxon", getTaxon(params)); // I guess base this on database.
		
		// Don't allow for refinement at this time
		xInput.addHeader("model refinement paramters");
		xInput.addVariable("refine", "", params.getVariableValue("xtandem", "refine"));
 
		xInput.addHeader("Residue modification");
		xInput.addVariable("residue", "modification mass", getModificationString(fixedMap, true));
		xInput.addVariable("residue", "potential modification mass", getModificationString(variableMap, false));
		
		xInput.addHeader("Scoring");
		HashMap<String, String> ionMap = getIonMap(params);  // Maps a,b,c,x,y,z to yes and no
		for(String ion : IONS) {
			xInput.addVariable("scoring", ion + " ions", ionMap.get(ion));
		}
		
		xInput.addVariable("scoring", "cyclic permutation", convertBoolean(params.getVariableValue("xtandem", "cyclic_permutation")));
		xInput.addVariable("scoring", "maximum missed cleavage sites", params.getVariableValue("general", "missed_cleavages"));
		xInput.addVariable("scoring", "minimum ion count", params.getVariableValue("xtandem", "minimum_ion_count"));
		
		xInput.addHeader("Spectrum general");
		//
		// Fragment mass error
		//
		
		// 1 is monoisotopic, 0 average
		int type = Integer.parseInt(params.getVariableValue("general", "mass_type_fragment"));
		xInput.addVariable("spectrum", "fragment mass type", type == 1 ? "monoisotopic" : "average");
		
		double fragmentTolerance = Double.parseDouble(params.getVariableValue("general","fragment_mass_tolerance"));
		String units = params.getVariableValue("general","fragment_mass_units");
		if(units.equals("mmu")) {
			fragmentTolerance = fragmentTolerance / 1000.0;
		}
		if(type == 1) {
			xInput.addVariable("spectrum", "fragment monoisotopic mass error", fragmentTolerance);
			xInput.addVariable("spectrum", "fragment monoisotopic mass error units", "Daltons");
		} else {
			xInput.addVariable("spectrum", "fragment mass error units", "Daltons");
			xInput.addVariable("spectrum", "fragment mass error", fragmentTolerance);
		}


		//
		// Peptide mass error
		//
		double peptideTolerance = Double.parseDouble(params.getVariableValue("general","peptide_mass_tolerance"));
		units = params.getVariableValue("general","peptide_mass_units");
		if(units.equals("mmu")) {
			peptideTolerance = peptideTolerance / 1000.0;
		}
		xInput.addVariable("spectrum", "parent monoisotopic mass error units", convertUnits(units));
		xInput.addVariable("spectrum", "parent monoisotopic mass error plus", peptideTolerance); // Should this be divided by 2.
		xInput.addVariable("spectrum", "parent monoisotopic mass error minus", peptideTolerance);		

		// XTandem specific spectrum properties
		xInput.addVariable("spectrum", "use contrast angle", convertBoolean(params.getVariableValue("xtandem", "use_contrast_angle")));
		xInput.addVariable("spectrum", "contrast angle", params.getVariableValue("xtandem", "contrast_angle"));
		xInput.addVariable("spectrum", "dynamic range", params.getVariableValue("xtandem", "dynamic_range"));
		
		xInput.addVariable("spectrum", "path", inputFilePath);
		//xInput.addVariable("spectrum", "path type", "mzxml");
		xInput.addVariable("spectrum", "path type", params.getVariableValue("xtandem", "input_type"));
		xInput.addVariable("spectrum", "minimum fragment mz", params.getVariableValue("xtandem", "minimum_fragment_mz"));
		xInput.addVariable("spectrum", "minimum peaks", params.getVariableValue("xtandem", "minimum_peaks"));
		xInput.addVariable("spectrum", "minimum parent m+h", params.getVariableValue("xtandem", "minimum_parent_mh"));
		xInput.addVariable("spectrum", "use noise suppression", convertBoolean(params.getVariableValue("xtandem", "use_noise_suppression")));	

		xInput.addVariable("spectrum", "neutral loss mass", params.getVariableValue("xtandem", "neutral_loss_mass"));
		xInput.addVariable("spectrum", "neutral loss window", params.getVariableValue("xtandem", "neutral_loss_window"));
		xInput.addVariable("spectrum", "use neutral loss window", convertBoolean(params.getVariableValue("xtandem", "use_neutral_loss_window")));	
		xInput.addVariable("spectrum", "parent monoisotopic mass isotope error", convertBoolean(params.getVariableValue("xtandem", "parent_monoisotopic_mass_isotope_error")));
		xInput.addVariable("spectrum", "total peaks", params.getVariableValue("xtandem", "total_peaks"));

		
		return xInput.toString();
		//XMLUtility<Bioml> jWrapper = new XMLUtility<Bioml>("edu.umn.msi.tropix.proteomics.bioml");
		//return jWrapper.serialize(bioml);
	}


	public Collection<TropixFile> getStoredDatabases(Object inputParameters) {
		return null;
	}

	public String getXTandemParameters(Object inputParameters, String outputPath,
			String inputPath, String taxon, String taxonomyPath) {
		return null;
	}
}
	