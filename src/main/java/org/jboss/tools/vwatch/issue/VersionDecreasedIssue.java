package org.jboss.tools.vwatch.issue;

import org.jboss.tools.vwatch.model.Bundle;
import org.jboss.tools.vwatch.model.Issue;
import org.jboss.tools.vwatch.model.Severity;
import org.jboss.tools.vwatch.validator.VersionDecreasedValidation;

/**
 * This issue describes situation when there component version is lower than
 * component version for previous version product
 * 
 * @author jpeterka
 * 
 */
public class VersionDecreasedIssue extends Issue {
	
	private VersionDecreasedIssue() {}
	
	public VersionDecreasedIssue(Bundle referenceBundle) {
		this.sticky = true;
		this.description = "Version must be higher";
		this.severity = Severity.ERROR;
		this.referenceBundle = referenceBundle;
		this.validation = new VersionDecreasedValidation();
	}		
}
