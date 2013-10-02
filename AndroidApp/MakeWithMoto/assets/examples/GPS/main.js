ui.setPadding(16, 16, 16, 16);

// Label for heading
var heading = ui.addLabel("Running GPS",10,10,500,100);

// Labels to hold lat, lng & city name values of current location
var latLabel = ui.addLabel("Latitude : ",10,100,500,100);
var lonLabel = ui.addLabel("Longitude : ",10,200,500,100);
var altLabel = ui.addLabel("City : ",10,300,500,100);

//start gps and use google maps static api to display the map 
//TOFIX: many updates makes the app to crash 

sensors.startGPS(function (lat, lon, alt, speed, bearing) { 
    latLabel.setText("Latitude : " + lat);
    lonLabel.setText("Longitude : " + lon);
    altLabel.setText("Altitude : " + alt);

	ui.addWebImage(0, 400, 700,500, "https://maps.googleapis.com/maps/api/staticmap?center="+lat+","+lon+"&zoom=20&size=700x500&sensor=false");


});
