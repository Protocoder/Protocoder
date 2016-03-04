package org.protocoder.server.networkexchangeobjects;

import org.protocoder.server.model.ProtoFileCode;
import org.protocoderrunner.models.Project;

import java.util.ArrayList;

/**
 * Network Exchange Object for [ Project ] actions
 * list / new / run / stop / load / save
 */
public class NEOProject {
    public String cmd = null;
    public Project project;
    public ArrayList<ProtoFileCode> files = new ArrayList<>();

}
