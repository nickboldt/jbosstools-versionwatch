package org.jboss.vwatch.util;

import org.jboss.tools.vwatch.report.Tag;

public class HtmlWriter {

	public String newLine() {
		return "<br/>";
	}
	
	public Tag html() {
		return new Tag("<html>");
	}
	
	public Tag body() {
		return new Tag("<body>");
	}

	public Tag table() {
		return new Tag("<table>");
	}

	public Tag head() {
		return new Tag("<head>");
	}

	public Tag title() {
		return new Tag("<title>");
	}

	public Tag h1() {
		return new Tag("<h1>");
	}

	public Tag h2() {
		return new Tag("<h2>");
	}

	public Tag h3() {
		return new Tag("<h3>");
	}

}
