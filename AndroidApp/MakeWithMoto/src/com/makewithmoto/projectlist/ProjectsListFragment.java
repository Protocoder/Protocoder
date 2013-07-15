package com.makewithmoto.projectlist;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.makewithmoto.MainActivity;
import com.makewithmoto.R;
import com.makewithmoto.base.BaseFragment;
import com.makewithmoto.beam.BeamActivity;
import com.makewithmoto.events.Events.ProjectEvent;
import com.makewithmoto.events.Project;
import com.makewithmoto.fragments.EditorFragment;

import de.greenrobot.event.EventBus;

@SuppressLint("NewApi")
public class ProjectsListFragment extends BaseFragment {

    ArrayList<Project> projects;
    private ProjectAdapter projectAdapter;
    private GridView gridView;

    public ProjectsListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.view_gridlayout, container, false);
        projects = new ArrayList<Project>();

        gridView = (GridView) v.findViewById(R.id.gridview);
        projectAdapter = new ProjectAdapter(getActivity(), projects);
        gridView.setAdapter(projectAdapter);

        final ArrayList<Project> projects = Project.all();

        // This could be more efficient.
        for (Project project : projects) {
            String projectURL = project.getUrl();
            String projectName = project.getName();
            addProject(projectName, projectURL);
        }

        registerForContextMenu(gridView);

        gridView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View v, int position, long id) {

                ProjectAnimations.projectRefresh(v);

                //ALog.d("Clicked");
                //Toast.makeText(getActivity(), "" + position, Toast.LENGTH_SHORT)
                //		.show();
                Project project = projects.get(position);
                ProjectEvent evt = new ProjectEvent(project, "run");
                EventBus.getDefault().post(evt);

            }
        });

        return v;
    }

    protected void deleteProject(int position) {

        File dir = new File(projects.get(position).getUrl());

        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                new File(dir, children[i]).delete();
            }
        }
        dir.delete();

        projects.remove(position);

        projectAdapter.notifyDataSetChanged();
        gridView.invalidateViews();
    }

    public void clear() {
        gridView.removeAllViews();
        projectAdapter.notifyDataSetChanged();
    }

    public void addProject(String projectName, String projectURL) {
        projects.add(new Project(projectName, projectURL));

        projectAdapter.notifyDataSetChanged();
        gridView.invalidateViews();
    }

    @Override
    public View getView() {
        return super.getView();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.project_list, menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
        final int index = info.position;
        Project project = projects.get(index);

        switch (item.getItemId()) {

        case R.id.menu_project_list_run:
            // m.applicationWebView.launchProject(project);
            ProjectEvent evt = new ProjectEvent(project, "run");
            EventBus.getDefault().post(evt);
            return true;
        case R.id.menu_project_list_edit:
            //FileIO.read(getActivity(), fileName)
            EditorFragment editorFragment = new EditorFragment(); //.newInstance(project);
            Bundle bundle = new Bundle();
            bundle.putString("project_name", project.getName());
            editorFragment.setArguments(bundle);
            ((MainActivity) getActivity()).addFragment(editorFragment, R.id.fragmentEditor, "editorFragment", true);

            return true;
        case R.id.menu_project_list_delete:

            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        // Yes button clicked

                        deleteProject(index);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        // No button clicked
                        break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();

            return true;

        case R.id.menu_project_list_add_shortcut:

            // create shortcut if requested
            ShortcutIconResource icon = Intent.ShortcutIconResource.fromContext(getActivity(), R.drawable.ic_script);

            Intent shortcutIntent = new Intent("com.makewithmoto.apprunner.MWMActivity");
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            shortcutIntent.putExtra("project_name", project.getName());

            final Intent putShortCutIntent = new Intent();

            putShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
            putShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, project.getName());
            putShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon); // can also be ignored too
            putShortCutIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
            getActivity().sendBroadcast(putShortCutIntent);

            // getActivity().setResult(getActivity().RESULT_OK, intent);

            return true;

        case R.id.menu_project_list_share_with:

            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, project.getCode());
            sendIntent.setType("text/plain");
            startActivity(Intent.createChooser(sendIntent, getResources().getText(R.string.send_to)));

            return true;
        case R.id.menu_project_list_beam:

            Intent beamIntent = new Intent(getActivity(), BeamActivity.class);
            //beamIntent.setAction(Intent.ACTION_SEND);
            beamIntent.putExtra(Intent.EXTRA_TEXT, project.getCode());
            beamIntent.setType("text/plain");
            startActivity(Intent.createChooser(beamIntent, getResources().getText(R.string.send_to)));
            startActivity(beamIntent);

            return true;
        default:
            return super.onContextItemSelected(item);
        }
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
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//		// Is this necessary??
//		for (Project project : projects) {
//			project = null;
//		}
//		projects = null;
    }

    public void projectRefresh(String projectName) {

        View v = gridView.findViewWithTag(projectName);
        ProjectAnimations.projectRefresh(v);

    }

    public void projectLaunch(String projectName) {
        View v = gridView.findViewWithTag(projectName);
        ProjectAnimations.projectLaunch(v);

    }

}