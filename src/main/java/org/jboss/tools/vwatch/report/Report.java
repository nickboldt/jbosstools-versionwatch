package org.jboss.tools.vwatch.report;

import java.util.List;

import org.jboss.tools.vwatch.model.Installation;

/**
 * Abstract for all Report classes 
 * @author jpeterka
 *
 */
public abstract class Report {
	
	protected StringBuilder sb;

	protected List<Installation> installations;
	
	public Report() {
		this.sb = new StringBuilder();
	}
	
	public abstract void generateReport();
	
	protected void generateHeader() {
		sb.append("<html>");
		sb.append("<head>");
		sb.append("<title>");
		sb.append("</title>");
		sb.append("<body>");
		
	}
	
	public String getContent() {
		return sb.toString();
	}

	protected void generateFooter() {
		sb.append("</body>");
		sb.append("</html>");
	}	
	
}
