package org.jboss.tools.vwatch;

import static org.junit.Assert.fail;

import org.junit.Test;

/*
 * Version Watch test
 */
public class VersionTest {

	@Test
	public void test() {
		try {
			VWatch.run();
		}
		catch (Exception e) {
			fail("Exception" + e.getMessage());
		}
	}

}
