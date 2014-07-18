package org.protocoder.services;

import org.protocoder.apprunner.AppRunnerActivity;
import org.protocoder.utils.MLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

	private static final String TAG = "AlarmReceiver";

	private void startComponent(Context c, int message, Intent intent) {

		Intent newIntent = new Intent(c, AppRunnerActivity.class);
		newIntent.putExtra("alarm_message", message);
		newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		newIntent.putExtras(intent);
		c.startActivity(newIntent);

	}

	@Override
	public void onReceive(Context c, Intent intent) {
		MLog.d(TAG, "alarm started");

		try {
			Bundle bundle = intent.getExtras();
			int message = bundle.getInt("alarm_message");
			MLog.d(TAG, "alarma para: " + message);
			startComponent(c, message, intent);

		} catch (Exception e) {
			MLog.d(TAG, "error on the alarm");
			Toast.makeText(c, "There was an error somewhere, but we still received an alarm", Toast.LENGTH_SHORT)
					.show();
			e.printStackTrace();

		}
	}
}