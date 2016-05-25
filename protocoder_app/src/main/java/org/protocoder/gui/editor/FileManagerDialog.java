package org.protocoder.gui.editor;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import org.protocoder.R;

public class FileManagerDialog extends DialogFragment {

    private String TAG = FileManagerDialog.class.getSimpleName();

    public static FileManagerDialog newInstance() {
        FileManagerDialog dialogFragment = new FileManagerDialog();
        return dialogFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.folderchooser_dialog, null);

        Window window = getDialog().getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);

        // retrieve display dimensions
        Rect displayRectangle = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        int w = (int) (displayRectangle.width() * 0.95);
        int h = (int) (displayRectangle.height() * 0.9);
        window.setLayout(w, h);

        WindowManager.LayoutParams params = window.getAttributes();
        window.setAttributes(params);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        FileManagerFragment fmf = FileManagerFragment.newInstance();
        Bundle bundle = new Bundle();
        bundle.putString(FileManagerFragment.ROOT_FOLDER, "/sdcard/protocodersandbox/examples");
        fmf.setArguments(bundle);
        fragmentTransaction.add(R.id.dialogchooserfl, fmf);
        fragmentTransaction.commit();

        super.onActivityCreated(savedInstanceState);
    }

}

