package org.jboss.tools.vwatch.model;

/**
 * Issue is storage for recording evaluating issue
 * 
 * @author jpeterka
 * 
 */
public class Issue {

	int severity = 0; // 0-3: note, caution, warning, error
	String message;

	public String getSeverityStr() {
		if (severity == 0) {
			return "Note";
		} else if (severity == 1) {
			return "Caution";
		} else if (severity == 2) {
			return "Warning";
		} else if (severity == 3) {
			return ("Error");
		} else {
			return ("Unknown");
		}
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setSeverity(int severity) {
		this.severity = severity;
	}

	public int getSeverity() {
		return severity;
	}

}
