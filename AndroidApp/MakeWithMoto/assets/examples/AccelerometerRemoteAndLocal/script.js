/*
*    New project by ....... 
*
*/
ui.button("Start Accelerometer", 0, 0, ui.screenWidth, 200, function() {
        webapp.showDashboard(true);
        ui.addPlot(0, 500, 700, 250); 
		ui.title("on!");

        plot = webapp.addPlot("w", 100, 100, 100, 100);

        sensors.startAccelerometer(function(x,y,z) {
            plot.update(y);
            ui.setPlotValue(y);
          }
       );
});

ui.button("Stop Accelerometer", 0, 300, ui.screenWidth, 200, function() { 
    webapp.showDashboard(false);
    sensors.stopAccelerometer();
});