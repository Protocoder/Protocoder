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

package org.protocoderrunner.api.widgets;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import org.protocoderrunner.api.common.ReturnInterface;
import org.protocoderrunner.api.common.ReturnObject;

public class PPopupDialogFragment extends DialogFragment {

    private static FragmentManager mFragmentManager;
    private ViewGroup mContainer;
    private String mTitle;
    private String mDescription;
    private String mOk;
    private String mCancel;
    private ReturnInterface mCallback;
    private String[] mChoice;
    private String[] mMultichoice;
    private boolean[] mMultichoiceState;
    private EditText mInput;
    private int mWidth = WindowManager.LayoutParams.WRAP_CONTENT;
    private int mHeight = WindowManager.LayoutParams.WRAP_CONTENT;
    private float mW = -2;
    private float mH = -2;

    public PPopupDialogFragment() {

    }

    public static PPopupDialogFragment newInstance(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
        PPopupDialogFragment frag = new PPopupDialogFragment();
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(mTitle);
        builder.setMessage(mDescription);

        final ReturnObject r = new ReturnObject();

        if (true) {
            mInput = new EditText(getActivity());
            mInput.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(mInput, 20, 20, 20, 20);
        }

        if (mChoice != null) {
            builder.setItems(mChoice, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    r.put("answer", mChoice[which]);
                    r.put("answerId", which);
                    dismiss();
                }
            });
        }

        if (mMultichoice != null) {
            builder.setMultiChoiceItems(mMultichoice, mMultichoiceState, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            mMultichoiceState[which] = isChecked;
                        }
                    }
            );
        }

        // only show ok if we dont have a selectable list
        if (mChoice == null) {
            builder.setPositiveButton(mOk, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (mCallback != null) {
                        r.put("accept", true);
                        if (mMultichoiceState != null) r.put("choices", mMultichoiceState);
                        if (mInput != null) r.put("answer", mInput.getText());
                        mCallback.event(r);
                    }
                }
            });
        }

        builder.setNegativeButton(mCancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                r.put("accept", false);
                if (mCallback != null) mCallback.event(r);
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();

        // if width and height is unset then we show the default size
        if (mW == -2 && mH == -2) return;

        calculateSize();
        getDialog().getWindow().setLayout(mWidth, mHeight);
    }

    public PPopupDialogFragment onAction(ReturnInterface callback) {
        mCallback = callback;

        return this;
    }

    public PPopupDialogFragment title(String title) {
        mTitle = title;

        return this;
    }

    public PPopupDialogFragment description(String description) {
        mDescription = description;

        return this;
    }

    public PPopupDialogFragment ok(String ok) {
        mOk = ok;

        return this;
    }

    public PPopupDialogFragment cancel(String cancel) {
        mCancel = cancel;

        return this;
    }

    public PPopupDialogFragment choice(String[] choices) {
        mChoice = choices;

        return this;
    }

    public PPopupDialogFragment multiChoice(String[] choices) {
        mMultichoice = choices;
        mMultichoiceState = new boolean[choices.length];

        return this;
    }

    public PPopupDialogFragment multiChoice(String[] choices, boolean[] state) {
        mMultichoice = choices;
        mMultichoiceState = state;

        return this;
    }

    public PPopupDialogFragment size(float w, float h) {
        mW = w;
        mH = h;

        return this;
    }

    public void show() {
        this.show(mFragmentManager, "popUpCustom");
    }

    public void addView(View v) {
        ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getDialog().addContentView(v, lp);
        mContainer.addView(v);
    }

    public void dismiss() {
        super.dismiss();
    }

    private void calculateSize() {
        Window window = getDialog().getWindow();
        Point size = new Point();
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        if (mW < 0) {
            mWidth = WindowManager.LayoutParams.WRAP_CONTENT;
        } else if (mW > 1) {
            mWidth = WindowManager.LayoutParams.MATCH_PARENT;
        } else {
            mWidth = (int) (size.x * mW);
        }

        if (mH < 0) {
            mHeight = WindowManager.LayoutParams.WRAP_CONTENT;
        } else if (mH > 1) {
            mHeight = WindowManager.LayoutParams.MATCH_PARENT;
        } else {
            mHeight = (int) (size.y * mH);
        }
    }
}
