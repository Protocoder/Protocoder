package org.protocoder.gui.editor;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.protocoder.R;
import org.protocoder.server.model.ProtoFile;

import java.lang.ref.WeakReference;

public class FileManagerListItem extends LinearLayout {

    private ImageView mImageView;
    private TextView mTextView;

    private WeakReference<View> v;
    private Context c;

    public FileManagerListItem(Context context) {
        super(context);
        this.c = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.v = new WeakReference<View>(inflater.inflate(R.layout.filemanager_file_view, this, true));

        mImageView = (ImageView) v.get().findViewById(R.id.img_file);
        mTextView = (TextView) v.get().findViewById(R.id.txt_file_name);
    }

    public FileManagerListItem(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setProtoFile(ProtoFile protoFile) {
        mImageView.setImageResource(getIcon(protoFile.type));
        mTextView.setText(protoFile.name);
    }

    private int getIcon(String type) {
        return type.equals("folder") ? R.drawable.protocoder_script_example : R.drawable.protocoder_script_project;
    }
}

