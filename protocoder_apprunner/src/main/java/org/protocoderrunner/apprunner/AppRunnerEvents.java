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

package org.protocoderrunner.apprunner;

import org.protocoderrunner.apprunner.project.Project;

public class AppRunnerEvents {
    public static final String PROJECT_RUN = "run";

    public static class ProjectEvent {
        private Project project;
        private String action;

        public ProjectEvent(Project project, String action) {
            this.project = project;
            this.action = action;
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

}