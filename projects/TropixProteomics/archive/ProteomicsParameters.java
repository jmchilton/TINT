package edu.umn.msi.tropix.proteomics;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.List;

import edu.umn.msi.tropix.proteomics.bioml.*;
import edu.umn.msi.tropix.proteomics.parameters.specification.Parameter;
import edu.umn.msi.tropix.proteomics.parameters.specification.ParameterSet;
import edu.umn.msi.tropix.proteomics.parameters.specification.ParameterType;

public class ProteomicsParameters {
	Bioml inputParameters;
	ParameterSet parametersSpecification;
	Map<String, String> paramMap = null;

	public ProteomicsParameters(Bioml inputParameters, ParameterSet parametersSpecification) {
		super();
		this.inputParameters = inputParameters;
		this.parametersSpecification = parametersSpecification;
	}

	private HashMap<String, String> getMapFromBioml(Bioml bioml) {
		HashMap<String, String > varMap = new HashMap<String, String>();
		List<Note> notes = bioml.getNote();

		for(Note note : notes) {
			if(! note.getType().equals("input")) {
				continue;
			}
			String label = note.getLabel();
			String value = note.getValue();
			String[] labelParts = label.split(", ");
			if(labelParts.length != 2) {
				//TODO Log
				continue;
			}

			varMap.put(label, value);
		}
		return varMap;
	}
	
	
	private void init() {
		paramMap = new HashMap<String,String>();
		HashMap<String, String> biomlMap = getMapFromBioml(inputParameters);
		List<Parameter> parameters = parametersSpecification.getParameter();
		for(Parameter parameter : parameters) {
			String name = parameter.getName();
			if(parameter.getType().equals(ParameterType.VARIABLE) && biomlMap.containsKey(name)) {
				paramMap.put(name, biomlMap.get(name));
			} else {
				paramMap.put(name, parameter.getDefault());
			}
		}
	}
	
	
	public String getVariableValue(String varGroup, String varName) {
		if(!varName.equals("")) {
			return getVariableValue(varGroup + ", " + varName);
		} else {
			return getVariableValue(varGroup);
		}
	}

	public String getVariableValue(String varGroupAndName) {
		if(paramMap == null) init();
		return paramMap.get(varGroupAndName);
	}

	public Set<String> getVariables() {
		if(paramMap == null) init();
		return paramMap.keySet();
	}
}
