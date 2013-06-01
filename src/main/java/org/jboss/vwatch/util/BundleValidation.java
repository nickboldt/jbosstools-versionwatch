package org.jboss.vwatch.util;

import org.jboss.tools.vwatch.model.Bundle;
import org.jboss.tools.vwatch.model.Issue;

/**
 * Abstract class for specific validation rules
 * @author jpeterka
 *
 */
public abstract class BundleValidation {
	
	private int severity = 0;	
	private String issueMessage;
	
	/**
	 * Validates bundle1 with bundle2
	 * @param b1 bundle1
	 * @param b2 bundle2
	 */
	public void validate(Bundle b1, Bundle b2) {
		if (!isValid(b1,b2)) {
			Issue i = new Issue();
			i.setMessage(issueMessage);
			i.setSeverity(severity);
			b2.getIssues().add(i);
		}		
	}
	
	/**
	 * Defines validation rule
	 * @param b1 bundle1
	 * @param b2 bundle2
	 * @return true if validation pass
	 */
	public abstract boolean isValid(Bundle b1, Bundle b2);

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

	
}
