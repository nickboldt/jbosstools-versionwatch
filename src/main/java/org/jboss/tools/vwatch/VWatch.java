package org.jboss.tools.vwatch;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.jboss.tools.vwatch.model.Installation;
import org.jboss.tools.vwatch.service.EvaluationService;
import org.jboss.tools.vwatch.service.FolderLookupSerivce;
import org.jboss.tools.vwatch.service.ReportService;
import org.jboss.tools.vwatch.service.StopWatch;

/**
 * VWatch Class root class for Version Watch usage
 * 
 * @author jpeterka
 * 
 */
public class VWatch {

	private String repoPath = "/opt/vw";
	private String reportFilter = "";
	private boolean md5checkEnabled = false;
	
	Logger log = Logger.getLogger(VWatch.class);
	List<Installation> installations = new ArrayList<Installation>();

	/**
	 * Main method
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		run();
	}

	/**
	 * Main function execution complete cycle of operations from configuration,
	 * loading data to creating reports
	 */
	public static void run() {
		StopWatch.start();
		VWatch vw = new VWatch();
		vw.configureLog4j();
		vw.configureVWatch();
		vw.loadInstallations();
		vw.evaluateInstallations();
		vw.createReport();
	}

	/**
	 * Read configuration from outside: Supported parameters: -
	 * vwatch.installationsDir : root with eclipse installations -
	 * vwatch.reportFilter : filter for filtering only desired bundle names
	 */
	private void configureVWatch() {
		String installationsDir = System.getProperty("vwatch.installationsDir");
		if (installationsDir != null) {
			repoPath = installationsDir;
			log.info("Installations dir set from outside to:" + repoPath);
		}
		String filter = System.getProperty("vwatch.filter");
		if (filter != null)
			reportFilter = filter;
		
		String md5check= System.getProperty("vwatch.md5check");
		if (md5check != null) {
			Settings.setMd5checkEnabled(true);
		}
		
	}

	/**
	 * Basic programmatic log4j configuration
	 */
	private void configureLog4j() {

		Logger root = Logger.getRootLogger();
		root.addAppender(new ConsoleAppender(new PatternLayout(
				PatternLayout.TTCC_CONVERSION_PATTERN)));
	}

	/**
	 * Load installations into collections
	 */
	private void loadInstallations() {
		log.info("Loading installation started");
		FolderLookupSerivce fls = new FolderLookupSerivce();
		installations = fls.getInstallations(repoPath);
		log.info("Installations loaded");

	}

	/**
	 * Sorts and evaluates installations and finds defined bundle conflicts
	 */
	private void evaluateInstallations() {
		EvaluationService es = new EvaluationService();
		installations = es.sortInstallations(installations);
		es.findConflicts(installations);
	}

	/**
	 * Create final report
	 */
	private void createReport() {
		ReportService rs = new ReportService();
		rs.generateReport(installations, reportFilter);
	}

	public boolean isMd5checkEnabled() {
		return md5checkEnabled;
	}

}
