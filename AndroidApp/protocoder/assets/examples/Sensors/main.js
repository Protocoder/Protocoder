/* 
*  Sensors
*
*/ 

var accelerometer = ui.addLabel("", 20, 20, 500, 100, 16);
var orientation = ui.addLabel("", 20, 120, 500, 100, 16);
var light = ui.addLabel("", 20, 220, 500, 100, 16);
var gyroscope = ui.addLabel("", 20, 320, 500, 100, 16);
var magnetic = ui.addLabel("", 20, 420, 500, 100, 16);
var barometer = ui.addLabel("", 20, 520, 500, 100, 16);
var proximity = ui.addLabel("", 20, 520, 500, 100, 16);


sensors.startAccelerometer(function(x, y, z) {
    accelerometer.setText("accelerometer " + x + ", " + y + ", " + z);
   //console.log("accelerometer " + x + ", " + y + ", " + z);
});

sensors.startOrientation(function(pitch, roll, yaw) {
  orientation.setText("accelerometer " + x + ", " + y + ", " + z);
});

sensors.startLightIntensity(function(intensity) {
   light.setText("accelerometer " + x + ", " + y + ", " + z);
});


sensors.startGyroscope(function(x, y, z) {
   gyroscope.setText("accelerometer " + x + ", " + y + ", " + z);
});

sensors.startMagnetic(function(value) {
   magnetic.setText("accelerometer " + x + ", " + y + ", " + z);
});

sensors.startBarometer(function(x) {
   barometer.setText("accelerometer " + x + ", " + y + ", " + z);
});

sensors.startProximity(function(distance) {
  proximity.setText("accelerometer " + x + ", " + y + ", " + z);
});
