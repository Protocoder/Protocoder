/*
* \\\ Example: Device info
*
* Shows different information about the device
*/

var infoTxt = ui.addText('', 0, 0);

infoTxt.append('battery ' + device.battery())
infoTxt.append('\ndevice: ' + device.type())
infoTxt.append('\nbrightness: ' + device.brightness())

var info = device.info()
infoTxt.append('\nscreenDpi ' + info.screenDpi + ' versionRelease' + info.versionRelease)
infoTxt.append('\n' + network.ipAddress() + ' ' + network.wifiInfo().getSSID())

var batteryTxt = ui.addText('', 0, 0.5);
//this is a callback that triggers everytime the battery changes
device.battery(function (e) {
  batteryTxt.setText('battery update: ' + e.level)
})
