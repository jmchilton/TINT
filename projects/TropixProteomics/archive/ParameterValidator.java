package edu.umn.msi.tropix.proteomics.parameters;

//import edu.umn.msi.tropix.proteomics.parameters.AbstractParameterValidator.ValidationInfo;
import edu.umn.msi.tropix.proteomics.parameters.specification.DomainElement;
import edu.umn.msi.tropix.proteomics.parameters.specification.Parameter;
import edu.umn.msi.tropix.proteomics.parameters.specification.ParameterSet;
import edu.umn.msi.tropix.proteomics.parameters.specification.ParameterType;
import edu.umn.msi.tropix.proteomics.parameters.specification.Validation;
import edu.umn.msi.tropix.proteomics.parameters.specification.*;

import java.util.List;

public class ParameterValidator {
	ParameterSet set;
	
	public ParameterValidator(ParameterSet set) {
		this.set = set;
	}	
	
	public ValidationInfo getValidationInfo(Parameter parameter, Validation validation) {
		ValidationInfo info = new ValidationInfo();
		String min = validation.getMinValue();
		info.minValString = min;
		
		String max = validation.getMaxValue();
		info.maxValString = max;
		
		info.isList = validation.isIsList();
		info.regex = validation.getRegex();
		info.type = validation.getType().toString();
		
		if(validation.isValidateAgainistDomain() && 
				parameter.getDomain() != null && 
				parameter.getDomain().getDomainElement() != null)  {
			
			List<DomainElement> domainElements = parameter.getDomain().getDomainElement();
			String domainStrings[] = new String[domainElements.size()];
			for(int i = 0; i < domainStrings.length; i++) {
				domainStrings[i] = domainElements.get(i).getValue();
			}
			info.domain = domainStrings;
		} else {
			info.domain = null;
		}
		
		return info;
	}
	
	ValidationInfo getValidationInfo(String parameterName) {
		List<Parameter> parameters = set.getParameter();
		for(Parameter parameter : parameters) {
			if(parameter.getName().equals(parameterName)) {
				if(parameter.getType().equals(ParameterType.VARIABLE)) {
					return getValidationInfo(parameter, parameter.getValidation());
				} else {
					return null;
				}
			}
		}
		return null;
	}
}
