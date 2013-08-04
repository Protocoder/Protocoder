// Label for heading
var heading = ui.label("Running GPS",10,10,500,100);

// Labels to hold lat, lng & city name values of current location
var latLabel = ui.label("Latitude : ",10,100,500,100);
var lonLabel = ui.label("Longitude : ",10,200,500,100);
var altLabel = ui.label("City : ",10,300,500,100);

sensors.startGPS(function (lat, lon, alt, speed, bearing) { 
    ui.labelSetText(latLabel, "Latitude : " + lat);
    ui.labelSetText(lonLabel, "Longitude : " + lon);
    ui.labelSetText(altLabel, "Altitude : " + alt);
});