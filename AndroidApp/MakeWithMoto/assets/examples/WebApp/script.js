var plot;

ui.button("Add plot", 0, 0,500,100, function(){
    plot = webapp.addPlot("w", 100, 100, 100, 100);
});

ui.button("Update", 0, 100,500,100, function(){
    plot.update(10 * Math.random());
});


ui.toggleButton("Show Hide dashboard", 0, 200,500,100,false, function(b){
    webapp.showDashboard(b);
});

//labels need an unique id in order to be identified in the webapp
var label = webapp.addLabel("id1", "default_value", 100, 100, 200, 100);

ui.button("hola", 0, 300,500,100, function(){
    label.setText("hola");

});

ui.button("adios", 0, 400,500,100, function(){
    label.setText("adios");
});

