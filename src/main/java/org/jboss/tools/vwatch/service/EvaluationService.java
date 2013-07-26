package org.jboss.tools.vwatch.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

import org.apache.log4j.Logger;
import org.jboss.tools.vwatch.Settings;
import org.jboss.tools.vwatch.model.Bundle;
import org.jboss.tools.vwatch.model.Installation;
import org.jboss.tools.vwatch.model.Issue;
import org.jboss.tools.vwatch.validator.VersionDecreasedValidation;
import org.jboss.vwatch.util.BundleValidation;

/**
 * Evaluation Service
 * 
 * @author jpeterka
 * 
 */
public class EvaluationService {

	Logger log = Logger.getLogger(EvaluationService.class);

	/**
	 * Sorts installations according to versions
	 * 
	 * @param originalList
	 *            original unsorted list
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
				if (min.getRootFolderName().compareTo(
						originalList.get(j + 1).getRootFolderName()) > 0) {
					min = originalList.get(j + 1);
				}
			}
			sortedList.add(min);
			originalList.remove(min);
			log.info("Sorted:" + min.getRootFolderName());
		}

		log.info("Original list: " + originalList.toString());
		log.info("Sorted list: " + sortedList.toString());

		return sortedList;
	}

	/**
	 * Finds conflicts in given installations
	 * 
	 * @param installations
	 *            installation list for evaluation
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
		File f2 = new File(b.getAbsolutePath() + ".jar");
		if (f.isFile()) {
			return MD5Service.getInstance().getMD5(f);
		}

		if (f2.isFile()) {
			return MD5Service.getInstance().getMD5(f2);
		}

		if (f.isDirectory()) {
			ZipFile bundleJar = null;
			try {
				log.warn("Bundle " + b.getName()
						+ " is a folder; must jar it to compare MD5 sums.");
				bundleJar = new ZipFile(f2);
				ZipParameters parameters = new ZipParameters();
				parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
				parameters
						.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
				bundleJar.createZipFileFromFolder(b.getAbsolutePath(),
						parameters, true, 10485760);
			} catch (ZipException e) {
				e.printStackTrace();
			}
			if (bundleJar != null) {
				return MD5Service.getInstance().getMD5(f2);
			}
		}
		log.warn("Could not generate MD5 for " + b.getAbsolutePath());
		return null;
	}

	/**
	 * Finds conflicts between two installations TODO: Separate validation rules
	 * 
	 * @param i1
	 *            first installation
	 * @param i2
	 *            second installation
	 * @param feature
	 *            boolean to set whether to use feature or plugin pack
	 */
	private void findConflictsBetweenTwo(final Installation i1,
			final Installation i2, String filter, boolean feature) {

		// version lower or equeal
		BundleValidation versionDecreasedValidation = new VersionDecreasedValidation();

		copyPreviousConflicts(i1, i2, feature);

		findVersionConflicts(i1, i2, feature, versionDecreasedValidation);
	}

	private void copyPreviousConflicts(Installation i1, Installation i2,
			boolean feature) {

		BundleService bs = new BundleService();
		for (Bundle b2 : i2.getBundles(feature)) {
			Bundle b1 = bs.getBundleFromList(i1.getBundles(feature),
					b2.getName());
			if (b1 != null) {

				// Check fixes
				if (!b1.hasMultipleInstances() && !b2.hasMultipleInstances()) {
					checkFixes(b1, b2);
				} else {
					log.info("Multiple instances, not supported yet");
				}
			} else {
				log.info("Not found " + b2.getName() + " in "
						+ i1.getRootFolderName());
			}
		}

	}

	private void checkFixes(Bundle b1, Bundle b2) {
		for (Issue i : b1.getIssues()) {
			BundleValidation validation = i.getValidation();
			Bundle refBundle = i.getReferenceBundle();
			if (!validation.isValid(refBundle, b2)) {
				validation.addIssue(refBundle, b2);
			}
		}
	}

	private void findVersionConflicts(Installation i1, Installation i2,
			boolean feature, BundleValidation validation) {
		BundleService bs = new BundleService();

		log.setLevel(Settings.getLogLevel());

		for (Bundle b2 : i2.getBundles(feature)) {
			// only validate if the filter matches, which saves a ton of time
			if (BundleValidation.isNullFilter(Settings.getFilter())
					|| b2.getName().matches(Settings.getFilter())) {
				Bundle b1 = bs.getBundleFromList(i1.getBundles(feature),
						b2.getName());
				if (b1 != null) {
					if (!b1.hasMultipleInstances()
							&& !b2.hasMultipleInstances()) {
						validation.validate(b1, b2);
					} else {
						log.info("Multiple instances, not supported yet");
					}
				} else {
					log.info("Could not find " + b2.getName() + " in "
							+ i1.getRootFolderName());
				}
			}
		}

	}
}
