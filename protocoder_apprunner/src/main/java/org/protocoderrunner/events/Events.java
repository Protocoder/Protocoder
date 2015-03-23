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

package org.protocoderrunner.events;

import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;

public class Events {
    public static class ProjectEvent {
        private Project project;
        private String name;
        private String action;

        public ProjectEvent(Project aProject, String anAction) {
            project = aProject;
            action = anAction;
        }

        public ProjectEvent(Project aProject, String aName, String anAction) {
            project = aProject;
            action = anAction;
            name = aName;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String newAction) {
            action = newAction;
        }

        public Project getProject() {
            if (project == null) {
                project = ProjectManager.getInstance().get(project.getFolder(), project.getName());
            }
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
}