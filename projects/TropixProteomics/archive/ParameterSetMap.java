package edu.umn.msi.tropix.proteomics.parameters.specification;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;

import edu.umn.msi.tropix.common.xml.XMLException;
import edu.umn.msi.tropix.common.xml.XMLUtility;
import edu.umn.msi.tropix.proteomics.parameters.specification.NameSet;
import edu.umn.msi.tropix.proteomics.parameters.specification.Parameter;
import edu.umn.msi.tropix.proteomics.parameters.specification.ParameterSet;

public class ParameterSetMap extends HashMap<String, Parameter> {
	ParameterSet parameterSet;
	XMLUtility<ParameterSet> parameterSetUtility = new XMLUtility<ParameterSet>(ParameterSet.class);
	
	public ParameterSetMap() {}
	
	public ParameterSetMap(ParameterSet parameterSet) {
		setParameterSet(parameterSet);
	}
		
	public ParameterSetMap(Reader parameterSetReader) throws XMLException {
		ParameterSet parameterSet = parameterSetUtility.deserialize(parameterSetReader);
		setParameterSet(parameterSet);
	}
	
	public ParameterSetMap(String parameterSetPath) throws IOException, XMLException {
		this(new File(parameterSetPath));
	}
	
	public ParameterSetMap(File parameterSetFile) throws IOException, XMLException {
		this(new FileReader(parameterSetFile));
	}

	public ParameterSet getParameterSet() {
		return parameterSet;
	}

	public void setParameterSet(ParameterSet parameterSet) {
		this.parameterSet = parameterSet;
		initMap();
	}
	
	private void initMap() {
		for(Parameter parameter : parameterSet.getParameter()) {
			if(parameter.getName() != null) {
				super.put(parameter.getName(), parameter);
			}
			if(parameter.getNameSet() != null) {
				NameSet nameSet = parameter.getNameSet();
				if(nameSet.getName() != null) {
					for(String name : nameSet.getName()) {
						super.put(name, parameter);
					}
				}
			}
		}
	}
	
}
