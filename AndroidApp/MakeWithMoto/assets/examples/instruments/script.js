function setup() {
  ui.addLabel("Running accelerometer");
  ui.addToggleButton("Turn accelerometer on/off", function(running) {
    console.log("Running: " + running);
    if (running === 'true') {
      sensors.startAccelerometer(function(msg) {});
    } else {
      sensors.stopAccelerometer();
    }
  });
}

function loop() {
}