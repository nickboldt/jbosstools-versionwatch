package org.jboss.tools.vwatch.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.tools.vwatch.Settings;
import org.jboss.tools.vwatch.model.Bundle;
import org.jboss.tools.vwatch.model.Installation;
import org.jboss.vwatch.util.BundleValidation;

/**
 * Evaluation Service 
 * @author jpeterka
 *
 */
public class EvaluationService {

	Logger log = Logger.getLogger(EvaluationService.class);
	
	/**
	 * Sorts installations according to versions
	 * 
	 * @param originalList original unsorted list
	 * @return sorted installations from min version to max version
	 */
	public List<Installation> sortInstallations(List<Installation> originalList) {

		List<Installation> sortedList = new ArrayList<Installation>();
		Installation min = originalList.get(0);
		VersionService vs = new VersionService();
		log.setLevel(Settings.getLogLevel());
		log.info("Original list: " + originalList.toString());
		int steps = originalList.size();
		for (int i = 0; i < steps; i++) {
			min = originalList.get(0);
			for (int j = 0; j < originalList.size() - 1; j++) {
				min = vs.getMinInstallation(min, originalList.get(j + 1));
			}
			sortedList.add(min);
			originalList.remove(min);
		}

		log.info("Original list: " + originalList.toString());
		log.info("Sorted list: " + sortedList.toString());
		return sortedList;
	}

	/**
	 * Finds conflicts in given installations
	 * @param installations installation list for evaluation
	 */
	public void findConflicts(List<Installation> installations, String filter) {

		log.setLevel(Settings.getLogLevel());

		for (int i = 0; i < installations.size() - 1; i++) {
			log.debug("Finding conflicts in installation: "
					+ installations.get(i + 1).getRootFolderAbsolutePath());
			findConflictsBetweenTwo(installations.get(i),
					installations.get(i + 1), filter, true);
			findConflictsBetweenTwo(installations.get(i),
					installations.get(i + 1), filter, false);
		}
	}
	
	private String getBundleMD5(Bundle b) {
		File f = new File(b.getAbsolutePath());
		if (f.isDirectory()) {
			return "dir";
		} else {
			return MD5Service.getInstance().getMD5(f);
		}
	}
	
	/**
	 * Finds conflicts between two installations
	 * TODO: Separate validation rules
	 * @param i1 first installation
	 * @param i2 second installation
	 * @param feature boolean to set whether to use feature or plugin pack 
	 */
	private void findConflictsBetweenTwo(final Installation i1,
			final Installation i2, String filter, boolean feature) {

		final VersionService vs = new VersionService();
	
		log.setLevel(Settings.getLogLevel());

		// Validation condition preparation
		// major
		BundleValidation majorHigher = new BundleValidation() {

			@Override
			public boolean isValid(Bundle b1, Bundle b2) {				
				setSeverity(1);
				setIssueMessage("Major version is the same");
				return vs.isMajorGreater(b1.getVersion(), b2.getVersion());
			}
		};
		//minor
		BundleValidation minorHigher = new BundleValidation() {

			@Override
			public boolean isValid(Bundle b1, Bundle b2) {
				setSeverity(1);
				setIssueMessage("Minor version is the same");
				return vs.isMinorGreater(b1.getVersion(), b2.getVersion());
			}
		};
		//build
		BundleValidation buildHigher = new BundleValidation() {

			@Override
			public boolean isValid(Bundle b1, Bundle b2) {
				setSeverity(1);
				setIssueMessage("Build version is the same");
				return vs.isBuildGreater(b1.getVersion(), b2.getVersion());
			}
		};
		
		//version equals
		BundleValidation versionNotBumped= new BundleValidation() {

			@Override
			public boolean isValid(Bundle b1, Bundle b2) {
				setSeverity(2);
				setIssueMessage("Version is not bumped");
				boolean ret =  !vs.isVersionEqual(b1.getVersion(), b2.getVersion());												
				
				log.setLevel(Settings.getLogLevel());

				// MD5 check
				if (!ret && Settings.isMd5checkEnabled()) {
					log.info("Version is not bumped, checking md5 for " + b1.getName());
					b1.setMd5(getBundleMD5(b1));
					b2.setMd5(getBundleMD5(b2));				
										
					// MD5 diff
					if ((b1.getMd5().equals("dir") && !b2.getMd5().equals("dir")) || (!b1.getMd5().equals("dir") && b2.getMd5().equals("dir"))){
						setSeverity(2);
						setIssueMessage(getIssueMessage() + ";" + "MD5 cannot be checked, one bundle is dir");
					}
					else if (b1.getMd5().equals("dir") && b2.getMd5().equals("dir")){
						setSeverity(0);
						setIssueMessage(getIssueMessage() + "Both bundles are dirs, md5 for dirs not yet supported");					
						}
					else if (!b1.getMd5().equals(b2.getMd5())) {
						
						if (b1.getFullName().equals(b2.getFullName())) {
							setSeverity(3);
							setIssueMessage(getIssueMessage() + ";" + "MD5 doesn't match for same build: " + b1.getMd5() + "!=" + b2.getMd5());
						} else {
							setIssueMessage(getIssueMessage() + ";" + "MD5 doesn't match for different build");
						}
					}
					
				}
				return ret;
			}
		};
		
		//version lower or equal
		BundleValidation versionGreaterOrEqual = new BundleValidation() {

			@Override
			public boolean isValid(Bundle b1, Bundle b2) {
				setSeverity(3);
				setIssueMessage("Version must be higher or at least equal");
				boolean ret =  vs.isVersionGreaterOrEqual(b1.getVersion(), b2.getVersion());
				if (!ret) log.error("ERROR - Version must be higher or equal to its predecessor");
				return ret;
			}
		};
	
		// Validation execution
		findVersionConflicts(i1, i2, feature, versionGreaterOrEqual, filter);
		findVersionConflicts(i1, i2, feature, versionNotBumped, filter);
		
		// major higher
		if (vs.isMajorDiff(i1.getVersion(), i2.getVersion())) {
			log.debug("Major diff finding between " + i1.getRootFolderName()
					+ " and " + i2.getRootFolderName());

			findVersionConflicts(i1, i2, feature, majorHigher, filter);
		}
		// minor higher
		else if (vs.isMinorDiff(i1.getVersion(), i2.getVersion())) {
			log.debug("Minor diff finding between " + i1.getRootFolderName()
					+ " and " + i2.getRootFolderName());

			findVersionConflicts(i1, i2, feature, minorHigher, filter);
		}
		// build higher
		else if (vs.isBuildDiff(i1.getVersion(), i2.getVersion())) {
			log.debug("Build diff finding between " + i1.getRootFolderName()
					+ " and " + i2.getRootFolderName());

			findVersionConflicts(i1, i2, feature, buildHigher, filter);
		}
	}

	private void findVersionConflicts(Installation i1, Installation i2,
			boolean feature, BundleValidation validation, String filter) {
		BundleService bs = new BundleService();
		
		log.setLevel(Settings.getLogLevel());

		for (Bundle b2 : i2.getBundles(feature)) {
			// only validate if the filter matches, which saves a ton of time
			if (BundleValidation.isNullFilter(filter) || b2.getName().matches(filter))
			{
				Bundle b1 = bs.getBundleFromList(i1.getBundles(feature), b2.getName());
				if (b1 != null) {
					if (!b1.hasMultipleInstances() && !b2.hasMultipleInstances()) {
						validation.validate(b1, b2);
					}
					else  {
						log.info("Multiple instances, not supported yet");
					}
				} else {
					log.info("Not found " + b2.getName() + " in "
							+ i1.getRootFolderName());
				}
			}
		}

	}

}
