package com.hadoop.bigmock.core;

import org.junit.Test;

public class SimpleTests {

	@Test
	public void test() {
		double a = 77;
		double b = 8;
		float p = 0.1f;
		System.out.println((77f/8f) + "== " + (a/b) + " -- " + Math.ceil(a / b));
	}

}
