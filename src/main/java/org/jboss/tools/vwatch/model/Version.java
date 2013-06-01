package org.jboss.tools.vwatch.model;


/**
 * Basic version class 
 * @author jpeterka
 *
 */
public class Version {

	int major;
	int minor;
	int build;
	
	public int getMajor() {
		return major;
	}
	public void setMajor(int major) {
		this.major = major;
	}
	public int getMinor() {
		return minor;
	}
	public void setMinor(int minor) {
		this.minor = minor;
	}
	public int getBuild() {
		return build;
	}
	public void setBuild(int build) {
		this.build = build;
	}
	public String toString() {
		return major + "." + minor + "." + build; 
	}
	public int toNumber() {
		
		return major*1000000 + minor*1000 + build;
	}
}
