/*
* Works with ioio boards, you have to pair them using the 
*   android bluetooth settings and input the code 4545 
*/

var led; 
var input;
var ioioConnected = false;

//the phone will vibrate and speak when connected to the ioio 
ui.addButton("Start ioio", 0, 0, ui.screenWidth, 300, function() { 
    ioio.start(function() { 
       //this function is executed when the ioio board is ready
       ioioConnected = true;
       led = ioio.openDigitalOutput(0);
       input = ioio.openAnalogInput(31);
       android.vibrate(500);  
       media.textToSpeech("ioio connected");
    });
});

util.loop(500, function() {
    if(ioioConnected == true) { 
        console.log("the reading is " + input.getVoltage());
    }
})

ui.addButton("On", 0, 320, ui.screenWidth / 2, 350, function() { 
   if (ioioConnected == true) led.write(false);
});


ui.addButton("Off", ui.screenWidth / 2, 320, ui.screenWidth / 2, 350, function() { 
      if (ioioConnected == true)   led.write(true);
});


//power off the ioio  
ui.addButton("Stop ioio", 0, 920, ui.screenWidth, 100, function() { 
    if (ioioConnected == true) {
      ioio.stop();
      ioioConnected = false
    }
});