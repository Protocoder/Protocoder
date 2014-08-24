/* Arduino example 
* Works with Android devices with OTG support and Arduinos 
* such as Uno, load the Arduino.ino in your Arduino to the 
* the example working 
*/


var arduino = boards.startArduino();

arduino.upload(Packages.com.physicaloid.lib.Boards.ARDUINO_UNO, "serialwrite.uno.hex");

arduino.onRead(function(data){
    console.log(data);
    media.textToSpeech(data);
});