package org.protocoderrunner.apprunner.api.other;

import com.codebutler.android_websockets.SocketIOClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.mozilla.javascript.NativeArray;
import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.ProtocoderScript;

import java.net.URI;

/**
 * Created by victormanueldiazbarrales on 15/09/14.
 */
public class PSocketIOClient extends SocketIOClient {
    public PSocketIOClient(URI uri, Handler handler) {
        super(uri, handler);
    }

    @Override
    @ProtocoderScript
    @APIMethod(description = "Sends a JSONObject to the destination", example = "")
    @APIParam(params = { "jsonObject" })
    public void emit(JSONObject jsonMessage) throws JSONException {
        super.emit(jsonMessage);
    }

    @ProtocoderScript
    @APIMethod(description = "Sends an array to the destination", example = "")
    @APIParam(params = { "message", "array" })
    public void emit(String message, NativeArray array) {
        try {
            JSONArray jsonArray = new JSONArray(array);
            super.emit(message, new JSONArray().put(jsonArray));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
