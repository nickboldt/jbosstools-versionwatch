package org.jboss.tools.vwatch.validator;

import org.jboss.tools.vwatch.issue.MultipleVersionIssue;
import org.jboss.tools.vwatch.model.Bundle;
import org.jboss.tools.vwatch.model.Issue;
import org.jboss.tools.vwatch.service.VersionService;
import org.jboss.vwatch.util.BundleValidation;

public class MultipleVersionValidation extends BundleValidation {
	final VersionService vs = new VersionService();

	@Override
	public boolean isValid(Bundle b1, Bundle b2) {
		if (b2.hasMultipleInstances()) return false;
		return true;
	}

	@Override
	public void addIssue(Bundle b1, Bundle b2) {
		Issue i = new MultipleVersionIssue(b1);
		b2.getIssues().add(i);
	}
}
