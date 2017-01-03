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

package org.protocoderrunner.api.other;

import org.protocoderrunner.api.ProtoBase;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunner;

public class PDeviceEditor extends ProtoBase {

    public PDeviceEditor(AppRunner appRunner) {
        super(appRunner);
    }

    //TODO reenable this 

    @ProtoMethod(description = "Shows the console in the device app", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void showConsole(boolean visible) {
        //((AppRunnerFragment) mContext).showConsole(visible);

    }


    @ProtoMethod(description = "Shows the editor in the device app", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void show(boolean visible) {
        //((AppRunnerActivity) mContext).showEditor(visible);


        //this.addFragment(editorFragment, EDITOR_ID, "editorFragment", true);
    }

    @Override
    public void __stop() {

    }
}