package org.jboss.tools.vwatch.issue;

import org.jboss.tools.vwatch.model.Bundle;
import org.jboss.tools.vwatch.model.Issue;
import org.jboss.tools.vwatch.model.Severity;
import org.jboss.tools.vwatch.validator.MultipleVersionValidation;
import org.jboss.vwatch.util.BundleValidation;

public class MultipleVersionIssue extends Issue {
	

	private MultipleVersionIssue() {} 
	
	public MultipleVersionIssue(Bundle referenceBundle) {
		this.sticky = false;
		this.description = "Multiple versions of same bundle";
		this.severity = Severity.ERROR;
		this.referenceBundle = referenceBundle;
		this.validation = new MultipleVersionValidation();
	}
}
