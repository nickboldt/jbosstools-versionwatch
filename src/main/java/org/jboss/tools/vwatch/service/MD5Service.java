package org.jboss.tools.vwatch.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Service {
	private MessageDigest md = null;
	private static MD5Service instance = null;

	private MD5Service() throws NoSuchAlgorithmException {
		md = MessageDigest.getInstance("MD5");
	}

	public static MD5Service getInstance() {
		if (instance == null) {
			try {
				instance = new MD5Service();
			} catch (NoSuchAlgorithmException e) {
				System.out.println("error");
				throw new RuntimeException(e.getMessage());
			}
		}
		return instance;
	}

	public String getMD5(File f) {
		String md5 = "";
		try {
			InputStream is = new FileInputStream(f.getAbsolutePath());
			try {
				is = new DigestInputStream(is, md);
				while (is.read() != -1) ;
					// do nothing
			} finally {
				is.close();
			}
			byte[] digest = md.digest();
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < digest.length; i++) {
				sb.append(Integer.toString((digest[i] & 0xff) + 0x100, 16)
						.substring(1));
			}
			md5 = sb.toString();

		} catch (Exception e) {
			e.printStackTrace();
		}		
		return md5;
	}
}
