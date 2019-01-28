package com.tvbus.engine;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class TVService extends Service {
	final static String TAG = "TVBusService";

	@Override
	public void onCreate() {
		super.onCreate();

		Log.e(TAG, "onCreate");

		TVServer server = new TVServer();
		Thread thread = new Thread(server);
		thread.setName("tvcore");
		thread.start();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return START_NOT_STICKY;
	}
	
	@Override
	public void onDestroy() {
		TVCore.getInstance().quit();

		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	
	private class TVServer implements Runnable {
		private static final String TAG = "TVBusServer";
		TVCore tvcore = TVCore.getInstance();

		@Override
		public void run() {
		    int retv = tvcore.init(getApplicationContext());

		    Log.e("HAHA", "tvcore init " + retv);
		    if(retv == 0) {
				tvcore.run();
		    }
		}
	}

}
