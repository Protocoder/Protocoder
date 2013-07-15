package com.makewithmoto.apprunner.hardware;

import ioio.lib.api.exception.ConnectionLostException;
import ioio.lib.util.BaseIOIOLooper;
import ioio.lib.util.IOIOLooper;
import ioio.lib.util.android.IOIOService;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class IOIOBoardService extends IOIOService {
    public static final int SET_CALLBACK = 1;
	protected static final String TAG = "IOIOBoardService";
	private HardwareCallback callback_;
	protected Boolean abort_ = false;
    // Binder given to clients
    private final IBinder mBinder = new IOIOServiceBinder();
    
    public class IOIOServiceBinder extends Binder {
    	IOIOBoardService getService() {return IOIOBoardService.this;}
    }

	@Override
	protected IOIOLooper createIOIOLooper() {
		Log.d(TAG, "createIOIOLooper");
		return new BaseIOIOLooper() {
			@Override
			protected void setup() throws ConnectionLostException, InterruptedException {
				Log.d(TAG, "Setup in IOIOLooper");
				callback_.onConnect(ioio_);
				callback_.setup();
				//abort_ = (resp != null && resp != true);
			}

			@Override
			public void loop() throws ConnectionLostException, InterruptedException {
				if (abort_) {
					this.disconnected();
				} else {
					callback_.loop();
					//abort_ = (resp != null && resp != true);
					Thread.sleep(100);
				}
			}
			
			@Override
			public void disconnected() {
				super.disconnected();
				Log.d("IOIOBoardService", "-----> Disconnecting <-----");
				ioio_.disconnect();
			}
		};
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		Log.d(TAG, "onSTART");
		super.onStart(intent, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}

	public void setCallback(HardwareCallback cb) {
		Log.d(TAG, "setCallback");
		callback_ = cb;
	}
	
	public void start(Intent in) {
		Log.d(TAG, "START WITH INTENT");
		startService(in);
	}
}
