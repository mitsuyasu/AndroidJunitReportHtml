package com.mitsu.android.junitreport.html;

import java.util.*;

public class TestSuiteElement
{

    public TestSuiteElement(String id, String name)
    {
        this.id = id;
        this.name = name;
        testCases = new ArrayList<TestCaseElement>();
        timeStamp = new Date();
    }

    public void addTestCase(TestCaseElement testCase)
    {
        testCases.add(testCase);
        time += testCase.getTime();
        if(testCase.isError())
            errors++;
        else
        if(testCase.isFailure())
            failures++;
    }

    public String getId()
    {
        return id;
    }

    public double getTimeInSecond()
    {
        return (double)time / 1000D;
    }

    public String getName()
    {
        return name;
    }

    public List<TestCaseElement> getTestCases()
    {
        return testCases;
    }

    public long getTests()
    {
        return (long)testCases.size();
    }

    public long getFailures()
    {
        return failures;
    }

    public long getErrors()
    {
        return errors;
    }

    public long getTime()
    {
        return time;
    }

    public Date getTimeStamp()
    {
        return timeStamp;
    }

    public String getPackage()
    {
        int index = name.lastIndexOf('.');
        if(index == -1)
            return "";
        else
            return name.substring(0, index);
    }

    private String id;
    private String name;
    private List<TestCaseElement> testCases;
    private long failures;
    private long errors;
    private long time;
    private Date timeStamp;
}
