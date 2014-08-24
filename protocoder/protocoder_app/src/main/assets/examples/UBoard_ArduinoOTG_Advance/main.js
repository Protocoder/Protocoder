/*
*	
*	Description ........ 
*	by ........ 
*
*/

app.doNotExecute(function() {
   
    var arduino = boards.startArduino();
    
    arduino.upload(Packages.com.physicaloid.lib.Boards.ARDUINO_UNO, "blink.uno.hex");
    arduino.upload(Packages.com.physicaloid.lib.Boards.ARDUINO_UNO, "serialwrite.uno.hex");
    
    var str = arduino.read();
    console.log("str: " + str);
     
});