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

import android.content.Context;

public class AppRunnerContext {
    private static AppRunnerContext instance;
    private Context mAppContext;

    public static AppRunnerContext get() {
        if (instance == null)
            instance = new AppRunnerContext();
        return instance;
    }

    public void init(Context context){
        if (mAppContext == null) {
            this.mAppContext = context.getApplicationContext();
        }
    }

    public Context getAppContext() {
        return get().mAppContext;
    }

}
