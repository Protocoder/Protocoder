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

package org.protocoderrunner.apprunner.api.widgets;

import android.content.Context;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.network.IDEcommunication;

public class PWebEditor extends PInterface {

    public PWebEditor(Context a) {
        super(a);

    }

    //TODO this is mContext place holder

    @ProtoMethod(description = "Loads a Html file in the webIde sidebar", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void loadHTMLonSideBar(boolean visible) {

    }

    //TODO this is mContext place holder

    @ProtoMethod(description = "Shows/Hides the webIde sidebar", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void showSideBar(boolean visible) {

    }


    @ProtoMethod(description = "Execute custom js in the webIde", example = "")
    @ProtoMethodParam(params = {"jsText"})
    public void sendJs(String js) {
        IDEcommunication.getInstance(getContext()).sendCustomJs(js);
    }
}