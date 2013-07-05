package org.jboss.tools.vwatch;

import org.apache.log4j.Level;

public class Settings {

	private static Level loglevel = Level.WARN;	// -Dloglevel=4

	public static void setLogLevel(Level level) {
		Settings.loglevel = level;
	}
	public static Level getLogLevel() {
		return Settings.loglevel;
	}
		
	private static boolean md5checkEnabled = false;	// -Dvwatch.md5check

	public static boolean isMd5checkEnabled() {
		return md5checkEnabled;
	}

	public static void setMd5checkEnabled(boolean md5checkEnabled) {
		Settings.md5checkEnabled = md5checkEnabled;
	}
}