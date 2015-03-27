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

package org.protocoderrunner.apprunner.api.other;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;

public class WhatIsRunning {

    private static WhatIsRunning instance;

    protected WhatIsRunning() {

    }

    public static WhatIsRunning getInstance() {
        if (instance == null)
            instance = new WhatIsRunning();
        return instance;

    }

    Vector<Object> runners = new Vector<Object>();

    public void stopAll() {
        for (Object o : runners) {
            Method method = null;

            //MLog.d("name", o.getClass().getCanonicalName());
            try {
                method = o.getClass().getMethod("stop");
            } catch (SecurityException e) {
            } catch (NoSuchMethodException e) {
            }

            try {
                method.invoke(o);
            } catch (IllegalArgumentException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }

        }
    }

    public void add(Object object) {
        runners.add(object);
    }

    public void remove(Object object) {
        runners.remove(object);
    }
}
