// Label for heading
var heading = ui.label("Accelerometer Example",10,10,500,100);

// Labels to hold lat, lng & city name values of current location
var xlabel = ui.label("x : ",10,100,500,100);
var ylabel = ui.label("y : ",10,200,500,100);
var zlabel = ui.label("z : ",10,300,500,100);

sensor.startAccelerometer(function (x, y, z) { 
    ui.labelSetText(xlabel, x);

});
