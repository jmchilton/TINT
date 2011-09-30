package edu.umn.msi.tropix.proteomics;

import java.util.regex.*;


public class UnimodId {
	private long modId;
	private long specificityId;
	
	public UnimodId(String idString) throws IllegalArgumentException {
		Pattern p = Pattern.compile("\\s*([0-9]*)\\s*;\\s*([0-9]*)\\s*");
		Matcher m = p.matcher(idString);
		if(!m.matches() || m.groupCount() != 2) {
			throw new IllegalArgumentException("Invalid form for unimod id - " + idString);
		}
		modId = Long.parseLong(m.group(1));
		specificityId = Long.parseLong(m.group(2));
	}

	public long getModId() {
		return modId;
	}

	public long getSpecificityId() {
		return specificityId;
	}
	
	public void setModId(long modId) {
		this.modId = modId;
	}

	public void setSpecificityId(long specificityId) {
		this.specificityId = specificityId;
	}
	
	public void setModId(String modId) {
		this.modId = Long.parseLong(modId);
	}

	public void setSpecificityId(String specificityId) {
		this.specificityId = Long.parseLong(specificityId);
	}
	
	public UnimodId(long modId, long specificityId) {
		super();
		this.modId = modId;
		this.specificityId = specificityId;
	}
	
	public String toString() {
		return modId + ";" + specificityId;
	}
	
	public static UnimodId fromString(String idString) throws IllegalArgumentException{
		return new UnimodId(idString);
	}
	
}
