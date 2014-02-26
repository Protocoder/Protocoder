/* 
*	GPS example 
* 
* 	Protocoder works with real GPS, therefore
*	you need be outdoor   
*/ 

// Labels to hold lat, lng & city name values of current location
var latLabel = ui.addLabel("Latitude : ",10,100,500,100);
var lonLabel = ui.addLabel("Longitude : ",10,200,500,100);
var altLabel = ui.addLabel("City : ",10,300,500,100);

//as demo purposes we are going to use google static maps 
//where for each update it will show an image of your current location 

//we start in latitude and longitude 0, 0
var map	= ui.addImage(0, 400, 700,500, "https://maps.googleapis.com/maps/api/staticmap?center=0,0&zoom=20&size=700x500&sensor=false");

//for each GPS update the image and values are changed 
sensors.startGPS(function (lat, lon, alt, speed, bearing) { 
    latLabel.setText("Latitude : " + lat);
    lonLabel.setText("Longitude : " + lon);
    altLabel.setText("Altitude : " + alt);
    map.setImage("https://maps.googleapis.com/maps/api/staticmap?center="+lat+","+lon+"&zoom=20&size=700x500&sensor=false")
});