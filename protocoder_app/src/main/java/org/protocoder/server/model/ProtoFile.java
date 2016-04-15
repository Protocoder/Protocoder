package org.protocoder.server.model;

import java.util.ArrayList;

/**
 * Created by victornomad on 3/03/16.
 */
public class ProtoFile {
    public String project_parent;
    public String name;
    public String path;
    public String type;
    public String code = null;
    public Long fileSize = null;
    public ArrayList<ProtoFile> files = null;

    public ProtoFile(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public ProtoFile() {

    }
}
