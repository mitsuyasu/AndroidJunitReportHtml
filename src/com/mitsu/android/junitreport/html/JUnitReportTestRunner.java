package com.mitsu.android.junitreport.html;
/*
 * Copyright (C) 2013 Masaaki Mitsuyasu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import android.os.Bundle;
import android.test.AndroidTestRunner;
import android.test.InstrumentationTestRunner;
import android.util.Log;

public class JUnitReportTestRunner extends InstrumentationTestRunner {
    
    private static final String ARG_REPORT_FILE = "reportFile";
    private static final String ARG_REPORT_DIR = "reportDir";
    private static final String DEFAULT_SINGLE_REPORT_FILE = "report-%s.xml";
   
    private static final String LOG_TAG = JUnitReportTestRunner.class.getSimpleName();
    
    private JUnitReportViewListener mListener;
    private String mReportFile;
	private String mReportDir;

    @Override
    public void onCreate(Bundle arguments) {
        if (arguments != null) {
            mReportFile = arguments.getString(ARG_REPORT_FILE);
            mReportDir = arguments.getString(ARG_REPORT_DIR);
        } else {
            Log.i(LOG_TAG, "No arguments provided");
        }

        if (mReportFile == null) {
            mReportFile =  DEFAULT_SINGLE_REPORT_FILE;
        }
        super.onCreate(arguments);
    }

    @Override
    protected AndroidTestRunner getAndroidTestRunner() {
        AndroidTestRunner runner = new AndroidTestRunner();
        mListener = new JUnitReportViewListener(getTargetContext(), mReportDir);
        runner.addTestListener(mListener);
        return runner;
    }

    @Override
    public void finish(int resultCode, Bundle results) {
        if (mListener != null) {
            mListener.close();
        }

        super.finish(resultCode, results);
    }
}
