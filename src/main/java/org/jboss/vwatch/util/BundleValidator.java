package org.jboss.vwatch.util;

import org.apache.log4j.Logger;
import org.jboss.tools.vwatch.model.Bundle;
import org.jboss.tools.vwatch.model.Issue;
import org.jboss.tools.vwatch.service.EvaluationService;

/**
 * Abstract class for specific validation rules
 * @author jpeterka
 *
 */
public abstract class BundleValidator {
	
	private int severity = 0;	
	private String issueMessage;
	public Logger log = Logger.getLogger(BundleValidator.class);
	
	
	/**
	 * Validates bundle1 with bundle2
	 * @param b1 bundle1
	 * @param b2 bundle2
	 */
	public void validate(Bundle b1, Bundle b2) {
		if (!isValid(b1,b2)) {
			addIssue(b1,b2);
		}		
	}
	
	/**
	 * Defines validation rule
	 * @param b1 bundle1
	 * @param b2 bundle2
	 * @return true if validation pass
	 */
	public abstract boolean isValid(Bundle b1, Bundle b2);

	/**
	 * Add issue
	 * @param b2
	 * @return
	 */
	public abstract void addIssue(Bundle b1, Bundle b2);
	
	public String getIssueMessage() {
		return issueMessage;
	}

	public void setIssueMessage(String issueMessage) {
		this.issueMessage = issueMessage;
	}

	public int isSeverity() {
		return severity;
	}

	public void setSeverity(int severity) {
		this.severity = severity;
	}

	public static boolean isNullFilter(String filter) {
		return filter == null || filter.equals("") || filter.equals(".*");
	}

	
}
