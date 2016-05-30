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

package org.protocoder.gui.projectlist;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.protocoder.R;
import org.protocoder.events.Events;
import org.protocoderrunner.base.utils.MLog;
import org.protocoderrunner.models.Project;

public class ProjectItem extends LinearLayout {

    private static final String TAG = ProjectItem.class.getSimpleName();

    //private final ProjectListFragment mPlf;
    private View mItemView;
    // private Context c;
    private final Context c;
    private String t;
    private boolean highlighted = false;
    private Project mProject;
    private TextView textViewName;
    private ImageView mMenuButton;

    public ProjectItem(Context context, boolean listMode) {
        super(context);
        this.c = context;
        //this.mPlf = plf;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (listMode) {
            this.mItemView = inflater.inflate(R.layout.projectlist_item_list, this, true);
        } else {
            this.mItemView = inflater.inflate(R.layout.projectlist_item_grid, this, true);
        }

        FrameLayout fl = (FrameLayout) findViewById(R.id.viewProjectItemBackground);
        textViewName = (TextView) mItemView.findViewById(R.id.customViewText);

        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // MLog.d(TAG, " " + position + " " + getPosition());

                /*
                AnimatorSet animSpin;
                animSpin = (AnimatorSet) AnimatorInflater.loadAnimator(v.getContext(), R.animator.run);
                animSpin.setTarget(v);
                animSpin.start();
                */

                Runnable r = new Runnable() {
                    @Override
                    public void run() {

                        Events.ProjectEvent evt = new Events.ProjectEvent(Events.PROJECT_RUN, mProject);
                        EventBus.getDefault().post(evt);
                        // getActivity().overridePendingTransition(R.anim.splash_slide_in_anim_set,
                        //        R.anim.splash_slide_out_anim_set);
                    }
                };

                Handler handler = new Handler();
                handler.postDelayed(r, 0);


            }
        });

    }

    public void setImage(int resId) {
        ImageView imageView = (ImageView) mItemView.findViewById(R.id.customViewImage);
        imageView.setImageResource(resId);

        // drawText(imageView, t);
    }

    public void setText(String text) {
        this.t = text;
        textViewName.setText(text);
    }

    public void reInit(String text, boolean selected) {
        setText(text);
        // TODO reenable this setHighlighted(selected);
    }

    public void drawText(ImageView imageView, String t2) {

        // ImageView myImageView =
        Bitmap myBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Config.RGB_565);
        Paint myPaint = new Paint();
        myPaint.setColor(Color.BLUE);
        myPaint.setAntiAlias(true);
        myPaint.setTextSize(80);

        int x1 = 10;
        int y1 = 80;
        int x2 = 20;
        int y2 = 20;

        // Create mContext new image bitmap and attach a brand new canvas to it
        Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas tempCanvas = new Canvas(tempBitmap);

        // Draw the image bitmap into the cavas
        tempCanvas.drawBitmap(myBitmap, 0, 0, null);

        // Draw everything else you want into the canvas, in this example mContext
        // rectangle with rounded edges
        tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myPaint);
        tempCanvas.drawText(t2.substring(0, 1).toUpperCase(), x1, y1, myPaint);

        // Attach the canvas to the ImageView
        imageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
    }

    public void setMenu() {
        // MLog.d("TAG", "setting menu for " + mProject.getName());
        mMenuButton = (ImageView) findViewById(R.id.card_menu_button);

        mItemView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showMenu(mMenuButton);

                return true;
            }
        });

        //imageView.setOnCreateContextMenuListener();
        mMenuButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showMenu(mMenuButton);
            }
        });
//        this.setOnLongClickListener(new OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                showContextMenu();
//                return true;
//            }
//        });
    }

    private void showMenu(View fromView) {
        MLog.d(TAG, "clicked");
        PopupMenu myPopup = new PopupMenu(c, fromView);
        myPopup.inflate(R.menu.project_actions);
        myPopup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(final MenuItem menuItem) {

                int itemId = menuItem.getItemId();

                if (itemId == R.id.menu_project_list_run) {
                    EventBus.getDefault().post(new Events.ProjectEvent(Events.PROJECT_RUN, mProject));
                    return true;
                } else if (itemId == R.id.menu_project_list_edit) {
                    EventBus.getDefault().post(new Events.ProjectEvent(Events.PROJECT_EDIT, mProject));
                    return true;
                } else if (itemId == R.id.menu_project_list_delete) {
                    //TODO make it work again
                    //Protocoder.getInstance(c).protoScripts.delete(mProject.getFolder(), mProject.getName());

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    EventBus.getDefault().post(new Events.ProjectEvent(Events.PROJECT_DELETE, mProject));
                                    //mPlf.removeItem(mProject);

                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    break;
                            }
                        }
                    };
                    AlertDialog.Builder builder = new AlertDialog.Builder(c);
                    builder.setMessage("Are you sure?").setPositiveButton("Yes", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                    return true;
                } else if (itemId == R.id.menu_project_list_add_shortcut) {
                    //TODO make it work again
                    //Protocoder.getInstance(c).protoScripts.addShortcut(mProject.getFolder(), mProject.getName());
                    return true;
                } else if (itemId == R.id.menu_project_list_share_with) {
                    //TODO make it work again
                    //Protocoder.getInstance(c).protoScripts.shareMainJsDialog(mProject.getFolder(), mProject.getName());
                    return true;
                } else if (itemId == R.id.menu_project_list_share_proto_file) {
                    //TODO make it work again
                    //Protocoder.getInstance(c).protoScripts.shareProtoFileDialog(mProject.getFolder(), mProject.getName());
                    return true;
                } else {
                    return true;
                }
            }
        });
        myPopup.show();

    }

    /*
    public Drawable getBg() {
        return bg;
    }

    public void setHighlighted(boolean highlighted) {
        if (highlighted) {
            getBg().setColorFilter(0x22000000, PorterDuff.Mode.MULTIPLY);
        } else {
            getBg().clearColorFilter();
        }
        this.highlighted = highlighted;
    }
    */

    public boolean isHighlighted() {
        return highlighted;
    }

    public void setProject(Project p) {
        mProject = p;
        setText(p.getName());
        setTag(p.getName());
        setMenu();
    }
}
