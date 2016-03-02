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

package org.protocoder.helpers;

import android.content.Context;

import org.protocoder.settings.ProtocoderSettings;
import org.protocoderrunner.base.utils.FileIO;

import java.io.File;

public class ProtoSettingsHelper {

    private static String PROTOCODER_EXTENSION = ".proto";

    public interface InstallListener {
        void onReady();
    }

    public static void installExamples(final Context c, final String assetsName, final InstallListener l) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                File dir = new File(ProtocoderSettings.getBaseDir() + assetsName);
                FileIO.deleteDir(dir);
                FileIO.copyFileOrDir(c, assetsName);
                l.onReady();
            }
        }).start();
    }

}
