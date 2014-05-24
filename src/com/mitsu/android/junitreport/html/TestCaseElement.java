package com.mitsu.android.junitreport.html;

public class TestCaseElement {
    
	public TestCaseElement(String className, String methodName)
    {
        this.className = className;
        this.name = methodName;
    }

    public String getName()
    {
        return name;
    }

    public String getClassName()
    {
        return className;
    }

    public void setTime(long time){
    	this.time = time;
    }
    
    public long getTime()
    {
        return time;
    }

    public double getTimeInSecond()
    {
        return (double)time / 1000D;
    }

    public void setTestResult(int testResult){
    	this.testResult = testResult;
    }
    
    public boolean isSuccess()
    {
        return testResult == 1;
    }

    public boolean isError()
    {
        return testResult == 2;
    }

    public boolean isFailure()
    {
        return testResult == 3;
    }

    public void setMessae(String message){
    	this.message = message;
    }
    public String getMessage()
    {
    	return message;
    }

    public void setType(String type){
    	this.type = type;
    }
    
    public String getType()
    {
    	return type;
    }

    public void setTrace(String trace) {
    	this.trace = trace;
    }
    
    public String getTrace()
    {
        return trace;
    }
    
    public TestCaseElement clone(){
    	TestCaseElement testCase = new TestCaseElement(className, name);
    	testCase.setMessae(message);
    	testCase.setTestResult(testResult);
    	testCase.setTime(time);
    	testCase.setTrace(trace);
    	testCase.setType(type);
    	return testCase;
    }

    public static final int TEST_SUCCESS = 1;
    public static final int TEST_ERROR = 2;
    public static final int TEST_FAILURE = 3;
    private String className;
    private String name;
    private int testResult = TEST_SUCCESS;
    private String trace;
    private long time;
    private String type;
    private String message;
}
