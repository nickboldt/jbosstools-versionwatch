package org.jboss.tools.vwatch.service;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jboss.tools.vwatch.Settings;
import org.jboss.tools.vwatch.issue.MultipleVersionIssue;
import org.jboss.tools.vwatch.model.Bundle;
import org.jboss.tools.vwatch.model.BundleInstance;
import org.jboss.tools.vwatch.model.BundleType;
import org.jboss.tools.vwatch.model.Installation;
import org.jboss.tools.vwatch.model.Issue;
import org.jboss.tools.vwatch.model.Version;

/**
 * Service providing single installation methods like bundle loading 
 * @author Jiri Peterka
 *
 */
public class InstallationService {
	
	Logger log = Logger.getLogger(InstallationService.class);
			
	/**
	 * Fill plugins and features informations
	 * @param installation
	 */
	public void fillPluginsAndFeatures(Installation installation) {
		readBundles(installation, true);
		readBundles(installation, false);
	}
	
	private void readBundles(Installation installation, boolean feature) {
		FilenameFilter ff = new FilenameFilter() {
			
			public boolean accept(File dir, String name) {
				
				log.setLevel(Settings.getLogLevel());

				if (name.matches(".+\\d+\\.\\d+.\\d+.+")) {
					
					log.info(name + " accepted");
					return true;
				}
				else {
					log.info(name + " ignored");
					return false;
				}
			}
		};
		
		String bundleRoot = "";
		if (feature) bundleRoot = installation.getAbsoluteFeaturesDir() ;
		else bundleRoot = installation.getAbsolutePluginsDir();
				
		String[] list = new File(bundleRoot).list(ff);
		
		for (String record  : list ) {			
			Bundle bundle = parseAndGetBundle(installation, feature, record);
			if (bundle != null) installation.getBundles(feature).add(bundle);
		}		
	}

	/**
	 * Parses bundle record and adds Bundle into Bundle Collection
	 * @param record
	 * @param bundles
	 */
	private Bundle parseAndGetBundle(Installation installation, boolean feature, String record) {
		//Bundle b = new Bundle();
		
		// parse version from record
		Pattern pattern = Pattern.compile("\\d+\\.\\d+\\.\\d+");
		Matcher m = pattern.matcher(record);
		m.find();
		String versionString = m.group();
			
		// parse major, minor and build version
		String[] split = versionString.split("\\.");			
		Version v = new Version();
		v.setMajor(Integer.parseInt(split[0]));
		v.setMinor(Integer.parseInt(split[1]));
		v.setBuild(Integer.parseInt(split[2]));
			
		// create bundle instance
		BundleInstance bi = new BundleInstance();
		bi.setVersion(v);
		bi.setAbsolutePath(installation.getAbsoluteBundleDir(feature) + File.separator + record);
		
		// get bundle type
		int bundleType = BundleType.NONE;
		if (new File(bi.getAbsolutePath()).isFile()) {
			bundleType = BundleType.JAR;
		} else {
			bundleType = BundleType.DIR;	
		}
		
		// Set full name (remove jar extension if any);
		if (bundleType == BundleType.JAR) {
			String fullName = record;
			String withoutJar = fullName.substring(0, fullName.length() - 4);
			bi.setFullName(withoutJar);
		}
		else {
			bi.setFullName(record);
		}
	
		// parse bundle name
		Pattern p2 = Pattern.compile("^.*?(?=_\\d+\\.\\d+\\.\\d+)");
		Matcher m2 = p2.matcher(record);
		m2.find();
		String found = m2.group();
		
		BundleService bs = new BundleService();
		Bundle b = bs.getBundleFromList(installation.getBundles(feature), found);
		if (b == null ) {
			b = new Bundle();
			b.setName(found);
		} else
		{			
			// if it contains same instance
			BundleInstance instance = b.getInstance(bi);
			if (instance != null) {
				// just add BundleType flag
				instance.setBundleType(instance.getBundleType() | bundleType);
				// return null, instance already exists
				return null;
			} else {
				Issue e = new MultipleVersionIssue(b);
				b.getIssues().add(e);				
			}
		}
		
		bi.setBundle(b);		
				
		// parse potfix TBD
		bi.setPostfix("");

		log.setLevel(Settings.getLogLevel());
		log.debug(b.toString());
		
		// Add bundle instance type
		File f = new File(installation.getAbsoluteBundleDir(feature) + File.separator + record);
		
		bi.setBundleType(bi.getBundleType() | bundleType);
		b.getInstances().add(bi);
		return b;							
	}
}
