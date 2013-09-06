/*
*	New project by ....... 
*
*/

var led; 

//the phone will vibrate and speak when connected to the moio 
ui.button("Start Moio", 0, 0, ui.screenWidth, 300, function() { 
    ioio.start(function() { 
       //this function is executed when the moio board is ready
        led = ioio.openDigitalOutput(0);
        android.vibrate(500);  
        media.textToSpeech("moio connected");
    });
});


ui.button("On", 0, 320, ui.screenWidth / 2, 350, function() { 
   led.write(false);
});


ui.button("Off", ui.screenWidth / 2, 320, ui.screenWidth / 2, 350, function() { 
      led.write(true);
});


//power off the moio  
ui.button("Stop Moio", 0, 920, ui.screenWidth, 100, function() { 
    ioio.stop();
});