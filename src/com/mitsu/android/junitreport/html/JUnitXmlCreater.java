package com.mitsu.android.junitreport.html;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

public class JUnitXmlCreater {
	private static final String TAG = JUnitXmlCreater.class.getSimpleName();
	public static final String TOKEN_SUITE = "__suite__";
	public static final String TOKEN_EXTERNAL = "__external__";
    private static final String ENCODING_UTF_8 = "utf-8";
    private static final String TAG_SUITE = "testsuite";
    private static final String TAG_CASE = "testcase";
    private static final String TAG_ERROR = "error";
    private static final String TAG_FAILURE = "failure";
    private static final String ATTRIBUTE_ID = "id";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_PACKAGE = "package";
    private static final String ATTRIBUTE_TESTS = "tests";
    private static final String ATTRIBUTE_ERRORS = "errors";
    private static final String ATTRIBUTE_FAILURES = "failures";
    private static final String ATTRIBUTE_TIMESTAMP = "timestamp";
    private static final String ATTRIBUTE_HOSTNAME = "hostname";
    private static final String ATTRIBUTE_CLASS = "classname";
    private static final String ATTRIBUTE_TYPE = "type";
    private static final String ATTRIBUTE_MESSAGE = "message";
    private static final String ATTRIBUTE_TIME = "time";
    public static String FILE_NAME_FORMAT = "report-%s.xml";
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
    private static final DecimalFormat DF = new DecimalFormat("0.000");
	private String mReportDir;
	private Context mTargetContext;
	private TestSuiteElement mTestSuite;
	private XmlSerializer mSerializer;
	
	public JUnitXmlCreater(Context targetContext, String reportDir){
		mTargetContext = targetContext;
		mReportDir = reportDir;
	}
	
	public void create(TestSuiteElement testSuite){
		mTestSuite = testSuite;
		OutputStream output = null;
		try{
			String fileName = String.format(FILE_NAME_FORMAT, mTestSuite.getId());
			output = openOutputStream(fileName);
	        mSerializer = Xml.newSerializer();
	        mSerializer.setOutput(output, ENCODING_UTF_8);
	        mSerializer.startDocument(ENCODING_UTF_8, true);
        
	        mSerializer.startTag("", TAG_SUITE);
            mSerializer.attribute("", ATTRIBUTE_ID, "" + mTestSuite.getId());
            mSerializer.attribute("", ATTRIBUTE_NAME, mTestSuite.getName());
            mSerializer.attribute("", ATTRIBUTE_PACKAGE, mTestSuite.getPackage());
            mSerializer.attribute("", ATTRIBUTE_TESTS, String.valueOf(mTestSuite.getTests()));
            mSerializer.attribute("", ATTRIBUTE_ERRORS, String.valueOf(mTestSuite.getErrors()));
            mSerializer.attribute("", ATTRIBUTE_FAILURES, String.valueOf(mTestSuite.getFailures()));
            mSerializer.attribute("", ATTRIBUTE_TIME, DF.format(mTestSuite.getTimeInSecond()));
            mSerializer.attribute("", ATTRIBUTE_TIMESTAMP, SDF.format(mTestSuite.getTimeStamp()));
            mSerializer.attribute("", ATTRIBUTE_HOSTNAME, System.getProperty("user.name", "unknown"));
            
            createTestCase(mTestSuite.getTestCases());
            
            mSerializer.endTag("", TAG_SUITE);

	        mSerializer.endDocument();
	        mSerializer.flush();
	        mSerializer = null;
		}catch(IOException e){
			Log.e(TAG, safeMessage(e));
		}finally{
			if(output != null){
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void createTestCase(List<TestCaseElement> list) throws IllegalArgumentException, IllegalStateException, IOException{
		for(TestCaseElement testCase: list){
			mSerializer.startTag("", TAG_CASE);
			mSerializer.attribute("", ATTRIBUTE_CLASS, testCase.getClassName());
            mSerializer.attribute("", ATTRIBUTE_NAME, testCase.getName());
            mSerializer.attribute("", ATTRIBUTE_TIME, DF.format(testCase.getTimeInSecond()));
			if(!testCase.isSuccess()){
				appendTestErrorChild(testCase);
			}
			mSerializer.endTag("", TAG_CASE);
			mSerializer.flush();
		}
	}
	
	private void appendTestErrorChild(TestCaseElement testCase) throws IllegalArgumentException, IllegalStateException, IOException{
		if(testCase.isSuccess())
            return;
		String tag = "";
        if(testCase.isError()){
        	tag = TAG_ERROR;
        }
        else if(testCase.isFailure()){
        	tag = TAG_FAILURE;
        }
        else{
        	
        }
        mSerializer.startTag("", tag);
        mSerializer.attribute("", ATTRIBUTE_TYPE, testCase.getType());
        String message = testCase.getMessage();
        if(message != null && !message.equals("")){
        	mSerializer.attribute("", ATTRIBUTE_MESSAGE, testCase.getMessage());
        }
        mSerializer.text(testCase.getTrace());
        mSerializer.endTag("", tag);
        mSerializer.flush();
	}
	
	private FileOutputStream openOutputStream(String fileName) throws IOException {
        if (mReportDir == null) {
            return mTargetContext.openFileOutput(fileName, Context.MODE_WORLD_READABLE);
        } else {
            if (mReportDir.contains(TOKEN_EXTERNAL)) {
                File externalDir = getExternalStorageDirectory(mTargetContext);
                if (externalDir == null) {
                    throw new IOException("Cannot access external storage");
                }
                String externalPath = externalDir.getAbsolutePath();
                if (externalPath.endsWith("/")) {
                    externalPath = externalPath.substring(0, externalPath.length() - 1);
                }
                mReportDir = mReportDir.replace(TOKEN_EXTERNAL, externalPath);
            }
            ensureDirectoryExists(mReportDir);
            File outputFile = new File(mReportDir, fileName);
            return new FileOutputStream(outputFile);
        }
    }

    private void ensureDirectoryExists(String path) throws IOException {
        File dir = new File(path);
        if (!dir.isDirectory() && !dir.mkdirs()) {
            throw new IOException();
        }
    }
    
    public File getExternalStorageDirectory(Context context){
	    final File externalRoot = Environment.getExternalStorageDirectory();
	    if (externalRoot == null) {
	        return null;
	    }
	
	    final String packageName = context.getApplicationContext().getPackageName();
	    return new File(externalRoot, "Android/data/" + packageName + "/files");
    }
    
    private String safeMessage(Throwable error) {
        String message = error.getMessage();
        return error.getClass().getName() + ": " + (message == null ? "<null>" : message);
    }

}
