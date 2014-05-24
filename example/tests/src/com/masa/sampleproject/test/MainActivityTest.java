package com.masa.sampleproject.test;

import android.test.ActivityInstrumentationTestCase2;
import com.masa.sampleproject.*;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.masa.sampleproject.MainActivityTest \
 * com.masa.sampleproject.tests/android.test.InstrumentationTestRunner
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTest() {
        super(MainActivity.class);
    }
    
    public void testMain(){
    	assertEquals(true, 1 + 1 == 2);
    }

}
