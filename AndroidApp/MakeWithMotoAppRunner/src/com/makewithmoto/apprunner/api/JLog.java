package com.makewithmoto.apprunner.api;

import org.json.JSONObject;

import android.os.RemoteException;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.makewithmoto.apprunner.MWMActivity;
import com.makewithmoto.apprunner.events.Events.ProjectEvent;
import com.makewithmoto.events.Events.LogEvent;
import com.makewithmoto.utils.ALog;

import de.greenrobot.event.EventBus;

public class JLog  extends JInterface {
	
	public JLog(FragmentActivity mwmActivity) {
		super(mwmActivity);
		//EventBus.getDefault().register(this);
	}

	@JavascriptInterface
	public void log(String msg) {
		//ALog.i(msg);
		try {
			c.get().tryLogToSockets("info", msg);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	public void onEventAsync(LogEvent evt) {
		// Quit the project
		Log.d(TAG, "event");
		//log(evt.getMessage());
	}

}
