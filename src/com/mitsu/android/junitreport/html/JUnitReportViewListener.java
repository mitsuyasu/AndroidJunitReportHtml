package com.mitsu.android.junitreport.html;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Hashtable;
import java.util.Set;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestListener;
import android.content.Context;
import android.util.Log;


public class JUnitReportViewListener implements TestListener {

	private static final String LOG_TAG = JUnitReportViewListener.class.getSimpleName();

    public static final String TOKEN_SUITE = "__suite__";
    public static final String TOKEN_EXTERNAL = "__external__";

    // With thanks to org.apache.tools.ant.taskdefs.optional.junit.JUnitTestRunner.
    // Trimmed some entries, added others for Android.
    private static final String[] DEFAULT_TRACE_FILTERS = new String[] {
            "junit.framework.TestCase", "junit.framework.TestResult",
            "junit.framework.TestSuite",
            "junit.framework.Assert.", // don't filter AssertionFailure
            "java.lang.reflect.Method.invoke(", "sun.reflect.",
            // JUnit 4 support:
            "org.junit.", "junit.framework.JUnit4TestAdapter", " more",
            // Added for Android
            "android.test.", "android.app.Instrumentation",
            "java.lang.reflect.Method.invokeNative",
    };
    private boolean mTimeAlreadyWritten = false;
    private long mTestStartTime;
    private TestCaseElement mTestCase;
	private Hashtable<String, TestSuiteElement> mTestSuiteTable = new Hashtable<String, TestSuiteElement>();
	private Context mTargetContext;
	private String mReportDir;
    
    public JUnitReportViewListener(Context targetContext, String reportDir){
    	mTargetContext = targetContext;
    	mReportDir = reportDir;
    }
    
	@Override
	public void startTest(Test test) {
		try {
            if (test instanceof TestCase) {
                TestCase testCase = (TestCase) test;
                mTestCase = new TestCaseElement(testCase.getClass().getName(), testCase.getName());
                mTimeAlreadyWritten = false;
                mTestStartTime = System.currentTimeMillis();
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, safeMessage(e));
        }	
	}

	@Override
	public void addError(Test test, Throwable error) {
		mTestCase.setTestResult(TestCaseElement.TEST_ERROR);
		addProblem(test, error);
	}

	@Override
	public void addFailure(Test test, AssertionFailedError error) {
		mTestCase.setTestResult(TestCaseElement.TEST_FAILURE);
		addProblem(test, error);
	}
	
	private void addProblem(Test test, Throwable error) {
        try {
            mTestCase.setMessae(safeMessage(error));
            mTestCase.setType(error.getClass().getName());
            StringWriter w = new StringWriter();
            error.printStackTrace(new FilteringWriter(w));
            mTestCase.setTrace(w.toString());
            endTestCase(test);
        } catch (Exception e) {
            Log.e(LOG_TAG, safeMessage(e));
        }
    }

	private void recordTestTime() {
        if (!mTimeAlreadyWritten) {
            mTimeAlreadyWritten = true;
            mTestCase.setTime(System.currentTimeMillis() - mTestStartTime);
        }
    }
	
	@Override
	public void endTest(Test test) {
		endTestCase(test);
	}
	
	public void endTestCase(Test test){
		TestCase testCase = (TestCase) test;
		String id = testCase.getClass().getName();
		if(mTestCase != null){
			recordTestTime();
			TestSuiteElement suite = (TestSuiteElement)mTestSuiteTable.get(id);
	        if(suite == null)
	        {
	            suite = new TestSuiteElement(id, testCase.getClass().getName());
	            mTestSuiteTable.put(id, suite);
	        }
			suite.addTestCase(mTestCase.clone());
		}
	}

	public void close(){
		Set<String> keys = mTestSuiteTable.keySet();
		JUnitXmlCreater xmlCreater = new JUnitXmlCreater(mTargetContext, mReportDir);
		for(String key: keys){
			TestSuiteElement testSuite = mTestSuiteTable.get(key);
			xmlCreater.create(testSuite);
		}
	}       
    
	private String safeMessage(Throwable error) {
        String message = error.getMessage();
        return error.getClass().getName() + ": " + (message == null ? "<null>" : message);
    }

    private static class FilteringWriter extends PrintWriter {
        public FilteringWriter(Writer out) {
            super(out);
        }

        @Override
        public void println(String s) {
            for (String filtered : DEFAULT_TRACE_FILTERS) {
                if (s.contains(filtered)) {
                    return;
                }
            }
            super.println(s);
        }
    }

}
