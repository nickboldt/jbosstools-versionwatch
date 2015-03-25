package org.jboss.tools.vwatch.issue;

import java.util.Arrays;

import org.jboss.tools.vwatch.model.Bundle;
import org.jboss.tools.vwatch.model.Issue;
import org.jboss.tools.vwatch.model.Severity;
import org.jboss.tools.vwatch.validator.MultipleVersionValidator;
import org.jboss.tools.vwatch.validator.PairValidator;

/**
 * See for ignored:
 * https://github.com/jbosstools/jbosstools-target-platforms/blob/4.31.x/jbosstools/multiple/jbosstools-multiple.target#L11 * 
 * @author jpeterka
 *
 */
public class MultipleVersionIssue extends Issue {

	String[] ignore = {"javax.wsdl","org.apache.commons.logging","com.jcraft.jsch"};
	
	private MultipleVersionIssue() {} 
	
	public MultipleVersionIssue(Bundle referenceBundle) {
		if (Arrays.asList(ignore).contains(referenceBundle.getName())) {
			this.description = "Multiple versions of same bundle are expected and ignored";
			this.severity = Severity.IGNORE;			
		} else {
			this.description = "Multiple versions of same bundle";
			this.severity = Severity.ERROR;			
		}
		this.sticky = false;
		this.referenceBundle = referenceBundle;
		this.validation = new MultipleVersionValidator();
		
	}
}
