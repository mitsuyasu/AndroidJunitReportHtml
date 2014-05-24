package com.masa.sampleproject.test;

import junit.framework.TestCase;

public class SampleProjectTest extends TestCase {

	public void testTest(){
		assertEquals(2, 1+1);
	}
	
	public void testTestError(){
		assertEquals(1, 1+1);
	}
	
	public void testTest2Error(){
		assertEquals(1, 1+1);
	}
}
