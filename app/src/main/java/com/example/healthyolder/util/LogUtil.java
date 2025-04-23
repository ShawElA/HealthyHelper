package com.example.healthyolder.util;

import android.util.Log;

public class LogUtil {
	public static final int VERBOSE = 1;
	public static final int DEBUG = 2;
	public static final int INFO = 3;
	public static final int WARN = 4;
	public static final int ERROR = 5;
	public static final int NOTHING = 3;
	public static final int LEVEL = NOTHING;

	public static void v(String tag, Object msg) {
		if (LEVEL <= VERBOSE) {
			if(msg!=null)
			Log.v(tag, msg.toString());
		}
	}

	public static void d(String tag, Object msg) {
		if (LEVEL <= DEBUG) {
			if(msg!=null)
			Log.d(tag, msg.toString());
		}
	}

	public static void i(String tag, Object msg) {
		if (LEVEL <= INFO) {
			if(msg!=null) {
				Log.i(tag, msg.toString());
			}else{
				Log.i(tag, "null");
			}
		}
	}

	public static void w(String tag, Object msg) {
		if (LEVEL <= WARN) {
			if(msg!=null)
			Log.w(tag, msg.toString());
		}
	}

	public static void e(String tag, Object msg) {
		if (LEVEL <= ERROR) {
			if(msg!=null)
			Log.e(tag, msg.toString());
		}
	}
}
