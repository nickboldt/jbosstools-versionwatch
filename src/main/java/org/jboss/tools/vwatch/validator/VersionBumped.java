package org.jboss.tools.vwatch.validator;

import org.jboss.tools.vwatch.model.Bundle;
import org.jboss.tools.vwatch.service.VersionService;

public class VersionBumped extends PairValidator {
	final VersionService vs = new VersionService();

	@Override
	public boolean isValid(Bundle b1, Bundle b2) {
		setIssueMessage("Version must be greater than or equal");
		boolean ret = vs.isVersionGreater(b1.getVersion(),
				b2.getVersion());
		if (ret)
			b2.setBumped();
		return ret;
	}

	@Override
	public void addIssue(Bundle b1, Bundle b2) {
		// do nothing
	}
}
