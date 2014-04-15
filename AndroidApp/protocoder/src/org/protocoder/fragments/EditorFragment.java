/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
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
import org.protocoder.utils.MLog;
import org.protocoder.utils.TextUtils;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GestureDetectorCompat;
import android.text.Editable;
import android.text.Selection;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
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

	protected static final String TAG = "EditorFragment";

	// TODO change this dirty hack
	private static final int MENU_RUN = 8;
	private static final int MENU_SAVE = 9;
	private static final int MENU_BACK = 10;
	private static final int MENU_FILES = 11;
	private static final int MENU_API = 12;

	private float currentSize;
	EditText edit;
	private View v;
	private Project currentProject;
	private EditorFragmentListener listener;

	private Menu menu;
	private boolean bFiles = true;
	private boolean bAPI = true;

	private FileManagerFragment fileFragment;
	private FragmentTransaction ft;

	private BaseWebviewFragment webviewFragment;

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
			MLog.d("mm", projectName);
			if (projectName != "") {
				loadProject(ProjectManager.getInstance().get(projectName, projectType));
			}
		}

		edit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				MLog.d("qq", "" + getCurrentCursorLine(edit.getEditableText()));
				if (listener != null) {
					listener.onLineTouched();
				}
			}

		});

		// Set actionbar override
		setHasOptionsMenu(true);

		GestureDetectorCompat qq = new GestureDetectorCompat(getActivity(), new GestureDetector.OnGestureListener() {

			@Override
			public boolean onSingleTapUp(MotionEvent e) {
				MLog.d(TAG, "singleTapUp");
				return false;
			}

			@Override
			public void onShowPress(MotionEvent e) {
				MLog.d(TAG, "showPress");
			}

			@Override
			public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
				MLog.d(TAG, "onScroll");
				return false;
			}

			@Override
			public void onLongPress(MotionEvent e) {
				MLog.d(TAG, "onLongPress");
			}

			@Override
			public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
				MLog.d(TAG, "onFling");
				return false;
			}

			@Override
			public boolean onDown(MotionEvent e) {
				MLog.d(TAG, "onDown");
				return false;
			}
		});

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
		this.menu = menu;
		menu.clear();
		menu.add(1, MENU_RUN, 0, "Run").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(1, MENU_SAVE, 0, "Save").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(1, MENU_FILES, 0, "Files").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
		menu.add(1, MENU_API, 0, "API").setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
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
		case MENU_FILES:
			MenuItem menuFiles = menu.findItem(MENU_FILES).setChecked(bFiles);
			showFileManager(bFiles);
			bFiles = !bFiles;

			return true;

		case MENU_API:
			MenuItem menuApi = menu.findItem(MENU_API).setChecked(bFiles);
			showAPI(bAPI);
			bAPI = !bAPI;

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

	public void showAPI(boolean b) {

		ft = getChildFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_out_left,
				R.anim.slide_out_left);

		if (b) {

			webviewFragment = new BaseWebviewFragment();
			Bundle bundle = new Bundle();
			bundle.putString("url", "http://localhost:8585/reference.html");
			webviewFragment.setArguments(bundle);
			ft.add(R.id.fragmentWebview, webviewFragment).addToBackStack(null);

			edit.animate().translationX(-50).setDuration(500);
			// fileFragment.getView().animate().translationX(-200).setDuration(500).start();
		} else {
			edit.animate().translationX(0).setDuration(500);
			// fileFragment.getView().animate().translationX(0).setDuration(500).start();
			ft.remove(webviewFragment);
			// ft.hide(fileFragment);
		}
		ft.commit();

	}

	public void showFileManager(boolean b) {

		ft = getChildFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_left);

		if (b) {

			fileFragment = new FileManagerFragment();
			Bundle bundle = new Bundle();
			bundle.putString("name", currentProject.name);
			bundle.putInt("type", currentProject.type);
			fileFragment.setArguments(bundle);
			ft.add(R.id.fragmentFileManager, fileFragment).addToBackStack("q");

			edit.animate().translationX(-50).setDuration(500);
			// fileFragment.getView().animate().translationX(-200).setDuration(500).start();
		} else {
			edit.animate().translationX(0).setDuration(500);
			// fileFragment.getView().animate().translationX(0).setDuration(500).start();
			// ft.remove(fileFragment);
			// ft.hide(fileFragment);
		}
		ft.commit();

	}
}