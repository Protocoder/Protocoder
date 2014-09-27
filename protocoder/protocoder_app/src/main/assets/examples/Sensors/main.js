/* 
*  Sensors
*
*  depending on your device, you will have access to certain 
*  sensors, check which ones work on your device!
* 
*/ 

var accelerometer   = ui.addText("", 20, 20, 600, 100);
var orientation     = ui.addText("", 20, 120, 600, 100);
var light           = ui.addText("", 20, 220, 600, 100);
var gyroscope       = ui.addText("", 20, 320, 600, 100);
var magnetic        = ui.addText("", 20, 420, 600, 100);
var barometer       = ui.addText("", 20, 520, 600, 100);
var proximity       = ui.addText("", 20, 620, 600, 100);

sensors.startAccelerometer(function(x, y, z) {
    accelerometer.setText("acc: " + x + ", " + y + ", " + z);
});

sensors.startOrientation(function(pitch, roll, yaw) {
  orientation.setText("orientation: " + pitch + ", " + roll + ", " + yaw);
});

sensors.startLightIntensity(function(intensity) {
   light.setText("light: " + intensity);
});

sensors.startGyroscope(function(x, y, z) {
   gyroscope.setText("gyro: " + x + ", " + y + ", " + z);
});

sensors.startMagnetic(function(x, y, z) {
   magnetic.setText("magnetic: " + x);
});

sensors.startBarometer(function(x) {
   barometer.setText("barometer " + x);
});

sensors.startProximity(function(intensity) {
  proximity.setText("proximity: " + intensity);
});

sensors.startStepCounter(function() {
    console.log("step detected");
});