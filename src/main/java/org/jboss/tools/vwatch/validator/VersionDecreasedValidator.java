package org.jboss.tools.vwatch.validator;

import org.jboss.tools.vwatch.issue.VersionDecreasedIssue;
import org.jboss.tools.vwatch.model.Bundle;
import org.jboss.tools.vwatch.model.Issue;
import org.jboss.tools.vwatch.service.VersionService;

public class VersionDecreasedValidator extends PairValidator {
	final VersionService vs = new VersionService();

	@Override
	public boolean isValid(Bundle b1, Bundle b2) {
		setSeverity(3);
		setIssueMessage("Version must be greater than or equal to previous when comparing " + b1.getFullName() + " :: "
				+ b1.getFullVersions() + " to " + b2.getFullName() + " :: " + b2.getFullVersions());
		boolean ret = vs.isVersionGreaterOrEqual(b1.getVersion(), b2.getVersion());
		if (!ret) {
			b2.setDecreased();
			log.error("ERROR - Version must be greater than or equal to previous when comparing " + b1.getFullName()
					+ " :: " + b1.getFullVersions() + " to " + b2.getFullName() + " :: " + b2.getFullVersions());
		}
		return ret;
	}

	// JBIDE-21391: report both reference version and current version in the error message
	@Override
	public void addIssue(Bundle b1, Bundle b2) {
		Issue i = new VersionDecreasedIssue(b1,b2);
		b2.getIssues().add(i);
	}
}
