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

package org.protocoder;

import android.os.Build;

import org.protocoderrunner.apprunner.AppRunnerSettings;

import java.io.File;

public class AppSettings extends AppRunnerSettings {

	// == APP SETTINGS ==========
	public final static boolean DEBUG = true;
	public final static boolean FULLSCREEN = false;
	public final static boolean PORTRAIT = false;
	public final static boolean STAY_AWAKE = false;
	public final static boolean OVERRIDE_HOME_BUTTONS = false;
	public final static boolean OVERRIDE_VOLUME_BUTTONS = false;
	public final static boolean HIDE_HOME_BAR = false;
	public final static boolean SCREEN_ALWAYS_ON = false;
	public final static boolean CLOSE_WITH_BACK = true;
	public static boolean STANDALONE = false;
    public static int MIN_SUPPORTED_VERSION = Build.VERSION_CODES.ICE_CREAM_SANDWICH;

    public static final String APP_FOLDER_CUSTOM_WEBEDITOR = "webeditors";

    public final static int WEBSOCKET_PORT = 8587;
    public final static int HTTP_PORT = 8585;
    public static int FTP_PORT = 8589;

    public String id;


    public static String getBaseWebEditorsDir() {
        return getBaseDir() + APP_FOLDER_CUSTOM_WEBEDITOR + File.separator;
    }

}
