package org.jboss.tools.vwatch.validator;

import org.jboss.tools.vwatch.issue.FolderAndJarIssue;
import org.jboss.tools.vwatch.model.Bundle;
import org.jboss.tools.vwatch.model.BundleInstance;
import org.jboss.tools.vwatch.model.BundleType;
import org.jboss.tools.vwatch.service.VersionService;

/**
 * Validator checks if there is just either folder or jar for given bundle, not both
 * @author jpeterka
 *
 */
public class FolderAndJarValidator extends BundleValidator {
	final VersionService vs = new VersionService();	

	@Override
	public boolean isValid(Bundle b) {
		for (BundleInstance i : b.getInstances()) {
			if (i.getBundleType() == BundleType.BOTH) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void addIssue(Bundle b) {
		FolderAndJarIssue i = new FolderAndJarIssue(b);
		b.getIssues().add(i);
	}
}
