// Label for heading
var heading = ui.label("Running accelerometer",10,10,500,100);

// Labels to hold x, y & z values of Accelerometer reading
var xLabel = ui.label("X : ",10,100,500,100);
var yLabel = ui.label("Y : ",250,100,500,100);
var zLabel = ui.label("Z : ",500,100,500,100);


ui.addPlot(0, 400, 700, 250); 

// Define the toggle button. if on start Acclerometer. else stop it.
ui.toggleButton("Turn accelerometer on/off", 0, 200, 500, 100, false, function(on){

    if (on === true){
        sensors.startAccelerometer(
         function(x,y,z){
             ui.labelSetText(xLabel, "X : "+x);
             ui.labelSetText(yLabel, "Y : "+y);
             ui.labelSetText(zLabel, "Z : "+z);

             ui.setPlotValue(x);
          }
       );
    } else {
        sensors.stopAccelerometer();
    }

});

