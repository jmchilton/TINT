package edu.umn.msi.tropix.proteomics.parameters;

class ValidationInfo {
	public String[] domain; // null if not validate against domain
	public String minValString;
	public String maxValString;
	public String regex;
	public String type;
	public boolean isList;
}

// Copied from the proteomics project (proteomics.jar). Should find a better
// way of sharing this code!


// Imports and dependencies kept to a minimum so GWT can make use of this class
abstract public class AbstractParameterValidator {
	public final static String TRUE_CONSTANT  = "1";
	public final static String FALSE_CONSTANT = "0";	
	
	abstract ValidationInfo getValidationInfo(String parameterName);
	
	private static boolean validateDomain(ValidationInfo vInfo, String value) {
		if(vInfo.domain != null) {
			for(int i = 0; i < vInfo.domain.length; i++) {
				if(vInfo.domain[i].equals(value)) {
					return true;
				}
			}
			return false;
		} else {
			return true;
		}
	}
	
	private static boolean validateType(ValidationInfo vInfo, String value) {
		if(vInfo.type == null) return true;
		try {
			if(vInfo.type.equals("float")) {
				Double.parseDouble(value);
			} else if(vInfo.type.equals("integer")) {
				Integer.parseInt(value);
			}
		} catch(NumberFormatException e) {
			return false;
		}
		
		if(vInfo.type.equals("boolean")) {
			if(!value.equals(FALSE_CONSTANT) && !value.equals(TRUE_CONSTANT)) {
				return false;
			}
		}
		return true;
	}
	
	private static boolean validateRange(ValidationInfo vInfo, String value) {
		if(vInfo.minValString == null && vInfo.maxValString == null) {
			return true;
		}
		
		if(vInfo.type.equals("integer")) {
			return validateIntegerRange(vInfo, value);
		} else if(vInfo.type.equals("float")) {
			return validateFloatingPointRange(vInfo, value);
		} else {
			return true;
		}
	}
	
	private static boolean validateIntegerRange(ValidationInfo vInfo, String value) {
		boolean tooSmall = false;
		boolean tooLarge = false;
		if(vInfo.minValString != null && !vInfo.minValString.trim().equals("")) {
			tooSmall = Integer.parseInt(value) < Integer.parseInt(vInfo.minValString);
		} 
		if(vInfo.maxValString != null && !vInfo.maxValString.trim().equals("")) {
			tooLarge = Integer.parseInt(value) > Integer.parseInt(vInfo.maxValString);
		}
		return !tooSmall && !tooLarge;
	}
	
	private static boolean validateFloatingPointRange(ValidationInfo vInfo, String value) {
		boolean tooSmall = false;
		boolean tooLarge = false;
		if(vInfo.minValString != null && !vInfo.minValString.trim().equals("")) {
			tooSmall = Double.parseDouble(value) < Double.parseDouble(vInfo.minValString);
		} 
		if(vInfo.maxValString != null && !vInfo.maxValString.trim().equals("")) {
			tooLarge = Double.parseDouble(value) > Double.parseDouble(vInfo.maxValString);
		}
		return !tooSmall && !tooLarge;
	}
	
	private static boolean validateRegex(ValidationInfo vInfo, String value) {
		if(vInfo.regex == null) {
			return true;
		} else {
			return value.matches(vInfo.regex);
		}
	}

	private boolean validateSingleValue(ValidationInfo vInfo, String value) {
		boolean validDomain = validateDomain(vInfo, value);
		boolean validType   = validateType(vInfo, value);
		boolean validRange  = validateRange(vInfo, value);
		boolean validRegex  = validateRegex(vInfo, value);
		
		return validDomain && validType && validRange && validRegex;
	}
	
	public boolean validate(String name, String value) {
		ValidationInfo vInfo = getValidationInfo(name);
		if(!vInfo.isList) {
			return validateSingleValue(vInfo, value);
		} else {
			String[] values = value.split("(\\s)+,(\\s)+");
			for(String curValue : values) {
				if(!validateSingleValue(vInfo, curValue)) {
					return false;
				}
			}
			return true;
		}
	}
}
