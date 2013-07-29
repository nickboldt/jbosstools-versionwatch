package org.jboss.tools.vwatch.model;

public class BundleType {

	public int type = NONE;
	
	public static final int NONE = 0x0000;
	public static final int JAR = 0x0001;
	public static final int DIR = 0x0002;
	public static final int BOTH = 0x0003;
	
	public boolean isJar() {
		return (type & JAR) == JAR;
	}

	public boolean isDir() {
		return (type & DIR) == DIR;
	}

	public boolean isBoth() {
		return (type & BOTH) == BOTH;
	}
	
	public BundleType(int type) {
		this.type = type;
	}
	
}
