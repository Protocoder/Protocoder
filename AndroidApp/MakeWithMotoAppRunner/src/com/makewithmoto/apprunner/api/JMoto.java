package com.makewithmoto.apprunner.api;

import com.makewithmoto.apprunner.events.Events.LogEvent;

import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class JMoto  extends JInterface {
	
	public JMoto(FragmentActivity mwmActivity) {
		super(mwmActivity);
		//EventBus.getDefault().register(this);
	}

	@JavascriptInterface
	public void ok(final String msg) {
		synchronized (c.get().callbackReply) {
			c.get().callbackReply = true;
		}
	}
	
	public void finish(final String msg) {
		// Quit the project
		Log.d(TAG, "Finishing app...");
		synchronized (c.get().callbackReply) {
			c.get().callbackReply = false;
		}
	}

}
