/* 
*	Communication with the dashboard  
*
*   Dashboard is an element in the Web Editor where you 
*   can add widgets that interact with your device 
*   
*   You can remote control it!
*/ 

var plot;

//add a plot on the dashboard 
ui.addButton("Add plot", 0, 0,500,100, function(){
    plot = dashboard.addPlot("name", 600, 100, 200, 100, 0, 10);
});

//update the plot 
ui.addButton("Update plot randomly", 0, 100, 500, 100, function(){
    plot.update(10 * Math.random());
});

//show and hide the dashboard 
ui.addToggle("Show Hide dashboard", 0, 200, 500, 100,false, function(b){
    dashboard.show(b);
});

//labels need an unique id in order to be identified in the dashboard
var label = dashboard.addLabel("default_value", 100, 100, 28, "#FFFFFF");

//change the label text 
ui.addButton("hola", 0, 300, 500, 100, function(){
    label.setText("hola");
});

ui.addButton("adios", 0, 400, 500, 100, function(){
    label.setText("adios");
});

slider = dashboard.addSlider("name", 50, 500, 200, 100, 0, 100, function(val) {
    console.log(val);
    android.vibrate(100);
});

//add a button on the webpapp and when clicked will execute the inner function
var webbutton = dashboard.addButton("hola", 400, 100, 100, 100, function() {
    ui.toast("hola", 200);
    android.vibrate(500);
});

//add custom html
dashboard.addHTML("<a href = 'http://www.slashdot.com' style='font-size:50px'> This is a link to Slashdot! </a>", 100, 200);