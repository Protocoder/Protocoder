/* 
*   Android accelerometer 
*
*/ 

// Label for heading
var heading = ui.label("Running accelerometer",10,10,500,100);

// Labels to hold x, y & z values of Accelerometer reading
var xLabel = ui.label("X : ",10,100,500,100);
var yLabel = ui.label("Y : ",250,100,500,100);
var zLabel = ui.label("Z : ",500,100,500,100);

//add plot setting the limits from -12 to 12 
var plot = ui.addPlot(0, 400, ui.screenWidth, 250, -12, 12); 

// Define the toggle button. if on start Acclerometer. else stop it.
ui.toggleButton("Turn accelerometer on/off", 0, 200, 500, 100, false, function(on){

    if (on === true){
        sensors.startAccelerometer(
         function(x,y,z){
            //update labels 
            ui.labelSetText(xLabel, "X : "+x);
            ui.labelSetText(yLabel, "Y : "+y);
            ui.labelSetText(zLabel, "Z : "+z);

            //update plot with the x value
            plot.update(x);
          }
       );
    } else {
        sensors.stopAccelerometer();
    }

});