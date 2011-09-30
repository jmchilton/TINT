package edu.umn.msi.tropix.proteomics.tools;

import java.util.List;

import org.unimod.UnimodT;

import edu.umn.msi.tropix.common.xml.XMLUtility;
import edu.umn.msi.tropix.proteomics.enzyme.*;
import edu.umn.msi.tropix.proteomics.parameters.specification.Domain;
import edu.umn.msi.tropix.proteomics.parameters.specification.DomainElement;
import edu.umn.msi.tropix.proteomics.parameters.specification.Parameter;
import edu.umn.msi.tropix.proteomics.parameters.specification.ParameterSet;
import edu.umn.msi.tropix.proteomics.Unimod;
import edu.umn.msi.tropix.proteomics.UnimodDisplay;
import edu.umn.msi.tropix.proteomics.parameters.specification.*;

public class SpecificationUtils {

	public static void fillInEnzymes(EnzymeSet enzymeSet, ParameterSet parameterSet, String enzymeParameterName) {
		List<Enzyme> enzymes = enzymeSet.getEnzymes();
		for(Parameter parameter : parameterSet.getParameter()) {
			if(enzymeParameterName.equals(parameter.getName())) {
				Domain domain = new Domain();
				for(Enzyme enzyme : enzymes) {
					DomainElement element = new DomainElement();
					element.setDisplay(enzyme.getName());
					element.setValue(enzyme.getName());
					domain.getDomainElement().add(element);
				}
				parameter.setDomain(domain);
			}
		}
	}
	
	public static void fillInModifications(Unimod unimod, ParameterSet parameterSet, String modificationParameterName) {
		List<UnimodDisplay> displayList = unimod.getUnimodDisplayList();
		for(Parameter parameter : parameterSet.getParameter()) {
			if(modificationParameterName.equals(parameter.getName())) {
					Domain domain = new Domain();
					for(UnimodDisplay display : displayList) {
						DomainElement element = new DomainElement();
						element.setDisplay(display.getDisplayString());
						element.setValue(display.getUnimodId().toString());
						domain.getDomainElement().add(element);
					}
					parameter.setDomain(domain);
			}
		}
	}
	
	public static void main(String[] args) {
		XMLUtility<ParameterSet> parameterSpecificationUtils = new XMLUtility<ParameterSet>(ParameterSet.class);
		if(args.length != 2) {
			System.out.println("Invalid number of arguments, call this with a parameter specification file and a unimod file");
		}
		try {
			ParameterSet parameterSet = parameterSpecificationUtils.deserialize(args[0]);
			System.out.println(parameterSet.getParameter().size());
			Unimod unimod = new Unimod(args[1]);
			fillInModifications(unimod, parameterSet, "general, variable_modifications");
			fillInModifications(unimod, parameterSet, "general, fixed_modifications" );
			parameterSpecificationUtils.serialize(parameterSet, "testparameters");
		} catch(Exception e) {
			System.out.println("Failed with exception");
			e.printStackTrace();
		}
	}
	
}
