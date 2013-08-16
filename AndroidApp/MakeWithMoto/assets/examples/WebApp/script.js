var plot;

ui.button("Add plot", 0, 0,500,100, function(){
    plot = webapp.addPlot("w", 100, 100, 100, 100);
});

ui.button("Update", 0, 200,500,100, function(){
    plot.update(10 * Math.random());
});


ui.toggleButton("Show Hide dashboard", 0, 400,500,100,false, function(b){
    webapp.showDashboard(b);
});