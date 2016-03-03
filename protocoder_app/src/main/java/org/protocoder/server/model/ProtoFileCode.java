package org.protocoder.server.model;

/**
 * Created by victornomad on 3/03/16.
 */
public class ProtoFileCode extends ProtoFile {
    public String code;

    public ProtoFileCode(String name, String path, String code) {
        super(name, path);
        this.type = "code";
        this.code = code;
    }
}
