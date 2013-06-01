package org.jboss.tools.vwatch;

public class Settings {

	private static boolean md5checkEnabled = false;	// -Dvwatch.md5check
	

	public static boolean isMd5checkEnabled() {
		return md5checkEnabled;
	}

	public static void setMd5checkEnabled(boolean md5checkEnabled) {
		Settings.md5checkEnabled = md5checkEnabled;
	}
}