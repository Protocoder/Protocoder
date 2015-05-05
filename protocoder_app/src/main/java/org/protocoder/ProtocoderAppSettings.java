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

public class ProtocoderAppSettings {

	// == PROTOCODER APP SETTINGS ==========
	public final static boolean DEBUG = true;
    public final static String APP_FOLDER = "protocodersandbox";
    public final String CUSTOM_WEBEDITOR = "webeditors";

    public final int WEBSOCKET_PORT = 8587;
    public final int HTTP_PORT = 8585;
    public final int FTP_PORT = 8589;

    private static ProtocoderAppSettings instance;

    public static ProtocoderAppSettings get() {
        if (instance == null)
            instance = new ProtocoderAppSettings();
        return instance;
    }


}
