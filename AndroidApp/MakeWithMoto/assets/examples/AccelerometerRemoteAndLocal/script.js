/*
*    New project by ....... 
*
*/
//ui.fullscreen();
ui.button("Start Accelerometer", 0, 0, ui.screenWidth, 200, function() {
        webapp.showDashboard(true);
        ui.addPlot(0, 500, 700, 250); 
		ui.title("on!");

        plot = webapp.addWidget("w", 100, 100, 100, 100);

        sensors.startAccelerometer(function(x,y,z) {
            plot.update(y);
            ui.setPlotValue(y);
          }
       );
});