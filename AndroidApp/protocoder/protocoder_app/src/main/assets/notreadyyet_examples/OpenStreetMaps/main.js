/*
*	New project by ....... 
*
*/

var map = ui.addMap(0, 0, ui.screenWidth, 200);
map.clearCache();

	//	qq[0] = "http://a.tiles.mapbox.com/v3/czana.map-e6nd3na3/";
map.setTileSource("https://a.tiles.mapbox.com/v3/biquillo.h8c46o8i/");
		
ui.move(map, 0, 0);
ui.rotate(map, 0, 0, 0);
ui.jump(map);
var q = map.addPoint("qq", "qq", 100 * Math.random(), 100 * Math.random());
if (false) {
    q.move(100 * Math.random(), 100 * Math.random());
}
ui.blink(map, 2);
map.moveTo(100 * Math.random(), 100 * Math.random());
map.setCenter(1, 12);
map.setZoom(2);
map.showControls(true);

var map2 = ui.addMap(0, 300, ui.screenWidth, 200);