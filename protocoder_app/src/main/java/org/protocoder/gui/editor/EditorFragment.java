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

package org.protocoder.gui.editor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.Selection;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.greenrobot.eventbus.EventBus;
import org.protocoder.R;
import org.protocoder.events.Events;
import org.protocoder.helpers.ProtoScriptHelper;
import org.protocoder.settings.ProtocoderSettings;
import org.protocoderrunner.base.BaseFragment;
import org.protocoderrunner.base.utils.MLog;
import org.protocoderrunner.base.utils.TextUtils;
import org.protocoderrunner.models.Project;

import java.io.File;

@SuppressLint("NewApi")
public class EditorFragment extends BaseFragment {

    protected static final String TAG = EditorFragment.class.getSimpleName();

    public interface EditorFragmentListener {
        void onLoad();
        void onLineTouched();
    }

    private Context mContext;

    private EditText mEdit;
    private HorizontalScrollView mExtraKeyBar;
    private View v;
    private EditorFragmentListener listener;

    // settings
    private EditorSettings mEditorSettings = new EditorSettings();

    private Project mCurrentProject;

    public EditorFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        v = inflater.inflate(R.layout.fragment_editor, container, false);

        mEdit = (EditText) v.findViewById(R.id.editText1);
        mEditorSettings.fontSize = mEdit.getTextSize();

        // change font
        TextUtils.changeFont(getActivity(), mEdit, mEditorSettings.font);

        bindUI();
        setExtraKeysBar();

        setHasOptionsMenu(true); // Set actionbar override

        /* get the code file */
        Bundle bundle = getArguments();
        if (bundle != null) {
            loadProject(new Project(bundle.getString(Project.FOLDER, ""), bundle.getString(Project.NAME, "")));
        }

        return v;
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

    /**
     * Bind the UI
     */
    private void bindUI() {
        Button btnIncreaseSize = (Button) v.findViewById(R.id.increaseSize);
        btnIncreaseSize.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mEditorSettings.fontSize += 1;
                mEdit.setTextSize(mEditorSettings.fontSize);
            }
        });

        Button btnDecreaseSize = (Button) v.findViewById(R.id.decreaseSize);
        btnDecreaseSize.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mEditorSettings.fontSize -= 1;
                mEdit.setTextSize(mEditorSettings.fontSize);
            }
        });
    }

    /**
     * Set the extra keys bar
     */
    private void setExtraKeysBar() {
        if (mEditorSettings.extraKeysBarEnabled == false) return;

        mExtraKeyBar = (HorizontalScrollView) v.findViewById(R.id.extraKeyBar);

        mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MLog.d(TAG, "line touched at " + getCurrentCursorLine(mEdit.getEditableText()));
                if (listener != null) {
                    listener.onLineTouched();
                }
            }

        });

        // detect if key board is open or close
        final RelativeLayout rootView = (RelativeLayout) v.findViewById(R.id.rootEditor);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = rootView.getRootView().getHeight() - rootView.getHeight();
                // MLog.d(TAG, "" + heightDiff);

                if (heightDiff > 300) {
                    mExtraKeyBar.setVisibility(View.VISIBLE);
                } else {
                    mExtraKeyBar.setVisibility(View.GONE);
                }
            }
        });


        // set all the extra buttons
        LinearLayout extraKeyBarLl = (LinearLayout) v.findViewById(R.id.extraKeyBarLl);
        for (int i = 0; i < extraKeyBarLl.getChildCount(); i++) {
            final Button b = (Button) extraKeyBarLl.getChildAt(i);

            b.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    String t = b.getText().toString();
                    mEdit.getText().insert(mEdit.getSelectionStart(), t);
                }
            });

        }
    }

    public static EditorFragment newInstance() {
        return newInstance(null);
    }

    public static EditorFragment newInstance(Bundle bundle) {
        EditorFragment myFragment = new EditorFragment();
        myFragment.setArguments(bundle);

        return myFragment;
    }

    @Override
    public View getView() {
        return super.getView();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        Toast.makeText(getActivity(), "HIDDEN? " + hidden, Toast.LENGTH_SHORT).show();
    }

    /**
     * Add a listner to know whe the cursor is
     */
    public void addListener(EditorFragmentListener listener) {
        this.listener = listener;
    }

    /**
     * Load a project
     */
    public void loadProject(Project project) {
        mCurrentProject = project;
        if (project != null) {
            mEdit.setText(ProtoScriptHelper.getCode(project));
        } else {
            mEdit.setText("Project loading failed");
        }
    }

    /**
     * Save the current project
     */
    public void saveProject() {
        ProtoScriptHelper.saveCode(mCurrentProject.getSandboxPath() + File.separator + ProtocoderSettings.MAIN_FILENAME, getCode());
        Toast.makeText(getActivity(), "Saving " + mCurrentProject.getName() + "...", Toast.LENGTH_SHORT).show();
    }

    /**
     * Run the current project
     */
    public void run() {
        EventBus.getDefault().post(new Events.ProjectEvent(Events.PROJECT_RUN, mCurrentProject));
    }

    /**
     * Toggle edittext editable
     */
    // TODO
    public void toggleEditable(boolean b) {

    }

    /**
     * Get current cursor line
     */
    public int getCurrentCursorLine(Editable editable) {
        int selectionStartPos = Selection.getSelectionStart(editable);

        // no selection
        if (selectionStartPos < 0) return -1;

        String preSelectionStartText = editable.toString().substring(0, selectionStartPos);
        return StringUtils.countMatches(preSelectionStartText, "\n");
    }

    /**
     * Get current code text
     */
    public String getCode() {
        return mEdit.getText().toString();
    }

    /**
     * Set the code text
     */
    public void setCode(String text) {
        mEdit.setText(text);
    }

}