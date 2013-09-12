package org.jboss.tools.vwatch.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.tools.vwatch.Settings;
import org.jboss.tools.vwatch.model.Installation;
import org.jboss.tools.vwatch.service.LogService;
import org.jboss.vwatch.util.CSSReader;
import org.jboss.vwatch.util.HtmlWriter;

/**
 * Abstract for all Report classes 
 * @author jpeterka
 *
 */
public abstract class Report {
	
	Logger log = Logger.getLogger(Report.class);
	protected HtmlWriter html = new HtmlWriter(); 
	
	
	protected StringBuilder sb;
	String fileName = "default.html";

	protected List<Installation> installations;
	
	public Report() {
		this.sb = new StringBuilder();
	}
	
	public void generateReport() {
		generateHeader();
		generateBody();
		generateFooter();		
		createReportFile();
	}
	
	private void createReportFile() {
		String readCSSFile = CSSReader.readCSSFile("vwstyle.css");
		File file = new File(getFileName());
		String filter = Settings.getFilter();

		try {
			PrintWriter pw = new PrintWriter(file);
			BufferedWriter bw = new BufferedWriter(pw);
			bw.append(sb.toString());
			bw.flush();
		} catch(Exception e) {
			LogService.logAndExit("Unable to generate report file");
		}
		log.warn("Report generated to file:///" + file.getAbsolutePath());
		
	}

	protected void generateHeader() {		
		String style = CSSReader.readCSSFile("vwstyle.css");
		add(html.html());
		add(html.head());	
		sb.append("<style type=\"text/css\">" + style + "</style>");
		sb.append("</head>");
		add(html.title());
		add(html.title().end());
		add(html.body());
	}
	
	protected abstract void generateBody();
	
	protected abstract String getFileName();	

	
	public String getContent() {
		return sb.toString();
	}

	protected void generateFooter() {
		add(html.body().end());
		add(html.html().end());
	}	
	protected void add(Tag str) {
		sb.append(str + "\n");
	}
	protected void add(String str) {
		sb.append(str + "\n");
	}

	protected void addAndBr(String str) {
		sb.append(str + "\n");
		sb.append(html.newLine());
	}


}
