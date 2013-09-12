package org.jboss.tools.vwatch.validator;

import org.apache.log4j.Logger;
import org.jboss.tools.vwatch.model.Bundle;

/**
 * Single bundle validator able validate without other installations
 * @author jpeterka
 *
 */
public abstract class BundleValidator extends Validator {
	public Logger log = Logger.getLogger(BundleValidator.class);
	
	
	/**
	 * Validates bundle1 with bundle2
	 * @param b1 bundle1
	 * @param b2 bundle2
	 */
	public void validate(Bundle b) {
		if (!isValid(b)) {
			addIssue(b);
		}		
	}
	
	/**
	 * Defines validation rule
	 * @param b1 bundle1
	 * @param b2 bundle2
	 * @return true if validation pass
	 */
	public abstract boolean isValid(Bundle b);

	/**
	 * Add issue
	 * @param b2
	 * @return
	 */
	public abstract void addIssue(Bundle b);
	
}
