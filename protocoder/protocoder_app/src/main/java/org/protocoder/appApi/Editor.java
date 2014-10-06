/*
 * Protocoder
 * A prototyping platform for Android devices
 *
 * Victor Diaz Barrales victormdb@gmail.com
 *
 * Copyright (C) 2014 Victor Diaz
 * Copyright (C) 2013 Motorola Mobility LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */
package org.protocoder.appApi;

import android.os.Bundle;

import org.protocoder.R;
import org.protocoder.fragments.EditorFragment;
import org.protocoderrunner.project.Project;

public class Editor {

    Protocoder protocoder;
    private EditorFragment editorFragment;

    Editor(Protocoder protocoder) {
        this.protocoder = protocoder;
    }

    public void show(boolean show, Project project) {
        //protocoder.app.highlight(null);
        //protocoder.app.shake();
        //protocoder.protoScripts.goTo("examples");
        //protocoder.protoScripts.goTo("examples", "Video");
        //protocoder.protoScripts.highlight("examples", "Video");

        if (show) {
            editorFragment = new EditorFragment();
            if (project != null) {
                Bundle bundle = new Bundle();
                bundle.putString(Project.NAME, project.getName());
                bundle.putString(Project.URL, project.getStoragePath());
                bundle.putString(Project.FOLDER, project.getFolder());

                editorFragment.setArguments(bundle);
            }
            protocoder.a.addFragment(editorFragment, R.id.fragmentEditor, "editorFragment", true);
        } else {
            protocoder.a.removeFragment(editorFragment);
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
