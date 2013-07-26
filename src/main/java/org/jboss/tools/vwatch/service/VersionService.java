package org.jboss.tools.vwatch.service;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jboss.tools.vwatch.Settings;
import org.jboss.tools.vwatch.model.Installation;
import org.jboss.tools.vwatch.model.Version;

/**
 * Bundle version service contating usefull methods for version evaluation
 * @author jpeterka
 *
 */
public class VersionService {

	Logger log = Logger.getLogger(VersionService.class);
	Version version = new Version();

	/**
	 * Check if jbds version and format is valid
	 * 
	 * @param text
	 * @return
	 */
	public boolean isValid(String text) {

		boolean isValid = false;

		log.setLevel(Settings.getLogLevel());

		// check format
		if (text.matches("jbds-\\d+\\.\\d+\\.\\d+.*")) {

			// Find version
			Pattern regex = Pattern.compile("\\d+\\.\\d+\\.\\d+");
			Matcher regexMatcher = regex.matcher(text);
			regexMatcher.find();
			String group = regexMatcher.group();

			String[] split = group.split("\\.");
			try {
				version.setMajor(Integer.parseInt(split[0]));
				version.setMinor(Integer.parseInt(split[1]));
				version.setBuild(Integer.parseInt(split[2]));
			} catch (NumberFormatException e) {
				log.error("Cannot convert versions to numbers" + e.getMessage());
				return isValid;
			}

			String[] split2 = text.split("-");
			String prefix = split2[0];

			String postfix = "";
			if (split2.length > 2) {
				postfix = split2[2];
			}

			// seems valid
			isValid = true;
			log.info("Version is valid " + prefix + version.toString()
					+ postfix);

		} else {
			String message = "Incorrect version format";
			log.error(message);
		}

		return isValid;
	}

	/**
	 * Return lesser version
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public Installation getMinInstallation(Installation i1, Installation i2) {

		Version v1 = i1.getVersion();
		Version v2 = i2.getVersion();

		if (v1.getMajor() < v2.getMajor())
			return i1;
		if (v1.getMajor() > v2.getMajor())
			return i2;
		if (v1.getMinor() < v2.getMinor())
			return i1;
		if (v1.getMinor() > v2.getMinor())
			return i2;
		if (v1.getBuild() < v2.getBuild())
			return i1;
		if (v1.getBuild() > v2.getBuild())
			return i2;

		log.warn("Versions identical, keeping first one");

		return i1;

	}

	/**
	 * Parse Version from installation forlder name
	 * @param i given installations
	 * @return returns version instance
	 */
	public Version parseVersionFromInstallationFolder(Installation i) {

		File f = new File(i.getRootFolderAbsolutePath());
		Version version = new Version();

		// Find version
		Pattern regex = Pattern.compile("\\d+\\.\\d+\\.\\d+");
		Matcher regexMatcher = regex.matcher(f.getName());
		regexMatcher.find();
		String group = regexMatcher.group();

		String[] split = group.split("\\.");
		try {
			version.setMajor(Integer.parseInt(split[0]));
			version.setMinor(Integer.parseInt(split[1]));
			version.setBuild(Integer.parseInt(split[2]));
		} catch (NumberFormatException e) {
			log.setLevel(Settings.getLogLevel());
			log.error("Cannot convert versions to numbers" + e.getMessage());
		}

		return version;

	}

	/**
	 * Returns true when versions diff si in major version number
	 * 
	 * @return
	 */
	public boolean isMajorDiff(Version v1, Version v2) {
		return v1.getMajor() != v2.getMajor();
	}

	/**
	 * Returns true when versions diff si in minor version number
	 * 
	 * @return
	 */
	public boolean isMinorDiff(Version v1, Version v2) {
		return v1.getMinor() != v2.getMinor();
	}
	
	/**
	 * Returns true when versions diff si in minor version number
	 * 
	 * @return
	 */
	public boolean isBuildDiff(Version v1, Version v2) {
		return v1.getBuild() != v2.getBuild();
	}
	
	/**
	 * Validates wheather v2 major version is greater
	 * @param v1
	 * @param v2
	 * @return
	 */
	public boolean isMajorGreater(Version v1, Version v2) {
		return v2.getMajor() > v1.getMajor();
	}

	/**
	 * Validates wheather v2 minor version is greater
	 * @param v1
	 * @param v2
	 * @return
	 */
	public boolean isMinorGreater(Version v1, Version v2) {
		return v2.getMinor() > v1.getMinor();
	}

	/**
	 * Validates wheather v2 build version is greater 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public boolean isBuildGreater(Version v1, Version v2) {
		return v2.getBuild() > v1.getBuild();
	}

	/**
	 * Returns true if v2 is equal or greater
	 * @param v1
	 * @param v2
	 * @return
	 */
	public boolean isVersionGreaterOrEqual(Version v1, Version v2) {
		return v2.toNumber() >= v1.toNumber();
	}
	
	/**
	 * Returns true if v1 and v2 are equal 
	 * @param v1
	 * @param v2
	 * @return
	 */
	public boolean isVersionEqual(Version v1, Version v2) {
		return v2.toNumber() == v1.toNumber();
	}
	
	
	
}
