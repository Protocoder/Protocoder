/* 
*  Sensors
*
*/ 

var label = ui.addLabel("", 20, 20, 500, 100, 16);

sensors.startAccelerometer(function(x, y, z) {
    label.setText("accelerometer " + x + ", " + y + ", " + z);
   //console.log("accelerometer " + x + ", " + y + ", " + z);
});

sensors.startOrientation(function(pitch, roll, yaw) {
  //console.log("orientation: " + pitch + ", " + roll + ", " + yaw);
});

sensors.startLightIntensity(function(intensity) {
   //console.log("light: " + intensity);
});


sensors.startGyroscope(function(x, y, z) {
   //console.log("gyroscope " + x + ", " + y + ", " + z);
});

sensors.startMagnetic(function(x, y, z) {
   //console.log("magnetic " + x);
});

sensors.startPressure(function(x) {
   //console.log("pressure " + x);
});

//not ready yet :(
//sensors.startProximity(function(intensity) {
//  console.log("proximity: " + intensity);
//});
