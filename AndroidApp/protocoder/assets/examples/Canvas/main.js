/*
*	draw on a canvas!
*
*/

var Paint = Packages.android.graphics.Paint;
var Color = Packages.android.graphics.Color;

var canvas = ui.addCanvas(0, 0, 500, 500);

var paint = new Paint();

var x1 = 0, y1 = 0; 
sensors.startAccelerometer(function(x, y, z) {
   x1 = x;
   y1 = y;
});

util.loop(35, function() { 
    paint.setColor(new Color().argb(15, 255, 0, 0));
    canvas.getCanvas().drawRect(0, 0, 500, 500, paint); 
    paint.setColor(Color.BLUE);
    //canvas.getCanvas().drawRect(x1 * 100, 500 * Math.random(), 500 * Math.random(), 500 * Math.random(), paint); 
    var mx = Math.round(250 + 50 * -x1);
    var my = Math.round(250 + 50 * y1);

    canvas.getCanvas().drawCircle(mx, my, 50, paint); 
    canvas.invalidate();
});