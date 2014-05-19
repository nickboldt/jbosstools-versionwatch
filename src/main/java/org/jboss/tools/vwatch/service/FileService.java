package org.jboss.tools.vwatch.service;

import java.io.File;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.util.Zip4jConstants;

public class FileService {
		
	private static FileService instance = null;;
	
	private FileService() {};
	
	public static  FileService getInstance() {
		if (instance == null) instance = new FileService();
		return instance;
		
	}
	
	public long getFolderSize(File dir) {
		long size = 0;
		for (File file : dir.listFiles()) {
			if (file.isFile()) {
				System.out.println(file.getName() + " " + file.length());
				size += file.length();
			} else
				size += getFolderSize(file);
		}
		return size;
	}
	
	
	public void zipFolder(File sourceFolder,File destinationFile) {
		ZipFile bundleJar = null;
		try {
			bundleJar = new ZipFile(destinationFile);
			ZipParameters parameters = new ZipParameters();
			parameters.setCompressionMethod(Zip4jConstants.COMP_DEFLATE);
			parameters
					.setCompressionLevel(Zip4jConstants.DEFLATE_LEVEL_NORMAL);
			bundleJar.createZipFileFromFolder(sourceFolder,
					parameters, false, 10485760);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

