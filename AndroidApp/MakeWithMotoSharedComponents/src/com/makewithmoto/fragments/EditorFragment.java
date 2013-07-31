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
import com.makewithmoto.events.Events.ProjectEvent;
import com.makewithmoto.events.Project;
import com.makewithmoto.events.ProjectManager;
import com.makewithmoto.sharedcomponents.R;
import com.makewithmoto.utils.Fonts;
import com.makewithmoto.utils.TextUtils;

import de.greenrobot.event.EventBus;

@SuppressLint("NewApi")
public class EditorFragment extends BaseFragment {

    public interface EditorFragmentListener {
        void onLoad();
    }

    // TODO change this dirty hack
    private static final int MENU_RUN = 10;
    private static final int MENU_SAVE = 11;
    private static final int MENU_BACK = 12;
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
            String projectURL = bundle.getString("project_url", "");
            int projectType = bundle.getInt("project_type", -1);
            Log.d("mm", projectName);
            if (projectName != "") {
                loadProject(ProjectManager.getInstance().get(projectName, projectType));
            }
        }

        //Set actionbar override
        setHasOptionsMenu(true);

        return v;
    }

    public static EditorFragment newInstance(ProjectManager project) {
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

    public void loadProject(Project project) {
        currentProject = project;
        if (project != null) {
            edit.setText(ProjectManager.getInstance().getCode(project));
        } else {
            edit.setText("Project loading failed");
        }
    }
    
    public void run() {
    
		ProjectEvent evt = new ProjectEvent(currentProject, "run");
		EventBus.getDefault().post(evt);
	
    }

    public void save() {
    	ProjectManager.getInstance().writeNewCode(currentProject, getCode());
        Toast.makeText(getActivity(), "Saving " + currentProject.getName() + "...", Toast.LENGTH_SHORT).show();

    }

    public String getCode() {
        return edit.getText().toString();
    }

}