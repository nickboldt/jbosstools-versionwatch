package org.jboss.tools.vwatch.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.tools.vwatch.Settings;
import org.jboss.tools.vwatch.model.Installation;
import org.jboss.tools.vwatch.service.LogService;
import org.jboss.tools.vwatch.service.ReportService;
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
		File file = new File(getFileName());

		try {
			PrintWriter pw = new PrintWriter(file);
			BufferedWriter bw = new BufferedWriter(pw);
			bw.append(sb.toString());
			bw.flush();
			bw.close();
		} catch(Exception e) {
			LogService.logAndExit("Unable to generate report file");
		}
		log.warn("Report generated to file:///" + file.getAbsolutePath());
		
	}

	protected abstract void generateHeader();
	
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
