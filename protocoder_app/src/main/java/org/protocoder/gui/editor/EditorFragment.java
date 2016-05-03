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
import android.text.TextWatcher;
import android.view.LayoutInflater;
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
import org.greenrobot.eventbus.Subscribe;
import org.protocoder.R;
import org.protocoder.events.Events;
import org.protocoder.helpers.ProtoScriptHelper;
import org.protocoder.server.model.ProtoFile;
import org.protocoderrunner.base.BaseFragment;
import org.protocoderrunner.base.utils.MLog;
import org.protocoderrunner.base.utils.TextUtils;
import org.protocoderrunner.models.Project;

import java.io.File;
import java.util.HashMap;

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
    private String mCurrentFile = "";

    HashMap<String, String> openedFiles = new HashMap<>();

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

        v = inflater.inflate(R.layout.editor_fragment, container, false);

        bindUI();

        // editor settings
        mEditorSettings.fontSize = mEdit.getTextSize();
        TextUtils.changeFont(getActivity(), mEdit, mEditorSettings.font);

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
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * Bind the UI
     */
    private void bindUI() {
        mEdit = (EditText) v.findViewById(R.id.editText1);
        mEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String originalText = openedFiles.get(mCurrentFile);
                String currentText = s.toString();

                // check if text is changed
                if (originalText.equals(currentText) == false) {
                    MLog.d(TAG, "text changed");

                    // update current code string
                    openedFiles.put(mCurrentFile, currentText);

                    // notifiy that a file is changed
                    EventBus.getDefault().post(new Events.EditorEvent(Events.EDITOR_FILE_CHANGED, mCurrentProject, new ProtoFile(mCurrentFile, "")) );
                }

            }
        });

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
        MLog.d("file load send", "qq");

        mCurrentProject = project;
        EventBus.getDefault().postSticky(new Events.EditorEvent(Events.EDITOR_FILE_TO_LOAD, mCurrentProject, new ProtoFile("main.js","")));

        loadFile("main.js");
    }

    public void loadFile(String fileName) {
        if (mCurrentProject == null) {
            mEdit.setText("Project loading failed");
            return;
        }

        mCurrentFile = fileName;

        String code = "";
        if (openedFiles.containsKey(fileName)) {
            code = openedFiles.get(fileName);
        } else {
            code = ProtoScriptHelper.getCode(mCurrentProject, fileName);
            openedFiles.put(fileName, code);
        }

        mEdit.setText(code);
    }

    /**
     * Save the current project
     */
    public void saveFile() {
        ProtoScriptHelper.saveCode(mCurrentProject.getSandboxPath() + File.separator + mCurrentFile, openedFiles.get(mCurrentFile));
        Toast.makeText(getActivity(), "Saving " + mCurrentProject.getName() + "...", Toast.LENGTH_SHORT).show();

        EventBus.getDefault().post(new Events.EditorEvent(Events.EDITOR_FILE_SAVE, mCurrentProject, new ProtoFile(mCurrentFile, "")));
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

    public void saveAll() {
        
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

    // load file in editor
    @Subscribe
    public void onEventMainThread(Events.EditorEvent e) {
        if (e.getAction().equals(Events.EDITOR_FILE_LOAD)) {
            ProtoFile f = e.getProtofile();
            loadFile(f.name);
        }
    }
}