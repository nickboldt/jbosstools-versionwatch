package org.jboss.tools.vwatch.report;

public class ProductReport extends Report {

	/** 
	 * Represents selected product (last one by default) report listing all issues
	 */
	public void generateReport() {
		generateHeader();
		generateBody();
		generateFooter();
	}

	protected void generateFooter() {
		super.generateHeader();

		
	}

	private void generateBody() {
		sb.append("hello");
		
	}

	protected void generateHeader() {
		
		super.generateFooter();
	}
}
