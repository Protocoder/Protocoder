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
package org.protocoderrunner.apprunner.api.widgets;

import android.view.MotionEvent;
import android.view.View;

public class WidgetHelper {

    public interface MoveCallback {
        void event(int x, int y);
    }

    public static void setMovable(View viewHandler, final View viewContainer, final MoveCallback callback) {
        View.OnTouchListener onMoveListener = new View.OnTouchListener() {

            public int x_init;
            public int y_init;

            @Override
            public boolean onTouch(View v, MotionEvent e) {
                int action = e.getActionMasked();

                switch (action) {

                    case MotionEvent.ACTION_DOWN:
                        x_init = (int) e.getRawX() - (int) viewContainer.getX();
                        //MLog.network(getContext(), TAG, "" + x_init + " " + (int) e.getRawX() + " " + (int) mWindow.getX() + " " + (int) mWindow.getLeft());
                        y_init = (int) e.getRawY() - (int) viewContainer.getY();

                        break;

                    case MotionEvent.ACTION_MOVE:

                        int x_cord = (int) e.getRawX();
                        int y_cord = (int) e.getRawY();

                        int posX = x_cord - x_init;
                        int posY = y_cord - y_init;

                        viewContainer.setX(posX);
                        viewContainer.setY(posY);

                        if (callback != null) callback.event(posX, posY);

                        break;
                }

                return true;
            }

        };

        viewHandler.setOnTouchListener(onMoveListener);
    }

    public static void removeMovable(View viewHandler) {
        viewHandler.setOnTouchListener(null);
    }
}
