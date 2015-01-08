package org.protocoderrunner.apprunner.api.other;

import org.mozilla.javascript.NativeObject;

/**
 * Created by victormanueldiazbarrales on 03/08/14.
 */
public class ProtocoderNativeObject extends NativeObject {

    public ProtocoderNativeObject() {
        super();
    }

    @Override
    public String toString() {

        return this.getClass().getName();
    }


    public void addPE(String name, Object o) {
        put(name, this, o);
    }
}
