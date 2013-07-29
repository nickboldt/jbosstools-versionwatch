package org.jboss.tools.vwatch.validator;

import org.jboss.tools.vwatch.issue.VersionDecreasedIssue;
import org.jboss.tools.vwatch.model.Bundle;
import org.jboss.tools.vwatch.model.Issue;
import org.jboss.tools.vwatch.service.VersionService;
import org.jboss.vwatch.util.PairValidator;

public class VersionDecreasedValidator extends PairValidator {
	final VersionService vs = new VersionService();

	@Override
	public boolean isValid(Bundle b1, Bundle b2) {
		setSeverity(3);
		setIssueMessage("Version must be higher or at least equal");
		boolean ret = vs.isVersionGreaterOrEqual(b1.getVersion(),
				b2.getVersion());
		if (!ret)
			log.error("ERROR - Version must be higher or equal to it's predecessor");
		return ret;
	}

	@Override
	public void addIssue(Bundle b1, Bundle b2) {
		if (b2.getIssues().size() > 5) {
			System.out.println("uff");
		}
		
		Issue i = new VersionDecreasedIssue(b1);
		b2.getIssues().add(i);
	}
}
