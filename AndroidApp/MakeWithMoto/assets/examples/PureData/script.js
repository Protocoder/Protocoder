/* 
*	Pure Data sound engine example 
*	transfer your pd patch and send values to it 
* 	with the sendFloat, sendBang methods 
*/

var pd = media.initPDPatch("notes.pd", function(type, data) { 
    console.log(type, data);
});

//add plot setting the limits from -12 to 12 
var plot = ui.addPlot(0, 400, ui.screenWidth, 250, -12, 12); 

sensors.startAccelerometer(function(x, y, z) {
   //console.log("accelerometer " + x + ", " + y + ", " + z);
    pd.sendFloat("midinote", 82 + Math.round(x));
    plot.update(x);
});