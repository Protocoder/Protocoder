// Label for heading
var heading = ui.label("Running accelerometer",10,10,500,100);

// Labels to hold x, y & z values of Accelerometer reading
var xlabel = ui.label("X : ",10,100,500,100);
var ylabel = ui.label("Y : ",250,100,500,100);
var zlabel = ui.label("Z : ",500,100,500,100);

// Show progress bar at the heading(moving dots)
var progress = 0;


// Define the toggle button. if on start Acclerometer. else stop it.
ui.toggleButton("Turn accelerometer on/off", 0, 200, 500, 100, false, function(running){

    if (running === true){
        android.toast("Accelerometer ON",2000);
        running = true;
        sensor.startAccelerometer(
         function(x,y,z){
             ui.labelSetText(xlabel, "X : "+x);
             ui.labelSetText(ylabel, "Y : "+y);
             ui.labelSetText(zlabel, "Z : "+z);
             progress = (progress + 1)%20;
             var txt="";
             for(var i=0;i<progress;i++){
                 txt=txt+".";
             }
             ui.labelSetText(heading, "Running accelerometer"+txt);
        
          }
       );
    }
    else{
        sensor.stopAccelerometer();
        running = false;
        android.toast("Accelerometer OFF",2000);
    }

});

