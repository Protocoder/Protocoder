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
    public Long size = null;
    public ArrayList<ProtoFile> files = null;

    public ProtoFile(String path, String name) {
        this.name = name;
        this.path = path;
    }

    public ProtoFile() {

    }

    public String getPath() {
        return path;
    }

    public String getFullPath() {
        return path + name;
    }
}
