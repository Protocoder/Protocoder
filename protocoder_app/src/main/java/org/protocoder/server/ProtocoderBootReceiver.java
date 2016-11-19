package org.protocoder.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.protocoder.MainActivity;
import org.protocoder.gui.settings.NewUserPreferences;
import org.protocoderrunner.base.utils.MLog;

/**
 * Created by biquillo on 13/09/16.
 */
public class ProtocoderBootReceiver extends BroadcastReceiver {

    private static final String TAG = ProtocoderBootReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        if ((boolean) NewUserPreferences.getInstance().get("launch_on_device_boot")) {
            MLog.d(TAG, "launching Protocoder on boot");
            Intent in = new Intent(context, MainActivity.class);
            in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(in);
        }
    }
}
