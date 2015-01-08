package org.jboss.tools.vwatch;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.jboss.tools.vwatch.counter.IssueCounter;
import org.jboss.tools.vwatch.issue.FolderAndJarIssue;
import org.jboss.tools.vwatch.issue.MD5Issue;
import org.jboss.tools.vwatch.issue.MultipleVersionIssue;
import org.jboss.tools.vwatch.issue.VersionDecreasedIssue;
import org.junit.BeforeClass;
import org.junit.Test;

/*
 * Version Watch test
 */
public class VersionTest {


	@BeforeClass
	public static void run() {
		try {
			VWatch.run();
		}
		catch (Exception e) {
			fail("Exception: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Test
	public void testFolderAndJarIssues() {
		int count = IssueCounter.getInstance().getCount(FolderAndJarIssue.class);
		assertTrue("There are " + count + " FolderAndJar issues", count == 0);
	}

	@Test
	public void testMD5Issues() {
		int count = IssueCounter.getInstance().getCount(MD5Issue.class);
		assertTrue("There are " + count + " MD5 issues", count == 0);
	}

	@Test
	public void testMultipleVersionsIssues() {
		int count = IssueCounter.getInstance().getCount(MultipleVersionIssue.class);
		assertTrue("There are " + count + " MultipleVersions issues", count == 0);
	}

	@Test
	public void versionDecreasedIssues() {
		int count = IssueCounter.getInstance().getCount(VersionDecreasedIssue.class);
		assertTrue("There are " + count + " Version decreased issues", count == 0);
	}

}
