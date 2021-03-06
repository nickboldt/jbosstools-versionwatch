package org.jboss.tools.vwatch.validator;

import org.jboss.tools.vwatch.issue.MultipleVersionIssue;
import org.jboss.tools.vwatch.model.Bundle;
import org.jboss.tools.vwatch.model.Issue;

/**
 * Multiple version validator checks if there are multiple version for given bundle
 * @author jpeterka
 *
 */
public class MultipleVersionValidator extends BundleValidator {

	@Override
	public boolean isValid(Bundle b) {
		if (b.hasMultipleInstances()) return false;
		return true;
	}

	@Override
	public void addIssue(Bundle b) {
		Issue i = new MultipleVersionIssue(b);
		b.getIssues().add(i);
	}
}
