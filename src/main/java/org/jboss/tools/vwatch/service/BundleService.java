package org.jboss.tools.vwatch.service;

import java.util.Set;

import org.jboss.tools.vwatch.model.Bundle;

/**
 * BundleService provides Bundle related operations
 * @author jpeterka
 *
 */
public class BundleService {

	/**
	 * Returns Bundle from the list of bundles
	 * @param bundles list of bundles
	 * @param name bundle name to be find
	 * @return null if not found or Bundle instance if found
	 */
	public Bundle getBundleFromList(Set<Bundle> bundles, String name) {
		
		Bundle ret = null;
		for (Bundle bundle : bundles) {
			if (bundle.getName().equals(name)) {
				ret = bundle;
				break;
			}
		}
		return ret;
	}
}
