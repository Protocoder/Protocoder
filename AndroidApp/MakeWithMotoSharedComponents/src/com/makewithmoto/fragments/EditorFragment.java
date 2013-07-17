package com.makewithmoto.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
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

import com.makewithmoto.base.BaseFragment;
import com.makewithmoto.events.Project;
import com.makewithmoto.sharedcomponents.R;
import com.makewithmoto.utils.Fonts;
import com.makewithmoto.utils.TextUtils;

@SuppressLint("NewApi")
public class EditorFragment extends BaseFragment {

    public interface EditorFragmentListener {
        void onLoad();
    }

    // TODO change this dirty hack
    private static final int MENU_SAVE = 10;
    private static final int MENU_BACK = 11;
    private float currentSize;
    EditText edit;
    private View v;
    private Project currentProject;

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
            String projectName = bundle.getString("project_name", "");
            Log.d("mm", projectName);
            if (projectName != "") {
                loadProject(Project.get(projectName));
            }
        }

        //Set actionbar override
        setHasOptionsMenu(true);

        return v;
    }

    public static EditorFragment newInstance(Project project) {
        EditorFragment myFragment = new EditorFragment();

        // Bundle args = new Bundle();
        // args.putSerializable("qq", project);
        // args.putInt("someInt", someInt);
        // myFragment.setArguments(args);

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
        menu.add(1, MENU_SAVE, 0, "Save").setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        case MENU_SAVE:
            save();
            return true;

        default:
            return super.onOptionsItemSelected(item);
        }
    }

    public void loadProject(Project project) {
        currentProject = project;
        Log.d("qq", "" + project + " " + edit);
        if (project != null) {
            edit.setText(project.getCode());
        } else {
            edit.setText("Project loading failed");
        }
    }

    public void save() {
        com.makewithmoto.utils.FileIO.writeStringToFile(currentProject.getName(), getCode());
        Toast.makeText(getActivity(), "Saving " + currentProject.getName() + "...", Toast.LENGTH_SHORT).show();

    }

    public String getCode() {
        return edit.getText().toString();
    }

}