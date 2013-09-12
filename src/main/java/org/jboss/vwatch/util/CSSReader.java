package org.jboss.vwatch.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.jboss.tools.vwatch.service.LogService;

public class CSSReader {

	public static String readCSSFile(String fileName) {
		String style = "";
		try {
			style = readFile(fileName);
		} catch (IOException e) {
			LogService.logAndExit("Unable to read css file " + fileName + "\n" + e.getStackTrace());
		}
		return style;
	}
	
	private static String readFile(String fileName) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		String line = null;
		StringBuilder stringBuilder = new StringBuilder();
		String ls = System.getProperty("line.separator");

		while ((line = reader.readLine()) != null) {
			stringBuilder.append(line);
			stringBuilder.append(ls);
		}

		reader.close();
		return stringBuilder.toString();
	}

	
}
