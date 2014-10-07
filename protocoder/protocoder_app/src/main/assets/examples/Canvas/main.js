/*
*   draw on a canvas!
*   This example imports native Android classes (Paint and Color) 
*   and uses them 
*/
var canvas = ui.addCanvas(0, 0, ui.screenWidth, 500);

canvas.loopDraw(35, function() {
    canvas.fill(255, 0, 0, 15);
    canvas.rect(0, 0, ui.screenWidth, 500);
    canvas.fill(0, 0, 255);
    var mx = Math.round(canvas.getWidth() / 2 + 50 * -x1);
    var my = Math.round(canvas.getHeight() / 2 + 50 * y1);
    canvas.ellipse(mx, my, 50, 50); 
});

var x1 = 0, y1 = 0; 
sensors.startAccelerometer(function(x, y, z) {
   x1 = x;
   y1 = y;
});