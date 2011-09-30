package edu.umn.msi.tropix.proteomics;

public class UnimodDisplay {
	private String displayString;
	private UnimodId unimodId;
	public String getDisplayString() {
		return displayString;
	}
	public void setDisplayString(String displayString) {
		this.displayString = displayString;
	}
	public UnimodId getUnimodId() {
		return unimodId;
	}
	public void setUnimodId(UnimodId unimodId) {
		this.unimodId = unimodId;
	}
	public UnimodDisplay(String displayString, UnimodId unimodId) {
		this.displayString = displayString;
		this.unimodId = unimodId;
	}
}