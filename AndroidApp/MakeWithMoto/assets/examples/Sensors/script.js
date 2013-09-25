/* 
*   Android accelerometer 
*
*/ 

/*
* New project by ....... 
*
*/

sensors.startAccelerometer(
 function(x, y, z) {
   //console.log("accelerometer " + x + ", " + y + ", " + z);
  });

sensors.startOrientation(
 function(pitch, roll, yaw) {
  // console.log("orientation: " + pitch + ", " + roll + ", " + yaw);
});

sensors.startLightIntensity(
 function(intensity) {
   console.log("light: " + intensity);
});

//not ready yet :(
//sensors.startProximity(
// function(intensity) {
//   console.log("proximity: " + intensity);
//});