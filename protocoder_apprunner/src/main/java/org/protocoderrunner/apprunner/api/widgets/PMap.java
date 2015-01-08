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

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.Log;

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
import org.protocoderrunner.apidoc.annotation.APIMethod;
import org.protocoderrunner.apidoc.annotation.APIParam;
import org.protocoderrunner.apprunner.ProtocoderScript;
import org.protocoderrunner.utils.MLog;

import java.util.ArrayList;

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

    @ProtocoderScript
    @APIMethod(description = "Creates a path in which it can be added new points", example = "")
    @APIParam(params = { "colorHex" })
	public PathOverlay addPath(String color) {
		int color1 = Color.parseColor(color);
		PathOverlay line = new PathOverlay(color1, c);
		mapView.getOverlays().add(line);

		return line;
	}

    @ProtocoderScript
    @APIMethod(description = "Add a point to the path", example = "")
    @APIParam(params = { "path", "latitude", "longitude" })
	public MapView addPointToPath(PathOverlay p, double lat, double lon) {
		p.addPoint(new GeoPoint(lat, lon));
		mapView.invalidate();

        return this;
	}

    @ProtocoderScript
    @APIMethod(description = "Clear the path", example = "")
    @APIParam(params = { "path" })
	public MapView clearPath(PathOverlay p) {
		p.clearPath();

        return this;
	}

    @ProtocoderScript
    @APIMethod(description = "Set a new tile source such as mapbox and others", example = "")
    @APIParam(params = { "url" })
	public MapView setTileSource(String url) {

		String[] tileSourcesUrl = new String[1];
		tileSourcesUrl[0] = url;
		MapTileProviderBasic tileProvider = new MapTileProviderBasic(c);
		ITileSource tileSource = new XYTileSource("Test", null, 3, 10, 256, ".png", tileSourcesUrl);

		tileProvider.setTileSource(tileSource);
		mapView.setTileSource(tileSource);

        return this;
	}

    @ProtocoderScript
    @APIMethod(description = "Add a new marker", example = "")
    @APIParam(params = { "title", "text", "latitude", "longitude" })
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
    @ProtocoderScript
    @APIMethod(description = "Clear the map cache", example = "")
    @APIParam(params = { "" })
	public MapView clearCache() {
		mapView.getTileProvider().clearTileCache();

        return this;
	}

    @ProtocoderScript
    @APIMethod(description = "Zoom in/out depending on the integer given", example = "")
    @APIParam(params = { "zoomValue" })
	public MapView setZoom(int z) {
		mapController.setZoom(z);

        return this;
    }

    @ProtocoderScript
    @APIMethod(description = "Show/hide the map controls", example = "")
    @APIParam(params = { "boolean" })
	public MapView showControls(boolean b) {
        mapView.setBuiltInZoomControls(b);

        return this;
	}

    @ProtocoderScript
    @APIMethod(description = "Enable/Disables the multitouch events in the map", example = "")
    @APIParam(params = { "boolean" })
	public MapView setMultitouch(boolean b) {
		mapView.setMultiTouchControls(b);
        return this;
	}

    @ProtocoderScript
    @APIMethod(description = "Enable/Disables the map following using the GPS", example = "")
    @APIParam(params = { "boolean" })
	public MapView follow(boolean b) {
		if (b) {
			myLocationOverlay.enableFollowLocation();
		} else {
			myLocationOverlay.disableFollowLocation();
		}

        return this;
	}

    @ProtocoderScript
    @APIMethod(description = "Move to a specified location", example = "")
    @APIParam(params = { "latitude", "longitude" })
	public MapView moveTo(double lat, double lon) {
		GeoPoint point2 = new GeoPoint(lat, lon);
		mapController.animateTo(point2);

        return this;
	}

    @ProtocoderScript
    @APIMethod(description = "Set the center of the map with the specified location", example = "")
    @APIParam(params = { "latitude", "longitude" })
	public MapView setCenter(double lat, double lon) {
		GeoPoint point2 = new GeoPoint(lat, lon);
		mapController.setCenter(point2);

        return this;
	}

    @ProtocoderScript
    @APIMethod(description = "Gets the current center of the map", example = "")
    @APIParam(params = { "" })
    public GeoPoint getCenter() {
        return mapView.getBoundingBox().getCenter();
    }

    @ProtocoderScript
    @APIMethod(description = "Gets the current zoom of the map", example = "")
    @APIParam(params = { "" })
    public float getZoom() {
        return mapView.getZoomLevel();
    }

    @ProtocoderScript
    @APIMethod(description = "Set the zoom limits", example = "")
    @APIParam(params = { "min", "max" })
    public MapView setZoomLimits(int min, int max) {
        mapView.setMinZoomLevel(min);
        mapView.setMaxZoomLevel(max);

        return this;
    }

    @ProtocoderScript
    @APIMethod(description = "Get coordinates from the pixel position of the map", example = "")
    @APIParam(params = { "x", "y" })
    public org.osmdroid.api.IGeoPoint getCoordinatesFromPixels(int x, int y) {
        return mapView.getProjection().fromPixels(x, y);
    }

    @ProtocoderScript
    @APIMethod(description = "Get coordinates from the pixel position of the map", example = "")
    @APIParam(params = { "x", "y" })
    public Point getPixelsFromCoordinates(double lat, double lon) {
        GeoPoint point = new GeoPoint(lat, lon);
        return mapView.getProjection().toPixels(point, null);
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