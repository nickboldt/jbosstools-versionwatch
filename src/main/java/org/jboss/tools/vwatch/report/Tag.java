package org.jboss.tools.vwatch.report;

public class Tag {

	String str;
	
	public Tag(String str) {
		this.str = str; 
	}
	
	public Tag end() {
		str = "</" + str.substring(1, str.length());
		return new Tag(str);
	}
	
	@Override
	public String toString() {
		return str;
	}
	
}
