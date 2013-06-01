package org.jboss.tools.vwatch.service;

import org.apache.log4j.Logger;

/**
 * Log service providing some log related methods
 * @author jpeterka
 *
 */
public class LogService {

	Logger log = Logger.getLogger(LogService.class);
	
	/**
	 * Logs the error and exits the application after some fatal error
	 * @param message error message string
	 */
	public void logAndExit(String message) {
		log.error(message);
		System.exit(-1);
	}
	
	
}
