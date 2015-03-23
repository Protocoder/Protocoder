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

package org.protocoderrunner.apprunner.logger;

import org.protocoderrunner.utils.MLog;

public class L {

    public static boolean enabled = true;
    private static OverlayLogger overlayLogger = null;
    private static String filter = null;

    // TODO clean this up
    public static void d(String TAG, String text) {

        if (enabled) {
            if (filter == null) {
                MLog.d(TAG, text);

                // MLog.d(TAG, "" + overlayLogger);
                if (overlayLogger != null) {
                    overlayLogger.addItem(text);
                }
            } else if (TAG.equals(filter)) {

                MLog.d(TAG, text);

                // MLog.d(TAG, "" + overlayLogger);
                if (overlayLogger != null) {
                    overlayLogger.addItem(text);
                }
            }

        }
    }

    public static void filterByTag(String tag) {
        filter = tag;
    }

    public static void addLoggerWindow(OverlayLogger ol) {
        overlayLogger = ol;

    }

}
