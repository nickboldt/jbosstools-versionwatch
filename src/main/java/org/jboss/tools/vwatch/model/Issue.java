package org.jboss.tools.vwatch.model;

import org.jboss.tools.vwatch.validator.Validator;
import org.jboss.vwatch.util.PairValidator;

/**
 * Abstract issue class
 * 
 * @author jpeterka
 * 
 */
public abstract class Issue {

	// Data for revalidation
	protected Validator validation;
	protected Bundle referenceBundle;
	
	protected Severity severity;
	protected String description;
	protected boolean sticky = false;
	
	public String getDescription() {
		return description;
	}

	public void setSeverity(Severity s) {
		this.severity = s;
	}
	
	public Severity getSeverity() {
		return severity;
	}

	public boolean isSticky() {
		return sticky;
	}

	public Validator getValidation() {
		return validation;
	}

	public void setValidation(PairValidator validation) {
		this.validation = validation;
	}

	public Bundle getReferenceBundle() {
		return referenceBundle;
	}

	public void setReferenceBundle(Bundle referenceBundle) {
		this.referenceBundle = referenceBundle;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setSticky(boolean sticky) {
		this.sticky = sticky;
	}
	
	
	
}
