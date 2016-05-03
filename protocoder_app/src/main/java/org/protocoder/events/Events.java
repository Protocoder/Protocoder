/*
* Part of Protocoder http://www.protocoder.org
* A prototyping platform for Android devices 
*
* Copyright (C) 2013 Victor Diaz Barrales victormdb@gmail.com
* 
* Protocoder is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Protocoder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with Protocoder. If not, see <http://www.gnu.org/licenses/>.
*/

package org.protocoder.events;

import org.protocoder.server.model.ProtoFile;
import org.protocoderrunner.models.Project;

import java.io.File;

public class Events {
    public static final String PROJECT_RUN          = "run";
    public static final String PROJECT_STOP         = "stop";
    public static final String PROJECT_STOP_ALL     = "stop_all";
    public static final String PROJECT_SAVE         = "save";
    public static final String PROJECT_NEW          = "new";
    public static final String PROJECT_UPDATE       = "update";
    public static final String PROJECT_EDIT         = "edit";
    public static final String PROJECT_DELETE       = "delete";
    public static final String EDITOR_FILE_TO_LOAD  = "editor_file_to_load";
    public static final String EDITOR_FILE_LOAD     = "editor_file_load";
    public static final String EDITOR_FILE_CHANGED  = "editor_file_changed";
    public static final String EDITOR_FILE_SAVE     = "editor_file_saved";
    public static final String EDITOR_FILE_PREVIEW  = "editor_file_preview" ;

    public static class ProjectEvent {
        private Project project;
        private String action;

        public ProjectEvent(String action, Project project) {
            this.action = action;
            this.project = project;
        }

        public ProjectEvent(String action, String folder, String name) {
            this.action = action;
            this.project = new Project(folder, name);
        }

        public String getAction() {
            return action;
        }

        public Project getProject() {
            return project;
        }
    }

    public static class ExecuteCodeEvent {
        private String code;

        public ExecuteCodeEvent(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }
    }

    public static class LogEvent {
        private String msg;
        private String tag;

        public LogEvent(final String aTag, final String aMsg) {
            msg = aMsg;
            tag = aTag;
        }

        public String getMessage() {
            return msg;
        }
        public String getTag() {
            return tag;
        }
    }

    public static class SelectedProjectEvent {
        private String folder;
        private String name;

        public SelectedProjectEvent(String folder, String name) {
            this.folder = folder;
            this.name = name;
        }

        public String getFolder() {
            return this.folder;
        }

        public String getName() {
            return this.name;
        }
    }

    public static class FolderChosen {
        private final String parent;
        private final String name;

        public FolderChosen(String folder, String name) {
            this.parent = folder;
            this.name = name;
        }

        public String getParent() {
            return parent;
        }

        public String getName() {
            return name;
        }

        public String getFullFolder() {
            return this.parent + File.separator + this.name;
        }
    }

    public static class HTTPServerEvent {

        private final String what;
        private final Project project;

        public HTTPServerEvent(String what) {
            this(what, null);
        }

        public HTTPServerEvent(String what, Project p) {
            this.what = what;
            this.project = p;
        }

        public String getWhat() { return what; }
    }

    public static class Connection {
        private final String type;
        private final String address;

        public Connection(String type, String address) {
            this.type = type;
            this.address = address;
        }

        public String getType() {
            return type;
        }

        public String getAddress() {
            return address;
        }
    }

    public static class EditorEvent {
        private final Project project;
        private final ProtoFile protofile;
        private final String action;
        private final String previewType;

        public EditorEvent(String action, Project project, ProtoFile protofile, String previewType) {
            this.action = action;
            this.project = project;
            this.protofile = protofile;
            this.previewType = previewType;
        }

        public EditorEvent(String action, Project project, ProtoFile protofile) {
            this(action, project, protofile, null);
        }

        public String getAction() {
            return action;
        }

        public Project getProject() {
            return project;
        }

        public ProtoFile getProtofile() {
            return protofile;
        }

        public String getPreviewType() {
            return previewType;
        }
    }
}