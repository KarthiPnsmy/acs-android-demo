package com.appcelerator.cloud.demo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;

import com.appcelerator.cloud.sdk.CCRequestMethod;
import com.appcelerator.cloud.sdk.CCResponse;
import com.appcelerator.cloud.sdk.ACSClient;
import com.appcelerator.cloud.sdk.ACSClientError;

public class DemoApplication extends Application {
	// TODO Update your own app_id here
	public static final String APP_ID = "rQP76EmuZydqkpaPZCIdlewc178VuxOX";
	public static final String FACEBOOK_APP_ID = "246624962151491";
	// TODO Update your own oAuth account here
	public static final String APP_CONSUMER_KEY = "lrG5MX2mF1qBxgElbX6To5f3SLl4cumX";
	public static final String APP_CONSUMER_SECRET = "qJIYgAisf6fzP6iU7Lt0cx4S6R2Xvhyf";
	private static ACSClient sdk = null;
	private static DemoSession session = null;

	@Override
	public void onCreate() {
		PreferenceManager.setDefaultValues(this, R.xml.default_values, false);

		// Initialize ACSClient
		initialize(APP_CONSUMER_KEY, APP_CONSUMER_SECRET, getApplicationContext());
	}

	private static void initialize(String appConsumerKey, String appConsumerSecret, Context appContext) {
//		Pass app key to the 'key' argument
		sdk = new ACSClient(APP_ID, appContext);
//		Pass both oauth key and secret
		sdk = new ACSClient(appConsumerKey, appConsumerSecret, appContext);
		
		//Pass both oauth key and secret and use 3-legged oauth
//		sdk = new ACSClient(appConsumerKey, appConsumerSecret, appContext);
		//for authentication/authorization with Authorization Server
//		sdk.useThreeLegged(true);
		
//		Use 3-legged OAuth but without OAuth secret specified - the 'key' argument expects OAuth key
//		sdk = new ACSClient(appConsumerKey, appContext);
//		sdk.useThreeLegged(true);
//		sdk = new ACSClient("iJ0BL5CHUc5sbAojXFgRhfffKWCYkf0u"); // app key
        
		sdk.setDlgCustomizer(new MyDlgCustomizer());
		session = new DemoSession();
	}

	@Override
	public void onTerminate() {
	}

	public static ACSClient getSdk() {
		return sdk;
	}

	public static DemoSession getSession() {
		return session;
	}
}
