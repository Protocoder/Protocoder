// Label for heading
var heading = ui.label("Running GPS",10,10,500,100);

// Labels to hold lat, lng & city name values of current location
var xlabel = ui.label("Latitude : ",10,100,500,100);
var ylabel = ui.label("Longitude : ",10,200,500,100);
var zlabel = ui.label("City : ",10,300,500,100);

// Show progress bar at the heading(moving dots)
var progress = 0;


// Define the toggle button. if on start Acclerometer. else stop it.
ui.toggleButton("Turn GPS on/on", 0, 400, 500, 100, false, function(running){

    if (running === true){
        android.toast("GPS ON",2000);

        sensor.startGPS(
         function(lat,lng,city){
             ui.labelSetText(xlabel, "Latitude : "+lat);
             ui.labelSetText(ylabel, "Longitude : "+lng);
             ui.labelSetText(zlabel, "City : "+city);

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
        android.toast("GPS is still ON :-)",2000);
    }

});

