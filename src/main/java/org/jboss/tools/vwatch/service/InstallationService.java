package org.jboss.tools.vwatch.service;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.jboss.tools.vwatch.Settings;
import org.jboss.tools.vwatch.model.Bundle;
import org.jboss.tools.vwatch.model.BundleInstance;
import org.jboss.tools.vwatch.model.BundleType;
import org.jboss.tools.vwatch.model.Installation;
import org.jboss.tools.vwatch.model.Issue;
import org.jboss.tools.vwatch.model.Version;

/**
 * Service providing single installation methods like bundle loading 
 * @author jpeterka
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
			
			installation.getBundles(feature).add(bundle);
		}		
	}

	/**
	 * Parses bundle record and adds Bundle into Bundle Collection
	 * @param record
	 * @param bundles
	 */
	private Bundle parseAndGetBundle(Installation installation, boolean feature, String record) {
		//Bundle b = new Bundle();
		
		// parse version
		Pattern pattern = Pattern.compile("\\d+\\.\\d+\\.\\d+");
		Matcher m = pattern.matcher(record);
		m.find();
		String versionString = m.group();
			
		String[] split = versionString.split("\\.");			
		Version v = new Version();
		v.setMajor(Integer.parseInt(split[0]));
		v.setMinor(Integer.parseInt(split[1]));
		v.setBuild(Integer.parseInt(split[2]));
			
		BundleInstance bi = new BundleInstance();
		bi.setVersion(v);
		bi.setFullName(record);
		bi.setAbsolutePath(installation.getAbsoluteBundleDir(feature) + File.separator + record);
			
		Pattern p2 = Pattern.compile("^.*?(?=_\\d+\\.\\d+\\.\\d+)");
		Matcher m2 = p2.matcher(record);
		m2.find();
		String found = m2.group();
		
		
		BundleService bs = new BundleService();
		Bundle b = bs.getBundleFromList(installation.getBundles(feature), found);
		if (b == null) {
			b = new Bundle();
			b.setName(found);
		} else
		{
			Issue e = new Issue();
			e.setSeverity(2);
			e.setMessage("Multiple versions");
			b.getIssues().add(e);
		}

		
		bi.setBundle(b);		
				
		// parse potfix TBD
		bi.setPostfix("");

		log.setLevel(Settings.getLogLevel());
		log.debug(b.toString());
		
		// Add bundle instance type
		File f = new File(installation.getAbsoluteBundleDir(feature) + File.separator + record);			
		bi.setBundleType(getBundleType(f));

		b.getInstances().add(bi);
		return b;							
	}
	
	private BundleType getBundleType(File f) {
		if (f.isDirectory()) 
			return BundleType.FOLDER;
		else 
			return BundleType.JAR;
	}
	

}
