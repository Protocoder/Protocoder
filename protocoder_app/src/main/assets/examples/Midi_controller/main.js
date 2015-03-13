/*
* Midi controller example
*
* Midi controllers will only work if your device has support of usb OTG
* If your device does not work create an issue in github indicating 
* your model and vendor id :)
*
*/


var midi = media.connectMidiDevice(function(status) {
	console.log("connected " + status);
});

midi.onNewInput(function(cable, ch, f, val) {
    console.log(cable, ch, f, val);
});

