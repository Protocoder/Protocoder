/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * 
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

package org.protocoder.fragments;

import org.apache.commons.lang3.StringUtils;
import org.protocoder.R;
import org.protocoder.base.BaseFragment;
import org.protocoder.events.Events.ProjectEvent;
import org.protocoder.events.Project;
import org.protocoder.events.ProjectManager;
import org.protocoder.utils.Fonts;
import org.protocoder.utils.TextUtils;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import de.greenrobot.event.EventBus;

@SuppressLint("NewApi")
public class EditorFragment extends BaseFragment {

    public interface EditorFragmentListener {
	void onLoad();

	void onLineTouched();
    }

    // TODO change this dirty hack
    private static final int MENU_RUN = 10;
    private static final int MENU_SAVE = 11;
    private static final int MENU_BACK = 12;
    private float currentSize;
    EditText edit;
    private View v;
    private Project currentProject;
    private EditorFragmentListener listener;

    public EditorFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

	v = inflater.inflate(R.layout.fragment_editor, container, false);
	edit = (EditText) v.findViewById(R.id.editText1);

	TextUtils.changeFont(getActivity(), edit, Fonts.CODE);

	currentSize = edit.getTextSize();
	Button btnIncreaseSize = (Button) v.findViewById(R.id.increaseSize);
	btnIncreaseSize.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		currentSize += 1;
		edit.setTextSize(currentSize);
	    }
	});

	Button btnDecreaseSize = (Button) v.findViewById(R.id.decreaseSize);
	btnDecreaseSize.setOnClickListener(new OnClickListener() {

	    @Override
	    public void onClick(View v) {
		currentSize -= 1;
		edit.setTextSize(currentSize);
	    }
	});

	Bundle bundle = getArguments();
	if (bundle != null) {
	    String projectName = bundle.getString(Project.NAME);
	    int projectType = bundle.getInt(Project.TYPE, -1);
	    Log.d("mm", projectName);
	    if (projectName != "") {
		loadProject(ProjectManager.getInstance().get(projectName, projectType));
	    }
	}

	edit.setOnClickListener(new View.OnClickListener() {
	    @Override
	    public void onClick(View v) {
		Log.d("qq", "" + getCurrentCursorLine(edit.getEditableText()));
		if (listener != null) {
		    listener.onLineTouched();
		}
	    }

	});

	// Set actionbar override
	setHasOptionsMenu(true);

	return v;
    }

    public static EditorFragment newInstance(ProjectManager project) {
	EditorFragment myFragment = new EditorFragment();

	return myFragment;
    }

    @Override
    public View getView() {
	return super.getView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
	super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onPause() {
	super.onPause();
    }

    @Override
    public void onResume() {
	super.onResume();

    }

    @Override
    public void onDestroy() {
	super.onDestroy();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
	super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
	Toast.makeText(getActivity(), "HIDDEN? " + hidden, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	menu.clear();
	menu.add(1, MENU_RUN, 0, "Run").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	menu.add(1, MENU_SAVE, 0, "Save").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
	super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
	// Handle item selection
	switch (item.getItemId()) {
	case MENU_RUN:
	    save();
	    run();
	    return true;
	case MENU_SAVE:
	    save();
	    return true;

	default:
	    return super.onOptionsItemSelected(item);
	}
    }

    public void addListener(EditorFragmentListener listener) {
	this.listener = listener;
    }

    public void loadProject(Project project) {
	currentProject = project;
	if (project != null) {
	    edit.setText(ProjectManager.getInstance().getCode(project));
	} else {
	    edit.setText("Project loading failed");
	}
    }

    public void setText(String text) {
	edit.setText(text);
    }

    public void run() {

	ProjectEvent evt = new ProjectEvent(currentProject, "run");
	EventBus.getDefault().post(evt);

    }

    public void save() {
	ProjectManager.getInstance().writeNewCode(currentProject, getCode());
	Toast.makeText(getActivity(), "Saving " + currentProject.getName() + "...", Toast.LENGTH_SHORT).show();

    }

    public void toggleEditable(boolean b) {

    }

    private int getCurrentCursorLine(Editable editable) {
	int selectionStartPos = Selection.getSelectionStart(editable);

	if (selectionStartPos < 0) {
	    // There is no selection, so return -1 like getSelectionStart() does
	    // when there is no seleciton.
	    return -1;
	}

	String preSelectionStartText = editable.toString().substring(0, selectionStartPos);
	return StringUtils.countMatches(preSelectionStartText, "\n");
    }

    public String getCode() {
	return edit.getText().toString();
    }

}