package org.jboss.tools.vwatch.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.tools.vwatch.Settings;
import org.jboss.tools.vwatch.model.Installation;
import org.jboss.tools.vwatch.model.Version;

/**
 * Services for loading eclipse installations data from given folder   
 * @author jpeterka
 *
 */
public class FolderLookupSerivce  {
	
	/**
	 * Load installations based in the repo path
	 * @param repoPath
	 * @return
	 */
	
	Logger log = Logger.getLogger(FolderLookupSerivce.class);
	LogService ls = new LogService();
	
	public List<Installation> getInstallations(String repoPath) {
				
		List<Installation> installations = new ArrayList<Installation>();
		// check repo dir
		if (!dirExists(repoPath)) ls.logAndExit(repoPath + " doesn't exist or is not directory");   
					
		// load folders list
		installations = getValidJBDSFolders(repoPath);	
		if (installations.size() == 0) ls.logAndExit(repoPath + " no installation folders found");
		
		// load installations content
		fillInstallationsContent(installations);
		if (installations.size() == 0) ls.logAndExit("No installations data loaded");
		
		return installations;
	}

	/**
	 * Check if directory exists
	 * @param repoPath
	 * @return
	 */
	private boolean dirExists(String repoPath) {
		File f = new File(repoPath);
		return (f.exists() && f.isDirectory());
	}


	/**
	 * Returns folder list matching valid pattern
	 * @param repoPath
	 * @return
	 */
	private List<Installation> getValidJBDSFolders(String repoPath) {

		List<Installation> installations = new ArrayList<Installation>();
		
		VersionService vs = new VersionService();

		log.info("Include versions: " + Settings.getIncludeVersions());
		log.info("Exclude versions: " + Settings.getExcludeVersions());

		File root = new File(repoPath);
			File[] listFiles = root.listFiles();
			Arrays.sort(listFiles);
			for (File f : listFiles) {
				if (f.isDirectory() && vs.isValid(f.getName())) {
					Installation i = new Installation();					
					i.setRootFolderAbsolutePath(f.getAbsolutePath());
					i.setRootFolderName(f.getName());
					setInstallationVersion(i);
					installations.add(i);
					log.info("Using install dir: "+f.toString());
				}
			}
		return installations;
		
	}

	private void setInstallationVersion(Installation i) {
		VersionService vs = new VersionService();		
		Version v = vs.parseVersionFromInstallationFolder(i);
		i.setVersion(v);				
	}

	/**
	 * One by one load installation Content
	 * @param folders
	 * @return
	 */
	private void fillInstallationsContent(List<Installation> installations) {

		for (Installation installation : installations) {
			if (!isInstallationValid(installation)) {
				installation.setValid(false);		
			}
			else {
				InstallationService is = new InstallationService();
				is.fillPluginsAndFeatures(installation);
			}
		}
			
	}

	/**
	 * Checks if installation is valid, i.e. it contains eclipse/studio, plugins and feature dirs
	 * @return
	 */
	private boolean isInstallationValid(Installation installation) {
		File f = new File(installation.getRootFolderAbsolutePath());
		String[] list = f.list();
		
		List<String> arrayList = new ArrayList<String>();
		Collections.addAll(arrayList, list);
		
		String eclipseFolder = "";		
		if (arrayList.contains("eclipse")) eclipseFolder = "eclipse";
		if (arrayList.contains("studio")) eclipseFolder = "studio";
		
		if (eclipseFolder.equals("")) {		
			log.error("Folder " + installation.getRootFolderAbsolutePath() + " isn't valid eclipse/jbds installation (doesn't contain eclipse or studio folder");
			return false;
		}
		installation.setEclipseFolder(eclipseFolder);
		
		File installationRootFolder = new File(installation.getRootFolderAbsolutePath() + File.separator + eclipseFolder);
		if (!installationRootFolder.isDirectory()) {
			log.error("Installation folder " + installationRootFolder.getAbsolutePath() + " is not directory");
			return false;
		}
		
		String[] installationRootList = installationRootFolder.list();
		arrayList.clear();
		Collections.addAll(arrayList, installationRootList);
		if (!arrayList.contains("plugins") && !arrayList.contains("features")) {
			log.error("Installation folder " + installationRootFolder.getAbsolutePath() + " doesn't contain plugins or features dirs");
			return false;
		}
		
		// everything seems ok
		return true;
	}
}

