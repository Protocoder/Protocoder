/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
 *
 * Copyright (C) 2014 Victor Diaz
 * Copyright (C) 2013 Motorola Mobility LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions: 
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 * 
 */

package org.protocoderrunner.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

/*
 * http://shreymalhotra.me/blog/tutorial/receive-sms-using-android-broadcastreceiver-inside-an-activity/
 * 
 */

public class SmsReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {

		Bundle extras = intent.getExtras();
		if (extras == null)
			return;

		// To display a Toast whenever there is an SMS.
		// Toast.makeText(mainScriptContext,"Recieved",Toast.LENGTH_LONG).show();

		Object[] pdus = (Object[]) extras.get("pdus");
		for (int i = 0; i < pdus.length; i++) {
			SmsMessage SMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
			String sender = SMessage.getOriginatingAddress();
			String body = SMessage.getMessageBody().toString();

			// A custom Intent that will used as another Broadcast
			Intent in = new Intent("SmsMessage.intent.MAIN").putExtra("get_msg", sender + ":" + body);

			// You can place your check conditions here(on the SMS or the
			// sender)
			// and then send another broadcast
			context.sendBroadcast(in);

			// This is used to abort the broadcast and can be used to silently
			// process incoming message and prevent it from further being
			// broadcasted. Avoid this, as this is not the way to program an
			// app.
			// this.abortBroadcast();
		}
	}
}
