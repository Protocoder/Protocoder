/*
*	New project by ....... 
*
*/

ui.button("Start", 0, 200,500,100, function(){
        webapp.showDashboard(true);
        ui.addPlot(0, 400, 700, 250); 
		ui.title("on!");

        var plot = webapp.addWidget("w", 100, 100, 100, 100);

        sensors.startAccelerometer(
         function(x,y,z){

              plot.update(x);

             ui.setPlotValue(x);
          }
       );
});


ui.toggleButton("Show Hide dashboard", 0, 400,500,100,false, function(b){
});


