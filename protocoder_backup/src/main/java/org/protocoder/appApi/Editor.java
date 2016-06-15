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

import android.os.Bundle;

import org.protocoder.R;
import org.protocoder.fragments.EditorFragment;
import org.protocoderrunner.project.Project;
import org.protocoderrunner.project.ProjectManager;

public class Editor {

    Protocoder protocoder;
    private EditorFragment editorFragment;

    Editor(Protocoder protocoder) {
        this.protocoder = protocoder;
    }

    public void show(boolean show, String folder, String appName) {
        Project project = ProjectManager.getInstance().get(folder, appName);

        if (show) {
            editorFragment = new EditorFragment();
            if (project != null) {
                Bundle bundle = new Bundle();
                bundle.putString(Project.NAME, project.getName());
                bundle.putString(Project.URL, project.getStoragePath());
                bundle.putString(Project.FOLDER, project.getFolder());

                editorFragment.setArguments(bundle);
            }
            protocoder.mActivityContext.addFragment(editorFragment, R.id.fragmentEditor, "editorFragment", true);
        } else {
            protocoder.mActivityContext.removeFragment(editorFragment);
            editorFragment = null;
        }
    }

    public void openTab(String fileName) {

    }

    public void closeTab(String fileName) {

    }

    public void closeAllTabs() {

    }


}
