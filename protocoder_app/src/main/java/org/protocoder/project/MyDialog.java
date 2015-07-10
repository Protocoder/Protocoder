package org.protocoder.project;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import org.protocoder.Events;
import org.protocoder.R;
import org.protocoderrunner.base.utils.MLog;

import de.greenrobot.event.EventBus;

public class MyDialog extends DialogFragment {

    private String TAG = MyDialog.class.getSimpleName();

    public static MyDialog newInstance() {
        MyDialog dialogFragment = new MyDialog();
        return dialogFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_project_chooser, null);

        Window window = getDialog().getWindow();
        window.requestFeature(Window.FEATURE_NO_TITLE);
        //window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //window.setGravity(Gravity.TOP | Gravity.LEFT);

        // retrieve display dimensions
        Rect displayRectangle = new Rect();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);

        int w = (int) (displayRectangle.width() * 0.9);
        int h = (int) (displayRectangle.height() * 0.9);
        window.setLayout(w, h);

        // after that, setting values for x and y works "naturally"
        WindowManager.LayoutParams params = window.getAttributes();
        //params.x = 280;
        //params.y = 280;
        //params.height = 200;
        //params.dimAmount = 0f;


        window.setAttributes(params);


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.dialogchooserfl, FolderChooserFragment.newInstance("", true));
        fragmentTransaction.commit();

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onResume();
        EventBus.getDefault().unregister(this);
    }

    //folder choose
    public void onEventMainThread(Events.FolderChosen evt) {
        MLog.d(TAG, "< Event (folderChosen)");
        dismiss();
        //MLog.d(TAG, "event -> " + code);
    }

}

