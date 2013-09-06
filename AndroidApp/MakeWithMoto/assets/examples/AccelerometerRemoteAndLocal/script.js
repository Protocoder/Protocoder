/*
*    New project by ....... 
*
*/

var plot, plot2; 
var webPlot; 

ui.button("Start Accelerometer", 0, 0, ui.screenWidth, 200, function() {
        webapp.showDashboard(true);
        plot = ui.addPlot(0, 500, ui.screenWidth, 250, -12, 12); 
        plot2 = ui.addPlot(0, 800, ui.screenWidth, 250, -12, 12); 

        ui.title("on!");

        webPlot = webapp.addPlot("w", 100, 100, 100, 100);

        sensors.startAccelerometer(function(x,y,z) {
            //console.log(x);
            webPlot.update(y);
            plot.update(y);
            plot2.update(x);
          }
       );
});

ui.button("Stop Accelerometer", 0, 300, ui.screenWidth, 200, function() { 
    webapp.showDashboard(false);
    sensors.stopAccelerometer();
});