package org.jboss.tools.vwatch.validator;

import org.jboss.tools.vwatch.model.Bundle;
import org.jboss.vwatch.util.PairValidator;

public class OkValidator extends PairValidator {
	

	@Override
	public boolean isValid(Bundle b1, Bundle b2) {
		return true;
	}

	@Override
	public void addIssue(Bundle b1, Bundle b2) {
		return;
	}
}
