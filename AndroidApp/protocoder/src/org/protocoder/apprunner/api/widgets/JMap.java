package org.protocoder.apprunner.api.widgets;

import java.util.ArrayList;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.api.IMapController;
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
import org.protocoder.R;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class JMap extends MapView {

	final String TAG = "MapView";

	private final IMapController mapController;
	private final MapView mapView;
	MyLocationNewOverlay myLocationOverlay;
	ItemizedIconOverlay<OverlayItem> iconOverlay;
	private final boolean firstMarker = false;
	private final ArrayList<OverlayItem> markerList;

	private Context c;

	public <T> JMap(Context c, int val) {
		super(c, val);
		this.c = c;

		// Create the mapview with the custom tile provider array
		this.mapView = this;
		markerList = new ArrayList<OverlayItem>();
		iconOverlay = new ItemizedIconOverlay<OverlayItem>(markerList, c.getResources().getDrawable(R.drawable.icon),
				new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {

					@Override
					public boolean onItemLongPress(int arg0, OverlayItem arg1) {
						Log.d(TAG, "long press");
						return false;
					}

					@Override
					public boolean onItemSingleTapUp(int arg0, OverlayItem arg1) {
						Log.d(TAG, "single press");
						return false;
					}
				}, new DefaultResourceProxyImpl(c.getApplicationContext()));

		mapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);

		mapView.setMultiTouchControls(true);
		mapController = mapView.getController();
		myLocationOverlay = new MyLocationNewOverlay(c, mapView);
		mapView.getOverlays().add(myLocationOverlay);
		mapView.getOverlays().add(iconOverlay);

		// myLocationOverlay.enableMyLocation();
		// myLocationOverlay.setDrawAccuracyEnabled(true);

		PathOverlay line = new PathOverlay(Color.RED, c);
		line.addPoint(new GeoPoint(51.2, 0.1));
		line.addPoint(new GeoPoint(51.7, 0.3));
		mapView.getOverlays().add(line);

	}

	public void setTileSource(String url) {

		String[] qq = new String[1];
		qq[0] = url;
		MapTileProviderBasic tileProvider = new MapTileProviderBasic(c);
		ITileSource tileSource = new XYTileSource("Test", null, 3, 10, 256, ".png", qq);

		tileProvider.setTileSource(tileSource);
		mapView.setTileSource(tileSource);
	}

	public OverlayItem addPoint(String title, String text, double lat, double lon) {

		OverlayItem olItem = new OverlayItem("Here", "SampleDescription", new GeoPoint(lat, lon));
		Drawable newMarker = c.getResources().getDrawable(R.drawable.icon);
		olItem.setMarker(newMarker);
		olItem.setMarkerHotspot(HotspotPlace.CENTER);
		markerList.add(olItem);
		iconOverlay.addItem(olItem);
		this.invalidate();

		return olItem;

	}

	public void clearCache() {
		mapView.getTileProvider().clearTileCache();
	}

	public void setZoom(int z) {
		mapController.setZoom(z);
	}

	public void showControls(boolean b) {
		mapView.setBuiltInZoomControls(b);
	}

	public void setMultitouch(boolean b) {
		mapView.setMultiTouchControls(b);
	}

	public void follow(boolean b) {
		if (b) {
			myLocationOverlay.enableFollowLocation();
		} else {
			myLocationOverlay.disableFollowLocation();
		}
	}

	public void moveTo(double lat, double lon) {
		GeoPoint point2 = new GeoPoint(lat, lon);
		mapController.animateTo(point2);
		// mapView.addMarker(lat, lon, "qq", "text");
	}

	public void setCenter(double lat, double lon) {
		GeoPoint point2 = new GeoPoint(lat, lon);
		mapController.setCenter(point2);
	}

}