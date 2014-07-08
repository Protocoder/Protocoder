/*
*	
* Device info example
*
*/
var batteryLabel = ui.addLabel("battery", 10, 10, 200, 50);

//this gets battery when we want 
batteryLabel.setText("battery: " + device.getBatteryLevel());

//this is a callback that triggers everytime the battery changes
device.startBatteryListener(function (e) {
    batteryLabel.setText("battery " + e.level);
});

var tabletLabel = ui.addLabel("tablet " + device.isTablet(), 10, 60, 200, 50);
var brightnessLabel = ui.addLabel("brightness " + device.getBrightness(), 10, 110, 200, 50);


var info = device.getInfo();
var infoText = "screenDpi " + info.screenDpi + " versionRelease" + info.versionRelease;
var infoLabel = ui.addLabel(infoText, 10, 170, 500, 50);
