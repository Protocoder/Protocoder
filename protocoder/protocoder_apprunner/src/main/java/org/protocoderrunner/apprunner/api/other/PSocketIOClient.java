package org.protocoderrunner.apprunner.api.other;

import com.codebutler.android_websockets.SocketIOClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.NativeArray;
import org.protocoderrunner.utils.MLog;

import java.net.URI;

/**
 * Created by victormanueldiazbarrales on 15/09/14.
 */
public class PSocketIOClient extends SocketIOClient {
    public PSocketIOClient(URI uri, Handler handler) {
        super(uri, handler);
    }

    @Override
    public void emit(JSONObject jsonMessage) throws JSONException {
        super.emit(jsonMessage);
    }

    public void emit(String message, NativeArray array) {
        try {
            JSONArray jsonArray = new JSONArray(array);
            super.emit(message, new JSONArray().put(jsonArray));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
