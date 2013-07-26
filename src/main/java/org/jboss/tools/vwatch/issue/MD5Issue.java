package org.jboss.tools.vwatch.issue;

import org.jboss.tools.vwatch.model.Bundle;
import org.jboss.tools.vwatch.model.Issue;
import org.jboss.tools.vwatch.model.Severity;
import org.jboss.tools.vwatch.validator.MD5Validation;

public class MD5Issue extends Issue {
	

	private MD5Issue() {} 
	
	public MD5Issue(Bundle referenceBundle) {
		this.sticky = false;
		this.description = "MD5 doesn't match for the same build";
		this.severity = Severity.ERROR;
		this.referenceBundle = referenceBundle;
		this.validation = new MD5Validation();
	}
}
