package org.protocoder.views;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

public class TouchableWrapper extends FrameLayout {

	private static final String TAG = "TouchableWrapped";

	public TouchableWrapper(Context context) {
		super(context);

	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			// MainActivity.mMapIsTouched = true;
			break;
		case MotionEvent.ACTION_UP:
			// MainActivity.mMapIsTouched = false;
			break;

		case MotionEvent.ACTION_MOVE:
			int x = (int) event.getX();
			int y = (int) event.getY();
			Log.d(TAG, "" + x + " " + y);

			// Point point = new Point(x, y);
			// LatLng latLng = map.getProjection().fromScreenLocation(point);
			// Point pixels = map.getProjection().toScreenLocation(latLng);;
			// mapCustomFragment.setTouch(latLng);

			// Log.d("qq2", x + " " + y + " " + latLng.latitude + " " +
			// latLng.longitude);
			break;
		}

		return super.dispatchTouchEvent(event);
	}
}