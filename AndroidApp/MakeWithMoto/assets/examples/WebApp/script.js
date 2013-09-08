/* 
*	Communication with the webapp  
*
*/ 

var plot;

//add a plot on the webapp 
ui.button("Add plot", 0, 0,500,100, function(){
    plot = webapp.addPlot("Plot", 600, 100, 200, 100);
});

//update the plot 
ui.button("Update plot randomly", 0, 100, 500, 100, function(){
    plot.update(10 * Math.random());
});

//show and hide the dashboard 
ui.toggleButton("Show Hide dashboard", 0, 200, 500, 100,false, function(b){
    webapp.showDashboard(b);
});

//labels need an unique id in order to be identified in the webapp
var label = webapp.addLabel("id1", "default_value", 100, 100, 200, 100);

//change the label text 
ui.button("hola", 0, 300, 500, 100, function(){
    label.setText("hola");
});

ui.button("adios", 0, 400, 500, 100, function(){
    label.setText("adios");
});

//add a button on the webpapp and when clicked will execute the inner function
var webbutton = webapp.addButton("id2", "hola", 400, 100, 100, 100, function() {
    android.toast("hola", 200);
    android.vibrate(500);
});