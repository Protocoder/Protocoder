package org.protocoderrunner.apprunner.api.other;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;

/**
 * Created by victormanueldiazbarrales on 03/08/14.
 */
public class ProtocoderNativeArray extends NativeArray {

    public ProtocoderNativeArray(int length) {
        super(length);
    }

    public void addPE(int i, String name) {
        put(i, this, name);
    }

}
