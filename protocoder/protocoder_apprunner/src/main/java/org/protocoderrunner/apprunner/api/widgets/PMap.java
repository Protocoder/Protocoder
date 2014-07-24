/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
 *
 * Copyright (C) 2014 Victor Diaz
 * Copyright (C) 2013 Motorola Mobility LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions: 
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 * 
 */

package org.protocoderrunner.apprunner.api.widgets;

import java.util.ArrayList;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.api.IMapController;
import org.osmdroid.events.DelayedMapListener;
import org.osmdroid.events.MapListener;
import org.osmdroid.events.ScrollEvent;
import org.osmdroid.events.ZoomEvent;
import org.osmdroid.tileprovider.MapTileProviderBasic;
import org.osmdroid.tileprovider.tilesource.ITileSource;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.OverlayItem.HotspotPlace;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;
import org.protocoderrunner.R;
import org.protocoderrunner.utils.MLog;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class PMap extends MapView {

	final String TAG = "MapView";

	private final IMapController mapController;
	private final MapView mapView;
	MyLocationNewOverlay myLocationOverlay;
	ItemizedIconOverlay<OverlayItem> iconOverlay;
	private final boolean firstMarker = false;
	private final ArrayList<OverlayItem> markerList;

	private Context c;

	public <T> PMap(Context c, int val) {
		super(c, val);
		this.c = c;

		// Create the mapview with the custom tile provider array
		this.mapView = this;
		markerList = new ArrayList<OverlayItem>();
		iconOverlay = new ItemizedIconOverlay<OverlayItem>(markerList, c.getResources().getDrawable(R.drawable.icon),
				new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {

					@Override
					public boolean onItemLongPress(int arg0, OverlayItem arg1) {
						MLog.d(TAG, "long press");
						return false;
					}

					@Override
					public boolean onItemSingleTapUp(int arg0, OverlayItem arg1) {
						MLog.d(TAG, "single press");
						return false;
					}
				}, new DefaultResourceProxyImpl(c.getApplicationContext()));

		mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

		mapView.setMultiTouchControls(true);
		mapController = mapView.getController();
		myLocationOverlay = new MyLocationNewOverlay(c, mapView);
		mapView.getOverlays().add(myLocationOverlay);
		mapView.getOverlays().add(iconOverlay);

        mapView.setClickable(true);
        mapView.setFocusable(true);
        mapView.setDuplicateParentStateEnabled(false);

        mapView.setMapListener(new DelayedMapListener(new MapListener() {
            @Override
            public boolean onScroll(ScrollEvent event) {
                Log.d(TAG, "qqqqqq");

                //mapView.getBoundingBox().getCenter();

                return true;
            }

            @Override
            public boolean onZoom(ZoomEvent event) {
                Log.d(TAG, "qqqqqq");

                //mapView.getBoundingBox().getCenter();


                return true;
            }
        }, 500));


		// myLocationOverlay.enableMyLocation();
		// myLocationOverlay.setDrawAccuracyEnabled(true);

	}

	public PathOverlay addPath(String color) {
		int color1 = Color.parseColor(color);
		PathOverlay line = new PathOverlay(color1, c);
		mapView.getOverlays().add(line);

		return line;
	}

	public MapView addPointToPath(PathOverlay p, double lat, double lon) {
		p.addPoint(new GeoPoint(lat, lon));
		mapView.invalidate();

        return this;
	}

	public MapView clearPath(PathOverlay p, double lat, double lon) {
		p.clearPath();

        return this;
	}

	public MapView setTileSource(String url) {

		String[] qq = new String[1];
		qq[0] = url;
		MapTileProviderBasic tileProvider = new MapTileProviderBasic(c);
		ITileSource tileSource = new XYTileSource("Test", null, 3, 10, 256, ".png", qq);

		tileProvider.setTileSource(tileSource);
		mapView.setTileSource(tileSource);

        return this;
	}

	public OverlayItem addMarker(String title, String text, double lat, double lon) {

		OverlayItem olItem = new OverlayItem(title, text, new GeoPoint(lat, lon));
		Drawable newMarker = c.getResources().getDrawable(R.drawable.marker);
		olItem.setMarker(newMarker);
		olItem.setMarkerHotspot(HotspotPlace.BOTTOM_CENTER);
		markerList.add(olItem);
		iconOverlay.addItem(olItem);
		this.invalidate();

		return olItem;

	}

	public MapView clearCache() {
		mapView.getTileProvider().clearTileCache();

        return this;
	}

	public MapView setZoom(int z) {
		mapController.setZoom(z);

        return this;
    }

	public MapView showControls(boolean b) {
        mapView.setBuiltInZoomControls(b);

        return this;
	}

	public MapView setMultitouch(boolean b) {
		mapView.setMultiTouchControls(b);
        return this;
	}

	public MapView follow(boolean b) {
		if (b) {
			myLocationOverlay.enableFollowLocation();
		} else {
			myLocationOverlay.disableFollowLocation();
		}

        return this;
	}

	public MapView moveTo(double lat, double lon) {
		GeoPoint point2 = new GeoPoint(lat, lon);
		mapController.animateTo(point2);
		// mapView.addMarker(lat, lon, "qq", "text");

        return this;
	}

	public MapView setCenter(double lat, double lon) {
		GeoPoint point2 = new GeoPoint(lat, lon);
		mapController.setCenter(point2);

        return this;
	}

    public GeoPoint getCenter() {
        return mapView.getBoundingBox().getCenter();
    }

    public float getZoom() {
        return mapView.getZoomLevel();
    }


    public MapView setZoomLimits(int min, int max) {
        mapView.setMinZoomLevel(min);
        mapView.setMaxZoomLevel(max);

        return this;
    }


//    @Override
//    public boolean onTouchEvent(MotionEvent ev) {
//        int action = ev.getAction();
//        switch (action) {
//            case MotionEvent.ACTION_DOWN:
//                // Disallow ScrollView to intercept touch events.
//                this.getParent().requestDisallowInterceptTouchEvent(true);
//                break;
//
//            case MotionEvent.ACTION_UP:
//                // Allow ScrollView to intercept touch events.
//                this.getParent().requestDisallowInterceptTouchEvent(false);
//                break;
//        }
//
//        // Handle MapView's touch events.
//        super.onTouchEvent(ev);
//        return true;
//    }
}