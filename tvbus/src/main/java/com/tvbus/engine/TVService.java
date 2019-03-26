package com.tvbus.engine;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TVService extends Service {
	final static String TAG = "TVBusService";

	public static boolean RUN = false;

	@Override
	public void onCreate() {
		super.onCreate();

		Log.e(TAG, "onCreate");

		TVServer server = new TVServer();
		Thread thread = new Thread(server);
		thread.setName("tvcore");
		thread.start();

		RUN = true;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(TAG, "onStartCommand startId="+startId);

		return START_NOT_STICKY;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(TAG, "onDestroy");

		TVCore.getInstance().quit();
		RUN = false;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	
	private class TVServer implements Runnable {
		TVCore tvcore = TVCore.getInstance();

		@Override
		public void run() {
			int retv = -1;
			try {

//				String pkg = getApplicationContext().getPackageName();
//				Class ContextImpl = Class.forName("android.app.ContextImpl");
//				java.lang.reflect.Field mPackageInfo = ContextImpl.getDeclaredField("mPackageInfo");
//				mPackageInfo.setAccessible(true);
//
//				Object loadedApk = mPackageInfo.get(getBaseContext());
//
//				Class LoadedApk = Class.forName("android.app.LoadedApk");
//				java.lang.reflect.Field mPackageName = LoadedApk.getDeclaredField("mPackageName");
//				mPackageName.setAccessible(true);
//				mPackageName.set(loadedApk, "io.binstream.github.demo");
//				Log.e("HAHA", getApplicationContext().getPackageName());
//
//				getApplicationContext().getPackageManager().getPackageInfo(getApplication().getPackageName(), 0);

				retv = tvcore.init(getApplicationContext().createPackageContext("io.binstream.github.demo", Context.CONTEXT_IGNORE_SECURITY | Context.CONTEXT_INCLUDE_CODE));
//				retv = tvcore.init(getApplicationContext());

//				mPackageName.set(loadedApk, pkg);
//				Log.e("HAHA", getApplicationContext().getPackageName());

				if(retv == 0) {
					tvcore.run();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
