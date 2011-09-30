package edu.umn.msi.tropix.proteomics.sequest;

import edu.umn.msi.tropix.common.xml.XMLException;
import edu.umn.msi.tropix.common.xml.XMLUtility;
import edu.umn.msi.tropix.models.TropixFile;
import edu.umn.msi.tropix.proteomics.enzyme.*;
import edu.umn.msi.tropix.proteomics.*;
import edu.umn.msi.tropix.proteomics.utils.*;

import java.util.*;
import java.io.*;

import org.apache.log4j.Logger;

// TODO: Test units, etc.
// TODO: Implement Enzyme limits (First number after enzyme name.)
public class SequestGenericParameterTranslator implements SequestParameterTranslator {
	private Instruments instruments;
	private EnzymeSet enzymes;
	private Unimod unimod;
	private static Logger logger = Logger.getLogger(SequestGenericParameterTranslator.class);
	private static final double CLUSTER_DELTA = .0001;
	
	private final static String[] PARAMETER_ORDER = {
		"first_database_name",
		"second_database_name",
		"peptide_mass_tolerance",
		"peptide_mass_units",
		"ion_series",
		"fragment_ion_tolerance",
		"num_output_lines",
		"num_results",
		"num_description_lines",
		"show_fragment_ions",
		"print_duplicate_references",
		"enzyme_info",
		"max_num_differential_per_peptide",
		"diff_search_options",
		"term_diff_search_options",
		"nucleotide_reading_frame",
		"mass_type_parent",
		"mass_type_fragment",
		"normalize_xcorr",
		"remove_precursor_peak",
		"ion_cutoff_percentage",
		"max_num_internal_cleavage_sites",
		"protein_mass_filter",
		"match_peak_count",
		"match_peak_allowed_error",
		"match_peak_tolerance",
		"partial_sequence",
		"sequence_header_filter",
		"digest_mass_range",
		"add_Cterm_peptide",
		"add_Cterm_protein",
		"add_Nterm_peptide",
		"add_Nterm_protein",
		"add_G_Glycine",
		"add_A_Alanine",
		"add_S_Serine",
		"add_P_Proline",
		"add_V_Valine",
		"add_T_Threonine",
		"add_C_Cysteine",
		"add_L_Leucine",
		"add_I_Isoleucine",
		"add_X_LorI",
		"add_N_Asparagine",
		"add_O_Ornithine",
		"add_B_avg_NandD",
		"add_D_Aspartic_Acid",
		"add_Q_Glutamine",
		"add_K_Lysine",
		"add_Z_avg_QandE",
		"add_E_Glutamic_Acid",
		"add_M_Methionine",
		"add_H_Histidine",
		"add_F_Phenylalanine",
		"add_R_Arginine",
		"add_Y_Tyrosine",
		"add_W_Tryptophan",
		"add_J_user_amino_acid",
		"add_U_user_amino_acid"
	};
	
	private static final String[] FIXED_RESIDUE_MODIFICATION_PARAM_NAMES = { 
		"add_G_Glycine" ,
		"add_A_Alanine" ,
		"add_S_Serine" ,
		"add_P_Proline" ,
		"add_V_Valine" ,
		"add_T_Threonine" ,
		"add_C_Cysteine" ,
		"add_L_Leucine" ,
		"add_I_Isoleucine" ,
		"add_X_LorI" ,
		"add_N_Asparagine" ,
		"add_O_Ornithine" ,
		"add_B_avg_NandD" ,
		"add_D_Aspartic_Acid" ,
		"add_Q_Glutamine" ,
		"add_K_Lysine" ,
		"add_Z_avg_QandE" ,
		"add_E_Glutamic_Acid" ,
		"add_M_Methionine" ,
		"add_H_Histidine" ,
		"add_F_Phenylalanine" ,
		"add_R_Arginine" ,
		"add_Y_Tyrosine" ,
		"add_W_Tryptophan"
	};

	/** 
	 *  Must manually initialize instruments, enzymes, and unimod with respective setter methods.
	 */
	public SequestGenericParameterTranslator() {
	}
	
	public SequestGenericParameterTranslator(String instrumentsFilePath, String enzymesFilePath, String unimodPath) throws IOException, XMLException  {
		instruments = new Instruments(instrumentsFilePath);
		XMLUtility<EnzymeSet> wrapper = new XMLUtility<EnzymeSet>(EnzymeSet.class);
		enzymes = (EnzymeSet) wrapper.deserialize(enzymesFilePath);
		unimod = new Unimod(unimodPath);
	}
	
	public void setUnimodPath(String unimodPath) {
		unimod = new Unimod(unimodPath);
	}
	
	public void setEnzymesPath(String enzymesPath) throws IOException, XMLException {
		XMLUtility<EnzymeSet> wrapper = new XMLUtility<EnzymeSet>(EnzymeSet.class);
		enzymes = (EnzymeSet) wrapper.deserialize(enzymesPath);		
	}
	
	public void setInstrumentsPath(String instrumentsPath) throws IOException {
		instruments = new Instruments(instrumentsPath);
	}
	
	public String getSequestParameters(Object inputParameters, String primaryDatabasePath) {
		if(inputParameters instanceof ProteomicsParameters) {
			return proteomicsInputToSequestInput((ProteomicsParameters) inputParameters);
		} else {
			throw new IllegalArgumentException("InputParameters not of type ProteomicsParameters");
		}
	}
	
	// A poor mans clustering algorithm... sort of
	// If map was  {K => 1.0000, L => 1.0000, M => 1.5, Q => 1.00000000045, N-term => 1.0000}
	// result will be { KLQ => 1.0000, M => 1.5, N-term => 1.0000 }
	// This helps keep under Sequest's 6 variable modification restriction
	private Map<String, Double> clusterResidues(Map<String,Double> modifications, double delta) {
		Map<String, Double> clusteredModifications = new HashMap<String, Double>();
		for(String residue : modifications.keySet()) {
			if(residue.length() != 1) {
				clusteredModifications.put(residue, modifications.get(residue));
				continue;
			}
			double modification = modifications.get(residue);
			boolean doAdd = true;
			for(String clusteredResidues : clusteredModifications.keySet()) {
				if(Math.abs(clusteredModifications.get(clusteredResidues) - modification) < .000001) {
					clusteredModifications.remove(clusteredResidues);
					clusteredModifications.put(residue + clusteredResidues, modification);
					doAdd = false;
					continue;
				}
			}
			if(doAdd) {
				clusteredModifications.put(residue, modification);
			}
		}

		return clusteredModifications;
	}

	private Map<String,String> getVariableModificationLines(String[] modificationIds, boolean monoMasses) {
		UnimodId[] unimodIds = new UnimodId[modificationIds.length];
		for(int i = 0; i < unimodIds.length; i++) {
			unimodIds[i] = new UnimodId(modificationIds[i]);
		}
		Map<String, Double> modifications = unimod.aggregateModifications(unimodIds, monoMasses);
 		Map<String, Double> clusteredModifications = clusterResidues(modifications, CLUSTER_DELTA);

		double deltaNTerm = clusteredModifications.containsKey("N-term") ? clusteredModifications.get("N-term") : 0.0D;
		double deltaCTerm = clusteredModifications.containsKey("C-term") ? clusteredModifications.get("C-term") : 0.0D;
		clusteredModifications.remove("N-term");
		clusteredModifications.remove("C-term");

		StringBuffer diffSearchString = new StringBuffer();
		int count = 0;
		for(String residues : clusteredModifications.keySet()) {
			diffSearchString.append(" " + clusteredModifications.get(residues) + " " + residues);
			count++;
			if(count == 6) { // Need exactly six value-residue pairs, must ignore the rest
				break;
			}
		}
		
		for(; count < 6; count++) {
			diffSearchString.append(" 0.0000 X");
		}
		
		Map<String, String> paramMap = new HashMap<String,String>();
		paramMap.put("term_diff_search_options", deltaCTerm + " " + deltaNTerm);
		paramMap.put("diff_search_options", diffSearchString.toString());
		return paramMap;
	}
	
	private Map<String, String> getFixedModificationLines(String[] modificationIds, boolean monoMasses) {
		UnimodId[] unimodIds = new UnimodId[modificationIds.length];
		for(int i = 0; i < unimodIds.length; i++) {
			unimodIds[i] = new UnimodId(modificationIds[i]);
		}
		Map<String, Double> modifications = unimod.aggregateModifications(unimodIds, monoMasses);
		
		Map<String, String> paramMap = new HashMap<String, String>();
		for(String name : FIXED_RESIDUE_MODIFICATION_PARAM_NAMES) {
			String site = "" + name.charAt("add_".length());
			if(modifications.containsKey(site)) {
				paramMap.put(name, modifications.get(name) + "");
			} else {
				paramMap.put(name, "0.0000");
			}
		}		
		String[] sequestTermParams = new String[]{"add_Cterm_peptide", 
												  "add_Nterm_peptide", 
												  "add_Cterm_protein", 
												  "add_Nterm_protein"};
		String[] unimodTermSites = new String[] { "C-term", "N-term", "Protein C-term", "Protein N-term"};
		for(int i = 0; i < 4; i++) {
			String site = unimodTermSites[i];
			String name = sequestTermParams[i];
			if(modifications.containsKey(site)) {
				paramMap.put(name, modifications.get(name) + "");
			} else {
				paramMap.put(name, "0.0000");
			}
		}		
		return paramMap;
	}
	
	private String getEnzymeInfo(String enzymeName) {
		Enzyme enzyme = null;
		for(Enzyme curEnzyme : enzymes.getEnzymes()) {
			if(curEnzyme.getName().equals(enzymeName)) {
				enzyme = curEnzyme;
				break;
			}
		}
		if(enzyme == null) {
			return null;
		}
		
		return getEnzymeInfo(enzyme);
	}
	
	private String getEnzymeInfo(Enzyme enzyme) {
		StringBuffer enzymeInfoBuffer = new StringBuffer();
		enzymeInfoBuffer.append(enzyme.getName() + " 1 "); // One should reflect enzyme limit
		enzymeInfoBuffer.append(enzyme.getTerm().equals(TermInfo.CTERM) ? "1" : "0");
		String cleave = enzyme.getDoCleave();
		String noCleave = enzyme.getDoNotCleave(); 
		enzymeInfoBuffer.append(" " + (cleave.equals("") ? "-" : cleave) + " " + (noCleave.equals("") ? "-" : noCleave));
		return enzymeInfoBuffer.toString();
	}
	
	private String getIonSeries(String instrumentName) {
		 //  nA nB nY ABCDVWXYZ (nA, nB, nY must be integers, rest must be floating point)
		String[] ionSeries = new String[]{"a-H2O if fragment includes STED", 
										 "b-H2O if fragment includes STED", 
										 "y-H2O if fragment includes STED",
										 "a series ions",
										 "b series ions",
										 "c series ions",
										 "d or d' series ions",
										 "v series ions",
										 "w or w' series ions",
										 "x series ions",
										 "y series ions",
										 "z series ions"
										};
		
		StringBuffer seriesBuffer = new StringBuffer();
		for(int i = 0; i < ionSeries.length; i++) {
			boolean useIons = instruments.ionUsed(instrumentName, ionSeries[i]);
			if(useIons) {
				seriesBuffer.append((i < 3) ? " 1" : " 1.0");
			} else {
				seriesBuffer.append((i < 3) ? " 0" : " 0.0");
			}
		}
		return seriesBuffer.toString();
	}
		
	private String convertUnits(String unitStr) {
		if(unitStr.trim().equals("amu")) {
			return "0";
		} else if(unitStr.trim().equals("mmu")) {
			return "1";
		} else if(unitStr.trim().equals("ppm")) {
			return "2";
		} else {
			logger.warn("Unknown units encountered " + unitStr);
			return "0";
		}
	}
		
	// TODO: Test units
	public String proteomicsInputToSequestInput(ProteomicsParameters input) {
		Map<String, String> sequestParams = new HashMap<String, String>();
		
		sequestParams.put("first_database_name", input.getVariableValue("general, database"));
		sequestParams.put("num_output_lines", input.getVariableValue("sequest, num_output_lines"));
		sequestParams.put("num_description_lines", input.getVariableValue("sequest, num_description_lines"));
		sequestParams.put("peptide_mass_tolerance", input.getVariableValue("general, peptide_mass_tolerance"));
		sequestParams.put("max_num_internal_cleavage_sites", input.getVariableValue("general, missed_cleavages"));
		
		sequestParams.put("num_results", input.getVariableValue("sequest, num_results"));
		sequestParams.put("show_fragment_ions", input.getVariableValue( "sequest, show_fragment_ions"));
		sequestParams.put("max_num_differential_per_peptide", input.getVariableValue("sequest, max_num_differential_per_peptide"));
		sequestParams.put("normalize_xcorr", input.getVariableValue("sequest, normalize_xcorr"));
		sequestParams.put("remove_precursor_peak", input.getVariableValue( "sequest, remove_precursor_peak"));
		sequestParams.put("ion_cutoff_percentage", input.getVariableValue( "sequest, ion_cutoff_percentage"));
		sequestParams.put("match_peak_count", input.getVariableValue("sequest, match_peak_count"));
		sequestParams.put("match_peak_allowed_error", input.getVariableValue("sequest, match_peak_allowed_error"));
		sequestParams.put("match_peak_tolerance", input.getVariableValue("sequest, match_peak_tolerance"));
		sequestParams.put("mass_type_parent", input.getVariableValue("sequest, mass_type_parent"));
		
		sequestParams.put("second_database_name", input.getVariableValue("sequest, second_database_name"));
		sequestParams.put("partial_sequence", input.getVariableValue("sequest, partial_sequence"));
		sequestParams.put("sequest_header", input.getVariableValue("sequest, sequest_header"));
		sequestParams.put("create_output_files", input.getVariableValue("sequest, create_output_files"));
		sequestParams.put("nucleotide_reading_frame", input.getVariableValue("sequest, nucleotide_reading_frame"));
		sequestParams.put("print_duplicate_references", input.getVariableValue("sequest, print_duplicate_references"));
		sequestParams.put("sequence_header_filter", input.getVariableValue("sequest, sequence_header_filter"));
		sequestParams.put("add_U_user_amino_acid", input.getVariableValue("sequest, add_U_user_amino_acid"));
		sequestParams.put("add_J_user_amino_acid", input.getVariableValue("sequest, add_J_user_amino_acid"));				
		
		String ionSeries = getIonSeries(input.getVariableValue("general", "instrument"));
		sequestParams.put("ion_series", ionSeries);

		boolean mono = Integer.parseInt(input.getVariableValue("general", "mass_type_fragment")) == 1 ? true : false;
		sequestParams.put("mass_type_fragment", mono ? "1" : "0");		
		sequestParams.put("peptide_mass_units", convertUnits(input.getVariableValue("general", "peptide_mass_units")));
		
		// "general, fragment_mass_tolerance", "fragment_ion_tolerance",
		String fragmentUnits = input.getVariableValue("general", "fragment_mass_units");
		double fragmentTolerence = Double.parseDouble(input.getVariableValue("general", "fragment_mass_tolerance"));
		if(fragmentUnits.equals("mmu")) {
			fragmentTolerence = fragmentTolerence / 1000.; // 1 mmu = .001 amu
		}
		
		sequestParams.put("fragment_ion_tolerance", ""+fragmentTolerence); // Always in amu, no way to change
		
		String enzymeName = input.getVariableValue("general", "enzyme"); 
		String enzymeInfo = getEnzymeInfo(enzymeName);
		sequestParams.put("enzyme_info", enzymeInfo);
		
		String rawFixedModifications = input.getVariableValue("general", "fixed_modifications");
		String rawVariableModifications = input.getVariableValue("general", "variable_modifications");
		
		String[] fixedModifications;    // = rawFixedModifications.split(", ");
		String[] variableModifications; // = rawVariableModifications.split(", ");

		if(rawFixedModifications != null && !rawFixedModifications.trim().equals("")) {
			fixedModifications = rawFixedModifications.split("\\s*,\\s*");
		} else {
			fixedModifications = new String[0];
		}
		if(rawVariableModifications != null && !rawVariableModifications.trim().equals("")) {
			variableModifications = rawVariableModifications.split("\\s*,\\s*");
		} else {
			variableModifications = new String[0];
		}
		
		Map<String,String> fixedModificationLines    = getFixedModificationLines(fixedModifications, mono);		
		Map<String,String> variableModificationLines = getVariableModificationLines(variableModifications, mono);
		
		for(String paramName : fixedModificationLines.keySet()) {
			sequestParams.put(paramName, fixedModificationLines.get(paramName));
		}
		
		for(String paramName : variableModificationLines.keySet()) {
			sequestParams.put(paramName, variableModificationLines.get(paramName));
		}
		
		double digest_mass_range_lower = Double.parseDouble(input.getVariableValue("sequest", "digest_mass_range_lower"));
		double digest_mass_range_upper = Double.parseDouble(input.getVariableValue("sequest", "digest_mass_range_upper"));
		sequestParams.put("digest_mass_range", digest_mass_range_lower + " " + digest_mass_range_upper);
		
		
		double protein_mass_filter_lower = Double.parseDouble(input.getVariableValue("sequest", "protein_mass_filter_lower"));
		double protein_mass_filter_upper = Double.parseDouble(input.getVariableValue("sequest", "protein_mass_filter_upper"));
		sequestParams.put("protein_mass_filter", protein_mass_filter_lower + " " + protein_mass_filter_upper);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append("[SEQUEST]\n");
		for(String paramName : PARAMETER_ORDER) {
			if(!sequestParams.containsKey(paramName)) { 
				logger.warn("Key not found in parameter map " + paramName);
			}
			buffer.append(paramName + " = " + sequestParams.get(paramName) + "\n");
		}
		
		return buffer.toString();
	}

	public TropixFile getStoredDatabase(Object inputParameters) {
		return null;
	}
}
