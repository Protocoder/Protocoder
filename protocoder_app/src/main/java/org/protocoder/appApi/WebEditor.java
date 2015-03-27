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

package org.protocoder.appApi;

import org.protocoderrunner.apprunner.api.PConsole;
import org.protocoderrunner.apprunner.api.PDashboard;

public class WebEditor {

    private final Protocoder protocoder;
    public PDashboard dashboard = null;
    public PConsole console = null;

    WebEditor(Protocoder protocoder) {
        this.protocoder = protocoder;
        console = new PConsole(Protocoder.mActivityContext);
        dashboard = new PDashboard(Protocoder.mActivityContext);
    }

    public void open(String folder, String appName) {

    }

    public void openInNewWindow(String folder, String appName) {

    }

    public void openTab(String filePath) {

    }

    public void closeTab(String filePath) {

    }

    public void closeAllTabs() {

    }

    public void refresh() {

    }

    public void showBar(boolean b) {

    }

}
