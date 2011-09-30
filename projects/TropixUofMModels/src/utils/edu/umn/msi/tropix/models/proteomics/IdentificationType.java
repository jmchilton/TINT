package edu.umn.msi.tropix.models.proteomics;

import edu.umn.msi.tropix.models.IdentificationParameters;

public enum IdentificationType {
  SEQUEST("SequestBean", "Sequest"),
  XTANDEM("XTandemBean", "X! Tandem"),
  OMSSA("OmssaXml", "OMSSA"),
  MYRIMATCH("MyriMatch", "MyriMatch"),
  TAGRECON("TagRecon", "TagRecon"),
  MASCOT("Mascot", "Mascot"),
  INSPECT("Insepct", "InsPecT");

  private String parameterType;
  private String display;

  private IdentificationType(final String parameterType, final String display) {
    this.parameterType = parameterType;
    this.display = display;
  }

  public String getParameterType() {
    return parameterType;
  }

  public String getDisplay() {
    return display;
  }

  public static IdentificationType fromParameterType(final String typeString) {
    IdentificationType type = null;
    for(IdentificationType queryType : values()) {
      if(queryType.parameterType.equals(typeString)) {
        type = queryType;
        break;
      }
    }
    return type;
  }

  public static IdentificationType forParameters(final IdentificationParameters identificationParameters) {
    return fromParameterType(identificationParameters.getType());
  }

}
