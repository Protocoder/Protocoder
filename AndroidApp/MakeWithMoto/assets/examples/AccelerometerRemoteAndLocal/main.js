/*
*   Plots android accelerometer data locally and remotely 
*
*/


//add android plots 
var plot = ui.addPlot(0, 500, ui.screenWidth, 250, -12, 12); 
var plot2 = ui.addPlot(0, 800, ui.screenWidth, 250, -12, 12); 

//add webplot 
var webPlot = dashboard.addPlot("x_axys", 400, 100, 250, 100);


//start button, when press add plots and start accelerometer 
ui.addButton("Start Accelerometer", 0, 0, ui.screenWidth, 200, function() {
        //change title to on 
        ui.setTitle("on!");

        dashboard.show(true);

        sensors.startAccelerometer(function(x,y,z) {
            //update plots
            webPlot.update(x);
            plot.update(x);
            plot2.update(y);
          }
       );
});

//stop accelerometer 
ui.addButton("Stop Accelerometer", 0, 300, ui.screenWidth, 200, function() { 
    dashboard.show(false);
    sensors.stopAccelerometer();
});