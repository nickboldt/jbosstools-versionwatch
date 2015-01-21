package org.jboss.tools.vwatch.issue;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.jboss.tools.vwatch.model.Bundle;
import org.jboss.tools.vwatch.model.Issue;
import org.jboss.tools.vwatch.model.Severity;
import org.jboss.tools.vwatch.report.BundleVersionReport;
import org.jboss.tools.vwatch.validator.MultipleVersionValidator;

/**
 * See for ignored:
 * https://github.com/jbosstools/jbosstools-target-platforms/blob/4.31.x/jbosstools/multiple/jbosstools-multiple.target#L11 * 
 * @author jpeterka
 *
 */
public class MultipleVersionIssue extends Issue {

	Logger log = Logger.getLogger(MultipleVersionIssue.class);

	private Properties multipleVersionIgnoreProperties = new Properties();
	ClassLoader classloader = Thread.currentThread().getContextClassLoader();
	
	InputStream is = classloader.getResourceAsStream("multiple-version-ignore.properties");{
		try {
			multipleVersionIgnoreProperties.load(is);
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}
	
	public MultipleVersionIssue(Bundle referenceBundle) {
		// TODO figure out why getVersions() only returns ONE version
		// TODO make this actually check referenceBundle.getVersions() against the values in the properties file
		if (multipleVersionIgnoreProperties.containsKey(referenceBundle.getName())) {
			this.description = "Multiple versions of bundle are expected and ignored - " + multipleVersionIgnoreProperties.getProperty(referenceBundle.getName());
			this.severity = Severity.IGNORE;
			log.info(referenceBundle.getName() + ": " + this.description);
			// TODO: this always returns size() = 1; but should be larger for dupe bundles so we can report what the two versions are in the summary report
//			String versions = "";
//			for (int i = 0; i < referenceBundle.getInstances().size(); i++) {
//				versions += referenceBundle.getInstances().get(i).getVersion();
//				if (i < referenceBundle.getInstances().size() - 1) {
//					versions +=", ";
//				}
//			}
//			log.info("Versions ["+referenceBundle.getInstances().size()+"] found: "+versions);

		} else {
			this.description = "Multiple versions of same bundle found!";
			this.severity = Severity.ERROR;			
			log.info(referenceBundle.getName() + ": " + this.description);
//			log.info("Versions found: "+referenceBundle.getVersions());
		}
		this.sticky = false;
		this.referenceBundle = referenceBundle;
		this.validation = new MultipleVersionValidator();
		
	}
}
