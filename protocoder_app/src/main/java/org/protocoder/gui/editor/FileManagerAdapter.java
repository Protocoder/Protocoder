package org.protocoder.gui.editor;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import org.protocoder.server.model.ProtoFile;
import org.protocoderrunner.base.utils.MLog;

import java.util.ArrayList;

public class FileManagerAdapter extends RecyclerView.Adapter<FileManagerAdapter.ViewHolder> {

    private static final String TAG = FileManagerAdapter.class.getSimpleName();
    private final FileManagerFragment mFragment;
    private ArrayList<ProtoFile> mDataSet;

    public void setData(ArrayList<ProtoFile> currentFileList) {
        mDataSet = currentFileList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final FileManagerListItem mView;

        public ViewHolder(FileManagerListItem v) {
            super(v);
            mView = v;
        }
    }

    public FileManagerAdapter(FileManagerFragment fragment, ArrayList<ProtoFile> currentFileList) {
        mFragment = fragment;
        mDataSet = currentFileList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        FileManagerListItem fileManagerListItem = new FileManagerListItem(mFragment.getContext());
        ViewHolder vh = new ViewHolder(fileManagerListItem);
        MLog.d(TAG, "created ");

        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ProtoFile protoFile = mDataSet.get(position);
        holder.mView.setProtoFile(protoFile);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MLog.d(TAG, "" + protoFile.name);

                // if its a folder move to that level
                if (protoFile.type.equals("folder")) {
                    mFragment.setCurrentFolder(protoFile.path);
                } else {

                }
                // EventBus.getDefault().post(new Events.EditorEvent(Events.EDITOR_FILE_TO_LOAD, mProject, f));
                // mCurrentSelected = position;
            }
        });

        holder.mView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mFragment.showMenuForItem(holder.mView, position);

                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

}
