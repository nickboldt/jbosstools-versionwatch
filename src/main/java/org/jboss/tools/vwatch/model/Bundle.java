package org.jboss.tools.vwatch.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Bundle class, used for storing bundle related data
 * 
 * @author jpeterka
 * 
 */
public class Bundle {
	String name;
	List<BundleInstance> instances = new ArrayList<BundleInstance>();
	List<String> versions = new ArrayList<String>();
	//List<Issue> issues = new ArrayList<Issue>();


	public List<Issue> getIssues() {
		return instances.get(0).getIssues();
	}

/*
	public void setIssues(List<Issue> issues) {
		this.issues = issues;
	}
*/
	
	// TODO: make this smarter so duplicate versions aren't considered multiple hits
	public String getVersions() {
		String s = "";
		for (int i = 0; i < getInstances().size(); i++) {
			String thisVersion = getInstances().get(i).getVersion().toString();
			if (!versions.contains(thisVersion)) 
			{
				versions.add(thisVersion);
				s += getInstances().get(i).getVersion();
				if (i < getInstances().size() - 1) {
					s +="<br/>";
				}
			}
		}
		if (versions.size()>1) {
			System.out.println("Multiple versions of " + name + " found: " + s);
			
		}
		return s;
	}

	public void setVersions(List<String> versions) {
		this.versions = versions;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<BundleInstance> getInstances() {
		return instances;
	}

	public void setInstances(List<BundleInstance> instances) {
		this.instances = instances;
	}
	
	// only report on multiple instances if there are multiple instances AND versions
	public boolean hasMultipleInstances() {
		if (instances.size() > 1 && versions.size() > 1) return true;
		return false;
	}

	public Version getVersion() {
		return instances.get(0).getVersion();
	}
	
	public String getAbsolutePath() {
		return (instances.get(0).getAbsolutePath());
	}
	
	public String getMd5() {
		return (instances.get(0).getMd5());
	}

	public String getFullName() {
		return (instances.get(0).getFullName());
	}

	public void setMd5(String md5) {
		instances.get(0).setMd5(md5);
	}
	public int getMaxSeverity() {
		return (instances.get(0).getMaxSeverity());
	}
	public String getErrorsAndWarnings() {
		return (instances.get(0).getErrorsAndWarnings());
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
		if (obj instanceof Bundle) {
			Bundle bundle = (Bundle)obj;
			if (bundle.getName().equals(this.getName())) {
				return true;
			}
		}
		return false;
	}	
}
