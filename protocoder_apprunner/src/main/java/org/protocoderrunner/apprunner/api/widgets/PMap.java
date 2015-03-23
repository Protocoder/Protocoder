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
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
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


    @ProtoMethod(description = "Creates a path in which it can be added new points", example = "")
    @ProtoMethodParam(params = {"colorHex"})
    public PathOverlay addPath(String color) {
        int color1 = Color.parseColor(color);
        PathOverlay line = new PathOverlay(color1, c);
        mapView.getOverlays().add(line);

        return line;
    }


    @ProtoMethod(description = "Add a point to the path", example = "")
    @ProtoMethodParam(params = {"path", "latitude", "longitude"})
    public MapView addPointToPath(PathOverlay p, double lat, double lon) {
        p.addPoint(new GeoPoint(lat, lon));
        mapView.invalidate();

        return this;
    }


    @ProtoMethod(description = "Clear the path", example = "")
    @ProtoMethodParam(params = {"path"})
    public MapView clearPath(PathOverlay p) {
        p.clearPath();

        return this;
    }


    @ProtoMethod(description = "Set a new tile source such as mapbox and others", example = "")
    @ProtoMethodParam(params = {"url"})
    public MapView tileSource(String url) {

        String[] tileSourcesUrl = new String[1];
        tileSourcesUrl[0] = url;
        MapTileProviderBasic tileProvider = new MapTileProviderBasic(c);
        ITileSource tileSource = new XYTileSource("Test", null, 3, 10, 256, ".png", tileSourcesUrl);

        tileProvider.setTileSource(tileSource);
        mapView.setTileSource(tileSource);

        return this;
    }


    @ProtoMethod(description = "Add a new marker", example = "")
    @ProtoMethodParam(params = {"title", "text", "latitude", "longitude"})
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

    @ProtoMethod(description = "Clear the map cache", example = "")
    @ProtoMethodParam(params = {""})
    public MapView clearCache() {
        mapView.getTileProvider().clearTileCache();

        return this;
    }


    @ProtoMethod(description = "Zoom in/out depending on the integer given", example = "")
    @ProtoMethodParam(params = {"zoomValue"})
    public MapView zoom(int z) {
        mapController.setZoom(z);

        return this;
    }


    @ProtoMethod(description = "Show/hide the map controls", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public MapView showControls(boolean b) {
        mapView.setBuiltInZoomControls(b);

        return this;
    }


    @ProtoMethod(description = "Enable/Disables the multitouch events in the map", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public MapView multitouch(boolean b) {
        mapView.setMultiTouchControls(b);
        return this;
    }


    @ProtoMethod(description = "Enable/Disables the map following using the GPS", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public MapView follow(boolean b) {
        if (b) {
            myLocationOverlay.enableFollowLocation();
        } else {
            myLocationOverlay.disableFollowLocation();
        }

        return this;
    }


    @ProtoMethod(description = "Move to a specified location", example = "")
    @ProtoMethodParam(params = {"latitude", "longitude"})
    public MapView moveTo(double lat, double lon) {
        GeoPoint point2 = new GeoPoint(lat, lon);
        mapController.animateTo(point2);

        return this;
    }


    @ProtoMethod(description = "Set the center of the map with the specified location", example = "")
    @ProtoMethodParam(params = {"latitude", "longitude"})
    public MapView center(double lat, double lon) {
        GeoPoint point2 = new GeoPoint(lat, lon);
        mapController.setCenter(point2);

        return this;
    }


    @ProtoMethod(description = "Gets the current center of the map", example = "")
    @ProtoMethodParam(params = {""})
    public GeoPoint center() {
        return mapView.getBoundingBox().getCenter();
    }


    @ProtoMethod(description = "Gets the current zoom of the map", example = "")
    @ProtoMethodParam(params = {""})
    public float zoom() {
        return mapView.getZoomLevel();
    }


    @ProtoMethod(description = "Set the zoom limits", example = "")
    @ProtoMethodParam(params = {"min", "max"})
    public MapView zoomLimits(int min, int max) {
        mapView.setMinZoomLevel(min);
        mapView.setMaxZoomLevel(max);

        return this;
    }


    @ProtoMethod(description = "Get coordinates from the pixel position of the map", example = "")
    @ProtoMethodParam(params = {"x", "y"})
    public org.osmdroid.api.IGeoPoint getCoordinatesFromPixels(int x, int y) {
        return mapView.getProjection().fromPixels(x, y);
    }


    @ProtoMethod(description = "Get coordinates from the pixel position of the map", example = "")
    @ProtoMethodParam(params = {"x", "y"})
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