package org.jboss.tools.vwatch.service;

/**
 * Simple stopwatch class used to evaluate processing time
 * @author jpeterka
 *
 */
public class StopWatch {

	private static long startTime = 0;
	private static long watchTime = 0;
	private static boolean started = false;
	
	public static void start() {
		started = true;
		startTime = System.currentTimeMillis();
	}
	public static long stop() {
		if (started) {
			watchTime = System.currentTimeMillis() - startTime;
			started = false;
		}
		return watchTime;
	}
	public static void reset() {
		if (!started) watchTime = 0;
	}
	
	public static long getTime() {
		return watchTime;
	}
}
