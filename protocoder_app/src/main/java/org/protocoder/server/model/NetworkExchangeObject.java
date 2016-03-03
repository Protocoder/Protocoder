package org.protocoder.server.model;

import org.protocoderrunner.models.Project;

import java.util.ArrayList;

/**
 * Created by victornomad on 2/03/16.
 */
public class NetworkExchangeObject {
    public String action = null;

    // Data
    public Project project;
    public ArrayList<ProtoFileCode> files = new ArrayList<>();

}
