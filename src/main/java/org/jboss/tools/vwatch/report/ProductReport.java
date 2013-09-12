package org.jboss.tools.vwatch.report;

import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jboss.tools.vwatch.issue.FolderAndJarIssue;
import org.jboss.tools.vwatch.issue.MD5Issue;
import org.jboss.tools.vwatch.issue.MultipleVersionIssue;
import org.jboss.tools.vwatch.issue.VersionDecreasedIssue;
import org.jboss.tools.vwatch.model.Bundle;
import org.jboss.tools.vwatch.model.Installation;
import org.jboss.tools.vwatch.model.Issue;
import org.jboss.tools.vwatch.model.Severity;

public class ProductReport extends Report {

	Logger log = Logger.getLogger(ProductReport.class);
	Installation installation;

	public ProductReport(Installation installation) {
		this.installation = installation;
	}

	protected void generateFooter() {
		super.generateFooter();

	}

	protected void generateBody() {

		add(html.h1());
		add("VW product report: " + installation.getRootFolderName());
		add(html.h1().end());
		
		reportVersionDecreasedIssue();
		reportMultipleVersionIssue();
		reportFolderAndJarIssue();
		reportMD5Issues();
	}

	private void reportIssues(Class<? extends Issue> class1) {

		int count = 0;
		count = searchBundlesForIssues(class1, installation.getBundles(true));
		count += searchBundlesForIssues(class1, installation.getBundles(false));
		if (count == 0) {
			add("No issues found");
		} 
		add(html.newLine());		
	}

	private int searchBundlesForIssues(Class<? extends Issue> class1,		
			Set<Bundle> bundles) {
		int counter = 0;
		for (Bundle b : bundles) {
			List<Issue> issues = b.getIssues();
			for (Issue i : issues) {
				if (i.getClass().equals(class1)) {
					String message = i.getReferenceBundle().getFullName();					
					if (i.getSeverity() == Severity.IGNORE) {
						message += " - ignored, known issue";
					}
					add(message);
					add(html.newLine());
					counter++;
				}
			}

		}
		return counter;
	}

	private void reportMD5Issues() {
		add(html.h2());
		addAndBr("Unmatching MD5 issues");
		add(html.h2().end());

		reportIssues(MD5Issue.class);

	}

	private void reportFolderAndJarIssue() {
		add(html.h2());
		addAndBr("Folder and JAR issue");
		add(html.h2().end());

		reportIssues(FolderAndJarIssue.class);
	}
	
	private void reportMultipleVersionIssue() {
		add(html.h2());
		addAndBr("Multiple Version issue");
		add(html.h2().end());

		reportIssues(MultipleVersionIssue.class);
	}
	
	
	private void reportVersionDecreasedIssue() {
		add(html.h2());
		addAndBr("Version decreased issue");
		add(html.h2().end());

		reportIssues(VersionDecreasedIssue.class);
	}

	protected void generateHeader() {
		super.generateHeader();
	}

	@Override
	protected String getFileName() {
		return "product.html";
	}


}
