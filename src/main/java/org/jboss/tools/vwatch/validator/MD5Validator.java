package org.jboss.tools.vwatch.validator;

import java.io.File;

import org.apache.log4j.Logger;
import org.jboss.tools.vwatch.issue.MD5Issue;
import org.jboss.tools.vwatch.model.Bundle;
import org.jboss.tools.vwatch.model.BundleInstance;
import org.jboss.tools.vwatch.model.BundleType;
import org.jboss.tools.vwatch.model.Issue;
import org.jboss.tools.vwatch.service.FileService;
import org.jboss.tools.vwatch.service.MD5Service;
import org.jboss.vwatch.util.PairValidator;

public class MD5Validator extends PairValidator {
	
	Logger log = Logger.getLogger(MD5Validator.class);
	MD5Service md5 = MD5Service.getInstance();

	@Override
	public boolean isValid(Bundle b1, Bundle b2) {
		for (BundleInstance i1 : b1.getInstances()) {
			BundleInstance matching = b1.getInstance(i1);
			if (matching != null) { 
					
				if (!i1.getMd5().equals(matching.getMd5())) {
					log.error("md5 invalid for " + matching.getFullName());					
					return false;
				}
				else {
					log.debug("md5 valid for " + matching.getFullName());
					return true;
				}
			}
			else {	
				log.info("unable to validate md5 for " + matching.getFullName());
				return true;
			}
		}
		
		return true;
		
	}

	@Override
	public void addIssue(Bundle b1, Bundle b2) {
		Issue i = new MD5Issue(b1);
		b2.getIssues().add(i);
	}
}
