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

package org.protocoder.gui.settings;

import android.os.Build;

import org.protocoderrunner.apprunner.AppRunnerSettings;

import java.io.File;

public class ProtocoderSettings extends AppRunnerSettings {

	/*
	 * Protocoder app settings
	 */
	public final static boolean DEBUG                       = true;
    public static String MAIN_FILENAME                      = "main.js";
    public static String PROTO_FILE_EXTENSION               = ".proto";

    public static final String APP_FOLDER_CUSTOM_WEBEDITOR  = "webeditors";

    public static int MIN_SUPPORTED_VERSION                 = Build.VERSION_CODES.ICE_CREAM_SANDWICH;

    public static final int WEBSOCKET_PORT                  = 8587;
    public static final int HTTP_PORT                       = 8585;
    public final int FTP_PORT                               = 8589;

    private static ProtocoderSettings instance;

    public static String getBaseWebEditorsDir() {
        return getBaseDir() + APP_FOLDER_CUSTOM_WEBEDITOR + File.separator;
    }
}
